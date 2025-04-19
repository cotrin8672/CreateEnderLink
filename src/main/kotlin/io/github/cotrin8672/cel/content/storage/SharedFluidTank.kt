package io.github.cotrin8672.cel.content.storage

import io.github.cotrin8672.cel.content.block.tank.EnderTankBlockEntity
import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.createmod.catnip.animation.LerpedFloat
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.templates.FluidTank

class SharedFluidTank(capacity: Int, private val handler: SharedStorageHandler?) : FluidTank(capacity) {
    var fluidLevel: LerpedFloat? = null
    var forceFluidLevelUpdate = true

    override fun onContentsChanged() {
        super.onContentsChanged()
        handler?.setDirty()
        onFluidStackChanged(getFluid())
    }

    override fun setFluid(stack: FluidStack) {
        super.setFluid(stack)
        handler?.setDirty()
        onFluidStackChanged(stack)
    }

    override fun readFromNBT(lookupProvider: HolderLookup.Provider, nbt: CompoundTag): FluidTank {
        val tank = super.readFromNBT(lookupProvider, nbt)

        val fillState = getFillState().toDouble()
        if (nbt.contains("ForceFluidLevel") || fluidLevel == null)
            fluidLevel = LerpedFloat.linear().startWithValue(fillState)
        fluidLevel?.chase(fillState, 0.5, LerpedFloat.Chaser.EXP)
        return tank
    }

    override fun writeToNBT(lookupProvider: HolderLookup.Provider, nbt: CompoundTag): CompoundTag {
        val tag = super.writeToNBT(lookupProvider, nbt)
        if (forceFluidLevelUpdate)
            tag.putBoolean("ForceFluidLevel", true)
        forceFluidLevelUpdate = false

        return tag
    }

    private fun getFillState(): Float {
        return fluidAmount.toFloat() / capacity.toFloat()
    }

    private fun onFluidStackChanged(newFluidStack: FluidStack) {
        val attributes = newFluidStack.fluid.fluidType
        val luminosity = (attributes.getLightLevel(newFluidStack) / 1.2f).toInt()
        val reversed = attributes.isLighterThanAir
        val maxY = ((getFillState() * 1) + 1).toInt()

        for (be in EnderTankBlockEntity.getLoadingBlockEntities()) {
            if (!be.hasLevel()) continue

            val isBright = if (reversed) (1 <= maxY) else (0 < maxY)
            val actualLuminosity = if (isBright) luminosity else if (luminosity > 0) 1 else 0

            val pos = be.blockPos
            be.level?.updateNeighbourForOutputSignal(pos, be.blockState.block)
            if (luminosity == actualLuminosity) continue
            be.setLuminosity(actualLuminosity)

            if (!be.level!!.isClientSide) {
                be.setChanged()
                be.sendData()
            }
        }

        if (fluidLevel == null) {
            val fillState = getFillState()
            fluidLevel = LerpedFloat.linear().startWithValue(fillState.toDouble())
        }
        fluidLevel?.chase(getFillState().toDouble(), .5, LerpedFloat.Chaser.EXP)
    }
}
