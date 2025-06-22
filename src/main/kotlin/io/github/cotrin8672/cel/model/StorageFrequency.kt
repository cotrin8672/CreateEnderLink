package io.github.cotrin8672.cel.model

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class StorageFrequency
private constructor(
    val stack: ItemStack,
    val profileKey: ProfileKey = GLOBAL_PROFILE,
) {
    data class FrequencyKey(
        val item: Item,
        val color: Int,
        val profileKey: ProfileKey = GLOBAL_PROFILE,
    )

    fun copy(
        stack: ItemStack = this.stack,
        profileKey: ProfileKey = this.profileKey,
    ): StorageFrequency {
        return of(stack, profileKey)
    }

    val isGlobalScope: Boolean
        get() = profileKey == GLOBAL_PROFILE

    val isPersonalScope: Boolean
        get() = !isGlobalScope

    val isEmpty: Boolean
        get() = this == EMPTY

    val isNotEmpty: Boolean
        get() = !isEmpty

    val color by lazy {
        val displayTag = stack.getTagElement("display")
        if (displayTag != null && displayTag.contains("color")) displayTag.getInt("color") else -1
    }

    companion object {
        val GLOBAL_PROFILE by lazy {
            ProfileKey(UUID.fromString("83695eeb-3b18-40d8-a790-d16d749e1413"), "Global")
        }

        private val MAP_CODEC: MapCodec<StorageFrequency> = RecordCodecBuilder.mapCodec { builder ->
            builder.group(
                ItemStack.CODEC.fieldOf("frequency_item").forGetter { it.stack },
                ProfileKey.CODEC.fieldOf("game_profile").forGetter { it.profileKey }
            ).apply(builder, ::StorageFrequency)
        }

        val CODEC: Codec<StorageFrequency> = MAP_CODEC.codec()

        val EMPTY = StorageFrequency(ItemStack.EMPTY)

        private val storageFrequencies = ConcurrentHashMap<FrequencyKey, StorageFrequency>()

        fun of(stack: ItemStack, profileKey: ProfileKey = GLOBAL_PROFILE): StorageFrequency {
            val displayTag = stack.getTagElement("display")
            val color = if (displayTag != null && displayTag.contains("color")) displayTag.getInt("color") else -1

            return storageFrequencies.computeIfAbsent(FrequencyKey(stack.item, color, profileKey)) {
                StorageFrequency(stack.item.defaultInstance, profileKey)
            }
        }

        fun parseOptional(tag: CompoundTag): StorageFrequency {
            val stack = if (tag.contains("ItemStack"))
                ItemStack.of(tag.getCompound("ItemStack"))
            else ItemStack.EMPTY
            val gameProfile = if (tag.contains("GameProfile"))
                ProfileKey.parseOptional(tag.getCompound("GameProfile"))
            else GLOBAL_PROFILE
            return of(stack, gameProfile)
        }
    }

    fun saveOptional(): CompoundTag {
        return CompoundTag().apply {
            put("ItemStack", stack.serializeNBT())
            put("GameProfile", profileKey.saveOptional())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is StorageFrequency) return false
        return stack.item == other.stack.item && profileKey == other.profileKey && color == other.color
    }

    override fun hashCode(): Int {
        return (31 * stack.item.hashCode() + profileKey.hashCode()) xor color
    }
}
