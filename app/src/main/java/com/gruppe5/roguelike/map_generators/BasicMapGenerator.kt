package com.gruppe5.roguelike.map_generators

import com.gruppe5.roguelike.MapTile
import com.gruppe5.roguelike.property.Position
import kotlin.random.Random

class BasicMapGenerator: MapGenerator {
    val height = 100
    val width = 100
    override fun getMap(): List<List<MapTile>> {
        val map: MutableList<MutableList<MapTile>> = mutableListOf()

        for(y in 0..<height) {
            map.add(mutableListOf())
            val row = map[y]

            for(x in 0..<width) {
                if(y == 0 || x == 0 || y == height-1 || x == width-1) {
                    row.add(MapTile.WALL_STONE)
                    continue
                }
                val randomFloat = Random.nextFloat()
                if(randomFloat < 0.2) {
                    row.add(MapTile.TREE)
                    continue
                }
                if(randomFloat < 0.3) {
                    row.add(MapTile.ROCK)
                    continue
                }
                row.add(MapTile.GROUND)
            }

        }

        return map
    }

    override fun getStartPos(): Position {
        return Position(1, 1)
    }

}
