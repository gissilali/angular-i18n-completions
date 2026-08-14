package com.silali.angularI18n

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile

class TranslationFileScanner(private val project: Project) {

    fun findTranslationFile(): VirtualFile? {
        val baseDir = project.guessProjectDir()
        if (baseDir == null) {
            LOG.warn("HtmlTranslationKeyCompletionContributor: Could not resolve project base directory")
            return null
        }

        val i18nDir = baseDir.findFileByRelativePath("public/i18n")
        if (i18nDir == null || !i18nDir.isDirectory) {
            LOG.info("HtmlTranslationKeyCompletionContributor: No public/i18n directory found at ${baseDir.path}/public/i18n")
            return null
        }

        val jsonFile = i18nDir.children.firstOrNull { it.extension == "json" }
        if (jsonFile == null) {
            LOG.info("HtmlTranslationKeyCompletionContributor: public/i18n exists but no .json file found inside")
            return null
        }

        LOG.info("HtmlTranslationKeyCompletionContributor: Found translation file: ${jsonFile.path}")
        return jsonFile
    }

    companion object {
        private val LOG = Logger.getInstance(TranslationFileScanner::class.java)
    }
}