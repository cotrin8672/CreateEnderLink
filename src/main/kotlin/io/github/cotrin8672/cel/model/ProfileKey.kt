package io.github.cotrin8672.cel.model

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import java.util.*

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

        fun parseOptional(tag: CompoundTag): ProfileKey {
            if (!tag.contains("uuid") || !tag.contains("name")) {
                throw IllegalArgumentException("ProfileKey tag missing 'uuid' or 'name'")
            }
            val uuid = tag.getUUID("uuid")
            val name = tag.getString("name")
            return ProfileKey(uuid, name)
        }
    }

    fun saveOptional(): CompoundTag =
        CompoundTag().apply {
            putUUID("uuid", uuid)
            putString("name", name)
        }
}
