package io.github.cotrin8672.cel.content.item

import com.simibubi.create.foundation.gui.menu.GhostItemMenu
import io.github.cotrin8672.cel.registry.CelMenuTypes
import io.github.cotrin8672.cel.util.storageFrequency
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.ItemStackHandler
import net.minecraftforge.items.SlotItemHandler

class ScopeFilterMenu : GhostItemMenu<ItemStack> {
    constructor(
        type: MenuType<*>,
        id: Int,
        inv: Inventory,
        extraData: FriendlyByteBuf?,
    ) : super(type, id, inv, extraData)

    constructor(
        type: MenuType<*>,
        id: Int,
        inv: Inventory,
        contentHolder: ItemStack,
    ) : super(type, id, inv, contentHolder)

    companion object {
        fun create(id: Int, inv: Inventory, stack: ItemStack): ScopeFilterMenu {
            return ScopeFilterMenu(CelMenuTypes.SCOPE_FILTER.get(), id, inv, stack)
        }
    }

    private fun getPlayerInventoryXOffset(): Int {
        return 27
    }

    private fun getPlayerInventoryYOffset(): Int {
        return 107
    }

    override fun createOnClient(extraData: FriendlyByteBuf): ItemStack {
        return extraData.readItem()
    }

    override fun addSlots() {
        addPlayerSlots(getPlayerInventoryXOffset(), getPlayerInventoryYOffset())
        addSlot(SlotItemHandler(ghostInventory, 0, 23, 27))
    }

    override fun createGhostInventory(): ItemStackHandler {
        return ItemStackHandler(1).apply {
            val storageFrequency = contentHolder.storageFrequency
            val frequencyItem = storageFrequency.stack
            setStackInSlot(0, frequencyItem)
        }
    }

    override fun allowRepeats(): Boolean {
        return false
    }

    override fun saveData(contentHolder: ItemStack) {
        val storageFrequency = contentHolder.storageFrequency.copy(
            stack = ghostInventory.getStackInSlot(0)
        )

        contentHolder.storageFrequency = storageFrequency
    }
}
