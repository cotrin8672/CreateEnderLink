package io.github.cotrin8672.cel.content.block.vault

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform
import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import io.github.cotrin8672.cel.registry.CelBlockEntityTypes
import io.github.cotrin8672.cel.util.SharedStorageHandler
import io.github.cotrin8672.cel.util.LinkCountManager
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.items.IItemHandler
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

        private val blockEntities: MutableSet<EnderVaultBlockEntity> = Collections.newSetFromMap(WeakHashMap())
    }

    init {
        val isAlreadyExists = blockEntities.map { it.blockPos }.contains(this.blockPos)
        if (!isAlreadyExists) blockEntities.add(this)
    }

    private var firstTick = true

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

    override fun tick() {
        super.tick()
        if (firstTick && level is ServerLevel) {
            firstTick = false
            LinkCountManager.sync(level as ServerLevel)
        }
    }

    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking)
        getBehaviour(SharedStorageBehaviour.TYPE).addToGoggleTooltip(tooltip, isPlayerSneaking)

        return true
    }

    override fun destroy() {
        super.destroy()
        blockEntities.remove(this)
        if (level is ServerLevel) LinkCountManager.sync(level as ServerLevel)
    }

    override fun remove() {
        super.remove()
        blockEntities.remove(this)
        if (level is ServerLevel) LinkCountManager.sync(level as ServerLevel)
    }

    override fun onChunkUnloaded() {
        super.onChunkUnloaded()
        blockEntities.remove(this)
        if (level is ServerLevel) LinkCountManager.sync(level as ServerLevel)
    }
}
