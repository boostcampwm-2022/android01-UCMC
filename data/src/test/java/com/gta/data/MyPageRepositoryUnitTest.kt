package com.gta.data

import com.gta.data.repository.MyPageRepositoryImpl
import com.gta.data.source.MyPageDataSource
import com.gta.data.source.StorageDataSource
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.MyPageRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq

@ExtendWith(MockitoExtension::class)
class MyPageRepositoryUnitTest(
    @Mock private val myPageDataSource: MyPageDataSource,
    @Mock private val storageDataSource: StorageDataSource
) {

    private val repository: MyPageRepository = MyPageRepositoryImpl(myPageDataSource, storageDataSource)

    @BeforeEach
    fun init() {
        `when`(storageDataSource.uploadPicture(anyString(), eq(GOOD_URI))).thenReturn(
            flow { emit(GOOD_UID) }
        )
        `when`(storageDataSource.uploadPicture(anyString(), eq(BAD_URI))).thenReturn(
            flow { emit(null) }
        )
        `when`(storageDataSource.uploadPicture("users/$BAD_UID/thumbnail", GOOD_URI)).thenReturn(
            flow { emit(null) }
        )
        `when`(myPageDataSource.setThumbnail(anyString(), anyString())).thenReturn(
            flow { emit(true) }
        )
    }

    @Test
    @DisplayName("setThumbnail : 유효한 uid와 uri를 받으면 Success(uri)를 반환한다.")
    fun Should_Success_When_GooduidAnduri() {
        runBlocking {
            val result = repository.setThumbnail(GOOD_UID, GOOD_URI)
            Assertions.assertEquals(UCMCResult.Success(GOOD_URI), result)
        }
    }

    @Test
    @DisplayName("setThumbnail : 유효하지 않은 uid를 받으면 Error(FirestoreException)를 반환한다.")
    fun Should_FirestoreException_When_Baduid() {
        runBlocking {
            val result = repository.setThumbnail(BAD_UID, GOOD_URI)
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    @Test
    @DisplayName("setThumbnail : 유효하지 않은 uri를 받으면 Error(FirestoreException)를 반환한다.")
    fun Should_FirestoreException_When_Baduri() {
        runBlocking {
            val result = repository.setThumbnail(GOOD_UID, BAD_URI)
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    @Test
    @DisplayName("setThumbnail : 썸네일 업데이트에 실패하면 Error(FirestoreException)를 반환한다.")
    fun Should_FirestoreException_When_queryFailed() {
        `when`(myPageDataSource.setThumbnail(anyString(), anyString())).thenReturn(
            flow { emit(false) }
        )
        runBlocking {
            val result = repository.setThumbnail(GOOD_UID, GOOD_URI)
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }
}
