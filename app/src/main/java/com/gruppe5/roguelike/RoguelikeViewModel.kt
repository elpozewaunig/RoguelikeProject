package com.gruppe5.roguelike

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.map_element.entity.ChaseEnemy
import com.gruppe5.roguelike.map_element.entity.Enemy
import com.gruppe5.roguelike.map_element.entity.Entity
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.map_generators.TileType
import com.gruppe5.roguelike.map_generators.BasicMapGenerator
import com.gruppe5.roguelike.map_generators.MapGenerator
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier

class RoguelikeViewModel : ViewModel() {
    private val mapGenerator: MapGenerator = BasicMapGenerator()
    val currentMap: List<List<MapTile>> = mapGenerator.getMap()
    val player: Player = Player(
        StatModifier(

        ),
        position = mapGenerator.getStartPos()
    )
    val enemies: MutableList<Enemy> = mutableListOf()
    var turn by mutableIntStateOf(0)

    val enemyCount: Int = 3
    init {
        spawnEnemies()
    }

    private fun move(x: Int, y: Int) {
        val targetPosition = Position(player.position.x + x, player.position.y + y)
        if (
            targetPosition.y < 0 ||
            targetPosition.x < 0 ||
            targetPosition.y >= currentMap.size ||
            targetPosition.x >= currentMap[targetPosition.y].size
        ) return

        val targetTileType: TileType = currentMap[targetPosition.y][targetPosition.x].type
        if (targetTileType.isWall) return

        val targetEnemy = getEnemyAt(targetPosition)
        if (targetEnemy != null) {
            attackLogic(player, targetEnemy)
        } else {
            player.position = targetPosition
        }
        moveAllEnemies()
        turn++
    }

    private fun moveAllEnemies() {
        for (enemy in enemies) {
            val targetPosition = enemy.move(currentMap, enemies, player.position)

            if (targetPosition == enemy.position) {
                continue
            }

            if (targetPosition == player.position) {
                attackLogic(enemy, player)
                continue
            }

            val occupant = getEnemyAt(targetPosition)
            if (occupant != null && occupant != enemy) {
                continue
            }
            enemy.position = targetPosition
        }
    }

    private fun getEnemyAt(position: Position): Enemy? {
        return enemies.firstOrNull { it.position == position }
    }

    private fun attackLogic(attacker: Entity, target: Entity) {
        // TODO machen schaden
    }

    private fun spawnEnemies() { // TODO bessere random spawn enemy logic statt erstes freie feld ab +5
        val startPos = player.position
        var spawnedCount = 0
        for (y in startPos.y + 5 until currentMap.size) {
            for (x in startPos.x + 5 until currentMap[y].size) {
                if (!currentMap[y][x].type.isWall) {
                    enemies.add(ChaseEnemy(StatModifier(), Position(x, y)))
                    spawnedCount++
                    if (spawnedCount >= enemyCount) return
                }
            }
        }
    }

    fun moveRight() {
        move(1, 0)
    }

    fun moveLeft() {
        move(-1, 0)
    }

    fun moveDown() {
        move(0, 1)
    }

    fun moveUp() {
        move(0, -1)
    }
}
