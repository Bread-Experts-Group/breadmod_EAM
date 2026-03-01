package org.bread_experts_group.breadmod

import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.core.BlockPos
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.world.level.block.entity.BlockEntity
import org.bread_experts_group.eam.minecraft.version_impl.v1x21x1.net.minecraft.world.level.block.state.BlockState

class TestBlockEntity(
	pos: BlockPos,
	state: BlockState,
	initAround: Boolean = true
) : BlockEntity(BMContent.TEST_ENTITY_TYPE, pos, state, initAround) {
	@Suppress("unused")
	constructor(around: Any) : this(BlockPos(0), BlockState(0), false) {
		this.around = around
	}
}