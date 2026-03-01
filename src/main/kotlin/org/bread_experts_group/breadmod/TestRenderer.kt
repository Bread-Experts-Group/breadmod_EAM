package org.bread_experts_group.breadmod

import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.com.mojang.blaze3d.vertex.PoseStack
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.Minecraft
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.ItemBlockRenderTypes
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.MultiBufferSource
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.entity.ItemRenderer
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.resources.model.BakedModel
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.resources.model.ModelResourceLocation
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.world.item.ItemDisplayContext
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.world.item.ItemStack

class TestRenderer(minecraft: Minecraft) : BlockEntityWithoutLevelRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels()) {
	val mainModel: BakedModel = Minecraft.getInstance().getModelManager().getModel(
		ModelResourceLocation("breadmod:item/tool_gun/item")
	)
	val coilModel: BakedModel = Minecraft.getInstance().getModelManager().getModel(
		ModelResourceLocation("breadmod:item/tool_gun/coil")
	)
	val itemRenderer = Minecraft.getInstance().getItemRenderer()

	override fun renderByItem(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val consumer = ItemRenderer.getFoilBufferDirect(
			bufferSource,
			ItemBlockRenderTypes.getRenderType(stack, false),
			true,
			stack.hasFoil()
		)

		poseStack.pushPose()
		itemRenderer.renderModelLists(mainModel, stack, packedLight, packedOverlay, poseStack, consumer)
		itemRenderer.renderModelLists(coilModel, stack, packedLight, packedOverlay, poseStack, consumer)
		poseStack.popPose()
	}
}