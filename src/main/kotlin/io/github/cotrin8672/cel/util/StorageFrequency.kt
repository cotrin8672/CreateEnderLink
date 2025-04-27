package io.github.cotrin8672.cel.util

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import java.util.*

class StorageFrequency
private constructor(
    val stack: ItemStack,
    val playerUuid: UUID? = null,
    val playerName: String? = null,
) {
    data class FrequencyKey(
        val stack: ItemStack,
        val ownerUUID: UUID? = null,
    )

    companion object {
        // Codecs
        val MAP_CODEC: MapCodec<StorageFrequency> = RecordCodecBuilder.mapCodec { builder ->
            builder.group(
                ItemStack.CODEC.fieldOf("frequency_item").forGetter { it.stack },
                Codec.STRING
                    .xmap({ UUID.fromString(it) }, { it.toString() })
                    .optionalFieldOf("frequency_owner", null)
                    .forGetter { it.playerUuid }
            ).apply(builder, ::StorageFrequency)
        }

        val CODEC: Codec<StorageFrequency> = MAP_CODEC.codec()

        val EMPTY = StorageFrequency(ItemStack.EMPTY)

        private val storageFrequencies = HashMap<FrequencyKey, StorageFrequency>()

        fun of(stack: ItemStack, playerUuid: UUID? = null, playerName: String? = null): StorageFrequency {
            return storageFrequencies.computeIfAbsent(FrequencyKey(stack, playerUuid)) {
                StorageFrequency(stack, playerUuid, playerName)
            }
        }

        fun of(stack: ItemStack, playerUuid: String, playerName: String? = null): StorageFrequency {
            val uuid = try {
                UUID.fromString(playerUuid)
            } catch (_: Exception) {
                null
            }
            return storageFrequencies.computeIfAbsent(FrequencyKey(stack, uuid)) {
                StorageFrequency(stack, uuid, playerName)
            }
        }
    }

    val color: Int
        get() {
            return stack.get(DataComponents.DYED_COLOR)?.rgb ?: -1
        }

    override fun equals(other: Any?): Boolean {
        if (other !is StorageFrequency) return false
        return stack.item == other.stack.item && playerUuid == other.playerUuid && color == other.color
    }

    override fun hashCode(): Int {
        return (31 * stack.item.hashCode() + (playerUuid?.hashCode() ?: 0)) xor color
    }
}
