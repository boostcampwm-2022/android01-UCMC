package com.gta.presentation.ui.mypage

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.gta.domain.model.UCMCResult
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentMypageBinding
import com.gta.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMypageBinding>(
    R.layout.fragment_mypage
) {
    private val viewModel: MyPageViewModel by viewModels()

    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.updateThumbnail(result.data?.data.toString())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        initCollector()
        initListener()
    }

    private fun initListener() {
        binding.ivMypageEditThumb.setOnClickListener {
            updateThumbnail()
        }
        binding.ivMypageEditNickname.setOnClickListener {
            viewModel.navigateNicknameEdit()
        }
        binding.btnMypageSignOut.setOnClickListener {
            viewModel.signOut()
        }
        binding.btnMypageCar.setOnClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_myPageCarListFragment)
        }
        binding.btnMypageTerms.setOnClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_myPageTermsFragment)
        }
        binding.btnMypageLicense.setOnClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_myPageLicenseFragment)
        }
        binding.btnMypageBuyHistory.setOnClickListener {
            findNavController().navigate(MyPageFragmentDirections.actionMyPageFragmentToTransactionListFragment(getString(R.string.mypage_btn_transaction_buy)))
        }
        binding.btnMypageSellHistory.setOnClickListener {
            findNavController().navigate(MyPageFragmentDirections.actionMyPageFragmentToTransactionListFragment(getString(R.string.mypage_btn_transaction_sell)))
        }
    }

    private fun initCollector() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.thumbnailUpdateEvent.collectLatest { result ->
                    if (result is UCMCResult.Error) {
                        sendSnackBar(getString(R.string.mypage_error_firestore))
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.nicknameEditEvent.collectLatest { thumb ->
                    findNavController().navigate(
                        MyPageFragmentDirections.actionMyPageFragmentToNicknameFragment(thumb)
                    )
                }
            }
        }
    }

    private fun updateThumbnail() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
        }
        imageResult.launch(intent)
    }
}
