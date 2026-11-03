package com.example.timetracker

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.Messages

class ResetStatsAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val answer = Messages.showYesNoDialog(
            project,
            "Reset working time, build and run counters for this project?",
            "Time Tracker",
            Messages.getWarningIcon()
        )
        if (answer == Messages.YES) {
            project.service<TimeTrackerService>().reset()
            refreshWidget(project)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}