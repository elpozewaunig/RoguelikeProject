package com.gruppe5.roguelike

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gruppe5.roguelike.level_generators.BasicLevelGenerator
import com.gruppe5.roguelike.level_generators.LevelGenerator
import com.gruppe5.roguelike.inventory.ItemInstance
import com.gruppe5.roguelike.inventory.Items
import com.gruppe5.roguelike.property.ActiveBuff
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext
import com.gruppe5.roguelike.turn.TurnEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoguelikeViewModel(gameDao: GameDao) : ViewModel() {
    private val repository: GameRepository = GameRepository(gameDao)
    private val mapGenerator: LevelGenerator = BasicLevelGenerator()
    private val _uiState = MutableStateFlow(initialState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    private val state: GameState get() = _uiState.value

    init {
        viewModelScope.launch {
            //gespeichertes game weiterspielen falls vorhanden (game-over saves interessieren uns ned)
            repository.load()?.takeIf { !it.isGameOver }?.let { _uiState.value = it }
        }
    }

    private fun initialState(): GameState {
        val inventory = mutableListOf<ItemInstance>()
        val activeBuffs = mutableListOf<ActiveBuff>()
        val player = Player(
            StatModifier(maxHealth = 500, health = 500, attack = 5, defense = 2),
            position = mapGenerator.getStartPos()
        )
        //start-items: die engine mutiert player/inventory/buffs in place (perma-buffs), unverändert
        TurnEngine.addPlayerInventory(
            listOf(Items.mediumHealthPotion(), Items.opStrengthPotion(), Items.sword()),
            inventory, player, activeBuffs
        )
        player.inventory = inventory
        return GameState(
            map = mapGenerator.getMap(),
            player = player,
            enemies = mapGenerator.getEnemies(),
            inventory = inventory,
            activeBuffs = activeBuffs,
            turn = 0,
            isGameOver = false,
        )
    }

    //von MainActivity.onStop aufgerufen, apps können jederzeit gekillt werden
    fun saveGame() {
        val snapshot = state
        viewModelScope.launch {
            if (snapshot.isGameOver) repository.clear() else repository.save(snapshot)
        }
    }
    
    fun loadGame() {
        viewModelScope.launch {
            repository.load()?.let { _uiState.value = it }
        }
    }

    private fun submitPlayerMove(dx: Int, dy: Int) {
        if (state.isGameOver) return

        val target = Position(state.player.position.x + dx, state.player.position.y + dy)

        // des muss nur hier beim resolven von player tap to move gemacht werden, die enemies müssen sich selber drum kümmern nicht zum cheaten
        if (!isValidPosition(target)) return
        if (state.map[target.y][target.x].type.isWall) return

        val ctx = buildContext()
        state.player.queued = ctx.getEntityAt(target)
            ?.let { listOf(Action.Attack(it)) }
            ?: listOf(Action.Move(target))

        runTurn(ctx)
    }

    private fun runTurn(ctx: TurnContext = buildContext()) {
        val s = state
        val enemies = s.enemies.toMutableList()
        val activeBuffs = s.activeBuffs.toMutableList()
        val over = TurnEngine.runTurn(ctx, s.player, enemies, activeBuffs)
        _uiState.value = s.copy(
            enemies = enemies,
            activeBuffs = activeBuffs,
            turn = s.turn + 1,
            isGameOver = s.isGameOver || over,
        )
    }

    private fun buildContext(): TurnContext = TurnContext(state.map, listOf(state.player) + state.enemies)

    private fun isValidPosition(pos: Position): Boolean =
        pos.y >= 0 && pos.x >= 0 &&
            pos.y < state.map.size && pos.x < state.map[pos.y].size

    fun moveSkip() {
        if (state.isGameOver) return
        state.player.queued = listOf(Action.Wait)
        runTurn()
    }

    fun moveRight() = submitPlayerMove(1, 0)

    fun moveLeft() = submitPlayerMove(-1, 0)

    fun moveDown() = submitPlayerMove(0, 1)

    fun moveUp() = submitPlayerMove(0, -1)

    fun onInventorySlotClicked(index: Int) = mutateInventory { inventory, activeBuffs ->
        TurnEngine.useItem(index, inventory, state.player, activeBuffs)
    }

    private inline fun mutateInventory(block: (MutableList<ItemInstance>, MutableList<ActiveBuff>) -> Unit) {
        val s = state
        val inventory = s.inventory.toMutableList()
        val activeBuffs = s.activeBuffs.toMutableList()
        block(inventory, activeBuffs)
        s.player.inventory = inventory
        _uiState.value = s.copy(inventory = inventory, activeBuffs = activeBuffs)
    }
}

class RoguelikeViewModelFactory(private val dao: GameDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return RoguelikeViewModel(dao) as T
    }
}
