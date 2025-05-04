package io.github.cotrin8672.cel.util

import com.mojang.blaze3d.vertex.PoseStack

fun <T> PoseStack.use(block: PoseStack.() -> T): T {
    this.pushPose()
    val result = this.block()
    this.popPose()
    return result
}