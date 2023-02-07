package com.atlasstudio.barcodescanner.ui.scanner

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
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

    private var sum = MutableStateFlow<Double>(0.0)
    val mSum: StateFlow<Double> get() = sum

    private var currentNumber = MutableStateFlow<String>("")
    val mCurrentNumber: StateFlow<String> get() = currentNumber

    private var codes : MutableList<Code?> = mutableListOf()

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

    private fun initialize() {
        state.value = ScannerFragmentState.Init
        clearSum()
    }

    fun addCodeToSumAndList(code : String) {
        var divider : Double = 1.0
        var length : Int = 0
        when (codeType.value) {
            CodeType.EAN13 ->  {
                divider = 1000.0
                length = 13
            }
            CodeType.None -> divider = 1.0
        }
        if(code.length == length) {
            sum.value += code.subSequence(startIndex.value, endIndex.value).toString()
                .toDouble() / divider
            codes.add(Code(code, codeType.value))
        }
        currentNumber.value = ""
    }

    fun addCurrentToSum() {
        if(!currentNumber.value.isEmpty()) {
            val toCheck = currentNumber.value.toDoubleOrNull() ?: 0.0
            if (toCheck >= 0.0001) {
                sum.value += toCheck
            }
            currentNumber.value = ""
        }
    }

    fun clearSum() {
        sum.value = 0.0
        codes.clear()
        currentNumber.value = ""
    }

    fun setCurrentNumber(number: String) {
        if (number.toDoubleOrNull() != null || number == ".") {
            currentNumber.value = number
        }
        else if(number.isEmpty())
        {
            currentNumber.value = ""
        }
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

    private fun navigateToSettings() {
        state.value = ScannerFragmentState.NavigateToSettings
    }
}

sealed class ScannerFragmentState {
    object Init : ScannerFragmentState()
    //data class IsLoading(val isLoading : Boolean) : ScannerFragmentState()
    data class ShowToast(val message : String) : ScannerFragmentState()
    object NavigateToSettings : ScannerFragmentState()
}
