package com.gruppe5.roguelike

enum class MapTile(val resId: Int, val isWall: Boolean = false, val stats: StatModifier = StatModifier()) {
    NONE(R.drawable.tile_none),
    GROUND(R.drawable.tile_ground),
    TREE(R.drawable.tile_tree, true),
    ROCK(R.drawable.tile_rock, true),
    WALL_STONE(R.drawable.tile_wall, true)
}
