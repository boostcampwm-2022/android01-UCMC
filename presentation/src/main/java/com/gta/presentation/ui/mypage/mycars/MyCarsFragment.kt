package com.gta.presentation.ui.mypage.mycars

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.domain.model.DeleteFailException
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UserNotFoundException
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentMyCarsBinding
import com.gta.presentation.ui.MainActivity
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

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
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.userCarEvent.collectLatest { result ->
                when (result) {
                    is UCMCResult.Success ->
                        adapter.submitList(result.data)
                    is UCMCResult.Error ->
                        handleErrorMessage(result.e)
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.deleteEvent.collectLatest { result ->
                when (result) {
                    is UCMCResult.Success -> {
                        viewModel.getCarList()
                    }
                    is UCMCResult.Error -> {
                        handleErrorMessage(result.e)
                    }
                }
            }
        }
    }

    private fun setupWithAdapterClickListener() {
        adapter.setOnItemClickListener(object : OnItemClickListener<String> {
            override fun onClick(value: String) {
                val navAction =
                    MyCarsFragmentDirections.actionMyPageCarListFragmentToCarDetailFragment(value)
                findNavController().navigate(navAction)
            }

            override fun onLongClick(v: View, value: String) {
                val popup = PopupMenu(requireContext(), v)
                requireActivity().menuInflater.inflate(R.menu.menu_mycars_item_click, popup.menu)

                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.popup_detail -> {
                            val navAction =
                                MyCarsFragmentDirections.actionMyPageCarListFragmentToCarDetailFragment(
                                    value
                                )
                            findNavController().navigate(navAction)
                        }
                        R.id.popup_delete -> {
                            viewModel.deleteCar(value)
                        }
                    }
                    return@setOnMenuItemClickListener false
                }
                popup.show()
            }
        })
    }

    private fun handleErrorMessage(e: Exception) {
        when (e) {
            is FirestoreException -> sendSnackBar(resources.getString(R.string.exception_car_list_fail))
            is DeleteFailException -> sendSnackBar(resources.getString(R.string.exception_delete_fail))
            is UserNotFoundException -> sendSnackBar(resources.getString(R.string.exception_user_not_found))
            else -> sendSnackBar(e.message)
        }
    }
}
