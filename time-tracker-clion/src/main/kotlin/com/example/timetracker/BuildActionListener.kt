package com.example.timetracker

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger

class BuildActionListener : AnActionListener {

    private val log = Logger.getInstance(BuildActionListener::class.java)

    private val buildActionIds = setOf(
        "CompileDirty",
        "MakeModule",
        "CompileProject",
        "CidrBuildRunToolbar"
    )

    override fun beforeActionPerformed(action: AnAction, event: AnActionEvent) {
        val id = ActionManager.getInstance().getId(action) ?: return
        val service = event.project?.service<TimeTrackerService>() ?: return
        if (id in buildActionIds) {
            service.onBuild()
        }
    }
}