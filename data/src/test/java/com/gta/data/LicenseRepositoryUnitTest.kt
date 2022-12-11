package com.gta.data

import android.content.res.Resources.NotFoundException
import com.gta.data.model.UserInfo
import com.gta.data.repository.LicenseRepositoryImpl
import com.gta.data.source.LicenseDataSource
import com.gta.data.source.StorageDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.DrivingLicense
import com.gta.domain.model.ExpiredItemException
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.LicenseRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import java.text.SimpleDateFormat
import java.util.Locale

@ExtendWith(MockitoExtension::class)
class LicenseRepositoryUnitTest(
    @Mock private val userDataSource: UserDataSource,
    @Mock private val licenseDataSource: LicenseDataSource,
    @Mock private val storageDataSource: StorageDataSource
) {

    private val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    private val repository: LicenseRepository =
        LicenseRepositoryImpl(userDataSource, licenseDataSource, storageDataSource)

    @BeforeEach
    fun init() {
        `when`(storageDataSource.uploadPicture(anyString(), anyString())).thenReturn(
            flow { emit("uri") }
        )
        `when`(licenseDataSource.registerLicense(eq(GOOD_UID), any())).thenReturn(
            flow { emit(true) }
        )
        `when`(licenseDataSource.registerLicense(eq(BAD_UID), any())).thenReturn(
            flow { emit(false) }
        )
    }

    @Test
    @DisplayName("getLicenseFromDatabase : 면허증이 있는 사용자의 uid를 받으면 Success(DrivingLicense)를 리턴한다.")
    fun Should_Success_When_getLicenseFromDatabaseWithLicensedUser() {
        `when`(userDataSource.getUser(GOOD_UID)).thenReturn(
            flow { emit(UserInfo(license = DrivingLicense())) }
        )
        runBlocking {
            val result = repository.getLicenseFromDatabase(GOOD_UID)
            Assertions.assertTrue(result is UCMCResult.Success<DrivingLicense>)
        }
    }

    @Test
    @DisplayName("getLicenseFromDatabase : 면허증이 없는 사용자 uid를 받으면 Error(NotFoundException)를 리턴한다.")
    fun Should_Success_When_getLicenseFromDatabaseWithUnlicensedUser() {
        `when`(userDataSource.getUser(GOOD_UID)).thenReturn(
            flow { emit(UserInfo(license = null)) }
        )
        runBlocking {
            val result = repository.getLicenseFromDatabase(GOOD_UID)
            Assertions.assertTrue(result is UCMCResult.Error && result.e is NotFoundException)
        }
    }

    @Test
    @DisplayName("getLicenseFromDatabase : 유효하지 않은 uid를 받으면 Error(FirestoreException)를 리턴한다.")
    fun Should_FirestoreException_When_getLicenseFromDatabaseWithBaduid() {
        `when`(userDataSource.getUser(BAD_UID)).thenReturn(
            flow { emit(null) }
        )
        runBlocking {
            val result = repository.getLicenseFromDatabase(BAD_UID)
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
        System.currentTimeMillis()
    }

    @Test
    @DisplayName("setLicense : 유효한 uid와 만료 시간을 초과하지 않은 DrivingLicense를 받으면 Success(DrivingLicense)를 리턴한다.")
    fun Should_Success_When_setLicenseWithGooduidAndLicense() {
        val license = DrivingLicense(expireDate = getDateAfterNDays(1))
        runBlocking {
            Assertions.assertEquals(UCMCResult.Success(Unit), repository.setLicense(GOOD_UID, license, "uri"))
        }
    }

    @Test
    @DisplayName("setLicense : 만료 시간을 초과한 DrivingLicense를 받으면 Error(ExpiredItemException)를 리턴한다.")
    fun Should_ExpiredItemException_When_setLicenseWithExpiredLicense() {
        val license = DrivingLicense(expireDate = getDateAfterNDays(-1))
        runBlocking {
            val result = repository.setLicense(GOOD_UID, license, "uri")
            Assertions.assertTrue(result is UCMCResult.Error && result.e is ExpiredItemException)
        }
    }

    @Test
    @DisplayName("setLicense : 유효하지 않은 uid를 받으면 Error(FirestoreException)를 리턴한다.")
    fun Should_FirestoreException_When_setLicenseWithBaduid() {
        val license = DrivingLicense(expireDate = getDateAfterNDays(1))
        runBlocking {
            val result = repository.setLicense(BAD_UID, license, "uri")
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    @Test
    @DisplayName("setLicense : 비어있는 uri를 받으면 Error(FirestoreException)를 리턴한다.")
    fun Should_FirestoreException_When_setLicenseWithEmptyuri() {
        `when`(storageDataSource.uploadPicture(anyString(), eq(""))).thenReturn(
            flow { emit("") }
        )
        val license = DrivingLicense(expireDate = getDateAfterNDays(1))
        runBlocking {
            val result = repository.setLicense(GOOD_UID, license, "")
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    private fun getDateAfterNDays(days: Int) =
        dateFormat.format(System.currentTimeMillis() + (24 * 60 * 60 * 1000) * days)
}
