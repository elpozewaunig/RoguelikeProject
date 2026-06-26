package com.gruppe5.roguelike.level_generators

import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.map_element.entity.ChaseEnemy
import com.gruppe5.roguelike.map_element.entity.ElephantEnemy
import com.gruppe5.roguelike.map_element.entity.Enemy
import com.gruppe5.roguelike.map_element.entity.GhostEnemy
import com.gruppe5.roguelike.map_element.entity.KnightEnemy
import com.gruppe5.roguelike.map_element.entity.RangeSightedEnemy
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import kotlin.random.Random

class BasicLevelGenerator: LevelGenerator {
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
        val spawnProtectedArea = 5
        val mapPercentUsed = 0.2 //for now to keep the enemies close to spawn
        val startPos = getStartPos()
        val enemies: MutableList<Enemy> = mutableListOf()
        val targetEnemyCount = 16

        val validPositions = mutableListOf<Position>()
        for (y in startPos.y + spawnProtectedArea until (tilemap.size*mapPercentUsed).toInt()) {
            for (x in startPos.x + spawnProtectedArea until (tilemap[y].size*mapPercentUsed).toInt()) {
                if (!tilemap[y][x].type.isWall) {
                    validPositions.add(Position(x, y))
                }
            }
        }

        validPositions.shuffle(Random)
        val spawnCount = minOf(targetEnemyCount, validPositions.size)

        //TODO hier noch speed property adden wenn des amal implemented ist
        val enemyPool = listOf<(Position) -> Enemy>(
            { pos -> ChaseEnemy(StatModifier(maxHealth = 12, health = 12, attack = 3, defense = 1), pos) },
            { pos -> RangeSightedEnemy(StatModifier(maxHealth = 8, health = 8, attack = 3), pos, lineOfSight = 2) },
            { pos -> KnightEnemy(StatModifier(maxHealth = 1, health = 1, attack = 4, defense = 1), pos) }, //let p(nsas) -> pferd selten
            { pos -> ElephantEnemy(StatModifier(maxHealth = 8, health = 8, attack = 6, defense = 3), pos) },
            { pos -> GhostEnemy(StatModifier(maxHealth = 8, health = 8, attack = 4), pos) },
            { pos -> ChaseEnemy(StatModifier(maxHealth = 12, health = 12, attack = 3, defense = 1), pos) },
            { pos -> RangeSightedEnemy(StatModifier(maxHealth = 8, health = 8, attack = 3), pos, lineOfSight = 2) },
            { pos -> ElephantEnemy(StatModifier(maxHealth = 8, health = 8, attack = 6, defense = 3), pos) },
            { pos -> GhostEnemy(StatModifier(maxHealth = 8, health = 8, attack = 4), pos) }
        )

        for (i in 0 until spawnCount) {
            val spawnPos = validPositions[i]
            val randomEnemyFactory = enemyPool.random(Random)
            enemies.add(randomEnemyFactory(spawnPos))
        }

        return enemies
    }

}
