package org.bread_experts_group.breadmod.camera

import org.bread_experts_group.breadmod.BMRenderType
import org.bread_experts_group.breadmod.BMUtil.component1
import org.bread_experts_group.breadmod.BMUtil.component2
import org.bread_experts_group.breadmod.BMUtil.component3
import org.bread_experts_group.breadmod.BMUtil.modLocation
import org.bread_experts_group.breadmod.Color
import org.bread_experts_group.breadmod.TestBlockEntity
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.com.mojang.blaze3d.pipeline.MainTarget
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.com.mojang.blaze3d.pipeline.RenderTarget
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.com.mojang.blaze3d.pipeline.TextureTarget
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.com.mojang.blaze3d.systems.RenderSystem
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.com.mojang.blaze3d.vertex.PoseStack
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.Camera
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.Minecraft
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.FogRenderer
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.GameRenderer
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.RenderType
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.texture.AbstractTexture
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.texture.TextureManager
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.client.renderer.texture.Tickable
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.core.BlockPos
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.resources.ResourceLocation
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.server.packs.resources.ResourceManager
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.world.entity.Display
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.world.entity.EntityType
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.org.joml.Matrix4f
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.org.joml.Quaternionf

class CameraTexture(
	private val pos: BlockPos,
	private val width: Int,
	private val height: Int,
	private val entity: TestBlockEntity,
	val location: ResourceLocation
) : AbstractTexture(), Tickable {
	companion object {
		private const val DEG_TO_RAD: Float = 0.017453292f
		@JvmField
		val camera: Camera = Camera()
		@JvmField
		var targetBeingRendered: RenderTarget? = null
		private var textureCounter: Int = 0
		val frameTargets: MutableMap<BlockPos, TextureTarget> = mutableMapOf()
		val textures: MutableMap<BlockPos, CameraTexture> = mutableMapOf()
		fun get(blockEntity: TestBlockEntity, width: Int, height: Int): CameraTexture {
			val pos = blockEntity.getBlockPos()
			return this.textures.getOrPut(pos) {
				CameraTexture(
					pos,
					width,
					height,
					blockEntity,
					modLocation("camera_texture_${this.textureCounter++}")
				)
			}
		}
	}

	var initialized: Boolean = false
	private val minecraft: Minecraft = Minecraft.getInstance()
	private val frameBuffer: MainTarget = MainTarget(this.width, this.height)

	fun init() {
		println("executing init")
		val level = this.entity.getLevel() ?: return
		if (TextureManager.textureLock) return
		if (camera.entity == null)
			camera.entity = Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level)

//		textureId = frameBuffer.getColorTextureId()
		RenderSystem.executeOnRenderThread {
			minecraft.getTextureManager().register(location, this)
//			this.updateTexture()
		}
		initialized = true
	}

	override fun tick() {
		if (!this.initialized) return
		RenderSystem.executeOnRenderThread(this::updateTexture)
	}

	override fun getId(): Int = frameBuffer.getColorTextureId()

	override fun releaseId() {
		RenderSystem.executeOnRenderThread {
//			this.textureId = -1
			this.frameBuffer.destroyBuffers()
		}
	}

	override fun close() {
		initialized = false
		releaseId()
		RenderSystem.executeOnRenderThread {
			minecraft.getTextureManager().release(this.location)
		}
	}

	override fun load(resourceManager: ResourceManager) {
	}

	private fun updateTexture() {
		val target = frameTargets.getOrPut(this.pos) {
			TextureTarget(this.width, this.height, true, Minecraft.ON_OSX)
		}
		val mainTarget = minecraft.getMainRenderTarget()

		bind()
		setupCamera()

		target.bindWrite(true)
		targetBeingRendered = target

		RenderSystem.clear(16640, Minecraft.ON_OSX)
		FogRenderer.setupNoFog()
		RenderSystem.enableCull()
		renderLevel(minecraft, target, camera)
		writeToFrameBuffer(minecraft, target)
		targetBeingRendered = null
		mainTarget.bindWrite(true)
	}

	private fun setupCamera() {
		val (x, y, z) = pos
		camera.setPosition(x.toDouble(), y + 1.0, z.toDouble())
	}

	private fun writeToFrameBuffer(minecraft: Minecraft, target: RenderTarget) {
		frameBuffer.clear(true)
		frameBuffer.bindWrite(true)

		RenderSystem.getModelViewMatrix().set(Matrix4f().identity())
		RenderSystem.getProjectionMatrix().set(Matrix4f().identity())
		val buffer = minecraft.renderBuffers().bufferSource()
		val renderType: RenderType = BMRenderType.renderTarget(target)
		val consumer = buffer.getBuffer(renderType)

		consumer.addVertex(-1f, -1f, 0f).setUv(0f, 1f).setColor(Color.WHITE)
		consumer.addVertex(1f, -1f, 0f).setUv(1f, 1f).setColor(Color.WHITE)
		consumer.addVertex(1f, 1f, 0f).setUv(1f, 0f).setColor(Color.WHITE)
		consumer.addVertex(-1f, 1f, 0f).setUv(0f, 0f).setColor(Color.WHITE)
		buffer.endBatch(renderType)
	}

	private fun createProjectionMatrix(gameRenderer: GameRenderer, target: RenderTarget, fov: Float): Matrix4f =
		Matrix4f().perspective(
			fov * DEG_TO_RAD,
			(target.width / target.height).toFloat(),
			0.05f,
			gameRenderer.getDepthFar()
		)

	private fun renderLevel(minecraft: Minecraft, target: RenderTarget, camera: Camera) {
		val deltaTracker = minecraft.getTimer()
		val gameRenderer = minecraft.gameRenderer
		val levelRenderer = minecraft.levelRenderer
		val projectionMatrix = createProjectionMatrix(gameRenderer, target, 70f)
		val poseStack = PoseStack()

		projectionMatrix.mul(poseStack.last().pose())
		gameRenderer.resetProjectionMatrix(projectionMatrix)
		val cameraRotation = camera.rotation().conjugate(Quaternionf())
		val frustumMatrix = Matrix4f().rotation(cameraRotation)
		levelRenderer.prepareCullFrustum(camera.getPosition(), frustumMatrix, projectionMatrix)
		levelRenderer.renderLevel(
			deltaTracker,
			false,
			camera,
			gameRenderer,
			gameRenderer.lightTexture(),
			frustumMatrix,
			projectionMatrix
		)
	}
}