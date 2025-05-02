package io.github.cotrin8672.cel.content.item

import com.simibubi.create.AllKeys
import io.github.cotrin8672.cel.registry.CelItems
import io.github.cotrin8672.cel.util.CelLang
import io.github.cotrin8672.cel.util.StorageFrequency
import io.github.cotrin8672.cel.util.storageFrequency
import net.minecraft.ChatFormatting
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraftforge.network.NetworkHooks

class ScopeFilterItem(properties: Properties) : Item(properties), MenuProvider {
    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player ?: return InteractionResult.PASS
        return use(context.level, player, context.hand).result
    }

    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val heldItem = player.getItemInHand(hand)
        val oldStorageFrequency = heldItem.storageFrequency
        val storageFrequency =
            StorageFrequency.of(oldStorageFrequency.stack, player.gameProfile)
        heldItem.storageFrequency = storageFrequency

        if (!player.isShiftKeyDown && hand == InteractionHand.MAIN_HAND) {
            if (!world.isClientSide && player is ServerPlayer)
                NetworkHooks.openScreen(player, this) { buf: FriendlyByteBuf ->
                    buf.writeItem(heldItem)
                }
            return InteractionResultHolder.success<ItemStack?>(heldItem)
        }
        return InteractionResultHolder.pass<ItemStack?>(heldItem)
    }

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag,
    ) {
        if (AllKeys.shiftDown()) return

        val storageFrequency = stack.storageFrequency
        val frequencyOwner = storageFrequency.gameProfile
        val frequencyOwnerTip = CelLang.translate("gui.goggles.frequency_scope")
            .add(
                if (frequencyOwner == StorageFrequency.GLOBAL_PROFILE)
                    CelLang.translate("gui.goggles.scope_global").component()
                else
                    Component.literal(frequencyOwner.name)
            )
            .style(ChatFormatting.YELLOW)
            .component()
        tooltipComponents.add(Component.empty())
        tooltipComponents.add(frequencyOwnerTip)
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu? {
        return ScopeFilterMenu.create(containerId, playerInventory, player.mainHandItem)
    }

    override fun getDisplayName(): Component {
        return CelLang.itemName(CelItems.SCOPE_FILTER.asStack()).component()
    }
}