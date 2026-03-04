package org.bread_experts_group.breadmod.transforms

import org.bread_experts_group.breadmod.camera.CameraTexture
import org.bread_experts_group.eam.classDesc
import org.bread_experts_group.eam.minecraft.mimic.MimickedClass
import org.bread_experts_group.eam.minecraft.transform.ModClassTransform
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.NativeConstantsV1x21x1
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.com.mojang.blaze3d.pipeline.RenderTarget
import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassElement
import java.lang.classfile.Opcode
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

class MinecraftTransform : ModClassTransform(NativeConstantsV1x21x1.net_minecraft_client_Minecraft) {
	val cameraClassDesc = CameraTexture::class.classDesc
	override fun transform(): (ClassBuilder, ClassElement) -> Unit = { classBuilder, classElement ->
		val mainTargetMethod = classBuilder.transformMethodCode(
			classElement,
			"h",
			MethodTypeDesc.of(RenderTarget.classDesc)
		) { codeBuilder, codeElement, index ->
			if (index == 1) {
				codeBuilder
					.getstatic(
						cameraClassDesc,
						"targetBeingRendered",
						RenderTarget.mimicClassDesc
					)
					.ifThen(Opcode.IFNONNULL) { builder ->
						builder
							.getstatic(
								cameraClassDesc,
								"targetBeingRendered",
								RenderTarget.mimicClassDesc
							)
							.getfield(
								MimickedClass.classDesc,
								"around",
								ConstantDescs.CD_Object
							)
							.checkcast(RenderTarget.classDesc)
							.areturn()
					}
			}
			codeBuilder.with(codeElement)
		}

		if (!mainTargetMethod) classBuilder.with(classElement)
	}
}