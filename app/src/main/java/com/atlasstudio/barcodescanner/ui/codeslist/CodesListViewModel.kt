package com.atlasstudio.barcodescanner.ui.codeslist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import com.atlasstudio.barcodescanner.R
import com.atlasstudio.barcodescanner.data.Code
import com.atlasstudio.barcodescanner.data.CodeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CodesListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val state = MutableStateFlow<CodesListFragmentState>(CodesListFragmentState.Init)
    val mState: StateFlow<CodesListFragmentState> get() = state

    private lateinit var codes : MutableList<Code?>

    private var startIndex = MutableStateFlow<Int>(7)
    private var endIndex = MutableStateFlow<Int>(12)
    private var codeType = MutableStateFlow<CodeType>(CodeType.EAN13)

    suspend fun createScannerFlow(): Flow<MutableList<Code?>> {
        return flowOf(codes)
    }

    fun onStart() {
        initialize()
    }

    fun onReady() {
    }

    fun onCodeDelete(position: Int) {
        codes.removeAt(position)
        showIdToast(R.string.code_deleted)
    }

    private fun initialize() {
        codes = mutableListOf()
        state.value = CodesListFragmentState.Init
    }

    /*private fun setLoading(){
        state.value = ScannerFragmentState.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = ScannerFragmentState.IsLoading(false)
    }*/

    private fun showToast(message: String){
        state.value = CodesListFragmentState.ShowToast(message)
    }

    private fun showIdToast(messageId: Int){
        state.value = CodesListFragmentState.ShowIdToast(messageId)
    }

    private fun navigateToSettings() {
        state.value = CodesListFragmentState.NavigateToSettings
    }
}

sealed class CodesListFragmentState {
    object Init : CodesListFragmentState()
    //data class IsLoading(val isLoading : Boolean) : CodesListFragmentState()
    data class ShowToast(val message : String) : CodesListFragmentState()
    data class ShowIdToast(val messageId : Int) : CodesListFragmentState()
    object NavigateToSettings : CodesListFragmentState()
}