package com.gruppe5.roguelike.map_generators

import com.gruppe5.roguelike.MapTile
import kotlin.random.Random

class BasicMapGenerator: MapGenerator {
    val height = 100
    val width = 100
    override fun getMap(): List<List<MapTile>> {
        val map: MutableList<MutableList<MapTile>> = mutableListOf()

        for(i in 0..<height) {
            map.add(mutableListOf())
            val row = map[i]

            for(j in 0..<width) {
                if(i == 0 || j == 0 || i == height-1 || j == width-1) {
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

}
