package com.chatmen.c_men.feature_members.data.local

import chatmen.cmen.MemberEntity
import kotlinx.coroutines.flow.Flow

interface MemberDataSource {

    fun getAllMembers(): Flow<List<MemberEntity>>

    suspend fun insert(
        username: String,
        name: String,
        bio: String?,
        profilePictureUrl: String?
    )

    suspend fun deleteAll()
}