package com.gta.presentation.ui.chatting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentChattingBinding
import com.gta.presentation.ui.base.BaseFragment

class ChattingFragment : BaseFragment<FragmentChattingBinding>(R.layout.fragment_chatting) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }
}
