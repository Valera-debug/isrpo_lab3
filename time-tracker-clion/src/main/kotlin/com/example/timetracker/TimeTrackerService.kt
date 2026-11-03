package com.example.timetracker

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros

@Service(Service.Level.PROJECT)
@State(name = "TimeTrackerState", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class TimeTrackerService : PersistentStateComponent<TimeTrackerService.State>, Disposable {

    class State {
        var totalSeconds: Long = 0
        var builds: Int = 0
        var runs: Int = 0
    }

    private var state = State()

    @Volatile
    private var lastActivityMillis: Long = System.currentTimeMillis()

    @Volatile
    var lastAction: String = "-"

    override fun getState(): State = state

    override fun loadState(loaded: State) {
        state = loaded
    }

    val totalSeconds: Long get() = state.totalSeconds
    val builds: Int get() = state.builds
    val runs: Int get() = state.runs

    @Synchronized fun addSecond() { state.totalSeconds++ }
    @Synchronized fun onBuild() { state.builds++ }
    @Synchronized fun onRun() { state.runs++ }

    @Synchronized
    fun reset() {
        state.totalSeconds = 0
        state.builds = 0
        state.runs = 0
    }

    fun touch() {
        lastActivityMillis = System.currentTimeMillis()
    }

    fun isUserActive(): Boolean =
        System.currentTimeMillis() - lastActivityMillis < IDLE_TIMEOUT_MILLIS

    fun asDisposable(): Disposable = this

    override fun dispose() {}

    companion object {
        private const val IDLE_TIMEOUT_MILLIS = 2 * 60 * 1000L
    }
}