package com.atlasstudio.barcodescanner.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.atlasstudio.barcodescanner.R
import com.atlasstudio.barcodescanner.databinding.FragmentSettingsBinding
import com.atlasstudio.barcodescanner.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModels()
    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onStart()

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
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

    private fun handleState(state: SettingsFragmentState){
        when(state){
            is SettingsFragmentState.Init -> Unit
            is SettingsFragmentState.SnackBarErrorSettings -> handleErrorSnackBar()
        }
    }

    private fun handleErrorSnackBar() {
        requireActivity().showToast(getString(R.string.error_settings))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}