package com.gta.presentation.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentNotificationListBinding
import com.gta.presentation.ui.base.BaseFragment

class NotificationListFragment : BaseFragment<FragmentNotificationListBinding>(
    R.layout.fragment_notification_list
) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }
}
