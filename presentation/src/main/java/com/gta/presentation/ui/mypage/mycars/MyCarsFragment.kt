package com.gta.presentation.ui.mypage.mycars

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentMyCarsBinding
import com.gta.presentation.ui.MainActivity
import com.gta.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyCarsFragment : BaseFragment<FragmentMyCarsBinding>(R.layout.fragment_my_cars) {
    private val viewModel: MyCarsViewModel by viewModels()
    private val adapter = MyCarsListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        setupWithActionBar()
        setupWithFloatingActionButton()
        setupWithRecyclerView()
        setupWithAdapterClickListener()
    }

    private fun setupWithActionBar() {
        (requireActivity() as MainActivity).supportActionBar?.apply {
            title = getString(R.string.mypage_carlist)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupWithFloatingActionButton() {
        binding.fab.apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_myPageCarListFragment_to_pinkSlipGuideFragment)
            }
        }
    }

    private fun setupWithRecyclerView() {
        binding.rvCarList.adapter = adapter
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getCarList()
                viewModel.userCarList.collectLatest {
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun setupWithAdapterClickListener() {
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onClick(carId: String) {
                val navAction =
                    MyCarsFragmentDirections.actionMyPageCarListFragmentToCarDetailFragment(carId)
                findNavController().navigate(navAction)
            }

            override fun onLongClick(v: View, carId: String) {
                val popup = PopupMenu(requireContext(), v)
                requireActivity().menuInflater.inflate(R.menu.menu_mycars_item_click, popup.menu)

                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.popup_detail -> {
                            val navAction =
                                MyCarsFragmentDirections.actionMyPageCarListFragmentToCarDetailFragment(
                                    carId
                                )
                            findNavController().navigate(navAction)
                        }
                        R.id.popup_delete -> {
                            viewModel.deleteCar(carId)
                        }
                    }
                    return@setOnMenuItemClickListener false
                }

                popup.show()
            }
        })
    }
}
