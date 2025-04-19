package io.github.cotrin8672.cel.content.ponder

import com.simibubi.create.AllItems
import com.simibubi.create.foundation.ponder.CreateSceneBuilder
import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import io.github.cotrin8672.cel.content.block.vault.EnderVaultBlockEntity
import net.createmod.catnip.math.Pointing
import net.createmod.catnip.math.VecHelper
import net.createmod.ponder.api.PonderPalette
import net.createmod.ponder.api.scene.SceneBuilder
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3

object EnderVaultPonderScene {
    fun sharing(builder: SceneBuilder, util: SceneBuildingUtil) {
        val rightVault = util.grid().at(1, 1, 2)
        val leftVault = util.grid().at(3, 1, 2)
        val inputFunnel = util.grid().at(1, 2, 2)
        val outputFunnel = util.grid().at(3, 1, 1)

        with(CreateSceneBuilder(builder)) {
            title("ender_vault_sharing", "Sharing Items via the Ender Vault")
            configureBasePlate(0, 0, 5)

            world().showSection(util.select().layer(0), Direction.UP)
            idle(5)
            world().showSection(util.select().fromTo(rightVault, leftVault), Direction.DOWN)
            idle(20)

            overlay().showText(80)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(rightVault))
                .text("The Ender Vault can share its contents like an Ender Chest")
            idle(100)

            val rightFrequencySlot = VecHelper.getCenterOf(rightVault)
                .add(Vec3.atLowerCornerOf(Direction.NORTH.normal).scale(0.5))
            overlay().showFilterSlotInput(rightFrequencySlot, Direction.NORTH, 70)

            val leftFrequencySlot = VecHelper.getCenterOf(leftVault)
                .add(Vec3.atLowerCornerOf(Direction.NORTH.normal).scale(0.5))
            overlay().showFilterSlotInput(leftFrequencySlot, Direction.NORTH, 70)

            idle(10)

            overlay().showText(60)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(rightFrequencySlot)
                .text("The Ender Vault has a slot for setting its frequency")
            idle(80)

            val andesiteAlloy = AllItems.ANDESITE_ALLOY.asStack()

            overlay().showControls(rightFrequencySlot, Pointing.DOWN, 30).withItem(andesiteAlloy)
            overlay().showControls(leftFrequencySlot, Pointing.DOWN, 30).withItem(andesiteAlloy)

            world().modifyBlockEntity(rightVault, EnderVaultBlockEntity::class.java) {
                it.getBehaviour(SharedStorageBehaviour.TYPE).setFrequencyItem(andesiteAlloy)
            }
            world().modifyBlockEntity(leftVault, EnderVaultBlockEntity::class.java) {
                it.getBehaviour(SharedStorageBehaviour.TYPE).setFrequencyItem(andesiteAlloy)
            }

            idle(20)

            overlay().showText(60)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(rightFrequencySlot)
                .text("Placing an item in the slot determines the frequency")
            idle(80)

            world().showSection(util.select().layer(2), Direction.DOWN)
            idle(10)

            val iron = ItemStack(Items.IRON_INGOT)
            overlay().showControls(util.vector().topOf(inputFunnel), Pointing.DOWN, 20).withItem(iron)
            idle(40)
            val entity =
                world().createItemEntity(util.vector().topOf(inputFunnel), util.vector().of(0.0, 0.2, 0.0), iron)
            idle(10)
            world().modifyEntity(entity, Entity::discard)

            idle(20)

            world().showSection(util.select().position(outputFunnel), Direction.SOUTH)
            idle(10)
            world().createItemEntity(
                util.vector().topOf(outputFunnel.below()).add(0.0, 0.0, -0.2),
                util.vector().of(0.0, 0.0, 0.0),
                iron
            )
            world().flapFunnel(outputFunnel, true)

            idle(20)

            overlay().showText(60)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(rightVault))
                .text("Ender Vaults with the same frequency share their contents")
            idle(80)

            overlay().showControls(util.vector().centerOf(rightVault), Pointing.DOWN, 50)
                .withItem(AllItems.GOGGLES.asStack())
            idle(7)
            overlay().showText(50)
                .text("When wearing Engineers' Goggles, the player can see how many Ender Vaults are currently linked")
                .attachKeyFrame()
                .colored(PonderPalette.MEDIUM)
                .pointAt(util.vector().centerOf(rightVault))
                .placeNearTarget()

            idle(70)
        }
    }

    fun contraption(builder: SceneBuilder, util: SceneBuildingUtil) {
        val rightVault = util.grid().at(0, 2, 3)
        val funnel = util.grid().at(0, 2, 2)
        val base = util.grid().at(0, 1, 3)
        val cobbleStone = util.grid().at(2, 1, 1)
        val drill = util.grid().at(2, 1, 3)
        val leftVault = util.grid().at(2, 2, 4)
        val largeCogwheel = util.grid().at(3, 0, 5)
        val underCogwheel = util.grid().at(4, 1, 5)
        val topCogwheel = util.grid().at(4, 2, 5)
        val gantryShaftSection = util.select().fromTo(4, 2, 4, 4, 2, 2)
        val gantryCarriage = util.grid().at(3, 2, 4)

        with(CreateSceneBuilder(builder)) {
            title("ender_vault_contraption", "Using Ender Vaults in Contraptions")
            configureBasePlate(0, 0, 5)

            world().setKineticSpeed(util.select().position(largeCogwheel), 8f)
            world().setKineticSpeed(util.select().position(underCogwheel), -16f)
            world().setKineticSpeed(util.select().position(topCogwheel), 16f)
            world().setKineticSpeed(gantryShaftSection, 16f)

            world().showSection(util.select().layer(0), Direction.UP)
            idle(5)
            world().showSection(util.select().fromTo(underCogwheel, topCogwheel), Direction.DOWN)
            world().showSection(gantryShaftSection, Direction.DOWN)
            world().showSection(util.select().position(base), Direction.DOWN)
            idle(10)
            val carriageSection =
                world().showIndependentSection(util.select().fromTo(drill, gantryCarriage), Direction.EAST)
            idle(10)
            world().showSection(util.select().position(rightVault), Direction.DOWN)

            idle(20)

            overlay().showText(60)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(leftVault))
                .text("Ender Vaults can be integrated into contraptions")
            idle(80)

            world().showSection(util.select().position(funnel), Direction.SOUTH)
            idle(10)
            world().showSection(util.select().position(cobbleStone), Direction.DOWN)
            idle(20)

            world().setKineticSpeed(util.select().position(largeCogwheel), -8f)
            world().setKineticSpeed(util.select().position(underCogwheel), 16f)
            world().setKineticSpeed(util.select().position(topCogwheel), -16f)
            world().setKineticSpeed(gantryShaftSection, -16f)

            world().setKineticSpeed(util.select().position(drill), 16f)
            world().moveSection(carriageSection, util.vector().of(0.0, 0.0, -1.0), 40)

            idle(40)

            for (i in 0..9) {
                idle(5)
                world().incrementBlockBreakingProgress(cobbleStone)
            }

            world().createItemEntity(
                util.vector().topOf(funnel.below()).add(0.0, 0.0, -0.2),
                util.vector().of(0.0, 0.0, 0.0),
                ItemStack(Items.COBBLESTONE)
            )
            world().flapFunnel(funnel, true)

            world().moveSection(carriageSection, util.vector().of(0.0, 0.0, -1.0), 40)

            idle(20)

            overlay().showText(60)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(rightVault))
                .text("Vaults in a contraption also share contents with other vaults on the same frequency")
            idle(20)

            world().setKineticSpeed(util.select().position(drill), 0f)

            idle(60)
        }
    }
}
