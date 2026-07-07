package com.gruppe5.roguelike

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gruppe5.roguelike.inventory.EquipSlot
import com.gruppe5.roguelike.inventory.ItemInstance
import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.map_element.MapTileEntity
import com.gruppe5.roguelike.map_element.entity.Enemy
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.Effect
import com.gruppe5.roguelike.property.Position
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Room für Data-Persistence. Siehe enemy für rationale warum kotlinx für polymorphism bei gegner und items
 */
class GameRepository(private val gameDao: GameDao) {

    suspend fun save(state: GameState) = withContext(Dispatchers.IO) {
        gameDao.replaceSave(
            game = state.toEntity(
                enemiesJson = Json.encodeToString<List<Enemy>>(state.enemies),
                inventoryJson = Json.encodeToString<List<ItemInstance>>(state.inventory),
                trinketsJson = Json.encodeToString<List<ItemInstance>>(state.trinkets),
                equipmentJson = Json.encodeToString<Map<EquipSlot, ItemInstance>>(state.equipment),
                effectsJson = Json.encodeToString<List<Effect>>(state.effects),
            ),
            tiles = state.map.flatten().map { it.toEntity() },
        )
    }

    suspend fun load(): GameState? = withContext(Dispatchers.IO) {
        val game = gameDao.getGame() ?: return@withContext null
        val tiles = gameDao.getTiles()
        if (tiles.isEmpty()) return@withContext null //halber save -> lieber frisch anfangen

        val enemies = Json.decodeFromString<List<Enemy>>(game.enemiesJson)
        enemies.forEach { it.onDeserialized(enemies) } //jeder gegner verlinkt sich selber (siehe Enemy.onDeserialized)

        val inventory = Json.decodeFromString<List<ItemInstance>>(game.inventoryJson)

        val player = Player(game.playerStats, Position(game.playerX, game.playerY))
        player.inventory = inventory

        GameState(
            map = toTileGrid(tiles),
            player = player,
            enemies = enemies,
            inventory = inventory,
            trinkets = Json.decodeFromString<List<ItemInstance>>(game.trinketsJson),
            equipment = Json.decodeFromString<Map<EquipSlot, ItemInstance>>(game.equipmentJson),
            effects = Json.decodeFromString<List<Effect>>(game.effectsJson),
            turn = game.turn,
            isGameOver = game.isGameOver,
        )
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        gameDao.clearSave()
    }

    private fun toTileGrid(rows: List<MapTileEntity>): List<List<MapTile>> {
        val byPos = rows.associateBy { it.x to it.y }
        val width = rows.maxOf { it.x } + 1
        val height = rows.maxOf { it.y } + 1
        return List(height) { y -> List(width) { x -> byPos.getValue(x to y).toTile() } }
    }
}

@Dao
interface GameDao {
    @Query("SELECT * FROM game")
    suspend fun getGame(): GameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)

    @Query("DELETE FROM game")
    suspend fun clearGame()

    @Query("SELECT * FROM map_tiles")
    suspend fun getTiles(): List<MapTileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTiles(tiles: List<MapTileEntity>)

    @Query("DELETE FROM map_tiles")
    suspend fun clearTiles()

    @Transaction
    suspend fun replaceSave(
        game: GameEntity,
        tiles: List<MapTileEntity>,
    ) {
        clearSave()
        insertGame(game)
        insertTiles(tiles)
    }

    @Transaction
    suspend fun clearSave() {
        clearTiles()
        clearGame()
    }
}
