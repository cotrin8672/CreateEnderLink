package io.github.cotrin8672.cel.util

import com.mojang.blaze3d.vertex.PoseStack

fun <T> PoseStack.use(block: PoseStack.() -> T): T {
    this.pushPose()
    val result = block(this)
    this.popPose()
    return result
}