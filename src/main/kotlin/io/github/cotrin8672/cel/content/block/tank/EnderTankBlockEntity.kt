package io.github.cotrin8672.cel.content.block.tank

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform
import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import io.github.cotrin8672.cel.content.storage.SharedFluidTank
import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.createmod.ponder.api.level.PonderLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.fluids.capability.IFluidHandler
import java.util.*
import javax.annotation.Nonnull

class EnderTankBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : SmartBlockEntity(type, pos, state), IHaveGoggleInformation {
    companion object {
        private val blockEntities: MutableSet<EnderTankBlockEntity> = Collections.newSetFromMap(WeakHashMap())

        fun getLoadingBlockEntities(): Set<EnderTankBlockEntity> = blockEntities.toSet()
    }

    init {
        val isAlreadyExists = blockEntities.map { it.blockPos }.contains(this.blockPos)
        if (!isAlreadyExists) blockEntities.add(this)
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
        return fluidTank
    }

    override fun <T> getCapability(@Nonnull cap: Capability<T>, side: Direction?): LazyOptional<T> {
        val tank = getFluidTank() ?: return super.getCapability(cap, side)
        val fluidCapability = LazyOptional.of<IFluidHandler> { tank }
        return if (cap === ForgeCapabilities.FLUID_HANDLER)
            fluidCapability.cast()
        else
            super.getCapability(cap, side)
    }

    override fun addBehaviours(behaviours: MutableList<BlockEntityBehaviour>) {
        behaviours.add(SharedStorageBehaviour(this, CenteredSideValueBoxTransform { _, direction ->
            direction.axis == Direction.Axis.Y
        }))
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

        containedFluidTooltip(tooltip, isPlayerSneaking, this.getCapability(ForgeCapabilities.FLUID_HANDLER))

        tooltip.add(CommonComponents.EMPTY)

        getBehaviour(SharedStorageBehaviour.TYPE).addToGoggleTooltip(tooltip, isPlayerSneaking, blockEntities)
        return true
    }

    override fun destroy() {
        super.destroy()
        blockEntities.remove(this)
    }

    override fun remove() {
        super.remove()
        blockEntities.remove(this)
    }

    override fun onChunkUnloaded() {
        super.onChunkUnloaded()
        blockEntities.remove(this)
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

    override fun read(tag: CompoundTag, clientPacket: Boolean) {
        super.read(tag, clientPacket)

        val prevLum = luminosity

        luminosity = tag.getInt("Luminosity")


        if (luminosity != prevLum && hasLevel())
            level?.chunkSource?.lightEngine?.checkBlock(worldPosition)
    }

    override fun write(tag: CompoundTag, clientPacket: Boolean) {
        tag.putInt("Luminosity", luminosity)
        super.write(tag, clientPacket)

        if (!clientPacket) return
        if (queuedSync)
            tag.putBoolean("LazySync", true)
    }
}
