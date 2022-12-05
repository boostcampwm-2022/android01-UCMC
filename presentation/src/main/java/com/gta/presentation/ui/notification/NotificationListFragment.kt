package com.gta.presentation.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gta.domain.model.NotificationType
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentNotificationListBinding
import com.gta.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class NotificationListFragment : BaseFragment<FragmentNotificationListBinding>(
    R.layout.fragment_notification_list
) {

    private val viewModel: NotificationViewModel by viewModels()
    private val adapter by lazy { NotificationListAdapter() }
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
                        NotificationType.REQUEST_RESERVATION -> {
                            // TODO 수락/거절 이동(RESERVASTON ID)
                        }
                        NotificationType.ACCEPT_RESERVATION -> {
                        }
                        NotificationType.REJECT_RESERVATION -> {
                        }
                        NotificationType.RETURN_CAR -> {
                            NotificationListFragmentDirections.actionNotificationListFragmentToReviewFragment(
                                reservation
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

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notificationList.collectLatest {
                    Timber.d("fragment $it")
                    adapter.submitData(it)
                }
            }
        }
    }
}
