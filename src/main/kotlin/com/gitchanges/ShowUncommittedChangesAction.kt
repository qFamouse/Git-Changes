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

class ShowUncommittedChangesAction : AnAction() {
    override fun getActionUpdateThread(): com.intellij.openapi.actionSystem.ActionUpdateThread {
        return com.intellij.openapi.actionSystem.ActionUpdateThread.BGT
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return

        // Получить выбранные изменения
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

        // Обработать изменения
        val result = processChanges(selectedChanges, project)

        // Скопировать в буфер обмена
        val selection = StringSelection(result)
        CopyPasteManager.getInstance().setContents(selection)

        // Показать уведомление
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

    private fun getSelectedChanges(e: AnActionEvent): List<Change> {
        // Попытка получить выбранные изменения из ChangesListView
        val changesView = e.getData(ChangesListView.DATA_KEY)
        if (changesView != null) {
            return changesView.selectedChanges.toList()
        }

        // Альтернативный способ: получить из VirtualFile
        val virtualFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        if (virtualFiles != null && virtualFiles.isNotEmpty()) {
            val project = e.project ?: return emptyList()
            val changeListManager = ChangeListManager.getInstance(project)
            return virtualFiles.mapNotNull { changeListManager.getChange(it) }
        }

        return emptyList()
    }

    private fun processChanges(changes: List<Change>, project: Project): String {
        val result = StringBuilder()

        changes.forEach { change ->
            when {
                // Измененный файл
                change.beforeRevision != null && change.afterRevision != null -> {
                    val path = getRelativePathFromGitRoot(change.afterRevision!!.file, project)
                    val oldContent = change.beforeRevision!!.content ?: ""
                    val newContent = change.afterRevision!!.content ?: ""

                    result.append("Было ($path):\n")
                    result.append(oldContent)
                    result.append("\n\nСтало:\n")
                    result.append(newContent)
                    result.append("\n\n---\n\n")
                }

                // Добавленный файл
                change.beforeRevision == null && change.afterRevision != null -> {
                    val path = getRelativePathFromGitRoot(change.afterRevision!!.file, project)
                    val content = change.afterRevision!!.content ?: ""

                    result.append("Добавлено ($path):\n")
                    result.append(content)
                    result.append("\n\n---\n\n")
                }

                // Удаленный файл
                change.beforeRevision != null && change.afterRevision == null -> {
                    val path = getRelativePathFromGitRoot(change.beforeRevision!!.file, project)
                    val content = change.beforeRevision!!.content ?: ""

                    result.append("Удалено ($path):\n")
                    result.append(content)
                    result.append("\n\n---\n\n")
                }
            }
        }

        return result.toString()
    }

    private fun getRelativePathFromGitRoot(filePath: com.intellij.openapi.vcs.FilePath, project: Project): String {
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

            // Убираем корень репозитория из пути
            val relativePath = absolutePath.replace("\\", "/").removePrefix(rootPath.replace("\\", "/")).removePrefix("/")
            relativePath
        } else {
            filePath.path
        }
    }
}