package com.atlasstudio.barcodescanner.ui.scanner

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.res.stringResource
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atlasstudio.barcodescanner.R
import com.atlasstudio.barcodescanner.data.Code
import com.atlasstudio.barcodescanner.databinding.FragmentScannerBinding
import com.atlasstudio.barcodescanner.ui.scanner.ScannerAdapter
import com.atlasstudio.barcodescanner.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScannerFragment : Fragment(), ScannerAdapter.OnItemClickListener {

    private var mBinding: FragmentScannerBinding? = null
    private val viewModel: ScannerViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onStart()

        val scannerAdapter = ScannerAdapter(this)

        binding.apply {
            recyclerViewScanner.apply {
                adapter = scannerAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val code = scannerAdapter.currentList[viewHolder.adapterPosition]
                    /*code.let {
                        viewModel.onCodeDelete(code)
                    }*/
                }
            }).attachToRecyclerView(recyclerViewScanner!!)
        }

        lifecycle.coroutineScope.launch {
            viewModel.createScannerFlow().collect() {
                scannerAdapter.submitList(it)
            }
        }

        binding.editText.inputType = InputType.TYPE_NULL

        if (viewModel.mSum.value != 0.0f) {
            binding.textView.text = String.format(getString(R.string.value_formatter), viewModel.mSum.value)
        }

        binding.editText.requestFocus()

        binding.buttonClear.setOnClickListener {
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            viewModel.clearSum()
            binding.textView.text = ""
            binding.editText.text.clear()
            binding.editText.hint = getString(R.string.input_edit_text)
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
            //is ScannerFragmentState.SnackBarCodeDeleted -> handleDeletedSnackBar()
        }
    }

    override fun onItemClick(code: Code) {
        // TBD
    }

    /*override fun onButtonDeleteClick(code: Code) {
        viewModel.onCodeDelete(code)
    }*/

    /*private fun handleDeletedSnackBar() {
        requireActivity().showToast(getString(R.string.location_deleted_confirmation))
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}