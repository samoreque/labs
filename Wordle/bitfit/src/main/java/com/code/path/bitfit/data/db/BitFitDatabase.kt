package com.code.path.bitfit.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.code.path.bitfit.data.db.dao.SleepDao
import com.code.path.bitfit.data.db.entity.SleepDataEntity

@Database(
    entities = [SleepDataEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class BitFitDatabase : RoomDatabase() {
    abstract fun sleepDao(): SleepDao

    companion object {
        private const val DB_NAME = "BitFitDatabase"

        @Volatile
        private var INSTANCE: BitFitDatabase? = null

        fun getInstance(context: Context): BitFitDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                BitFitDatabase::class.java, DB_NAME
            ).fallbackToDestructiveMigration().build()
    }
}
