package io.github.cotrin8672.cel.model

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import java.util.*

@JvmRecord
data class ProfileKey(
    val uuid: UUID,
    val name: String,
) {
    companion object {
        val MAP_CODEC: MapCodec<ProfileKey> = RecordCodecBuilder.mapCodec { builder ->
            builder.group(
                UUIDUtil.CODEC.fieldOf("uuid").forGetter { it.uuid },
                Codec.STRING.fieldOf("name").forGetter { it.name }
            ).apply(builder, ::ProfileKey)
        }

        val CODEC: Codec<ProfileKey> = MAP_CODEC.codec()
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, ProfileKey> = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            { freq -> freq.uuid },
            ByteBufCodecs.STRING_UTF8,
            { freq -> freq.name },
            ::ProfileKey
        )
    }
}
