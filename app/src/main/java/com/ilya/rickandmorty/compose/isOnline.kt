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
import java.net.HttpURLConnection
import java.net.URL

suspend fun hasInternetAccess(): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("https://rickandmortyapi.com/api/")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 1500
            connection.readTimeout = 1500
            connection.connect()
            connection.responseCode in 200..399
        } catch (e: Exception) {
            false
        }
    }
}

