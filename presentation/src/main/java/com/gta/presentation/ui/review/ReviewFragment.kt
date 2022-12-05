package com.gta.presentation.ui.review

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.domain.model.DuplicatedItemException
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.UCMCResult
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentReviewBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ReviewFragment : BaseFragment<FragmentReviewBinding>(
    R.layout.fragment_review
) {

    private val viewModel: ReviewViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        initCollector()
    }

    private fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.reviewDTOEvent.collectLatest { result ->
                if (result is UCMCResult.Error) {
                    when (result.e) {
                        is DuplicatedItemException -> {
                            sendSnackBar(getString(R.string.review_error_firestore_review_duplicated))
                        }
                        is FirestoreException -> {
                            sendSnackBar(getString(R.string.review_error_firestore_load_reservation))
                        }
                    }
                    findNavController().popBackStack()
                }
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.addReviewEvent.collectLatest { result ->
                when (result) {
                    is UCMCResult.Error -> {
                        sendSnackBar(
                            message = getString(R.string.review_error_firestore_review_apply),
                            anchorView = binding.btnReviewApply
                        )
                    }
                    is UCMCResult.Success -> {
                        sendSnackBar(getString(R.string.review_apply_success))
                        findNavController().navigate(ReviewFragmentDirections.actionReviewFragmentToMapFragment())
                    }
                }
            }
        }
    }
}
