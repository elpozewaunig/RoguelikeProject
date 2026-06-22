package com.gruppe5.roguelike.map_element

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.StatModifier

enum class MapTile(override val resId: Int, val isWall: Boolean = false, val stats: StatModifier = StatModifier()): MapElement {
    NONE(R.drawable.tile_none),
    GROUND(R.drawable.tile_ground),
    TREE(R.drawable.tile_tree, true),
    ROCK(R.drawable.tile_rock, true),
    WALL_STONE(R.drawable.tile_wall, true)
}
