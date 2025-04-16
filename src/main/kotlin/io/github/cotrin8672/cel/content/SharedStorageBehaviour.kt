package io.github.cotrin8672.cel.content

import com.simibubi.create.AllBlocks
import com.simibubi.create.AllItems
import com.simibubi.create.AllSoundEvents
import com.simibubi.create.content.logistics.filter.FilterItem
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.*
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour.ValueSettings
import com.simibubi.create.foundation.utility.CreateLang
import com.simibubi.create.infrastructure.config.AllConfigs
import io.github.cotrin8672.cel.util.CelLang
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import kotlin.math.max

open class SharedStorageBehaviour(
    be: SmartBlockEntity,
    private val slotPositioning: ValueBoxTransform,
) : BlockEntityBehaviour(be), ValueSettingsBehaviour {
    companion object {
        val TYPE = BehaviourType<SharedStorageBehaviour>()
    }

    private var frequencyItem: Frequency = Frequency.EMPTY

    override fun getType(): BehaviourType<*> {
        return TYPE
    }

    override fun write(nbt: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        nbt.put("Frequency", getFrequencyItem().stack.saveOptional(registries))

        super.write(nbt, registries, clientPacket)
    }

    override fun read(nbt: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        frequencyItem = Frequency.of(ItemStack.parseOptional(registries, nbt.getCompound("Frequency")))
        super.read(nbt, registries, clientPacket)
    }

    override fun testHit(hit: Vec3): Boolean {
        val state = blockEntity.blockState
        val localHit = hit.subtract(Vec3.atLowerCornerOf(blockEntity.blockPos))
        return slotPositioning.testHit(world, pos, state, localHit)
    }

    override fun isActive(): Boolean {
        return true
    }

    override fun getSlotPositioning(): ValueBoxTransform {
        return slotPositioning
    }

    fun getFrequencyItem(): Frequency = frequencyItem

    override fun createBoard(player: Player?, hitResult: BlockHitResult?): ValueSettingsBoard {
        val frequency: ItemStack = getFrequencyItem().stack
        val maxAmount =
            if (frequency.item is FilterItem) 64 else frequency.getOrDefault(DataComponents.MAX_STACK_SIZE, 64)
        return ValueSettingsBoard(
            CreateLang.translateDirect("logistics.filter.extracted_amount"),
            maxAmount,
            16,
            CreateLang.translatedOptions("logistics.filter", "up_to", "exactly"),
            ValueSettingsFormatter(this::formatValue)
        )
    }

    private fun formatValue(value: ValueSettings): MutableComponent? {
        if (value.row() == 0 && value.value() == getFrequencyItem().stack.getOrDefault(
                DataComponents.MAX_STACK_SIZE,
                64
            )
        )
            return CreateLang.translateDirect("logistics.filter.any_amount_short")
        return Component.literal((if (value.row() == 0) "\u2264" else "=") + max(1.0, value.value().toDouble()))
    }

    override fun acceptsValueSettings(): Boolean {
        return false
    }

    open fun getRenderDistance(): Float {
        return AllConfigs.client().filterItemRenderDistance.f
    }

    open fun setFilter(face: Direction?, stack: ItemStack): Boolean {
        return setFilter(stack)
    }

    open fun setFilter(stack: ItemStack): Boolean {
        val filter = stack.copy()
        this.frequencyItem = Frequency.of(filter)
        blockEntity.setChanged()
        blockEntity.sendData()
        return true
    }

    open fun canShortInteract(toApply: ItemStack): Boolean {
        if (AllItems.WRENCH.isIn(toApply)) return false
        if (AllBlocks.MECHANICAL_ARM.isIn(toApply)) return false
        return true
    }

    override fun onShortInteract(player: Player, hand: InteractionHand, side: Direction?, hitResult: BlockHitResult?) {
        val level = world
        val pos = pos
        val itemInHand = player.getItemInHand(hand)
        val toApply = itemInHand.copy()

        if (!canShortInteract(toApply)) return
        if (level.isClientSide()) return

        if (!setFilter(side, toApply)) {
            AllSoundEvents.DENY.playOnServer(player.level(), player.blockPosition(), 1f, 1f)
        }

        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, .25f, .1f)
    }

    open fun getLabel(): MutableComponent {
        return CelLang.translate("tooltip.frequency.frequency").component()
    }

    open fun getTip(): MutableComponent {
        return CelLang.translate(if (frequencyItem.stack.isEmpty) "tooltip.frequency.click_to_set" else "tooltip.frequency.click_to_replace")
            .component()
    }

    override fun setValueSettings(
        player: Player?,
        valueSetting: ValueSettings?,
        ctrlDown: Boolean,
    ) {
    }

    override fun getValueSettings(): ValueSettings {
        return ValueSettings(0, 0)
    }
}
