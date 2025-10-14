package com.github.k61n.intellijpluginzstd.toolWindow

import com.github.k61n.intellijpluginzstd.MyBundle
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.panels.VerticalBox
import com.intellij.ui.content.ContentFactory
import com.github.k61n.intellijpluginzstd.services.MyProjectService

class MyToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        content.setDisposer(myToolWindow)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(private val toolWindow: ToolWindow) : Disposable {

        private val project = toolWindow.project
        private val service = project.service<MyProjectService>()

        private val labelCurrentFile = JBLabel("No file")

        fun getContent() = VerticalBox().apply {
            add(labelCurrentFile)
            project.messageBus.connect(this@MyToolWindow).subscribe(
                FileEditorManagerListener.FILE_EDITOR_MANAGER,
                object : FileEditorManagerListener {
                    override fun selectionChanged(event: FileEditorManagerEvent) {
                        labelCurrentFile.text = MyBundle.message("currentfile", event.newFile?.presentableUrl ?: "No file")
                    }
                }
            )
//            add(JButton("Compress").apply {
//                addActionListener {
//                    ...
//                }
//            })
        }

        override fun dispose() {
        }
    }
}