package com.atlasstudio.barcodescanner.ui.scanner

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
class ScannerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val state = MutableStateFlow<ScannerFragmentState>(ScannerFragmentState.Init)
    val mState: StateFlow<ScannerFragmentState> get() = state

    private var sum = MutableStateFlow<Float>(0.0f)
    val mSum: StateFlow<Float> get() = sum

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
        state.value = ScannerFragmentState.Init
    }

    fun addCodeToSumAndList(code : String) {
        var divider : Float = 1.0f
        var length : Int = 0
        when (codeType.value) {
            CodeType.EAN13 ->  {
                divider = 1000.0f
                length = 13
            }
            CodeType.None -> divider = 1.0f
        }
        //if(code.length == length) {
            //sum.value += code.subSequence(startIndex.value, endIndex.value).toString()
            //    .toFloat() / divider
            sum.value += code.toString().toFloat()
            codes.add(Code(code, codeType.value))
        //}
    }

    fun clearSum() {
        sum.value = 0.0f
    }

    /*private fun setLoading(){
        state.value = ScannerFragmentState.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = ScannerFragmentState.IsLoading(false)
    }*/

    private fun showToast(message: String){
        state.value = ScannerFragmentState.ShowToast(message)
    }

    private fun showIdToast(messageId: Int){
        state.value = ScannerFragmentState.ShowIdToast(messageId)
    }

    private fun navigateToSettings() {
        state.value = ScannerFragmentState.NavigateToSettings
    }
}

sealed class ScannerFragmentState {
    object Init : ScannerFragmentState()
    //data class IsLoading(val isLoading : Boolean) : ScannerFragmentState()
    data class ShowToast(val message : String) : ScannerFragmentState()
    data class ShowIdToast(val messageId : Int) : ScannerFragmentState()
    object NavigateToSettings : ScannerFragmentState()
}
