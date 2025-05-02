package io.github.cotrin8672.cel.util

import com.mojang.authlib.GameProfile
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class StorageFrequency
private constructor(
    val stack: ItemStack,
    val gameProfile: GameProfile = GLOBAL_PROFILE,
) {
    data class FrequencyKey(
        val item: Item,
        val color: Int,
        val gameProfile: GameProfile = GLOBAL_PROFILE,
    )

    fun copy(
        stack: ItemStack = this.stack,
        gameProfile: GameProfile = this.gameProfile,
    ): StorageFrequency {
        return of(stack, gameProfile)
    }

    val isGlobalScope: Boolean
        get() = gameProfile == GLOBAL_PROFILE

    val isPersonalScope: Boolean
        get() = !isGlobalScope

    private val isEmpty: Boolean
        get() = this == EMPTY

    val color by lazy {
        val displayTag = stack.getTagElement("display")
        if (displayTag != null && displayTag.contains("color")) displayTag.getInt("color") else -1
    }

    companion object {
        val GLOBAL_PROFILE by lazy {
            GameProfile(UUID.fromString("83695eeb-3b18-40d8-a790-d16d749e1413"), "Global")
        }

        // Codecs
        private val GAME_PROFILE_CODEC = RecordCodecBuilder.mapCodec { builder ->
            builder.group(
                UUIDUtil.CODEC.fieldOf("id").forGetter(GameProfile::getId),
                Codec.STRING.fieldOf("name").forGetter(GameProfile::getName)
            ).apply(builder, ::GameProfile)
        }

        private val MAP_CODEC: MapCodec<StorageFrequency> = RecordCodecBuilder.mapCodec { builder ->
            builder.group(
                ItemStack.CODEC.fieldOf("frequency_item").forGetter { it.stack },
                GAME_PROFILE_CODEC.fieldOf("game_profile").forGetter { it.gameProfile }
            ).apply(builder, ::StorageFrequency)
        }

        val CODEC: Codec<StorageFrequency> = MAP_CODEC.codec()

        val EMPTY = StorageFrequency(ItemStack.EMPTY)

        private val storageFrequencies = ConcurrentHashMap<FrequencyKey, StorageFrequency>()

        fun of(stack: ItemStack, gameProfile: GameProfile = GLOBAL_PROFILE): StorageFrequency {
            val displayTag = stack.getTagElement("display")
            val color = if (displayTag != null && displayTag.contains("color")) displayTag.getInt("color") else -1

            return storageFrequencies.computeIfAbsent(FrequencyKey(stack.item, color, gameProfile)) {
                StorageFrequency(stack, gameProfile)
            }
        }

        fun parseOptional(tag: CompoundTag): StorageFrequency {
            val stack = if (tag.contains("ItemStack"))
                ItemStack.of(tag.getCompound("ItemStack"))
            else ItemStack.EMPTY
            val gameProfile = if (tag.contains("GameProfile"))
                deserializeGameProfile(tag.getCompound("GameProfile"))
            else GLOBAL_PROFILE
            return of(stack, gameProfile)
        }

        private fun serializeGameProfile(gameProfile: GameProfile): CompoundTag {
            return CompoundTag().apply {
                putUUID("UUID", gameProfile.id)
                putString("Name", gameProfile.name)
            }
        }

        private fun deserializeGameProfile(tag: CompoundTag): GameProfile {
            return GameProfile(tag.getUUID("UUID"), tag.getString("Name"))
        }
    }

    fun saveOptional(): Tag {
        return CompoundTag().apply {
            put("ItemStack", stack.serializeNBT())
            put("GameProfile", serializeGameProfile(gameProfile))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is StorageFrequency) return false
        return stack.item == other.stack.item && gameProfile == other.gameProfile && color == other.color
    }

    override fun hashCode(): Int {
        return (31 * stack.item.hashCode() + gameProfile.hashCode()) xor color
    }
}