package com.example.timetracker

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import java.awt.Component
import java.awt.event.MouseEvent
import com.intellij.util.Consumer

class TimeTrackerWidget(private val project: Project) :
    StatusBarWidget, StatusBarWidget.TextPresentation {

    companion object {
        const val ID = "TimeTrackerWidget"
    }

    override fun ID(): String = ID

    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

    override fun install(statusBar: StatusBar) {}

    override fun dispose() {}

    override fun getText(): String {
        val s = project.service<TimeTrackerService>()
        return "⏱ ${formatTime(s.totalSeconds)} | 🔨 ${s.builds} | ▶ ${s.runs}"
    }

    override fun getTooltipText(): String {
        val s = project.service<TimeTrackerService>()
        return "Time: ${formatTime(s.totalSeconds)}, builds: ${s.builds}, runs: ${s.runs}"
    }

    override fun getAlignment(): Float = Component.CENTER_ALIGNMENT

    override fun getClickConsumer(): Consumer<MouseEvent>? = null

    private fun formatTime(totalSeconds: Long): String {
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val sec = totalSeconds % 60
        return "%d:%02d:%02d".format(h, m, sec)
    }
}