package com.atlasstudio.barcodescanner.ui.scanner

import android.view.View

interface OnBottomSheetCallbacks {
    fun onStateChanged(bottomSheet: View, newState: Int)
}