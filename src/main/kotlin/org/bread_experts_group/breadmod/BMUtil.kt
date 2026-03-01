package org.bread_experts_group.breadmod

import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.core.Vec3i
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.resources.ResourceLocation

object BMUtil {
	fun modLocation(vararg path: String): ResourceLocation =
		path.toMutableList().let {
			ResourceLocation.fromNamespaceAndPath("breadmod", it.joinToString("/"))
		}

	operator fun Vec3i.component1(): Int = this.getX()
	operator fun Vec3i.component2(): Int = this.getY()
	operator fun Vec3i.component3(): Int = this.getZ()
}