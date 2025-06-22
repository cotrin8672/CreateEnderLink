package io.github.cotrin8672.cel.network

import io.github.cotrin8672.cel.model.StorageFrequency
import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class UpdateSharedTankPacket(val storageFrequency: StorageFrequency, val fluidStack: FluidStack) {
    companion object {
        fun decode(buf: FriendlyByteBuf): UpdateSharedTankPacket {
            val tag = buf.readNbt() ?: return UpdateSharedTankPacket(StorageFrequency.EMPTY, FluidStack.EMPTY)
            val storageFrequency = StorageFrequency.parseOptional(tag.getCompound("storageFrequency"))
            val fluidStack = FluidStack.loadFluidStackFromNBT(tag.getCompound("fluidStack"))

            return UpdateSharedTankPacket(storageFrequency, fluidStack)
        }
    }

    fun encode(buf: FriendlyByteBuf) {
        val tag = CompoundTag().apply {
            put("storageFrequency", storageFrequency.saveOptional())
            put("fluidStack", fluidStack.writeToNBT(CompoundTag()))
        }

        buf.writeNbt(tag)
    }

    fun handleOnClient(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork {
            SharedStorageHandler.instance?.updateTankContentFromPacket(this)
        }

        ctx.get().packetHandled = true
    }
}
