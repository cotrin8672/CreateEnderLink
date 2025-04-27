package io.github.cotrin8672.cel.content.item

import com.simibubi.create.foundation.gui.menu.GhostItemMenu
import io.github.cotrin8672.cel.registry.CelDataComponents
import io.github.cotrin8672.cel.registry.CelMenuTypes
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemContainerContents
import net.neoforged.neoforge.items.ItemStackHandler
import net.neoforged.neoforge.items.SlotItemHandler

class ScopeFilterMenu : GhostItemMenu<ItemStack> {
    constructor(
        type: MenuType<*>,
        id: Int,
        inv: Inventory,
        extraData: RegistryFriendlyByteBuf?,
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

    override fun createOnClient(extraData: RegistryFriendlyByteBuf): ItemStack {
        return ItemStack.STREAM_CODEC.decode(extraData)
    }

    override fun addSlots() {
        addPlayerSlots(getPlayerInventoryXOffset(), getPlayerInventoryYOffset())
        addSlot(SlotItemHandler(ghostInventory, 0, 23, 27))
    }

    override fun createGhostInventory(): ItemStackHandler {
        return ItemStackHandler(1).apply {
            val frequencyItemContainer = contentHolder.get(CelDataComponents.FREQUENCY_ITEM) ?: return@apply
            if (frequencyItemContainer.slots == 0) return@apply
            val frequencyItem = frequencyItemContainer.getStackInSlot(0) ?: ItemStack.EMPTY
            setStackInSlot(0, frequencyItem)
        }
    }

    override fun allowRepeats(): Boolean {
        return false
    }

    override fun saveData(contentHolder: ItemStack) {
        contentHolder.set(
            CelDataComponents.FREQUENCY_ITEM,
            ItemContainerContents.fromItems(listOf(ghostInventory.getStackInSlot(0)))
        )
    }
}
