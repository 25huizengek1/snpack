package me.huizengek.snpack

import android.app.Application
import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import me.huizengek.snpack.preferences.PreferencesHolder
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DependencyGraph.postInit()
    }
}

@GlideModule
class PackGlideModel : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setLogLevel(Log.INFO)
    }
}

private val dependencyScope = CoroutineScope(Dispatchers.Default)

object DependencyGraph {
    private val continuations = mutableListOf<Continuation<Unit>>()
    private var isInitialized = false
    lateinit var application: Application

    context(Application)
    private suspend fun init() {
        if (!isInitialized) {
            application = this@Application
            DatabaseAccessor.init()

            isInitialized = true
        }

        continuations.map { dependencyScope.launch { runCatching { it.resume(Unit) } } }.joinAll()
    }

    context(Application)
    internal fun postInit() = dependencyScope.launch { init() }

    suspend fun awaitInitialization() {
        if (!isInitialized) suspendCoroutine { continuations += it }
    }
}

open class GlobalPreferencesHolder : PreferencesHolder(DependencyGraph.application, "preferences")
