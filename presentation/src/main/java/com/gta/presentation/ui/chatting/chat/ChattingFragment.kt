package com.gta.presentation.ui.chatting.chat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentChattingBinding
import com.gta.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.ui.message.input.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.factory.MessageListViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ChattingFragment : BaseFragment<FragmentChattingBinding>(R.layout.fragment_chatting) {

    private val args: ChattingFragmentArgs by navArgs()

    private val viewModel: ChattingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCollector()
        initChatting(args.cid)
    }

    private fun initCollector() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.car.collectLatest { car ->
                    binding.inChattingCarSummary.car = car
                }
            }
        }
    }

    private fun initChatting(cid: String) {
        Timber.tag("chatting").i("옴")
        // Stream에서 제공하는 UI를 써먹기 위해선 뷰에 맞는 ViewModel을 만들고 bind하는 작업이 필요해요
        // bind된 뷰들은 viewLifecycleOwner를 통해 죽을 때가 되면 알아서 해제가 돼요
        val factory = MessageListViewModelFactory(cid)
        val messageListViewModel: MessageListViewModel by viewModels { factory }
        val messageInputViewModel: MessageInputViewModel by viewModels { factory }

        messageListViewModel.bindView(binding.mlChatting, viewLifecycleOwner)
        messageInputViewModel.bindView(binding.miChatting, viewLifecycleOwner)

        messageListViewModel.mode.observe(viewLifecycleOwner) { mode ->
            Timber.tag("chatting").i("모드 $mode")
            when (mode) {
                is MessageListViewModel.Mode.Thread -> {
                    messageInputViewModel.setActiveThread(mode.parentMessage)
                }
                is MessageListViewModel.Mode.Normal -> {
                    messageInputViewModel.resetThread()
                }
            }
        }

        // 채팅방의 이벤트를 감시하는 리쓰너에요
        messageListViewModel.state.observe(viewLifecycleOwner) { state ->
            Timber.tag("chatting").i(state.toString())
        }
    }
}
