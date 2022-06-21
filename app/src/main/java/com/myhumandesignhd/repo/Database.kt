package com.myhumandesignhd.repo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.myhumandesignhd.model.Child
import com.myhumandesignhd.model.Converters
import com.myhumandesignhd.model.User
import com.myhumandesignhd.repo.dao.ChildDao
import com.myhumandesignhd.repo.dao.UserDao

@Database(
    entities = [User::class, Child::class],
    version = 2
)
@TypeConverters(
    Converters::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun childDao(): ChildDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "hd_design.db"
        )
            .build()
    }
}
