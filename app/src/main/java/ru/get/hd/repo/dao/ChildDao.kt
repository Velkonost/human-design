package ru.get.hd.repo.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.get.hd.model.Child
import ru.get.hd.model.User

@Dao
interface ChildDao {

    @Query("SELECT * FROM children")
    fun getAll(): List<Child>

    @Query("SELECT * FROM children WHERE id LIKE :id")
    fun findById(id: Long): Child

    @Insert
    fun insert(user: Child)

    @Delete
    fun delete(user: Child)

    @Update
    fun updateUser(user: Child)

}