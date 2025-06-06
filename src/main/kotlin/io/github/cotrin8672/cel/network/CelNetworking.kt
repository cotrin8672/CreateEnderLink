package io.github.cotrin8672.cel.network

import io.github.cotrin8672.cel.CreateEnderLink
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel

object CelNetworking {
    private var syncSharedStoragePacketID = 0
    private var linkedBlockCountPacketID = 0

    val CHANNEL: SimpleChannel = NetworkRegistry.newSimpleChannel(
        CreateEnderLink.asResource("sync_shared_storage"),
        { "1.1" },
        { it == "1.1" },
        { it == "1.1" },
    )

    fun registerPacket() {
        CHANNEL.messageBuilder(
            SyncSharedStoragePacket::class.java,
            syncSharedStoragePacketID++,
            NetworkDirection.PLAY_TO_CLIENT
        )
            .encoder(SyncSharedStoragePacket::encode)
            .decoder(SyncSharedStoragePacket::decode)
            .consumerNetworkThread(SyncSharedStoragePacket::handleOnClient)
            .add()

        CHANNEL.messageBuilder(
            FullLinkedCountPacket::class.java,
            linkedBlockCountPacketID++,
            NetworkDirection.PLAY_TO_CLIENT
        )
            .encoder(FullLinkedCountPacket::encode)
            .decoder(FullLinkedCountPacket::decode)
            .consumerNetworkThread(FullLinkedCountPacket::handleOnClient)
            .add()
    }
}
