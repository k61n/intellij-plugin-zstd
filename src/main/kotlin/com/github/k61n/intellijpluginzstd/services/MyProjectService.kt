package com.github.k61n.intellijpluginzstd.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class MyProjectService(private val project: Project) {

}
