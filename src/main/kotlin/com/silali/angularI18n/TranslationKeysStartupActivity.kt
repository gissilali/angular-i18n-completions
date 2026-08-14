package com.silali.angularI18n

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class TranslationKeysStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        ProgressManager.getInstance().run(
            object : Task.Backgroundable(project, "Indexing i18n files", false) {
                override fun run(indicator: ProgressIndicator) {
                    indicator.text = "Scanning for translation keys..."
                    indicator.isIndeterminate = false

                    TranslationFileScanner(project).findTranslationFile()
                }
            }
        )
    }

}