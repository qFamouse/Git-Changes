package com.gitchanges

import com.intellij.diff.comparison.ComparisonManager
import com.intellij.diff.comparison.ComparisonPolicy
import com.intellij.openapi.progress.DumbProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.Change

class CopyDiffChangesAction : BaseCopyChangesAction() {
    override fun formatChanges(changes: List<Change>, project: Project): String {
        val result = StringBuilder()
        val comparisonManager = ComparisonManager.getInstance()

        changes.forEach { change ->
            when {
                change.beforeRevision != null && change.afterRevision != null -> {
                    val path = getRelativePathFromGitRoot(change.afterRevision!!.file, project)
                    val oldContent = (change.beforeRevision!!.content ?: "").replace("\r\n", "\n")
                    val newContent = (change.afterRevision!!.content ?: "").replace("\r\n", "\n")

                    result.append("Изменено ($path):\n\n")

                    try {
                        val fragments = comparisonManager.compareLines(
                            oldContent,
                            newContent,
                            ComparisonPolicy.DEFAULT,
                            DumbProgressIndicator.INSTANCE
                        )

                        val oldLines = oldContent.lines()
                        val newLines = newContent.lines()

                        fragments.forEach { fragment ->
                            if (fragment.startLine1 < fragment.endLine1 || fragment.startLine2 < fragment.endLine2) {
                                if (fragment.startLine1 < fragment.endLine1) {
                                    result.append("- Было (строки ${fragment.startLine1 + 1}-${fragment.endLine1}):\n")
                                    for (i in fragment.startLine1 until fragment.endLine1) {
                                        if (i < oldLines.size) {
                                            result.append("  ").append(oldLines[i]).append("\n")
                                        }
                                    }
                                }

                                if (fragment.startLine2 < fragment.endLine2) {
                                    result.append("+ Стало (строки ${fragment.startLine2 + 1}-${fragment.endLine2}):\n")
                                    for (i in fragment.startLine2 until fragment.endLine2) {
                                        if (i < newLines.size) {
                                            result.append("  ").append(newLines[i]).append("\n")
                                        }
                                    }
                                }

                                result.append("\n")
                            }
                        }
                    } catch (e: Exception) {
                        result.append("Ошибка сравнения: ${e.message}\n")
                    }

                    result.append("---\n\n")
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
                    result.append("Удалено ($path)\n\n---\n\n")
                }
            }
        }

        return result.toString()
    }
}