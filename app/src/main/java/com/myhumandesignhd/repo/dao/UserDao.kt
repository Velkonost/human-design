package com.myhumandesignhd.repo.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.myhumandesignhd.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    @Transaction
    fun getAll(): List<User>

    @Query("SELECT * FROM users WHERE id LIKE :id")
    @Transaction
    fun findById(id: Long): User?

    @Insert
    fun insert(user: User)

    @Delete
    fun delete(user: User)

    @Update
    fun updateUser(user: User)
}