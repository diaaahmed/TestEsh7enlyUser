package com.esh7enly.esh7enlyuser.connectivity

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@Singleton
class InternetAvailabilityRepository @Inject constructor(
    networkStatusTracker: NetworkStatusTracker
) {
    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected

    init {
        CoroutineScope(Dispatchers.Default).launch {
            networkStatusTracker.networkStatus.collect {
                _isConnected.value = it
            }
        }
    }
}