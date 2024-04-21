package com.esh7enly.esh7enlyuser

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Language


import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApp : Application()