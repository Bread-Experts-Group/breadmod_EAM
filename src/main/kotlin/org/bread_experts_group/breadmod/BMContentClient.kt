package org.bread_experts_group.breadmod

import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.Minecraft
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.ShaderInstance

object BMContentClient {
	val TEST_RENDERER: BlockEntityWithoutLevelRenderer = TestRenderer(Minecraft.getInstance())

	lateinit var TEST_SHADER_INSTANCE: ShaderInstance
}