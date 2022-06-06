package com.atlasstudio.barcodescanner.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(): ViewModel() {
    private val state = MutableStateFlow<SettingsFragmentState>(SettingsFragmentState.Init)
    val mState: StateFlow<SettingsFragmentState> get() = state

    fun onStart() {
        initialize()
    }

    private fun initialize() {
        state.value = SettingsFragmentState.Init
    }

    private fun showErrorSnackBar() {
        state.value = SettingsFragmentState.SnackBarErrorSettings
    }
}

sealed class SettingsFragmentState {
    object Init : SettingsFragmentState()
    object SnackBarErrorSettings: SettingsFragmentState()
}