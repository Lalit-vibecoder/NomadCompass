package com.example.nomadcompass.data.repository

import com.example.nomadcompass.data.local.dao.UserProfileDao
import com.example.nomadcompass.data.local.entity.UserProfileEntity
import com.example.nomadcompass.domain.model.UserProfile
import com.example.nomadcompass.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: UserProfileDao,
) : ProfileRepository {

    override fun getProfile(): Flow<UserProfile?> =
        profileDao.getProfile().map { entity ->
            entity?.let {
                UserProfile(
                    userName = it.userName,
                    homeCountryCca3 = it.homeCountryCca3,
                    baseCurrencyCode = it.baseCurrencyCode,
                    tempUnit = it.tempUnit,
                    photoUri = it.photoUri,
                    isBiometricEnabled = it.isBiometricEnabled,
                    accessCode = it.accessCode,
                    themeMode = it.themeMode,
                    bgPhotoUri = it.bgPhotoUri,
                    bgBlurRadius = it.bgBlurRadius,
                )
            }
        }

    override suspend fun saveProfile(profile: UserProfile) {
        profileDao.save(
            UserProfileEntity(
                id = 1,
                userName = profile.userName,
                homeCountryCca3 = profile.homeCountryCca3,
                baseCurrencyCode = profile.baseCurrencyCode,
                tempUnit = profile.tempUnit,
                photoUri = profile.photoUri,
                isBiometricEnabled = profile.isBiometricEnabled,
                accessCode = profile.accessCode,
                themeMode = profile.themeMode,
                bgPhotoUri = profile.bgPhotoUri,
                bgBlurRadius = profile.bgBlurRadius,
            )
        )
    }

    override suspend fun hasProfile(): Boolean {
        return profileDao.count() > 0
    }
}
