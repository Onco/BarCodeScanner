package com.atlasstudio.barcodescanner.ui.scanner

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.atlasstudio.barcodescanner.R
import com.atlasstudio.barcodescanner.databinding.FragmentScannerBinding
import com.atlasstudio.barcodescanner.ui.MainActivity
import com.atlasstudio.barcodescanner.ui.codeslist.CodesListFragment
import com.atlasstudio.barcodescanner.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ScannerFragment : BottomSheetDialogFragment(), OnBottomSheetCallbacks {

    private var mBinding: FragmentScannerBinding? = null
    private val viewModel: ScannerViewModel by viewModels()
    private var currentState: Int = BottomSheetBehavior.STATE_EXPANDED

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as CodesListFragment).setOnBottomSheetCallbacks(this)

        mBinding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onStart()

        binding.editText.inputType = InputType.TYPE_NULL

        if (viewModel.mSum.value != 0.0f) {
            binding.textView.text = String.format(getString(R.string.value_formatter), viewModel.mSum.value)
        }

        binding.editText.requestFocus()

        binding.editText.setOnClickListener {
            (activity as CodesListFragment).closeBottomSheet()
        }

        binding.buttonClear.setOnClickListener {
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            viewModel.clearSum()
            binding.textView.text = ""
            binding.editText.text.clear()
            binding.editText.hint = getString(R.string.input_edit_text)

            if (currentState == BottomSheetBehavior.STATE_EXPANDED) {
                (activity as CodesListFragment).closeBottomSheet()
            } else  {
                (activity as CodesListFragment).openBottomSheet()
            }
        }

        binding.editText.addTextChangedListener {
            if (binding.editText.text.isNotEmpty()) {
                viewModel.addCodeToSumAndList(binding.editText.text.toString())
                binding.textView.text = String.format(getString(R.string.value_formatter), viewModel.mSum.value)
                binding.editText.hint = binding.editText.text
                binding.editText.text.clear()

            }
        }

        binding.editText.setOnFocusChangeListener { _, b ->
            if(!b) {
                binding.editText.requestFocus()
            }
        }

        observe()
    }

    private fun observeState(){
        viewModel.mState
            .flowWithLifecycle(lifecycle)
            .onEach { state ->
                handleState(state)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observe(){
        observeState()
        // and more...
    }

    private fun handleState(state: ScannerFragmentState){
        when(state){
            is ScannerFragmentState.Init -> Unit
            is ScannerFragmentState.ShowToast -> handleSnackBar(state.message)
            is ScannerFragmentState.ShowIdToast -> handleSnackBar(getString(state.messageId))
        }
    }

    private fun handleSnackBar(message: String) {
        requireActivity().showToast(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        currentState = newState
        when (newState) {
            BottomSheetBehavior.STATE_EXPANDED -> {
            }
            BottomSheetBehavior.STATE_COLLAPSED -> {
            }
        }
    }
}