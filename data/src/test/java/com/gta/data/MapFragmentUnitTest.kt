package com.gta.data

import com.gta.data.model.Meta
import com.gta.data.model.SearchResult
import com.gta.data.repository.MapRepositoryImpl
import com.gta.data.source.MapDataSource
import com.gta.domain.model.UCMCResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class MapFragmentUnitTest(
    @Mock private val mapDataSource: MapDataSource
) {
    private val repository = MapRepositoryImpl(mapDataSource)

    private val GOOD_QUERY = "good"
    private val BAD_QUERY = "bad"

    @BeforeEach
    fun init() {
        runBlocking {
            `when`(mapDataSource.getSearchAddressList(GOOD_QUERY)).thenReturn(
                SearchResult(
                    meta = Meta(0, 0, true),
                    documents = emptyList()
                )
            )
            `when`(mapDataSource.getSearchKeywordList(GOOD_QUERY)).thenReturn(
                SearchResult(
                    meta = Meta(0, 0, true),
                    documents = emptyList()
                )
            )
            `when`(mapDataSource.getSearchAddressList(BAD_QUERY)).thenThrow(
                NullPointerException()
            )
            `when`(mapDataSource.getSearchKeywordList(BAD_QUERY)).thenThrow(
                NullPointerException()
            )
        }
    }

    @Test
    @DisplayName("getSearchAddressList : 검색어를 받으면 Success(List<LocationInfo>)를 리턴한다.")
    fun Should_Success_When_getSearchAddressList_withQuery() {
        runBlocking {
            val result = repository.getSearchAddressList(GOOD_QUERY).first()
            Assertions.assertTrue(result is UCMCResult.Success)
        }
    }

    @Test
    @DisplayName("getSearchAddressList : 검색어를 받으면 Error(Exception)를 리턴한다.")
    fun Should_Exception_When_getSearchAddressList_withBadQuery() {
        runBlocking {
            val result = repository.getSearchAddressList(BAD_QUERY).first()
            Assertions.assertTrue(result is UCMCResult.Error)
        }
    }
}
