package io.github.cotrin8672.cel.content.block.tank

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform
import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import io.github.cotrin8672.cel.content.storage.SharedFluidTank
import io.github.cotrin8672.cel.registry.CelBlockEntityTypes
import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.createmod.ponder.api.level.PonderLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.network.PacketDistributor
import io.github.cotrin8672.cel.network.SyncSharedStoragePacket
import java.util.*

class EnderTankBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : SmartBlockEntity(type, pos, state), IHaveGoggleInformation {
    companion object {
        fun registerCapability(event: RegisterCapabilitiesEvent) {
            event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                CelBlockEntityTypes.ENDER_TANK.get()
            ) { be, _ ->
                return@registerBlockEntity be.getFluidTank()
            }
        }
    }

    private var luminosity = 0
    private var queuedSync = false
    private var syncCooldown = 0
    private var ponderTank: SharedFluidTank? = null

    fun getFluidTank(): SharedFluidTank? {
        if (level is PonderLevel) {
            if (ponderTank == null) ponderTank = SharedFluidTank(10000, null)
            return ponderTank
        }
        val behaviour = getBehaviour(SharedStorageBehaviour.TYPE) ?: return null
        val fluidTank = SharedStorageHandler.instance?.getOrCreateSharedFluidStorage(behaviour.getFrequency())
        if (level is ServerLevel) {
            fluidTank?.serverLevel = level as ServerLevel
        }
        return fluidTank
    }

    override fun addBehaviours(behaviours: MutableList<BlockEntityBehaviour>) {
        behaviours.add(SharedStorageBehaviour(this, CenteredSideValueBoxTransform { _, direction ->
            direction.axis == Direction.Axis.Y
        }))
    }

    override fun onLoad() {
        super.onLoad()
        updateFrequencyCount(1)
    }

    fun setLuminosity(luminosity: Int) {
        if (level!!.isClientSide) return
        if (this.luminosity == luminosity) return
        this.luminosity = luminosity
        sendData()
    }

    override fun tick() {
        super.tick()

        if (syncCooldown > 0) {
            syncCooldown--
            if (syncCooldown == 0 && queuedSync) sendData()
        }
    }

    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking)

        containedFluidTooltip(
            tooltip, isPlayerSneaking,
            level?.getCapability(Capabilities.FluidHandler.BLOCK, blockPos, Direction.UP)
        )
        tooltip.add(CommonComponents.EMPTY)

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

    override fun sendData() {
        if (syncCooldown > 0) {
            queuedSync = true
            return
        }
        super.sendData()
        queuedSync = false
        syncCooldown = 8
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

    override fun read(tag: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        super.read(tag, registries, clientPacket)

        val prevLum = luminosity

        luminosity = tag.getInt("Luminosity")


        if (luminosity != prevLum && hasLevel())
            level?.chunkSource?.lightEngine?.checkBlock(worldPosition)

    }

    override fun write(tag: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        tag.putInt("Luminosity", luminosity)
        super.write(tag, registries, clientPacket)

        if (!clientPacket) return
        if (queuedSync)
            tag.putBoolean("LazySync", true)
    }
}
