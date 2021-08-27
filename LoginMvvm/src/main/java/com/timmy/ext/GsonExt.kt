package com.timmy.ext

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun Any.toJson() = Gson().toJson(this)

inline fun <reified T> String.toDataBean(param: T) = Gson().fromJson<T>(this, object : TypeToken<T>() {}.type)