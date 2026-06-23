package com.gruppe5.roguelike.map_generators

import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.map_element.entity.ChaseEnemy
import com.gruppe5.roguelike.map_element.entity.Enemy
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import kotlin.random.Random

class BasicMapGenerator: MapGenerator {
    val height = 100
    val width = 100

    private val tilemap: List<List<MapTile>> = generateMap()
    private val enemies: List<Enemy> = generateEnemies()


    override fun getMap(): List<List<MapTile>> {
        return tilemap
    }

    override fun getEnemies(): List<Enemy> {
        return enemies
    }

    override fun getStartPos(): Position {
        return Position(1, 1)
    }

    private fun generateMap(): List<List<MapTile>> {
        val map: MutableList<MutableList<MapTile>> = mutableListOf()

        for(y in 0..<height) {
            map.add(mutableListOf())
            val row = map[y]

            for(x in 0..<width) {
                val position = Position(x, y)
                if(y == 0 || x == 0 || y == height-1 || x == width-1) {
                    row.add(MapTile(TileType.WALL_STONE, position))
                    continue
                }
                val randomFloat = Random.nextFloat()
                if(randomFloat < 0.1) {
                    row.add(MapTile(TileType.TREE, position))
                    continue
                }
                if(randomFloat < 0.2) {
                    row.add(MapTile(TileType.ROCK, position))
                    continue
                }
                row.add(MapTile(TileType.GROUND, position))
            }

        }

        return map
    }

    private fun generateEnemies(): List<Enemy> {
        val startPos = getStartPos()
        val enemies: MutableList<Enemy> = mutableListOf()
        val enemyCount = 3

        var spawnedCount = 0
        for (y in startPos.y + 5 until tilemap.size) {
            for (x in startPos.x + 5 until tilemap[y].size) {
                if (!tilemap[y][x].type.isWall) {
                    enemies.add(ChaseEnemy(StatModifier(), Position(x, y)))
                    spawnedCount++
                    if (spawnedCount >= enemyCount) return enemies
                }
            }
        }
        return enemies
    }

}
