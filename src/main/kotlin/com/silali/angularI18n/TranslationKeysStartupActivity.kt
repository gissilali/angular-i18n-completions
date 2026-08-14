package com.silali.angularI18n

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class TranslationKeysStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        val translationKeysService = project.getService(TranslationKeysService::class.java)
        ProgressManager.getInstance().run(
            object : Task.Backgroundable(project, "Indexing i18n files", false) {
                override fun run(indicator: ProgressIndicator) {
                    indicator.text = "Scanning for translation keys..."
                    indicator.isIndeterminate = false

                    val keys = TranslationFileScanner(project).loadKeys()

                    translationKeysService.setKeys(
                        keys
                    )

                    LOG.info("HtmlTranslationKeyCompletionContributor loaded ${keys.size} keys - v1.0.0")

                }
            }
        )
    }

    companion object {
        private val LOG = Logger.getInstance(TranslationFileScanner::class.java)
    }

}