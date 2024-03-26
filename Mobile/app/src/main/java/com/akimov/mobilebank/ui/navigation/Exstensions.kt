package com.akimov.mobilebank.ui.navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

fun NavController.navigateWithArguments(
    route: String,
    arguments: Bundle,
    navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}
) {
    currentBackStackEntry?.arguments?.putAll(arguments)
    navigate(route, navOptionsBuilder)
}