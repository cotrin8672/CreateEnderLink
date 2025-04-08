package io.github.cotrin8672.cel.util

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.ItemStackHandler

class SharedItemStackHandler(slots: Int, private val handler: SharedStorageHandler) : ItemStackHandler(slots) {
    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        handler.setDirty()
        super.setStackInSlot(slot, stack)
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        handler.setDirty()
        return super.insertItem(slot, stack, simulate)
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        handler.setDirty()
        return super.extractItem(slot, amount, simulate)
    }
}
