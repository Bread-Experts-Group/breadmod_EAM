package org.bread_experts_group.breadmod

import org.bread_experts_group.breadmod.BMContentClient.TEST_RENDERER
import org.bread_experts_group.breadmod.camera.CameraTexture
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
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.org.joml.Matrix4f
import java.awt.Color
import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassElement
import java.lang.classfile.ClassFile.ACC_PRIVATE
import java.lang.classfile.Opcode
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

// todo cleanup really dirty breadmod test code transferring from the EAM repo to prepare for loading mods from their own jar files
class BreadMod : MinecraftMod("breadmod"), CodeTransformer {
	override val existingElements: MutableList<String> = mutableListOf()

	override fun addBlocks(blocks: MinecraftBlockFeature) {
		blocks.add(
			Identifier("breadmod", "bread_block"),
			object : MinecraftBlock(), MinecraftEntityBlock {
				// todo make abstract in the future
				override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
					TestBlockEntity(pos, state)
			}
		)
	}

	override fun addItems(items: MinecraftItemFeature) {
		items.add(Identifier("breadmod", "bread_2"), MinecraftItem(MinecraftItemProperties()))
		items.add(
			Identifier(
				"breadmod",
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
		items.add(Identifier("breadmod", "tool_gun"), MinecraftItem(MinecraftItemProperties()))
	}

	override fun addLayers(layers: MinecraftLayerFeature) {
		layers.add(Identifier("breadmod", "test_layer"), object : MinecraftLayer() {
			override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
				guiGraphics.drawString(Minecraft.getInstance().font, "I LOVE REGISTERED OVERLAYS", 0, 20, Color.WHITE.rgb)
			}
		})
	}

	override fun addCreativeTabs(tabs: MinecraftCreativeTabFeature) {
		tabs.add(Identifier("breadmod", "breadmod"), MinecraftCreativeTab())
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

	// todo the way the transform from mods is applied to transforms in eam is causing duplicates,
	//  need to figure out how to properly pass mod transforms into the loader
	override fun transformClasses(transforms: MutableMap<String, (ClassBuilder, ClassElement) -> Unit>) {
		val cameraNative = NativeConstantsV1x21x1.nativeClassDesc(Camera::class)
		val levelRendererNative = NativeConstantsV1x21x1.nativeClassDesc(LevelRenderer::class)
//		transforms[net_minecraft_client_Minecraft] = { classBuilder, classElement ->
//			val mainTargetMethod = classBuilder.transformMethodCode(
//				classElement,
//				"h",
//				MethodTypeDesc.of(RenderTarget.classDesc)
//			) { codeBuilder, codeElement, index ->
//				if (index == 1) {
//					codeBuilder
//						.getstatic(
//							CameraTexture::class.classDesc,
//							"targetBeingRendered",
//							RenderTarget.mimicClassDesc
//						)
//						.ifThen(Opcode.IFNONNULL) { builder ->
//							builder
//								.getstatic(
//									CameraTexture::class.classDesc,
//									"targetBeingRendered",
//									RenderTarget.mimicClassDesc
//								)
//								.getfield(
//									MimickedClass.classDesc,
//									"around",
//									ConstantDescs.CD_Object
//								)
//								.checkcast(RenderTarget.classDesc)
//								.areturn()
//						}
//				}
//				codeBuilder.with(codeElement)
//			}
//
//			if (!mainTargetMethod) classBuilder.with(classElement)
//		}
//		transforms[net_minecraft_client_renderer_LevelRenderer] = { classBuilder, classElement ->
//			val renderLevelTransform = classBuilder.transformMethodCode(
//				classElement,
//				"a",
//				MethodTypeDesc.of(
//					ConstantDescs.CD_void,
//					DeltaTracker.classDesc,
//					ConstantDescs.CD_boolean,
//					cameraNative,
//					GameRenderer.classDesc,
//					LightTexture.classDesc,
//					Matrix4f.classDesc,
//					Matrix4f.classDesc
//				)
//			) { codeBuilder, codeElement, index ->
//				codeBuilder.with(codeElement)
//				when (index) {
//					527 -> codeBuilder.aload(0) // Before INVOKEVIRTUAL ffy.i ()Z // Bytecode Line 4694
//					529 -> codeBuilder // After INVOKEVIRTUAL ffy.i ()Z // Bytecode Line 4694
//						.invokevirtual(
//							levelRendererNative,
//							"cameraIsDetachedOverride",
//							MethodTypeDesc.of(ConstantDescs.CD_boolean, ConstantDescs.CD_boolean)
//						)
//					546 -> codeBuilder.aload(0) // Before ALOAD 3 // Bytecode Line 4713
//					549 -> codeBuilder  // After ALOAD 27 // Bytecode Line 4715
//						.invokevirtual(
//							levelRendererNative,
//							"getCameraEntityOrOriginal",
//							MethodTypeDesc.of(Entity.classDesc, Entity.classDesc, Entity.classDesc)
//						)
//						.aload(27)
//				}
//			}
//			val shouldShowEntityOutlines = classBuilder.transformMethodCode(
//				classElement,
//				"d",
//				MethodTypeDesc.of(ConstantDescs.CD_boolean)
//			) { codeBuilder, codeElement, index ->
//				when (index) {
//					0 -> codeBuilder.localVariable(
//						1,
//						"original",
//						ConstantDescs.CD_boolean,
//						codeBuilder.startLabel(),
//						codeBuilder.endLabel()
//					)
//					23 -> codeBuilder // Replaces IRETURN // Bytecode Line 2552
//						.istore(1)
//						.getstatic(
//							CameraTexture::class.classDesc,
//							"targetBeingRendered",
//							RenderTarget.mimicClassDesc
//						)
//						.ifThen(Opcode.IFNONNULL) { builder ->
//							builder
//								.iconst_0()
//								.ireturn()
//						}
//						.iload(1)
//						.ireturn()
//					else -> codeBuilder.with(codeElement)
//				}
//			}
//
//			classBuilder.addMethod(
//				"getCameraEntityOrOriginal",
//				MethodTypeDesc.of(Entity.classDesc, Entity.classDesc, Entity.classDesc),
//				ACC_PRIVATE
//			) { methodBuilder ->
//				methodBuilder.withCode { codeBuilder ->
//					codeBuilder
//						.getstatic(
//							CameraTexture::class.classDesc,
//							"targetBeingRendered",
//							RenderTarget.mimicClassDesc
//						)
//						.ifThen(Opcode.IFNONNULL) { builder ->
//							builder
//								.aload(2)
//								.instanceOf(LivingEntity.classDesc)
//								.ifThen { b ->
//									b.aload(2).areturn()
//								}
//						}
//						.aload(1)
//						.areturn()
//				}
//			}
//
//			classBuilder.addMethod(
//				"cameraIsDetachedOverride",
//				MethodTypeDesc.of(ConstantDescs.CD_boolean, ConstantDescs.CD_boolean),
//				ACC_PRIVATE
//			) { methodBuilder ->
//				methodBuilder.withCode { codeBuilder ->
//					codeBuilder
//						.getstatic(
//							CameraTexture::class.classDesc,
//							"targetBeingRendered",
//							RenderTarget.mimicClassDesc
//						)
//						.ifThen(Opcode.IFNONNULL) { builder ->
//							builder
//								.iconst_1()
//								.ireturn()
//						}
//						.iload(1)
//						.ireturn()
//				}
//			}
//
//			if (!(shouldShowEntityOutlines || renderLevelTransform)) classBuilder.with(classElement)
//		}
	}
}