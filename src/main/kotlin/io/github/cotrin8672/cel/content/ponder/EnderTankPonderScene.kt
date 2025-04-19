package io.github.cotrin8672.cel.content.ponder

import com.simibubi.create.AllItems
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity
import com.simibubi.create.foundation.ponder.CreateSceneBuilder
import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import io.github.cotrin8672.cel.content.block.tank.EnderTankBlockEntity
import net.createmod.catnip.math.Pointing
import net.createmod.ponder.api.PonderPalette
import net.createmod.ponder.api.scene.SceneBuilder
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.minecraft.core.Direction
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler

object EnderTankPonderScene {
    fun sharing(builder: SceneBuilder, util: SceneBuildingUtil) {
        val largeCogwheel = util.grid().at(1, 0, 5)
        val cogwheel1 = util.grid().at(2, 1, 5)
        val cogwheel2 = util.grid().at(2, 1, 3)
        val pump = util.grid().at(1, 1, 3)
        val tank = util.grid().at(1, 1, 4)
        val rightTank = util.grid().at(1, 1, 2)
        val leftTank = util.grid().at(3, 1, 2)

        with(CreateSceneBuilder(builder)) {
            title("ender_tank_sharing", "Sharing Fluids via the Ender Tank")
            configureBasePlate(0, 0, 5)

            world().showSection(util.select().layer(0), Direction.UP)
            idle(5)
            world().showSection(util.select().fromTo(rightTank, leftTank), Direction.DOWN)
            idle(20)

            overlay().showText(80)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(rightTank))
                .text("The Ender Tank can share its fluid contents")
            idle(100)

            overlay().showCenteredScrollInput(rightTank, Direction.UP, 70)
            overlay().showCenteredScrollInput(leftTank, Direction.UP, 70)

            idle(10)

            overlay().showText(60)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(rightTank))
                .text("The Ender Tank has a slot for setting its frequency")
            idle(80)

            val andesiteAlloy = AllItems.ANDESITE_ALLOY.asStack()

            overlay().showControls(util.vector().topOf(rightTank), Pointing.DOWN, 30).withItem(andesiteAlloy)
            overlay().showControls(util.vector().topOf(leftTank), Pointing.DOWN, 30).withItem(andesiteAlloy)

            world().modifyBlockEntity(rightTank, EnderTankBlockEntity::class.java) {
                it.getBehaviour(SharedStorageBehaviour.TYPE).setFrequencyItem(andesiteAlloy)
            }
            world().modifyBlockEntity(leftTank, EnderTankBlockEntity::class.java) {
                it.getBehaviour(SharedStorageBehaviour.TYPE).setFrequencyItem(andesiteAlloy)
            }

            idle(20)

            overlay().showText(60)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(rightTank))
                .text("Placing an item in the slot sets the frequency")
            idle(80)

            world().setKineticSpeed(util.select().position(largeCogwheel), 8f)
            world().setKineticSpeed(util.select().fromTo(cogwheel1, cogwheel2), -16f)
            world().setKineticSpeed(util.select().position(pump), 16f)

            world().modifyBlockEntity(tank, FluidTankBlockEntity::class.java) {
                it.tankInventory.fluid = FluidStack(Fluids.WATER, 6000)
            }

            world().showSection(util.select().fromTo(cogwheel1, cogwheel2), Direction.DOWN)
            world().showSection(util.select().position(pump), Direction.DOWN)
            idle(10)
            world().showSection(util.select().position(tank), Direction.NORTH)
            idle(20)


            for (i in 0 until 60) {
                idle(1)
                world().modifyBlockEntity(rightTank, EnderTankBlockEntity::class.java) {
                    it.getFluidTank()?.fill(FluidStack(Fluids.WATER, 100), IFluidHandler.FluidAction.EXECUTE)
                }
                world().modifyBlockEntity(leftTank, EnderTankBlockEntity::class.java) {
                    it.getFluidTank()?.fill(FluidStack(Fluids.WATER, 100), IFluidHandler.FluidAction.EXECUTE)
                }
                world().modifyBlockEntity(tank, FluidTankBlockEntity::class.java) {
                    it.tankInventory.drain(FluidStack(Fluids.WATER, 100), IFluidHandler.FluidAction.EXECUTE)
                }
            }

            overlay().showText(60)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(rightTank))
                .text("Ender Tanks with the same frequency share their contents")
            idle(80)

            overlay().showControls(util.vector().centerOf(rightTank), Pointing.DOWN, 50)
                .withItem(AllItems.GOGGLES.asStack())
            idle(7)
            overlay().showText(50)
                .text("When wearing Engineers' Goggles, the player can see how many Ender Tanks are currently linked")
                .attachKeyFrame()
                .colored(PonderPalette.MEDIUM)
                .pointAt(util.vector().centerOf(rightTank))
                .placeNearTarget()

            idle(70)
        }
    }
}
