package com.gta.presentation.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.gta.domain.model.NotificationType
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentNotificationListBinding
import com.gta.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationListFragment : BaseFragment<FragmentNotificationListBinding>(
    R.layout.fragment_notification_list
) {

    private val viewModel: NotificationListViewModel by viewModels()
    private val adapter by lazy { NotificationListAdapter() }

    private val pagingListener: (CombinedLoadStates) -> Unit = {
        binding.rvNotification.visibility = View.VISIBLE
        binding.pgLoading.visibility = View.GONE

        when (it.source.refresh) {
            is LoadState.Loading -> {
                binding.pgLoading.visibility = View.VISIBLE
            }
            is LoadState.NotLoading -> {
                if (it.append.endOfPaginationReached && adapter.itemCount < 1) {
                    binding.rvNotification.visibility = View.GONE
                }
            }
            is LoadState.Error -> {
                sendSnackBar(resources.getString(R.string.exception_load_data))
            }
            else -> {}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.rvNotification.adapter = adapter.apply {
            setItemClickListener(object : NotificationListAdapter.OnItemClickListener {
                override fun onClick(type: NotificationType, reservation: String) {
                    when (type) {
                        NotificationType.REQUEST_RESERVATION, NotificationType.ACCEPT_RESERVATION, NotificationType.DECLINE_RESERVATION -> {
                            findNavController().navigate(
                                NotificationListFragmentDirections
                                    .actionNotificationListFragmentToReservationCheckFragment(
                                        reservation
                                    )
                            )
                        }
                        NotificationType.RETURN_CAR -> {
                            findNavController().navigate(
                                NotificationListFragmentDirections
                                    .actionNotificationListFragmentToReviewFragment(
                                        reservation
                                    )
                            )
                        }
                    }
                }
            })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.addLoadStateListener(pagingListener)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notificationList.collectLatest {
                    adapter.submitData(it)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        adapter.removeLoadStateListener(pagingListener)
    }
}
