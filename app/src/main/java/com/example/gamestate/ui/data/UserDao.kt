package com.example.gamestate.ui.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addUser(user: User)

    @Query("SELECT * FROM user_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<User>>

    @Query("SELECT * FROM user_table WHERE username = :username AND password = :password")
    fun Loginuser(username:String,password:String):User?

    @Query("SELECT * FROM user_table WHERE email = :email")
    fun getUserByEmail(email: String): User?

    @Update
    fun updateUser(user: User)

}