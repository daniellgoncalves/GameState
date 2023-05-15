package com.example.gamestate.ui.data

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {

    val readAllData: LiveData<List<User>> = userDao.readAllData()

    suspend fun addUser(user: User) {
        userDao.addUser(user)
    }

    suspend fun LoginUser(username:String,password:String):User? {
        return userDao.Loginuser(username,password)
    }
}