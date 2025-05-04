package io.github.cotrin8672.cel.content.block

import io.github.cotrin8672.cel.registry.CelItems
import io.github.cotrin8672.cel.util.CelLang
import io.github.cotrin8672.cel.util.consumeItem
import io.github.cotrin8672.cel.util.storageFrequency
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block

class SharedStorageBlockItem(block: Block, properties: Properties) : BlockItem(block, properties) {
    override fun isFoil(stack: ItemStack): Boolean {
        val storageFrequency = stack.storageFrequency
        return storageFrequency.isNotEmpty
    }

    override fun place(context: BlockPlaceContext): InteractionResult {
        val player = context.player
        val heldItem = context.itemInHand
        val storageFrequency = heldItem.storageFrequency

        return if (player == null) {
            if (storageFrequency.isGlobalScope) super.place(context)
            else InteractionResult.FAIL
        } else {
            if (storageFrequency.isGlobalScope) {
                super.place(context)
            } else {
                if (player.isCreative) super.place(context)
                else if (player.inventory.consumeItem(1, CelItems.SCOPE_FILTER::isIn)) {
                    super.place(context)
                } else {
                    player.displayClientMessage(
                        CelLang
                            .translate("storage_frequency.not_holding_enough_scope_filter")
                            .style(ChatFormatting.RED)
                            .component(),
                        true
                    )
                    InteractionResult.FAIL
                }
            }
        }
    }

    override fun appendHoverText(stack: ItemStack, level: Level?, tooltip: MutableList<Component?>, flag: TooltipFlag) {
        super.appendHoverText(stack, level, tooltip, flag)

        val storageFrequency = stack.storageFrequency
        if (storageFrequency.isNotEmpty) {
            CelLang.translate("storage_frequency.is_set")
                .style(ChatFormatting.GOLD)
                .addTo(tooltip)

            CelLang.translate("storage_frequency.craft_to_reset")
                .style(ChatFormatting.GRAY)
                .addTo(tooltip)
        }
    }
}