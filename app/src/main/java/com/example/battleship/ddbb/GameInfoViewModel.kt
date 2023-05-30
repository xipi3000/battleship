package com.example.battleship.ddbb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/*Tal qual del tutorial*/
class GameInfoViewModel (private val repository: GameInfoRepository) : ViewModel() {
    var allGames = repository.allGames.asLiveData()

    fun insert(game: GameInfo) = viewModelScope.launch {
        repository.insert(game)
    }
}

class GameInfoViewModelFactory(private val repository: GameInfoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameInfoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
