package io.github.cotrin8672.cel.content.block.tank

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform
import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import io.github.cotrin8672.cel.content.storage.SharedFluidTank
import io.github.cotrin8672.cel.registry.CelBlockEntityTypes
import io.github.cotrin8672.cel.util.CelLang
import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import java.util.*

class EnderTankBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : SmartBlockEntity(type, pos, state), IHaveGoggleInformation {
    companion object {
        fun registerCapability(event: RegisterCapabilitiesEvent) {
            event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                CelBlockEntityTypes.ENDER_TANK.get()
            ) { be, _ ->
                return@registerBlockEntity be.getFluidTank()
            }
        }

        private val blockEntities: MutableSet<EnderTankBlockEntity> = Collections.newSetFromMap(WeakHashMap())

        fun getLoadingBlockEntities(): Set<EnderTankBlockEntity> = blockEntities.toSet()
    }

    init {
        val isAlreadyExists = blockEntities.map { it.blockPos }.contains(this.blockPos)
        if (!isAlreadyExists) blockEntities.add(this)
    }

    private var luminosity = 0

    private var queuedSync = false
    private var syncCooldown = 0

    fun getFluidTank(): SharedFluidTank? {
        val behaviour = getBehaviour(SharedStorageBehaviour.TYPE) ?: return null
        val fluidTank = SharedStorageHandler.instance?.getOrCreateSharedFluidStorage(behaviour.getFrequencyItem())
        return fluidTank
    }

    override fun addBehaviours(behaviours: MutableList<BlockEntityBehaviour>) {
        behaviours.add(SharedStorageBehaviour(this, CenteredSideValueBoxTransform { _, direction ->
            direction.axis == Direction.Axis.Y
        }))
    }

    fun setLuminosity(luminosity: Int) {
        if (level!!.isClientSide) return
        if (this.luminosity == luminosity) return
        this.luminosity = luminosity
        sendData()
    }

    override fun tick() {
        super.tick()

        if (syncCooldown > 0) {
            syncCooldown--
            if (syncCooldown == 0 && queuedSync) sendData()
        }
    }

    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking)

        containedFluidTooltip(
            tooltip, isPlayerSneaking,
            level?.getCapability(Capabilities.FluidHandler.BLOCK, blockPos, Direction.UP)
        )

        val count = blockEntities.count {
            getBehaviour(SharedStorageBehaviour.TYPE).getFrequencyItem().stack.item ==
                    it.getBehaviour(SharedStorageBehaviour.TYPE).getFrequencyItem().stack.item
        }

        CelLang.translate("gui.goggles.storage_stat").forGoggles(tooltip)

        CelLang.translate("gui.goggles.same_frequency_count")
            .style(ChatFormatting.GRAY)
            .forGoggles(tooltip)

        CelLang.number(count.toDouble())
            .space()
            .translate(if (count > 1.0) "gui.goggles.block.plural" else "gui.goggles.block.singular")
            .style(ChatFormatting.AQUA)
            .space()
            .add(CelLang.translate("gui.goggles.at_current_loading").style(ChatFormatting.DARK_GRAY))
            .forGoggles(tooltip, 1)

        return true
    }

    override fun destroy() {
        super.destroy()
        blockEntities.remove(this)
    }

    override fun remove() {
        super.remove()
        blockEntities.remove(this)
    }

    override fun onChunkUnloaded() {
        super.onChunkUnloaded()
        blockEntities.remove(this)
    }

    override fun sendData() {
        if (syncCooldown > 0) {
            queuedSync = true
            return
        }
        super.sendData()
        queuedSync = false
        syncCooldown = 8
    }

    override fun read(tag: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        super.read(tag, registries, clientPacket)

        val prevLum = luminosity

        luminosity = tag.getInt("Luminosity")


        if (luminosity != prevLum && hasLevel())
            level?.chunkSource?.lightEngine?.checkBlock(worldPosition)

    }

    override fun write(tag: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        tag.putInt("Luminosity", luminosity)
        super.write(tag, registries, clientPacket)

        if (!clientPacket) return
        if (queuedSync)
            tag.putBoolean("LazySync", true)
    }
}
