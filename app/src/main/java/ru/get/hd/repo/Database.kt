package ru.get.hd.repo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.get.hd.model.User
import ru.get.hd.repo.dao.UserDao

@Database(
    entities = [User::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            AppDatabase::class.java, "hd_design.db")
            .build()
    }
}
