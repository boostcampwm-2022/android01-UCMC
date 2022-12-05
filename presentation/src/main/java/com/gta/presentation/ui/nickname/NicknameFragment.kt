package com.gta.presentation.ui.nickname

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.domain.model.NicknameState
import com.gta.domain.model.UCMCResult
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentNicknameBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class NicknameFragment : BaseFragment<FragmentNicknameBinding>(
    R.layout.fragment_nickname
) {

    private val viewModel: NicknameViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        initCollector()
    }

    private fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.nicknameState.collectLatest { state ->
                handleNicknameState(state)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.nicknameChangeEvent.collectLatest { state ->
                when (state) {
                    is UCMCResult.Error -> {
                        sendSnackBar(
                            message = getString(R.string.nickname_error_firestore),
                            anchorView = binding.btnNicknameApply
                        )
                    }
                    is UCMCResult.Success -> {
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
