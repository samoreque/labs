package com.code.path.bitfit.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = SLEEP_TABLE_NAME)
class SleepDataEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = SLEEP_ID)
    val sleepId: Long = 0,
    @ColumnInfo(name = "sleepHours")
    val sleepHours: Float = 0f,
    @ColumnInfo(name = "feelingRate")
    val feelingRate: Int = 0,
    @ColumnInfo(name = "notes")
    val notes: String?,
    @ColumnInfo(name = "photoUrl")
    val mediaImageUrl: String?,
    @ColumnInfo(name = "createdAt")
    val createdAt: Date = Date()
)

internal const val SLEEP_TABLE_NAME = "sleep"
internal const val SLEEP_ID = "sleepId"