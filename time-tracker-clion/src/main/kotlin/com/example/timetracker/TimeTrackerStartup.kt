package com.example.timetracker

import com.intellij.ide.IdeEventQueue
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.wm.WindowManager
import kotlinx.coroutines.delay

class TimeTrackerStartup : ProjectActivity {

    override suspend fun execute(project: Project) {
        val service = project.service<TimeTrackerService>()


        IdeEventQueue.getInstance().addActivityListener({ service.touch() }, service.asDisposable())


        while (true) {
            delay(1000)
            val frame = WindowManager.getInstance().getFrame(project)
            if (frame != null && frame.isActive && service.isUserActive()) {
                service.addSecond()
            }
            ApplicationManager.getApplication().invokeLater {
                refreshWidget(project)
            }
        }
    }
}

fun refreshWidget(project: Project) {
    if (project.isDisposed) return
    WindowManager.getInstance().getStatusBar(project)?.updateWidget(TimeTrackerWidget.ID)
}