package com.gta.presentation.util

import com.gta.presentation.util.EventFlow.Companion.DEFAULT_REPLAY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.atomic.AtomicBoolean

/*
    collect가 닫혀있을때 이벤트가 들어와서 휘발되는 것을 막아야합니다.
    아직 소비되지 않은 이벤트를 캐싱하는 이벤트 플로우 입니다.
*/
interface EventFlow<out T> : Flow<T> {
    companion object {
        const val DEFAULT_REPLAY = 1
    }
}

// MutableEventFlow는 Emit을 할 수 있는 FlowCollector를 상속받습니다.
interface MutableEventFlow<T> : EventFlow<T>, FlowCollector<T>

// collect에 들어가서 소비가 잘 됐는지 확인하기 위한 consumed 변수가 정의됐습니다.
private class EventFlowSlot<T>(val value: T) {
    private val consumed = AtomicBoolean(false)
    // markConsumed가 호출되면 consumed를 리턴한 다음 consumed를 true로 바꿔줍니다.
    fun markConsumed() = consumed.getAndSet(true)
}

// emit 할 수가 없는 ReadOnlyFlow를 정의합니다.
private class ReadOnlyEventFlow<T>(flow: EventFlow<T>) : EventFlow<T> by flow

private class EventFlowImpl<T>(replay: Int) : MutableEventFlow<T> {

    // replay 만큼 이전에 emit한 값을 가지고 있습니다.
    private val flow: MutableSharedFlow<EventFlowSlot<T>> = MutableSharedFlow(replay)

    // 새로운 collect가 연결되면 flow는 이전 값을 방출 합니다.
    // 이전 값이 collect가 열려 있는 상태에서 들어왔었다면 consumed는 markConsumed()에 의해 true가 됐기 때문에 이번 방출에서 emit 되지 않습니다.
    // 이전 값이 collect가 닫혀 있는 상태에서 들어왔었다면 consumed는 초기값 false 그대로기 때문에 이번 방출에서 emit 됩니다.
    override suspend fun collect(collector: FlowCollector<T>) =
        flow.collect { slot ->
            // 한 번 markConsumed가 호출된 slot은 다음 번 방출에서는 값을 emit하지 못합니다.
            if (!slot.markConsumed()) {
                collector.emit(slot.value)
            }
        }

    // EventFlowSlot의 consumed를 이용해야 하기 때문에 value는 EventFlowSlot에 감싸서 넣어줍니다.
    override suspend fun emit(value: T) {
        flow.emit(EventFlowSlot(value))
    }
}

// 사용할 MutableEventFlow와 EventFlow를 메소드 형태로 정의 해줍니다.
@Suppress("FunctionName")
fun <T> MutableEventFlow(replay: Int = DEFAULT_REPLAY): MutableEventFlow<T> = EventFlowImpl(replay)

fun <T> MutableEventFlow<T>.asEventFlow(): EventFlow<T> = ReadOnlyEventFlow(this)
