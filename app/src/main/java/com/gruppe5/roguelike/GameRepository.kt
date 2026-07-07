package com.gruppe5.roguelike

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gruppe5.roguelike.inventory.ItemInstance
import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.map_element.MapTileEntity
import com.gruppe5.roguelike.map_element.entity.Enemy
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.BuffEntity
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
        val enemiesJson = Json.encodeToString<List<Enemy>>(state.enemies)
        val inventoryJson = Json.encodeToString<List<ItemInstance>>(state.inventory)
        gameDao.replaceSave(
            game = state.toEntity(enemiesJson, inventoryJson),
            tiles = state.map.flatten().map { it.toEntity() },
            buffs = state.activeBuffs.mapIndexed { slot, buff ->
                //buff -> item link über den inventar-slot (buffs bleiben in Room)
                val itemSlot = state.inventory.indexOfFirst { it === buff.sourceItem }.takeIf { it >= 0 }
                buff.toEntity(slot, itemSlot)
            },
        )
    }

    suspend fun load(): GameState? = withContext(Dispatchers.IO) {
        val game = gameDao.getGame() ?: return@withContext null
        val tiles = gameDao.getTiles()
        if (tiles.isEmpty()) return@withContext null //halber save -> lieber frisch anfangen

        val enemies = Json.decodeFromString<List<Enemy>>(game.enemiesJson)
        enemies.forEach { it.onDeserialized(enemies) } //jeder gegner verlinkt sich selber (siehe Enemy.onDeserialized)

        val inventory = Json.decodeFromString<List<ItemInstance>>(game.inventoryJson)
        val buffs = gameDao.getBuffs().sortedBy { it.slot }.map { it.toBuff(inventory) }

        val player = Player(game.playerStats, Position(game.playerX, game.playerY))
        player.inventory = inventory

        GameState(
            map = toTileGrid(tiles),
            player = player,
            enemies = enemies,
            inventory = inventory,
            activeBuffs = buffs,
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

    @Query("SELECT * FROM buffs")
    suspend fun getBuffs(): List<BuffEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuffs(buffs: List<BuffEntity>)

    @Query("DELETE FROM buffs")
    suspend fun clearBuffs()

    @Transaction
    suspend fun replaceSave(
        game: GameEntity,
        tiles: List<MapTileEntity>,
        buffs: List<BuffEntity>,
    ) {
        clearSave()
        insertGame(game)
        insertTiles(tiles)
        insertBuffs(buffs)
    }

    @Transaction
    suspend fun clearSave() {
        clearBuffs()
        clearTiles()
        clearGame()
    }
}
