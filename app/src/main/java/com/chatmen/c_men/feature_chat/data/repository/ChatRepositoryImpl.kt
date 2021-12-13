package com.chatmen.c_men.feature_chat.data.repository

import com.chatmen.c_men.core.data.util.Resource
import com.chatmen.c_men.core.data.util.Response
import com.chatmen.c_men.core.data.util.SimpleResponse
import com.chatmen.c_men.core.data.util.networkBoundResource
import com.chatmen.c_men.core.presentation.util.UiText
import com.chatmen.c_men.feature_chat.data.local.ChatDataSource
import com.chatmen.c_men.feature_chat.data.remote.ChatService
import com.chatmen.c_men.feature_chat.data.remote.request.CreateChatRequest
import com.chatmen.c_men.feature_chat.domain.model.Chat
import com.chatmen.c_men.feature_chat.domain.model.toChat
import com.chatmen.c_men.feature_chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(
    private val dataSource: ChatDataSource,
    private val service: ChatService
) : ChatRepository {

    override fun getAllChats(refresh: Boolean): Flow<Resource<List<Chat>>> = networkBoundResource(
        query = {
            dataSource.getAllChats().map { entities ->
                entities.map { it.toChat() }
            }
        },
        fetch = { service.getAllChats() },
        saveFetchResult = { result ->
            result.forEach { chatDto ->
                dataSource.insert(
                    id = chatDto.chatId,
                    name = chatDto.name,
                    description = chatDto.description,
                    timestamp = chatDto.timestamp,
                    chatIconUrl = chatDto.chatIconUrl,
                    lastMessage = chatDto.lastMessage
                )
            }
        },
        shouldFetch = { it.isNullOrEmpty() || refresh }
    )

    override suspend fun createChat(createChatRequest: CreateChatRequest): SimpleResponse {
        val response = service.createChat(createChatRequest)

        return if (response.successful) {
            response.data?.let { chatDto ->
                dataSource.insert(
                    id = chatDto.chatId,
                    name = chatDto.name,
                    description = chatDto.description,
                    timestamp = chatDto.timestamp,
                    chatIconUrl = chatDto.chatIconUrl,
                    lastMessage = chatDto.lastMessage
                )
            }
            Response.Success(Unit)
        } else {
            Response.Error(
                response.message?.let { UiText.Dynamic(it) }
                    ?: UiText.unknownError()
            )
        }
    }

    override suspend fun updateLastMessageForChat(id: String, lastMessage: String) {
        dataSource.updateLastMessageForChat(lastMessage, id)
    }

    override suspend fun deleteChatById(id: String) {
        dataSource.deleteById(id)
    }

    override suspend fun deleteAll() {
        dataSource.deleteAll()
    }
}