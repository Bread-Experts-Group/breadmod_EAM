package org.bread_experts_group.breadmod

import org.bread_experts_group.breadmod.camera.CameraTexture
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.com.mojang.blaze3d.vertex.PoseStack
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.Minecraft
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.ItemBlockRenderTypes
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.MultiBufferSource
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.RenderType
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.entity.ItemRenderer
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.resources.model.BakedModel
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.resources.model.ModelResourceLocation
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.core.registries.BuiltInRegistries
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.resources.ResourceLocation
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.org.joml.Vector3f

class TestBlockEntityRenderer(
	context: BlockEntityRendererProvider.Context
) : BlockEntityRenderer<TestBlockEntity>(context, TestBlockEntity::class.java) {
	private val mainModel: BakedModel = Minecraft.getInstance().getModelManager().getModel(
		ModelResourceLocation("breadmod:item/tool_gun/item")
	)
	private val coilModel: BakedModel = Minecraft.getInstance().getModelManager().getModel(
		ModelResourceLocation("breadmod:item/tool_gun/coil")
	)

	override fun render(
		blockEntity: TestBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val texture = CameraTexture.get(blockEntity, 400, 400)
		if (!texture.initialized) texture.init()
//		println(texture)
//		println(texture.location)
		val itemRenderer = this.context.getItemRenderer()
		val noOverlay = 0 or 10 shl 16
		val fullBright = 15728880

//		println(blockEntity.safeCast(BlockEntity::class.java))

		val buffer = bufferSource.getBuffer(RenderType.text(texture.location))
		val pose = poseStack.last().pose()
		val topLeft = pose.transformPosition(0f, 0f, 0f, Vector3f())
		val bottomLeft = pose.transformPosition(0f, -1f, 0f, Vector3f())
		val bottomRight = pose.transformPosition(1f, -1f, 0f, Vector3f())
		val topRight = pose.transformPosition(1f, 0f, 0f, Vector3f())

		buffer.addVertex(topLeft.x(), topRight.y(), topLeft.z()).setColor(Color.WHITE).setLight(fullBright).setUv(0f, 0f)
		buffer.addVertex(bottomLeft.x(), bottomLeft.y(), bottomLeft.z()).setColor(Color.WHITE).setLight(fullBright).setUv(0f, 1f)
		buffer.addVertex(bottomRight.x(), bottomRight.y(), bottomRight.z()).setColor(Color.WHITE).setLight(fullBright).setUv(1f, 1f)
		buffer.addVertex(topRight.x(), topRight.y(), topRight.z()).setColor(Color.WHITE).setLight(fullBright).setUv(1f, 0f)

		val stack = BuiltInRegistries.ITEM.get(ResourceLocation.parse("breadmod:tool_gun")).getDefaultInstance()
		val consumer = ItemRenderer.getFoilBufferDirect(
			bufferSource,
			ItemBlockRenderTypes.getRenderType(stack, false),
			true,
			stack.hasFoil()
		)

		poseStack.pushPose()
		poseStack.scale(2f, 2f, 2f)
		itemRenderer.renderModelLists(mainModel, stack, fullBright, noOverlay, poseStack, consumer)
		itemRenderer.renderModelLists(coilModel, stack, fullBright, noOverlay, poseStack, consumer)
		poseStack.popPose()
	}
}