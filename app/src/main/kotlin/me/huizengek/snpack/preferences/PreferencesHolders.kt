package me.huizengek.snpack.preferences

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.Snapshot
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private val coroutineScope = CoroutineScope(Dispatchers.IO)

fun <T> sharedPreferencesProperty(
    getValue: SharedPreferences.(key: String) -> T,
    setValue: SharedPreferences.Editor.(key: String, value: T) -> Unit,
    defaultValue: T
) = object : ReadWriteProperty<PreferencesHolder, T> {
    private var state = mutableStateOf(defaultValue)
    private var listener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    override fun getValue(thisRef: PreferencesHolder, property: KProperty<*>): T {
        if (listener == null && !Snapshot.current.readOnly) {
            state.value = thisRef.getValue(property.name)
            listener = SharedPreferences.OnSharedPreferenceChangeListener { preferences, key ->
                if (key == property.name) preferences.getValue(property.name)
                    .let { if (it != state) state.value = it }
            }
            thisRef.registerOnSharedPreferenceChangeListener(listener)
        }
        return state.value
    }

    override fun setValue(thisRef: PreferencesHolder, property: KProperty<*>, value: T) {
        if (state == value || Snapshot.current.readOnly) return
        state.value = value
        coroutineScope.launch {
            thisRef.edit(commit = true) {
                setValue(property.name, value)
            }
        }
    }
}

/**
 * A snapshottable, thread-safe, compose-first, extensible SharedPreferences wrapper that supports
 * virtually all types, and if it doesn't, one could simply type `fun myNewType(...) = sharedPreferencesProperty(...)`
 * and start implementing. Starts off as given defaultValue until we are allowed to subscribe to SharedPreferences
 */
open class PreferencesHolder(
    application: Application,
    name: String,
    mode: Int = Context.MODE_PRIVATE
) : SharedPreferences by application.getSharedPreferences(name, mode) {
    fun boolean(defaultValue: Boolean) = sharedPreferencesProperty(
        getValue = { getBoolean(it, defaultValue) },
        setValue = { k, v -> putBoolean(k, v) },
        defaultValue
    )

    fun string(defaultValue: String?) = sharedPreferencesProperty(
        getValue = { getString(it, null) ?: defaultValue },
        setValue = { k, v -> putString(k, v) },
        defaultValue
    )

    fun int(defaultValue: Int) = sharedPreferencesProperty(
        getValue = { getInt(it, defaultValue) },
        setValue = { k, v -> putInt(k, v) },
        defaultValue
    )

    fun float(defaultValue: Float) = sharedPreferencesProperty(
        getValue = { getFloat(it, defaultValue) },
        setValue = { k, v -> putFloat(k, v) },
        defaultValue
    )

    fun long(defaultValue: Long) = sharedPreferencesProperty(
        getValue = { getLong(it, defaultValue) },
        setValue = { k, v -> putLong(k, v) },
        defaultValue
    )

    inline fun <reified T : Enum<T>> enum(defaultValue: T?) = sharedPreferencesProperty(
        getValue = {
            getString(it, null)?.let { runCatching { enumValueOf<T>(it) }.getOrNull() }
                ?: defaultValue
        },
        setValue = { k, v -> putString(k, v?.name) },
        defaultValue
    )

    fun stringSet(defaultValue: Set<String>) = sharedPreferencesProperty(
        getValue = { getStringSet(it, null) ?: defaultValue },
        setValue = { k, v -> putStringSet(k, v) },
        defaultValue
    )
}