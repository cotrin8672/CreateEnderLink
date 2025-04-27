package io.github.cotrin8672.cel.util

import net.neoforged.neoforge.items.ItemStackHandler

val ItemStackHandler.isEmpty: Boolean
    get() {
        return (0 until this.slots).all { this.getStackInSlot(it).isEmpty }
    }
