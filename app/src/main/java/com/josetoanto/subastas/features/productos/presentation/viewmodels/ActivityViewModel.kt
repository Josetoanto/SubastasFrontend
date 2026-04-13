package com.josetoanto.subastas.features.productos.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josetoanto.subastas.core.local.AppLocalStore
import com.josetoanto.subastas.features.productos.presentation.screens.ActivityUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val appLocalStore: AppLocalStore
) : ViewModel() {

    private val _state = MutableStateFlow(ActivityUIState())
    val state: StateFlow<ActivityUIState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            appLocalStore.activitySummaryFlow.collectLatest { summary ->
                _state.update {
                    it.copy(
                        summary = summary,
                        isLoading = false
                    )
                }
            }
        }
    }
}
