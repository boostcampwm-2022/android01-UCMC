package com.gta.presentation.ui.chatting.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentChattingListBinding
import com.gta.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory
import javax.inject.Inject

@AndroidEntryPoint
class ChattingListFragment : BaseFragment<FragmentChattingListBinding>(
    R.layout.fragment_chatting_list
) {
    // 채팅 작업이 필요할 땐 얘를 부르면 됩니다
    @Inject
    lateinit var chatClient: ChatClient

    private val channelListViewModel: ChannelListViewModel by viewModels {
        ChannelListViewModelFactory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        channelListViewModel.bindView(binding.clChattingList, viewLifecycleOwner)
        binding.clChattingList.setChannelItemClickListener { channel ->
            findNavController().navigate(
                ChattingListFragmentDirections.actionChattingListFragmentToChattingFragment(channel.cid)
            )
        }
    }

    // 채팅 채널을 만드는 메쏘드에요
    // 이 화면에서는 필요하지 않지만 예약 완료 화면에서 이 메쏘드를 참고해 채팅방을 생성해야 하기 때문에 냅뒀어요
    // channelId는 대여자uid-차id 에요
    // 채널을 만들기 전에 중복 확인을 위한 queryChannel을 먼저 돌릴 필요가 있어요
/*    private fun createChannel() {
        chatClient.createChannel(
            channelType = "messaging",
            channelId = "${user.id}-차id",
            memberIds = listOf(user.id, "상대방uid"),
            extraData = emptyMap()
        ).enqueue() { result ->
            if (result.isError) {
                Timber.tag("chatting").i(result.error().message)
            }
        }
    }*/

    // 채널을 만들기 전에 중복 확인을 위한 queryChannel을 먼저 돌릴 필요가 있어요
    // 이미 채팅방이 있다면 만들어선 안되겠죠..?!
/*    private fun checkChannel() {
        chatClient.queryChannel(
            "messaging",
            "${user.id}-차id",
            QueryChannelRequest()
        ).enqueue { result ->
            if (result.isSuccess) {
                // 쿼리에 성공했다면 채널id를 받을 수 있어요
                // 이 채널id를 들고 채팅 화면으로 넘어가면 돼요
                Timber.tag("chatting").i(result.data().cid)
            } else {
                // 채널이 없다면 여기로 와요
                // 이럴 땐 쿼리에 쓴 채널Id로 새로운 채널을 만들어 봐요
                Timber.tag("chatting").i(result.error().message)
            }
        }
    }*/
}
