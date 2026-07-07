package com.gruppe5.roguelike

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gruppe5.roguelike.inventory.ItemInstance
import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.map_element.entity.Enemy
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.ActiveBuff
import com.gruppe5.roguelike.property.StatModifier

data class GameState(
    val map: List<List<MapTile>>,
    val player: Player,
    val enemies: List<Enemy>,
    val inventory: List<ItemInstance>,
    val activeBuffs: List<ActiveBuff>,
    val turn: Int,
    val isGameOver: Boolean,
) {

    //"Entity" heißt in dem Sinne nicht Enemy oder Player sondern `@Entity` annotation <- es is der GameState
    fun toEntity(enemiesJson: String, inventoryJson: String): GameEntity = GameEntity(
        turn = turn,
        isGameOver = isGameOver,
        playerStats = player.stats,
        playerX = player.position.x,
        playerY = player.position.y,
        enemiesJson = enemiesJson,
        inventoryJson = inventoryJson,
    )
}

@Entity(tableName = "game")
data class GameEntity(
    val turn: Int,
    @ColumnInfo(name = "is_game_over") val isGameOver: Boolean,
    @Embedded val playerStats: StatModifier,
    @ColumnInfo(name = "player_x") val playerX: Int,
    @ColumnInfo(name = "player_y") val playerY: Int,
    @ColumnInfo(name = "enemies_json") val enemiesJson: String,
    @ColumnInfo(name = "inventory_json") val inventoryJson: String,
) {
    @PrimaryKey
    var id: Int = 0 //Es gibt nur eines. Außer wir wollen save-state slots oder so später
}
