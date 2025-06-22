package io.github.cotrin8672.cel.model

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block

data class LinkedCount(
    val blockKey: ResourceKey<Block>,
    val linkedList: Map<StorageFrequency, Int> = mapOf(),
) {
    companion object {
        val MAP_CODEC: MapCodec<LinkedCount> = RecordCodecBuilder.mapCodec { builder ->
            builder.group(
                ResourceKey.codec(Registries.BLOCK)
                    .fieldOf("block_key")
                    .forGetter { it.blockKey },
                Codec.unboundedMap(StorageFrequency.CODEC, Codec.INT)
                    .fieldOf("linked_list")
                    .forGetter { it.linkedList }
            ).apply(builder, ::LinkedCount)
        }

        val CODEC: Codec<LinkedCount> = MAP_CODEC.codec()

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, LinkedCount> = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.BLOCK),
            { link -> link.blockKey },
            ByteBufCodecs.map(
                ::HashMap,
                StorageFrequency.STREAM_CODEC,
                ByteBufCodecs.INT,
                256
            ),
            { link -> link.linkedList },
            ::LinkedCount
        )
    }
}
