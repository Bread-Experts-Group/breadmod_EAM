package org.bread_experts_group.breadmod

import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.com.mojang.blaze3d.pipeline.RenderTarget
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.com.mojang.blaze3d.systems.RenderSystem
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.com.mojang.blaze3d.vertex.DefaultVertexFormat
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.com.mojang.blaze3d.vertex.VertexFormat
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.Util
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.RenderStateShard
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.RenderType
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.RenderType.Companion.TRANSIENT_BUFFER_SIZE
import java.util.function.Function

object BMRenderType {
	val TEST_STATE_SHARD: RenderStateShard.ShaderStateShard = RenderStateShard.ShaderStateShard(BMContentClient::TEST_SHADER_INSTANCE)

	private val RENDER_TARGET: Function<RenderTarget, RenderType> = Util.memoize { renderTarget ->
		RenderType.create(
			"render_target",
			DefaultVertexFormat.POSITION_TEX_COLOR,
			VertexFormat.Mode.QUADS,
			TRANSIENT_BUFFER_SIZE,
			true,
			false,
			RenderType.CompositeState.builder()
				.setShaderState(TEST_STATE_SHARD)
				.setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
				.setCullState(RenderStateShard.NO_CULL)
				.setTexturingState(RenderStateShard.TexturingStateShard("set_texture", {
					RenderSystem.setShaderTexture(0, renderTarget.getColorTextureId())
				}, {}))
				.createCompositeState(false)
		)
	}

	fun renderTarget(target: RenderTarget): RenderType = this.RENDER_TARGET.apply(target)
}