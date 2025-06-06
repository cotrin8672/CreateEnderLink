package io.github.cotrin8672.cel.content.storage

import io.github.cotrin8672.cel.network.SyncSharedStoragePacket
import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.createmod.catnip.animation.LerpedFloat
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import net.neoforged.neoforge.network.PacketDistributor

class SharedFluidTank(capacity: Int, private val handler: SharedStorageHandler?) : FluidTank(capacity) {
    var fluidLevel: LerpedFloat? = null
    var forceFluidLevelUpdate = true
    var serverLevel: ServerLevel? = null

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

        val level = serverLevel
        if (level != null) {
            val nbt = SharedStorageHandler.instance?.save(CompoundTag(), level.registryAccess()) ?: return
            PacketDistributor.sendToAllPlayers(SyncSharedStoragePacket(nbt))
        }

        if (fluidLevel == null) {
            val fillState = getFillState()
            fluidLevel = LerpedFloat.linear().startWithValue(fillState.toDouble())
        }
        fluidLevel?.chase(getFillState().toDouble(), .5, LerpedFloat.Chaser.EXP)
    }
}
