package com.example.gamestate.ui.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application): AndroidViewModel(application) {

    private val readAllData: LiveData<List<User>>
    private val repository: UserRepository
    private var loginstatus = 0//0-Default,-1 Login Incorreto, 1 Login Correto
    private var user: User? = null

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        readAllData = repository.readAllData
    }

    fun addUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(user)
        }
    }

    fun LoginUser(username: String, password: String):Int{
        viewModelScope.launch {
            val user = repository.LoginUser(username, password)
            if (user != null) {
                loginstatus = 1
            } else {
                loginstatus = -1
            }
        }
        return loginstatus
    }

    fun getUserByEmail(email: String): User? {
        user = repository.getUserByEmail(email)
        return user
    }

    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(user)
        }
    }
}