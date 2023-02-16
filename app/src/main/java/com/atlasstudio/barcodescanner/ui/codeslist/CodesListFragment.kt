package com.atlasstudio.barcodescanner.ui.codeslist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atlasstudio.barcodescanner.R
import com.atlasstudio.barcodescanner.databinding.FragmentCodesListBinding
import com.atlasstudio.barcodescanner.ui.scanner.OnBottomSheetCallbacks
import com.atlasstudio.barcodescanner.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CodesListFragment : Fragment(), CodesListAdapter.OnItemClickListener {

    private var mBinding: FragmentCodesListBinding? = null
    private val viewModel: CodesListViewModel by viewModels()

    private var listener: OnBottomSheetCallbacks? = null
    private var mBottomSheetBehavior: BottomSheetBehavior<View?>? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentCodesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onStart()

        val codesListAdapter = CodesListAdapter(this)

        binding.apply {
            recyclerViewCodesList.apply {
                adapter = codesListAdapter
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
                    val codePos = viewHolder.adapterPosition//codesListAdapter.currentList[viewHolder.adapterPosition]
                    codePos.let {
                        viewModel.onCodeDelete(codePos)
                    }
                }
            }).attachToRecyclerView(recyclerViewCodesList!!)
        }

        lifecycle.coroutineScope.launch {
            viewModel.createScannerFlow().collect() {
                codesListAdapter.submitList(it)
            }
        }

        configureBackdrop()
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

    private fun handleState(state: CodesListFragmentState){
        when(state){
            is CodesListFragmentState.Init -> Unit
            is CodesListFragmentState.ShowToast -> handleSnackBar(state.message)
            is CodesListFragmentState.ShowIdToast -> handleSnackBar(getString(state.messageId))
            is CodesListFragmentState.NavigateToSettings -> Unit
        }
    }

    override fun onItemClick(position: Int) {
        // TBD
    }

    override fun onButtonDeleteClick(position: Int) {
        viewModel.onCodeDelete(position)
    }

    private fun handleSnackBar(message: String) {
        requireActivity().showToast(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    fun setOnBottomSheetCallbacks(onBottomSheetCallbacks: OnBottomSheetCallbacks) {
        this.listener = onBottomSheetCallbacks
    }

    fun closeBottomSheet() {
        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun openBottomSheet() {
        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun configureBackdrop() {
        val fragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_scanner)

        fragment?.view?.let {
            BottomSheetBehavior.from(it).let { bs ->
                bs.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        listener?.onStateChanged(bottomSheet, newState)
                    }
                })

                bs.state = BottomSheetBehavior.STATE_EXPANDED
                mBottomSheetBehavior = bs
            }
        }
    }
}