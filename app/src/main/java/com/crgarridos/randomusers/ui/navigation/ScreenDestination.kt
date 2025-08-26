package com.crgarridos.randomusers.ui.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument


object AppDestinations {

    object UserList {
        const val ROUTE = "userList"
    }

    object UserDetail {
        private const val ROUTE_USER_DETAIL_BASE = "userDetail"
        const val ARG_USER_ID = "userId"
        const val ROUTE = "$ROUTE_USER_DETAIL_BASE/{$ARG_USER_ID}"

        fun createRouteWithArgs(userId: String): String {
            return "$ROUTE_USER_DETAIL_BASE/$userId"
        }

        fun navArguments(): List<NamedNavArgument> {
            return listOf(navArgument(ARG_USER_ID) { type = NavType.StringType })
        }

        fun SavedStateHandle.getUserId(): String {
            return requireNotNull(get<String>(ARG_USER_ID))
        }

    }
}