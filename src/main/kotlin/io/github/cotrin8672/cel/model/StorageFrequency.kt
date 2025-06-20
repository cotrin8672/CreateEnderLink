package io.github.cotrin8672.cel.model

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.Optional
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.optionals.getOrDefault

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
        get() = this@StorageFrequency.profileKey == GLOBAL_PROFILE

    val isPersonalScope: Boolean
        get() = !isGlobalScope

    val isEmpty: Boolean
        get() = this == EMPTY

    val isNotEmpty: Boolean
        get() = !isEmpty
    val color by lazy { stack.get(DataComponents.DYED_COLOR)?.rgb ?: -1 }

    companion object {
        val GLOBAL_PROFILE by lazy {
            ProfileKey(UUID.fromString("83695eeb-3b18-40d8-a790-d16d749e1413"), "Global")
        }

        // Codecs
        private val MAP_CODEC: MapCodec<StorageFrequency> = RecordCodecBuilder.mapCodec { builder ->
            builder.group(
                ItemStack.OPTIONAL_CODEC.fieldOf("frequency_item").forGetter { it.stack },
                ProfileKey.CODEC.fieldOf("profile_key").forGetter { it.profileKey }
            ).apply(builder, ::StorageFrequency)
        }

        val CODEC: Codec<StorageFrequency> = MAP_CODEC.codec()

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, StorageFrequency> = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC,
            { freq -> freq.stack },
            ProfileKey.STREAM_CODEC,
            { freq -> freq.profileKey },
            StorageFrequency::of
        )

        val EMPTY = StorageFrequency(ItemStack.EMPTY)

        private val storageFrequencies = ConcurrentHashMap<FrequencyKey, StorageFrequency>()

        fun of(stack: ItemStack, profileKey: ProfileKey = GLOBAL_PROFILE): StorageFrequency {
            val color = stack.get(DataComponents.DYED_COLOR)?.rgb ?: -1
            return storageFrequencies.computeIfAbsent(FrequencyKey(stack.item, color, profileKey)) {
                StorageFrequency(stack.item.defaultInstance, profileKey)
            }
        }

        fun parse(lookupProvider: HolderLookup.Provider, tag: CompoundTag): Optional<StorageFrequency> {
            return CODEC.parse(
                lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag
            ).resultOrPartial()
        }

        fun parseOptional(lookupProvider: HolderLookup.Provider, tag: CompoundTag): StorageFrequency {
            return if (tag.isEmpty) EMPTY else parse(lookupProvider, tag).orElse(EMPTY)
        }
    }

    fun save(lookupProvider: HolderLookup.Provider, tag: Tag = CompoundTag()): Tag {
        return CODEC.encodeStart(
            lookupProvider.createSerializationContext(NbtOps.INSTANCE),
            this
        )
            .resultOrPartial()
            .getOrDefault(CompoundTag())
    }

    fun saveOptional(lookupProvider: HolderLookup.Provider): Tag {
        return if (this.isEmpty) CompoundTag() else save(lookupProvider, CompoundTag())
    }

    override fun equals(other: Any?): Boolean {
        if (other !is StorageFrequency) return false
        return stack.item == other.stack.item && this@StorageFrequency.profileKey == other.profileKey && color == other.color
    }

    override fun hashCode(): Int {
        return (31 * stack.item.hashCode() + this@StorageFrequency.profileKey.hashCode()) xor color
    }
}