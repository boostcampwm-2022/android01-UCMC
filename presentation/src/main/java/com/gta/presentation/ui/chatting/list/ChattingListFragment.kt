package com.gta.presentation.ui.chatting.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentChattingListBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.FirebaseUtil
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelRequest
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory
import timber.log.Timber
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

    /*
     name과 image는 유저의 닉네임과 썸네일이에요
     하지만 현재 상황에서는 쿼리를 통해서 name, image를 가져와야 하므로 User객체를 즉시 초기화 할 수 없어요
     그렇기 때문이 일단 고정값으로만 설정해뒀어요
     사용자의 UserInfo를 지속적으로 감시하면서 UserProfile 객체를 메모리에 저장 해놓는 작업이 필요해요
    */
    private val user by lazy {
        User(
            id = FirebaseUtil.uid,
            name = "이동훈",
            image = "https://firebasestorage.googleapis.com/v0/b/boostcamp-ucmc.appspot.com/o/thumb%2F1669079674202667835770?alt=media&token=25c8ec38-32c8-4bd8-aad1-58e26e0c1b61"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 유저를 연결하기 위해선 User객체와 token이 필요해요
        // Stream의 토큰은 기본적으로 무기한이기 때문에 만료걱정을 안해도 돼요
        // 성공적으로 연결됐다면 채팅리스트 뷰모델에 채팅리스트 뷰를 바인딩 해줘요
        // 뷰가 바인딩 되면 내가 속해 있는 채팅 목록을 확인할 수 있어요
        chatClient.connectUser(
            user = user,
            token = chatClient.devToken(user.id)
        ).enqueue { result ->
            if (result.isSuccess) {
                channelListViewModel.bindView(binding.clChattingList, viewLifecycleOwner)
            } else {
                Timber.tag("chatting").i(result.error().message)
            }
        }
        // 채팅방이 선택 됐을 때 호출되는 리쓰너에요
        // 해당 채팅방의 id를 들고 채팅 화면으로 넘어갑니다.
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
    private fun createChannel() {
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
    }

    // 채널을 만들기 전에 중복 확인을 위한 queryChannel을 먼저 돌릴 필요가 있어요
    // 이미 채팅방이 있다면 만들어선 안되겠죠..?!
    private fun checkChannel() {
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
    }
}
