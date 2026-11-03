package com.example.timetracker

import com.intellij.execution.ExecutionListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.components.service

class RunListener : ExecutionListener {
    override fun processStarting(executorId: String, env: ExecutionEnvironment) {
        env.project.service<TimeTrackerService>().onRun()
    }
}