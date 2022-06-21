package com.myhumandesignhd.repo.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.myhumandesignhd.model.Child

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