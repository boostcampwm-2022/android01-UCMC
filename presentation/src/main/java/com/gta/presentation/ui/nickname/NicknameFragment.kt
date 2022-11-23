package com.gta.presentation.ui.nickname

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.gta.domain.model.NicknameState
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentNicknameBinding
import com.gta.presentation.ui.MainActivity
import com.gta.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NicknameFragment : BaseFragment<FragmentNicknameBinding>(
    R.layout.fragment_nickname
) {

    private val viewModel: NicknameViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).supportActionBar?.title = getString(R.string.nickname_toolbar)
        binding.vm = viewModel
        initCollector()
    }

    private fun initCollector() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.nicknameState.collectLatest { state ->
                    handleNicknameState(state)
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.nicknameChangeEvent.collectLatest { state ->
                    if (state) {
                        findNavController().navigate(R.id.action_nicknameFragment_to_myPageFragment)
                    }
                }
            }
        }
    }

    private fun handleNicknameState(state: NicknameState) {
        when (state) {
            NicknameState.IDLE, NicknameState.GREAT -> {
                binding.tlNicknameInput.helperText = getString(R.string.nickname_condition)
            }
            NicknameState.SHORT_LENGTH -> {
                binding.tlNicknameInput.error = getString(R.string.nickname_error_min_length)
            }
            NicknameState.CONTAIN_SYMBOL -> {
                binding.tlNicknameInput.error = getString(R.string.nickname_error_symbols)
            }
        }
    }
}
