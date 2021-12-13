package com.chatmen.c_men.feature_members.domain.model

import chatmen.cmen.MemberEntity

data class Member(
    val username: String,
    val name: String,
    val profilePictureUrl: String?,
    val bio: String?,
)

fun MemberEntity.toMember(): Member = Member(
    username = username,
    name = name,
    profilePictureUrl = profilePictureUrl,
    bio = bio
)