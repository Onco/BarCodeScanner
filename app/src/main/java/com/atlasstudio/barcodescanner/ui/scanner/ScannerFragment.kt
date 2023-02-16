package com.atlasstudio.barcodescanner.ui.scanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atlasstudio.barcodescanner.R
import com.atlasstudio.barcodescanner.data.Code
import com.atlasstudio.barcodescanner.databinding.FragmentScannerBinding
import com.atlasstudio.barcodescanner.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Scanner

@AndroidEntryPoint
class ScannerFragment : Fragment(), ScannerAdapter.OnItemClickListener {

    private var mBinding: FragmentScannerBinding? = null
    //private var editTextListenerEnabled: Boolean = true
    private val viewModel: ScannerViewModel by viewModels()
    private lateinit var intentFilter: IntentFilter

    protected var broadcatReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            /*if ("add-to-sum".equals(intent.action)) {
                viewModel.addCurrentToSum()
                binding.textView.text = String.format(getString(R.string.value_formatter), viewModel.mSum.value)
                binding.editText.hint = getString(R.string.input_edit_text)
                binding.editText.text.clear()
            }*/
            if ("clear-sum".equals(intent.action)) {
                viewModel.clearSum()
                //viewModel.setCurrentNumber("")
                binding.textView.text = ""
                binding.editText.text.clear()
                binding.editText.hint = getString(R.string.input_edit_text)
            }
            /*if ("clear-number".equals(intent.action)) {
                editTextListenerEnabled = false
                binding.editText.setText(viewModel.mCurrentNumber.value.dropLast(1))
                editTextListenerEnabled = true
                viewModel.setCurrentNumber(viewModel.mCurrentNumber.value.dropLast(1))
            }
            if("concat-to-number".equals((intent.action)))
            {
                val num = intent.getIntExtra("singleNumber", 0).toString()
                editTextListenerEnabled = false
                binding.editText.setText(viewModel.mCurrentNumber.value.plus(num))
                editTextListenerEnabled = true
                viewModel.setCurrentNumber(viewModel.mCurrentNumber.value.plus(num))
            }
            if("decimal-separator".equals(intent.action))
            {
                editTextListenerEnabled = false
                binding.editText.setText(viewModel.mCurrentNumber.value.plus("."))
                editTextListenerEnabled = true
                viewModel.setCurrentNumber(viewModel.mCurrentNumber.value.plus("."))
            }*/
        }
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //(activity as CodesListFragment).setOnBottomSheetCallbacks(this)

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
                    /*val code = scannerAdapter.currentList[viewHolder.adapterPosition]
                    code.let {
                        viewModel.onCodeDelete(code)
                    }*/
                }
            }).attachToRecyclerView(recyclerViewScanner)
        }

        /*lifecycle.coroutineScope.launch {
            viewModel.createScannerFlow().collect() {
                scannerAdapter.submitList(it)
            }
        }*/

        binding.editText.inputType = InputType.TYPE_NULL

        if (viewModel.mSum.value != 0.0) {
            binding.textView.text = String.format(getString(R.string.value_formatter), viewModel.mSum.value)
        }

        binding.editText.requestFocus()

        binding.editText.setOnClickListener {
            //(activity as CodesListFragment).closeBottomSheet()
        }

        binding.buttonClear.setOnClickListener {
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            viewModel.clearSum()
            //viewModel.setCurrentNumber("")
            binding.textView.text = ""
            binding.editText.text.clear()
            binding.editText.hint = getString(R.string.input_edit_text)

            /*if (currentState == BottomSheetBehavior.STATE_EXPANDED) {
                (activity as CodesListFragment).closeBottomSheet()
            } else  {
                (activity as CodesListFragment).openBottomSheet()
            }*/
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

        intentFilter = IntentFilter("clear-sum") /*intentFilter.addAction("add-to-sum")*/
        /*intentFilter.addAction("clear-number")
        intentFilter.addAction("concat-to-number")
        intentFilter.addAction("decimal-separator")*/
        LocalBroadcastManager.getInstance(context!!).registerReceiver(broadcatReceiver, intentFilter);
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
            is ScannerFragmentState.NavigateToSettings -> Unit
            is ScannerFragmentState.ShowToast -> Unit
            is ScannerFragmentState.ShowIdToast -> Unit
            //is ScannerFragmentState.SnackBarCodeDeleted -> handleDeletedSnackBar()
        }
    }

    override fun onItemClick(position: Int) {
        // TBD
    }

    override fun onButtonDeleteClick(position: Int) {
        viewModel.onCodeDelete(position)
    }

    /*private fun handleDeletedSnackBar() {
        requireActivity().showToast(getString(R.string.location_deleted_confirmation))
    }*/

    private fun handleSnackBar(message: String) {
        requireActivity().showToast(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    /*override fun onStateChanged(bottomSheet: View, newState: Int) {
        currentState = newState
        when (newState) {
            BottomSheetBehavior.STATE_EXPANDED -> {
            }
            BottomSheetBehavior.STATE_COLLAPSED -> {
            }
        }
    }*/
}