package com.ilya.rickandmorty.compose

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun hasInternetAccess(): Boolean {
    return try {
        val client = withContext(Dispatchers.IO) { java.net.URL("https://www.google.com").openConnection() }
        client.connectTimeout = 1500
        client.getInputStream().close()
        true
    } catch (e: Exception) {
        false
    }
}
