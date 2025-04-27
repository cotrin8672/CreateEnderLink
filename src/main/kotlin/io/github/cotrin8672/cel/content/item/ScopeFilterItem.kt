package io.github.cotrin8672.cel.content.item

import com.simibubi.create.AllKeys
import io.github.cotrin8672.cel.registry.CelDataComponents
import io.github.cotrin8672.cel.registry.CelItems
import io.github.cotrin8672.cel.util.CelLang
import net.minecraft.ChatFormatting
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

class ScopeFilterItem(properties: Properties) : Item(properties), MenuProvider {
    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player ?: return InteractionResult.PASS
        return use(context.level, player, context.hand).result
    }

    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val heldItem = player.getItemInHand(hand)
        heldItem.set(CelDataComponents.FREQUENCY_OWNER_UUID, player.stringUUID)
        heldItem.set(CelDataComponents.FREQUENCY_OWNER_NAME, player.name.string)
        if (!world.isClientSide && player is ServerPlayer)
            player.openMenu(this) { buf ->
                ItemStack.STREAM_CODEC.encode(buf, heldItem)
            }
        return InteractionResultHolder.success(heldItem)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag,
    ) {
        if (AllKeys.shiftDown()) return

        val frequencyOwnerName = stack.get(CelDataComponents.FREQUENCY_OWNER_NAME)
        val frequencyOwnerTip = CelLang.translate("gui.goggles.frequency_scope")
            .add(
                if (frequencyOwnerName == null)
                    CelLang.translate("gui.goggles.scope_global").component()
                else
                    Component.literal(frequencyOwnerName)
            )
            .style(ChatFormatting.YELLOW)
            .component()
        tooltipComponents.add(Component.empty())
        tooltipComponents.add(frequencyOwnerTip)
    }

    override fun createMenu(id: Int, inv: Inventory, player: Player): AbstractContainerMenu {
        return ScopeFilterMenu.create(id, inv, player.mainHandItem)
    }

    override fun getDisplayName(): Component {
        return CelLang.itemName(CelItems.SCOPE_FILTER.asStack()).component()
    }
}
