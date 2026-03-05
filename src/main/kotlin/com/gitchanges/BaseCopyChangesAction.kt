package com.gitchanges

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vcs.changes.Change
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.openapi.vcs.changes.ui.ChangesListView
import java.awt.datatransfer.StringSelection

abstract class BaseCopyChangesAction : AnAction() {
    override fun getActionUpdateThread(): com.intellij.openapi.actionSystem.ActionUpdateThread {
        return com.intellij.openapi.actionSystem.ActionUpdateThread.BGT
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return
        val selectedChanges = getSelectedChanges(e)

        if (selectedChanges.isEmpty()) {
            Messages.showMessageDialog(
                project,
                "No changes selected",
                "Info",
                Messages.getInformationIcon()
            )
            return
        }

        val result = formatChanges(selectedChanges, project)

        val selection = StringSelection(result)
        CopyPasteManager.getInstance().setContents(selection)

        Messages.showMessageDialog(
            project,
            "Changes copied to clipboard (${selectedChanges.size} files)",
            "Success",
            Messages.getInformationIcon()
        )
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val selectedChanges = getSelectedChanges(e)
        e.presentation.isEnabled = project != null && selectedChanges.isNotEmpty()
    }

    protected abstract fun formatChanges(changes: List<Change>, project: Project): String

    protected fun getSelectedChanges(e: AnActionEvent): List<Change> {
        val changesView = e.getData(ChangesListView.DATA_KEY)
        if (changesView != null) {
            return changesView.selectedChanges.toList()
        }

        val virtualFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        if (virtualFiles != null && virtualFiles.isNotEmpty()) {
            val project = e.project ?: return emptyList()
            val changeListManager = ChangeListManager.getInstance(project)
            return virtualFiles.mapNotNull { changeListManager.getChange(it) }
        }

        return emptyList()
    }

    protected fun getRelativePathFromGitRoot(filePath: com.intellij.openapi.vcs.FilePath, project: Project): String {
        val vcsManager = ProjectLevelVcsManager.getInstance(project)
        val vcsRoot = vcsManager.getVcsRootFor(filePath)

        return if (vcsRoot != null) {
            val file = filePath.virtualFile ?: filePath.ioFile
            val rootPath = vcsRoot.path
            val absolutePath = if (file is java.io.File) {
                file.absolutePath
            } else {
                filePath.path
            }

            val relativePath = absolutePath.replace("\\", "/").removePrefix(rootPath.replace("\\", "/")).removePrefix("/")
            relativePath
        } else {
            filePath.path
        }
    }
}