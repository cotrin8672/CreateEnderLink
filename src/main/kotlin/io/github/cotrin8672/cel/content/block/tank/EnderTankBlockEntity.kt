package io.github.cotrin8672.cel.content.block.tank

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform
import io.github.cotrin8672.cel.content.SharedItemStorageBehaviour
import io.github.cotrin8672.cel.content.storage.SharedFluidTank
import io.github.cotrin8672.cel.registry.CelBlockEntityTypes
import io.github.cotrin8672.cel.util.CelLang
import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.createmod.catnip.animation.LerpedFloat
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.fluids.FluidStack
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

        private val blockEntities: MutableSet<EnderTankBlockEntity> = Collections.newSetFromMap(WeakHashMap())
    }

    init {
        val isAlreadyExists = blockEntities.map { it.blockPos }.contains(this.blockPos)
        if (!isAlreadyExists) blockEntities.add(this)
    }

    private var fluidLevel: LerpedFloat? = null
    private var luminosity = 0

    fun getFluidTank(): SharedFluidTank? {
        val behaviour = getBehaviour(SharedItemStorageBehaviour.TYPE) ?: return null
        val nonNullLevel = level ?: return null
        if (nonNullLevel is ServerLevel) {
            val fluidTank = SharedStorageHandler.instance?.getOrCreateSharedFluidStorage(behaviour.getFrequencyItem())
            return fluidTank
        }
        return null
    }

    override fun addBehaviours(behaviours: MutableList<BlockEntityBehaviour>) {
        behaviours.add(SharedItemStorageBehaviour(this, CenteredSideValueBoxTransform { _, direction ->
            direction.axis == Direction.Axis.Y
        }))
    }

    fun getFillState(): Float {
        return (getFluidTank()?.fluidAmount?.toFloat() ?: 0f) / (getFluidTank()?.capacity?.toFloat() ?: 10000f)
    }

    private fun onFluidStackChanged(newFluidStack: FluidStack) {
        if (!hasLevel()) return

        val attributes = newFluidStack.fluid.fluidType
        val luminosity = (attributes.getLightLevel(newFluidStack) / 1.2f).toInt()
        val reversed = attributes.isLighterThanAir
        val maxY = ((getFillState() * 1) + 1).toInt()

        for (yOffset in 0..<1) {
            val isBright = if (reversed) (1 <= maxY) else (0 < maxY)
            val actualLuminosity = if (isBright) luminosity else if (luminosity > 0) 1 else 0

            val pos = worldPosition.offset(0, 0, 0)
            level?.updateNeighbourForOutputSignal(pos, blockState.block)
            if (luminosity == actualLuminosity) continue
            setLuminosity(actualLuminosity)
        }

        if (!level!!.isClientSide) {
            setChanged()
            sendData()
        }

        if (isVirtual) {
            if (fluidLevel == null) fluidLevel = LerpedFloat.linear()
                .startWithValue(getFillState().toDouble())
            fluidLevel.chase(getFillState().toDouble(), .5, LerpedFloat.Chaser.EXP)
        }
    }

    protected fun setLuminosity(luminosity: Int) {
        if (level!!.isClientSide) return
        if (this.luminosity == luminosity) return
        this.luminosity = luminosity
        sendData()
    }

    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking)

        val count = blockEntities.count {
            getBehaviour(SharedItemStorageBehaviour.TYPE).getFrequencyItem().stack.item ==
                    it.getBehaviour(SharedItemStorageBehaviour.TYPE).getFrequencyItem().stack.item
        }

        CelLang.translate("gui.goggles.storage_stat").forGoggles(tooltip)

        CelLang.translate("gui.goggles.same_frequency_count")
            .style(ChatFormatting.GRAY)
            .forGoggles(tooltip)

        CelLang.number(count.toDouble())
            .space()
            .translate(if (count > 1.0) "gui.goggles.block.plural" else "gui.goggles.block.singular")
            .style(ChatFormatting.AQUA)
            .space()
            .add(CelLang.translate("gui.goggles.at_current_loading").style(ChatFormatting.DARK_GRAY))
            .forGoggles(tooltip, 1)

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
}
