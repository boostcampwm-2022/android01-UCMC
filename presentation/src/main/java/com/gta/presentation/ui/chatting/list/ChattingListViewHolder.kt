package com.gta.presentation.ui.chatting.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.getstream.sdk.chat.utils.formatDate
import com.gta.domain.usecase.car.GetSimpleCarUseCase
import com.gta.presentation.databinding.ItemChattingListBinding
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.extensions.internal.lastMessage
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.core.internal.InternalStreamChatApi
import io.getstream.chat.android.ui.ChatUI
import io.getstream.chat.android.ui.channel.list.ChannelListView
import io.getstream.chat.android.ui.channel.list.adapter.ChannelListPayloadDiff
import io.getstream.chat.android.ui.channel.list.adapter.viewholder.BaseChannelListItemViewHolder
import io.getstream.chat.android.ui.channel.list.adapter.viewholder.ChannelListItemViewHolderFactory
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChattingListItemViewHolderFactory(
    private val job: CompletableJob,
    private val getSimpleCarUseCase: GetSimpleCarUseCase
) : ChannelListItemViewHolderFactory() {
    override fun createChannelViewHolder(parentView: ViewGroup): BaseChannelListItemViewHolder {
        return ChattingListViewHolder(
            parentView,
            job,
            getSimpleCarUseCase,
            listenerContainer.channelClickListener,
            listenerContainer.channelLongClickListener
        )
    }
}

class ChattingListViewHolder(
    parent: ViewGroup,
    private val job: CompletableJob,
    private val getSimpleCarUseCase: GetSimpleCarUseCase,
    private val channelClickListener: ChannelListView.ChannelClickListener,
    private val channelLongClickListener: ChannelListView.ChannelLongClickListener,
    private val binding: ItemChattingListBinding = ItemChattingListBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    )
) : BaseChannelListItemViewHolder(binding.root) {

    private lateinit var channel: Channel

    init {
        binding.root.run {
            setOnClickListener { channelClickListener.onClick(channel) }
            setOnLongClickListener { channelLongClickListener.onLongClick(channel) }
        }
    }

    @OptIn(InternalStreamChatApi::class)
    override fun bind(channel: Channel, diff: ChannelListPayloadDiff) {
        this.channel = channel
        val carId = channel.id.split("-").last()
        CoroutineScope(Dispatchers.IO + job).launch {
            binding.carImage = getSimpleCarUseCase(carId).first().image
        }
        binding.ivChannelListThumb.setChannelData(channel)
        binding.tvChannelListName.text = ChatUI.channelNameFormatter.formatChannelName(
            channel = channel,
            currentUser = ChatClient.instance().getCurrentUser()
        )
        binding.tvChannelListTime.text = ChatUI.dateFormatter.formatDate(channel.lastMessageAt)
        val message = channel.lastMessage ?: return
        binding.tvChannelListMessage.text = ChatUI.messagePreviewFormatter.formatMessagePreview(
            channel = channel,
            message = message,
            currentUser = null
        )
    }
}
