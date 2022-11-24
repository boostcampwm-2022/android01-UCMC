package com.gta.presentation.ui.cardetail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentCarDetailEditBinding
import com.gta.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CarDetailEditFragment : BaseFragment<FragmentCarDetailEditBinding>(
    R.layout.fragment_car_detail_edit
) {

    private val viewModel: CarDetailEditViewModel by viewModels()
    private val imagesAdapter by lazy { CarEditImagesAdapter() }

    private val maxImagesMsg by lazy {
        Snackbar.make(
            binding.btnDone,
            "이미지는 최대 10장까지 선택 할 수 있어요.",
            Snackbar.LENGTH_SHORT
        )
    }

    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.updateImage(result.data?.data.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.rvImages.adapter = imagesAdapter.apply {
            setItemClickListener(object : CarEditImagesAdapter.OnItemClickListener {
                override fun onClick(position: Int) {
                    viewModel.deleteImage(position)
                }
            })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddImage.setOnClickListener {
            if (viewModel.images.value.size < 10) {
                addImageAtGallery()
            } else {
                maxImagesMsg.show()
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.images.collectLatest {
                    imagesAdapter.submitList(it)
                    binding.rvImages.scrollToPosition(0)
                }
            }
        }
    }

    private fun addImageAtGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
        }
        imageResult.launch(intent)
    }
}
