package com.gitchanges

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.Change

class CopyPathsOnlyAction : BaseCopyChangesAction() {
    override fun formatChanges(changes: List<Change>, project: Project): String {
        val result = StringBuilder()

        changes.forEach { change ->
            when {
                change.beforeRevision != null && change.afterRevision != null -> {
                    val path = getRelativePathFromGitRoot(change.afterRevision!!.file, project)
                    result.append("Изменено: $path\n")
                }

                change.beforeRevision == null && change.afterRevision != null -> {
                    val path = getRelativePathFromGitRoot(change.afterRevision!!.file, project)
                    result.append("Добавлено: $path\n")
                }

                change.beforeRevision != null && change.afterRevision == null -> {
                    val path = getRelativePathFromGitRoot(change.beforeRevision!!.file, project)
                    result.append("Удалено: $path\n")
                }
            }
        }

        return result.toString()
    }
}