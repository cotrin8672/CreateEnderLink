package io.github.cotrin8672.cel.network

import io.github.cotrin8672.cel.CreateEnderLink
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel
import java.util.*

object CelNetworking {
    private var id = 0
    val CHANNEL: SimpleChannel = NetworkRegistry.newSimpleChannel(
        CreateEnderLink.asResource("sync_shared_storage"),
        { "1.0" },
        { it == "1.0" },
        { it == "1.0" },
    )

    fun registerPacket() {
        CHANNEL.registerMessage(
            id++,
            SyncSharedStoragePacket::class.java,
            SyncSharedStoragePacket::encode,
            SyncSharedStoragePacket::decode,
            SyncSharedStoragePacket::handleOnClient,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        )
    }
}
