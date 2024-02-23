package com.code.path.bitfit.data.db.dao

import androidx.room.Dao
import androidx.room.Ignore
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.code.path.bitfit.data.db.entity.SleepDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sleepDataEntity: SleepDataEntity)

    @Query("SELECT * FROM sleep ORDER BY sleepId DESC")
    fun getSleeps(): Flow<List<SleepDataEntity>>

    @Query("SELECT SUM(sleepHours)/ COUNT(*) AS hours, SUM(feelingRate)/ COUNT(*) AS feeling FROM sleep")
    fun getSleepStatistics(): Flow<SleepStatistics>
}

data class SleepStatistics(val hours: Float?, val feeling: Int?) {

    @Ignore
    val getHoursAverageCopy = getHoursAverageCopy(hours)

    @Ignore
    val getFeelingAverageCopy = getFeelingAverageCopy(feeling)

    companion object {
        private const val FEELING_MAX = 10

        fun getHoursAverageCopy(hours: Float?) = hours?.toString() ?: "0"

        fun getFeelingAverageCopy(feeling: Int?) = "${(feeling ?: 0)}/$FEELING_MAX"
    }
}