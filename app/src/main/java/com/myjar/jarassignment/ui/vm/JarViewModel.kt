package com.myjar.jarassignment.ui.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myjar.jarassignment.createRetrofit
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.data.repository.JarRepository
import com.myjar.jarassignment.data.repository.JarRepositoryImpl
import com.myjar.jarassignment.data.utils.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JarViewModel : ViewModel() {

    private val _listStringData = MutableStateFlow<List<ComputerItem>>(emptyList())

    private val repository: JarRepository = JarRepositoryImpl(createRetrofit())

    var queryText = MutableStateFlow("")
        private set

    var filterList: StateFlow<List<ComputerItem>> =
        combine(queryText, _listStringData) { query, list ->
            if (query.isEmpty()) {
                list
            } else {
                list.filter { it.name.startsWith(prefix = query, ignoreCase = true) }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(500),
            initialValue = emptyList<ComputerItem>()
        )
        private set


    fun searchQuery(name: String) {
        if (name.isNotEmpty()) {
            queryText.value = name
        }
    }

    fun fetchData(contex: Context) {

        val preferenceManager = PreferenceManager(contex)

        viewModelScope.launch {
            try {
                repository.fetchResults().collect {
                    _listStringData.value = it
                    preferenceManager.saveData(it)
                }
            } catch (e: Exception) {
                val data = preferenceManager.retriveData()
                _listStringData.value = data
            }
        }
    }
}