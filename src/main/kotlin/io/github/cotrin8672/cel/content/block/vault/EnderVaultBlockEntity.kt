package io.github.cotrin8672.cel.content.block.vault

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform
import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import io.github.cotrin8672.cel.registry.CelBlockEntityTypes
import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.network.PacketDistributor
import io.github.cotrin8672.cel.network.SyncSharedStoragePacket
import java.util.*

class EnderVaultBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : SmartBlockEntity(type, pos, state), IHaveGoggleInformation {
    companion object {
        fun registerCapabilities(event: RegisterCapabilitiesEvent) {
            event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CelBlockEntityTypes.ENDER_VAULT.get()
            ) { be, _ ->
                return@registerBlockEntity be.getInventory()
            }
        }

    }

    private fun getInventory(): IItemHandler? {
        val behaviour = getBehaviour(SharedStorageBehaviour.TYPE) ?: return null
        val nonNullLevel = level ?: return null
        if (nonNullLevel is ServerLevel) {
            val inventory = SharedStorageHandler.instance?.getOrCreateSharedItemStorage(behaviour.getFrequency())
            return inventory
        }
        return null
    }

    override fun addBehaviours(behaviours: MutableList<BlockEntityBehaviour>) {
        behaviours.add(SharedStorageBehaviour(this, CenteredSideValueBoxTransform { state, direction ->
            state.getValue(BlockStateProperties.HORIZONTAL_AXIS) == direction.axis
        }))
    }

    override fun onLoad() {
        super.onLoad()
        updateFrequencyCount(1)
    }

    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking)
        getBehaviour(SharedStorageBehaviour.TYPE).addToGoggleTooltip(tooltip, isPlayerSneaking)

        return true
    }

    override fun destroy() {
        super.destroy()
        updateFrequencyCount(-1)
    }

    override fun remove() {
        super.remove()
        updateFrequencyCount(-1)
    }

    override fun onChunkUnloaded() {
        super.onChunkUnloaded()
        updateFrequencyCount(-1)
    }

    private fun updateFrequencyCount(delta: Int) {
        if (level is ServerLevel) {
            val freq = getBehaviour(SharedStorageBehaviour.TYPE).getFrequency()
            val handler = SharedStorageHandler.instance ?: return
            if (delta > 0) handler.incrementFrequency(freq) else handler.decrementFrequency(freq)
            val nbt = handler.save(CompoundTag(), (level as ServerLevel).registryAccess())
            PacketDistributor.sendToAllPlayers(SyncSharedStoragePacket(nbt))
        }
    }
}
