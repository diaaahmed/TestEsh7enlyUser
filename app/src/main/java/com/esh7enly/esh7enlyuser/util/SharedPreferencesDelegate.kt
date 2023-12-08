package com.esh7enly.esh7enlyuser.util

import android.content.Context
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SharedPreferencesDelegate(
    private val context:Context,
    private val name:String,
    private val defaultValue:String = "",
):ReadWriteProperty<Any?,String> {

    private val sharedPreferences by lazy{
        context.getSharedPreferences("my_data",Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return sharedPreferences.getString(name,defaultValue) ?: defaultValue
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        sharedPreferences.edit().putString(name,value).apply()
    }
}

fun Context.sharedPreferences(name: String) = SharedPreferencesDelegate(this,name)