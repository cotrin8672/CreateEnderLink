package io.github.cotrin8672.cel.content.storage

import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank

class SharedFluidTank(capacity: Int, private val handler: SharedStorageHandler) : FluidTank(capacity) {
    override fun onContentsChanged() {
        super.onContentsChanged()
        handler.setDirty()
    }
}
