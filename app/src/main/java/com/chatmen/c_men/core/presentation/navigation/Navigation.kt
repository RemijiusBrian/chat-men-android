package com.chatmen.c_men.core.presentation.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.chatmen.c_men.feature_auth.presentation.login.LoginViewModel
import com.chatmen.c_men.feature_auth.presentation.login.ui.Login
import com.chatmen.c_men.feature_auth.presentation.splash_screen.SplashScreenViewModel
import com.chatmen.c_men.feature_auth.presentation.splash_screen.ui.SplashScreen
import com.chatmen.c_men.feature_chat.presentation.chats_list.ChatViewModel
import com.chatmen.c_men.feature_chat.presentation.chats_list.ui.Chats
import com.chatmen.c_men.feature_chat.presentation.messages.MessagesViewModel
import com.chatmen.c_men.feature_chat.presentation.messages.ui.Messages
import com.chatmen.c_men.feature_members.presentation.members_list.MembersViewModel
import com.chatmen.c_men.feature_members.presentation.members_list.ui.Members
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun Navigation(navController: NavHostController) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Destination.SplashScreen.route
    ) {

        // Auth Destinations
        splashDestination(navController)
        loginDestination(navController)

        // Chat Destinations
        chatsDestination(navController)
        messagesDestination(navController)

        // Member Destinations
        membersDestination(navController)

    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.splashDestination(navController: NavHostController) {
    composable(Destination.SplashScreen.route) {
        val viewModel: SplashScreenViewModel = hiltViewModel()

        SplashScreen(
            eventFlow = viewModel.events,
            navigate = { destination ->
                navController.navigate(destination) { popUpTo(destination) }
            }
        )
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.loginDestination(navController: NavHostController) {
    composable(Destination.Login.route) {
        val viewModel: LoginViewModel = hiltViewModel()

        Login(
            isLoading = viewModel.isLoading.value,
            usernameState = viewModel.usernameState.value,
            passwordState = viewModel.passwordState.value,
            onEvent = viewModel::onEvent,
            eventsFlow = viewModel.events,
            navigate = navController::navigate
        )
    }
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
private fun NavGraphBuilder.chatsDestination(navController: NavHostController) {
    composable(
        route = Destination.Chats.route,
        exitTransition = { NavAnimations.slideOutToLeftWithFadeOut() },
        popEnterTransition = { NavAnimations.slideInFromLeftWithFadeIn() }
    ) {
        val viewModel: ChatViewModel = hiltViewModel()

        Chats(
            state = viewModel.state.value,
            onEvent = viewModel::onEvent,
            eventsFlow = viewModel.events,
            navigate = navController::navigate
        )
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
private fun NavGraphBuilder.membersDestination(navController: NavHostController) {
    composable(
        route = Destination.Members.route,
        enterTransition = { NavAnimations.slideInFromRightWithFadeIn() },
        popExitTransition = { NavAnimations.slideOutToRightWithFadeOut() }
    ) {
        val viewModel: MembersViewModel = hiltViewModel()

        Members(
            state = viewModel.state.value,
            onEvent = viewModel::onEvent,
            eventsFlow = viewModel.events,
            navigate = navController::navigate,
            navigateUp = navController::popBackStack
        )
    }
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
private fun NavGraphBuilder.messagesDestination(navController: NavHostController) {
    composable(
        route = Destination.Messages.route + "/{${NavArgs.CHAT_ID}}/{${NavArgs.CHAT_NAME}}",
        arguments = Destination.Messages.arguments,
        enterTransition = { NavAnimations.slideInFromRightWithFadeIn() },
        popExitTransition = { NavAnimations.slideOutToRightWithFadeOut() }
    ) { navBackStackEntry ->
        val viewModel: MessagesViewModel = hiltViewModel()
        val messages by viewModel.messages.collectAsState(initial = emptyList())
        val chatName = navBackStackEntry.arguments?.getString(NavArgs.CHAT_NAME)
            ?: stringResource(id = Destination.Messages.titleRes)

        Messages(
            state = viewModel.state.value,
            messages = messages,
            chatName = chatName,
            messageInput = viewModel.messageInputState.value,
            onEvent = viewModel::onEvent,
            navigateUp = navController::popBackStack
        )
    }
}