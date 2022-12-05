package com.gta.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.QuerySnapshot
import com.gta.data.repository.NotificationRepositoryImpl
import com.gta.domain.model.Notification
import com.gta.domain.model.NotificationInfo
import com.gta.domain.model.toInfo

class NotificationPagingSource(
    private val userId: String,
    private val dataSource: NotificationDataSource,
    private val repository: NotificationRepositoryImpl
) : PagingSource<QuerySnapshot, NotificationInfo>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, NotificationInfo>): QuerySnapshot? =
        null

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, NotificationInfo> {
        return try {
            val currentPage = params.key ?: dataSource.getNotificationInfoCurrentItem(userId)
            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]
            val nextPage = dataSource.getNotificationInfoNextItem(userId, lastDocumentSnapshot)

            LoadResult.Page(
                data = currentPage.map {
                    // repository.getNotificationInfoDetailItem(
                    it.toObject(
                        Notification::class.java
                    ).toInfo(it.id)
                    // )
                },
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
