package io.github.cotrin8672.cel.util

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack


fun Inventory.consumeItem(count: Int, predicate: (ItemStack) -> Boolean): Boolean {
    val matches = items
        .withIndex()
        .filter { predicate(it.value) }
        .map { it.index to it.value }
    val numItems = matches.sumOf { it.second.count }
    if (numItems < count) return false

    var remaining = count
    for ((slot, stack) in matches) {
        val take = minOf(stack.count, remaining)
        stack.shrink(take)
        if (stack.isEmpty) items[slot] = ItemStack.EMPTY
        remaining -= take
        if (remaining <= 0) break
    }
    return true
}