package org.bread_experts_group.breadmod

import org.bread_experts_group.breadmod.BMContentClient.TEST_RENDERER
import org.bread_experts_group.breadmod.camera.CameraTexture
import org.bread_experts_group.breadmod.transforms.LevelRendererTransform
import org.bread_experts_group.breadmod.transforms.MinecraftTransform
import org.bread_experts_group.eam.classDesc
import org.bread_experts_group.eam.minecraft.feature.Identifier
import org.bread_experts_group.eam.minecraft.feature.MinecraftMod
import org.bread_experts_group.eam.minecraft.feature.block.MinecraftBlock
import org.bread_experts_group.eam.minecraft.feature.block.MinecraftBlockFeature
import org.bread_experts_group.eam.minecraft.feature.block.MinecraftEntityBlock
import org.bread_experts_group.eam.minecraft.feature.creative_tab.MinecraftCreativeTab
import org.bread_experts_group.eam.minecraft.feature.creative_tab.MinecraftCreativeTabFeature
import org.bread_experts_group.eam.minecraft.feature.event.EventSystem
import org.bread_experts_group.eam.minecraft.feature.item.MinecraftItem
import org.bread_experts_group.eam.minecraft.feature.item.MinecraftItemFeature
import org.bread_experts_group.eam.minecraft.feature.item.MinecraftItemProperties
import org.bread_experts_group.eam.minecraft.feature.layer.MinecraftLayer
import org.bread_experts_group.eam.minecraft.feature.layer.MinecraftLayerFeature
import org.bread_experts_group.eam.minecraft.mimic.MimickedClass
import org.bread_experts_group.eam.minecraft.transform.CodeTransformer
import org.bread_experts_group.eam.minecraft.transform.ModTransformHolder
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.NativeConstantsV1x21x1
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.NativeConstantsV1x21x1.net_minecraft_client_Minecraft
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.NativeConstantsV1x21x1.net_minecraft_client_renderer_LevelRenderer
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.com.mojang.blaze3d.pipeline.RenderTarget
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.com.mojang.blaze3d.vertex.DefaultVertexFormat
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.com.mojang.blaze3d.vertex.PoseStack
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.Camera
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.DeltaTracker
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.Minecraft
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.gui.GuiGraphics
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.GameRenderer
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.LevelRenderer
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.LightTexture
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.MultiBufferSource
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.ShaderInstance
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.sounds.SoundEvents
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.sounds.SoundSource
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.core.BlockPos
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.core.Registry
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.core.registries.BuiltInRegistries
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.resources.ResourceLocation
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.server.packs.resources.ResourceProvider
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.world.InteractionResult
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.world.entity.Entity
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.world.entity.LivingEntity
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.world.item.ItemDisplayContext
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.world.item.ItemStack
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.world.item.context.UseOnContext
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.world.level.Level
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.world.level.block.entity.BlockEntity
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.world.level.block.entity.BlockEntityType.Builder
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.world.level.block.state.BlockState

// todo cleanup really dirty breadmod test code transferring from the EAM repo to prepare for loading mods from their own jar files
class BreadMod : MinecraftMod() {
	init {
		println("BreadMod Test Loading ...")
		println("*** Conflict resolution ID: ${BreadMod::class.java.modID}")
		println("*** ClassLoader: ${BreadMod::class.java.classLoader}")
	}

	override fun addBlocks(blocks: MinecraftBlockFeature) {
		blocks.add(
			Identifier(BreadMod::class.java.modID, "bread_block"),
			object : MinecraftBlock(), MinecraftEntityBlock {
				// todo make abstract in the future
				override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
					TestBlockEntity(pos, state)
			}
		)
	}

	override fun addItems(items: MinecraftItemFeature) {
		items.add(
			Identifier(
				BreadMod::class.java.modID,
				"bread_2"
			),
			MinecraftItem(MinecraftItemProperties())
		)
		items.add(
			Identifier(
				BreadMod::class.java.modID,
				"bread_3"
			), object : MinecraftItem(MinecraftItemProperties()) {
				override fun useOn(context: UseOnContext): InteractionResult {
					println("useOn test??")
					return InteractionResult.CONSUME
				}

				override fun inventoryTick(
					stack: ItemStack,
					level: Level,
					entity: Entity,
					slot: Int,
					beingHeld: Boolean
				) {
				}
		})
		// todo mouse and keyboard hooks
		items.add(
			Identifier(
				BreadMod::class.java.modID,
				"tool_gun"
			),
			MinecraftItem(MinecraftItemProperties())
		)
	}

	override fun addLayers(layers: MinecraftLayerFeature) {
		layers.add(
			Identifier(
				BreadMod::class.java.modID,
				"test_layer"
			),
			object : MinecraftLayer() {
			override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
				val player = Minecraft.getInstance().player ?: return
				val font = Minecraft.getInstance().font
				guiGraphics.drawString(font, "The Breadmod", 0, 0, Color.ORANGE)
				guiGraphics.drawString(font, "${player.getX()}, ${player.getY()}, ${player.getZ()}", 0, 10, Color.WHITE)
			}
		})
	}

	override fun addCreativeTabs(tabs: MinecraftCreativeTabFeature) {
		tabs.add(
			Identifier(
				BreadMod::class.java.modID,
				BreadMod::class.java.modID
			),
			MinecraftCreativeTab()
		)
	}

	override fun registerEvents() {
		EventSystem.addListener(EventSystem.MOUSE_BUTTON_PRE) { event, button, action, modifiers ->
		}

		EventSystem.addListener(EventSystem.MOUSE_BUTTON_POST) { event, button, action, modifiers ->
		}

		EventSystem.addListener(EventSystem.MOUSE_SCROLLED) { event, mouseHandler, scrollX, scrollY ->
			val minecraft = Minecraft.getInstance()
			val player = minecraft.player ?: return@addListener
			val level = minecraft.level ?: return@addListener
			val item = BuiltInRegistries.ITEM.get(ResourceLocation.parse("breadmod:tool_gun"))
			if (player.isHolding(item) && player.isShiftKeyDown()) {
				level.playSound(
					player.getX(),
					player.getY(),
					player.getZ(),
					SoundEvents.NOTE_BLOCK_PLING.value(),
					SoundSource.AMBIENT,
					1f,
					1f,
					false,
					42L
				)
				event.setCanceled(true)
			}
		}
	}

	override fun afterCreateContents() {
		BMContent.TEST_ENTITY_TYPE = Registry.register(
			BuiltInRegistries.BLOCK_ENTITY_TYPE,
			"breadmod:test_entity",
			Builder.of(
				BlockEntitySupplier(::TestBlockEntity),
				BuiltInRegistries.BLOCK.get(ResourceLocation.parse("breadmod:bread_block"))
			).build()
		)
	}

	override fun renderBEWLRs(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		TEST_RENDERER.renderByItem(stack, displayContext, poseStack, bufferSource, packedLight, packedOverlay)
	}

	override fun postClientInit() {
		BlockEntityRenderers.register(
			BMContent.TEST_ENTITY_TYPE,
			BlockEntityRendererProvider(::TestBlockEntityRenderer)
		)
	}

	override fun registerShaders(resourceProvider: ResourceProvider, shaderList: MutableList<Any>) {
		shaderList.add(
			makeShaderPair(
				ShaderInstance(
					resourceProvider,
					"position_tex_color_no_cutout",
					DefaultVertexFormat.POSITION_TEX_COLOR
				)
			) { BMContentClient.TEST_SHADER_INSTANCE = it }
		)
	}

	override fun gatherClassTransforms(holder: ModTransformHolder) {
		holder.submit(this, NativeConstantsV1x21x1.net_minecraft_client_Minecraft, MinecraftTransform())
		holder.submit(
			this,
			NativeConstantsV1x21x1.net_minecraft_client_renderer_LevelRenderer,
			LevelRendererTransform()
		)
	}
}