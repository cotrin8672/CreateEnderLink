package io.github.cotrin8672.cel.client

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.AllBlocks
import com.simibubi.create.AllSpecialTextures
import com.simibubi.create.CreateClient
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox.ItemValueBox
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxRenderer
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform.Sided
import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import io.github.cotrin8672.cel.registry.CelItems
import io.github.cotrin8672.cel.util.StorageFrequency
import net.createmod.catnip.data.Iterate
import net.createmod.catnip.data.Pair
import net.createmod.catnip.math.VecHelper
import net.createmod.catnip.outliner.Outliner
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.forge.use

object FrequencyRenderer {
    private val scopeFilter = CelItems.SCOPE_FILTER.asStack()

    fun tick() {
        val mc = Minecraft.getInstance()
        val target = mc.hitResult
        if (target == null || target !is BlockHitResult) return

        val level = mc.level ?: return
        val pos = target.blockPos
        val state = level.getBlockState(pos)
        val blockEntity = level.getBlockEntity(pos)
        val player = mc.player ?: return

        if (player.isShiftKeyDown) return
        if (blockEntity !is SmartBlockEntity) return

        for (behaviour in blockEntity.allBehaviours) {
            if (behaviour !is SharedStorageBehaviour) continue

            if (behaviour.slotPositioning is Sided)
                (behaviour.slotPositioning as Sided).fromSide(target.direction)
            if (!behaviour.slotPositioning.shouldRender(level, pos, state)) continue

            val frequencyItem = behaviour.getFrequency().stack
            val hit = behaviour.slotPositioning.testHit(
                level,
                pos,
                state,
                target.location.subtract(Vec3.atLowerCornerOf(pos))
            )

            val emptyBB = AABB(Vec3.ZERO, Vec3.ZERO)
            val bb = emptyBB.inflate(0.25)
            val box = ItemValueBox(
                behaviour.getLabel(),
                bb,
                pos,
                frequencyItem,
                Component.empty()
            ).apply { passive(!hit) }

            Outliner.getInstance()
                .showOutline(Pair.of("frequency1", pos), box.transform(behaviour.slotPositioning))
                .lineWidth(1 / 64f)
                .withFaceTexture(if (hit) AllSpecialTextures.THIN_CHECKERED else null)
                .highlightFace(target.direction)

            if (!hit) continue

            val tip = ArrayList<MutableComponent>().apply {
                add(behaviour.getLabel().copy())
                add(behaviour.getTip().copy())
            }
            CreateClient.VALUE_SETTINGS_HANDLER.showHoverTip(tip)
        }
    }

    fun renderOnBlockEntity(
        be: SmartBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        if (be.isRemoved) return

        val level = be.level ?: return
        val blockPos = be.blockPos

        for (behaviour in be.allBehaviours) {
            if (behaviour !is SharedStorageBehaviour) continue
            if (!be.isVirtual) {
                val cameraEntity = Minecraft.getInstance().cameraEntity
                if (cameraEntity != null && level == cameraEntity.level()) {
                    val max = behaviour.getRenderDistance()
                    if (cameraEntity.position().distanceToSqr(VecHelper.getCenterOf(blockPos)) > (max * max)) {
                        continue
                    }
                }
            }

            if (!behaviour.isActive) continue
            if (behaviour.getFrequency().stack.isEmpty && behaviour.getFrequency().isGlobalScope) continue

            val slotPositioning = behaviour.slotPositioning
            val blockState = be.blockState

            if (slotPositioning is Sided) {
                val side = slotPositioning.side
                for (direction in Iterate.directions) {
                    val frequency = behaviour.getFrequency()
                    if (frequency.stack.isEmpty && behaviour.getFrequency().isGlobalScope) continue

                    slotPositioning.fromSide(direction)
                    if (!slotPositioning.shouldRender(level, blockPos, blockState)) continue

                    ms.use {
                        slotPositioning.transform(level, blockPos, blockState, ms)
                        if (AllBlocks.CONTRAPTION_CONTROLS.has(blockState))
                            ValueBoxRenderer.renderFlatItemIntoValueBox(frequency.stack, ms, buffer, light, overlay)
                        else
                            ms.renderFrequencyItem(frequency, buffer, light, overlay)
                    }
                }
                slotPositioning.fromSide(side)
                continue
            } else if (slotPositioning.shouldRender(level, blockPos, blockState)) {
                ms.use {
                    slotPositioning.transform(level, blockPos, blockState, ms)
                    ms.renderFrequencyItem(behaviour.getFrequency(), buffer, light, overlay)
                }
            }
        }
    }

    private fun PoseStack.renderFrequencyItem(
        frequency: StorageFrequency,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        val frequencyItem = frequency.stack

        if (frequency.isPersonalScope) {
            val mc = Minecraft.getInstance()
            val itemRenderer = mc.itemRenderer
            val modelWithOverrides = itemRenderer.getModel(frequencyItem, null, null, 0)
            val blockItem = modelWithOverrides.isGui3d
            val factor = if (blockItem) 1.25f else 1f
            val scale = 1.75f * factor

            this.use {
                this.scale(scale, scale, 1f)
                ValueBoxRenderer.renderItemIntoValueBox(scopeFilter, this, buffer, light, overlay)
            }
            this.use {
                translate(0f, 0f, -0.01f)
                ValueBoxRenderer.renderItemIntoValueBox(frequencyItem, this, buffer, light, overlay)
            }
        } else {
            this.use {
                ValueBoxRenderer.renderItemIntoValueBox(frequencyItem, this, buffer, light, overlay)
            }
        }
    }}
