package com.gruppe5.roguelike.property

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gruppe5.roguelike.inventory.ItemInstance

//TODO des konzept is mit serialization so wie i des jz da hab richtig scuffed.. müss ma gemeinsam noch amal drüberschaun Thomas bitte danke
class ActiveBuff(
    val statsMod: StatModifier,
    var remainingTurns: Int,
    val sourceItem: ItemInstance? = null,
) {
    fun toEntity(slot: Int, itemSlot: Int?): BuffEntity =
        BuffEntity(slot, statsMod, remainingTurns, itemSlot)
}

@Entity(tableName = "buffs")
data class BuffEntity(
    @PrimaryKey val slot: Int,
    @Embedded val statsMod: StatModifier,
    @ColumnInfo(name = "remaining_turns") val remainingTurns: Int,
    @ColumnInfo(name = "item_slot") val itemSlot: Int?,
) {
    fun toBuff(inventory: List<ItemInstance>): ActiveBuff =
        ActiveBuff(statsMod, remainingTurns, sourceItem = itemSlot?.let(inventory::getOrNull))
}
