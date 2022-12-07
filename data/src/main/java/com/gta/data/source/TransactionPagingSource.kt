package com.gta.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.gta.domain.model.Reservation
import com.gta.domain.model.SimpleReservation
import com.gta.domain.model.toSimpleReservation
import kotlin.reflect.KSuspendFunction1
import kotlin.reflect.KSuspendFunction2

class TransactionPagingSource(
    private val userId: String,
    isLender: Boolean,
    private val dataSource: TransactionDataSource
) : PagingSource<QuerySnapshot, SimpleReservation>() {

    private val getTransactions: KSuspendFunction1<String, QuerySnapshot> =
        if (isLender) dataSource::getYourCarTransactions else dataSource::getMyCarTransactions

    private val getTransactionsNext: KSuspendFunction2<String, DocumentSnapshot, QuerySnapshot> =
        if (isLender) dataSource::getYourCarTransactionsFromCursor else dataSource::getMyCarTransactionsFromCursor

    override fun getRefreshKey(state: PagingState<QuerySnapshot, SimpleReservation>): QuerySnapshot? =
        null

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, SimpleReservation> {
        return try {
            val currentPage = params.key ?: getTransactions(userId)
            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]
            val nextPage = getTransactionsNext(userId, lastDocumentSnapshot)

            LoadResult.Page(
                data = currentPage.map { snapshot ->
                    snapshot.toObject(Reservation::class.java).toSimpleReservation(snapshot.id)
                },
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}