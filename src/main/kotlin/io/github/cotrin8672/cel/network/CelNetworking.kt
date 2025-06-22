package io.github.cotrin8672.cel.network

import io.github.cotrin8672.cel.CreateEnderLink
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel

object CelNetworking {
    private var packetId = 0

    private fun nextId() = packetId++

    val CHANNEL: SimpleChannel = NetworkRegistry.newSimpleChannel(
        CreateEnderLink.asResource("sync_shared_storage"),
        { "1.1" },
        { it == "1.1" },
        { it == "1.1" },
    )

    fun registerPacket() {
        CHANNEL.messageBuilder(
            SyncSharedStoragePacket::class.java,
            nextId(),
            NetworkDirection.PLAY_TO_CLIENT
        )
            .encoder(SyncSharedStoragePacket::encode)
            .decoder(SyncSharedStoragePacket::decode)
            .consumerNetworkThread(SyncSharedStoragePacket::handleOnClient)
            .add()

        CHANNEL.messageBuilder(
            SyncFullLinkPacket::class.java,
            nextId(),
            NetworkDirection.PLAY_TO_CLIENT
        )
            .encoder(SyncFullLinkPacket::encode)
            .decoder(SyncFullLinkPacket::decode)
            .consumerNetworkThread(SyncFullLinkPacket::handleOnClient)
            .add()

        CHANNEL.messageBuilder(
            UpdateSharedTankPacket::class.java,
            nextId(),
            NetworkDirection.PLAY_TO_CLIENT
        )
            .encoder(UpdateSharedTankPacket::encode)
            .decoder(UpdateSharedTankPacket::decode)
            .consumerNetworkThread(UpdateSharedTankPacket::handleOnClient)
            .add()
    }
}
