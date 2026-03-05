package com.gitchanges

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.Change

class CopyFullChangesAction : BaseCopyChangesAction() {
    override fun formatChanges(changes: List<Change>, project: Project): String {
        val result = StringBuilder()

        changes.forEach { change ->
            when {
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

                change.beforeRevision == null && change.afterRevision != null -> {
                    val path = getRelativePathFromGitRoot(change.afterRevision!!.file, project)
                    val content = change.afterRevision!!.content ?: ""

                    result.append("Добавлено ($path):\n")
                    result.append(content)
                    result.append("\n\n---\n\n")
                }

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
}