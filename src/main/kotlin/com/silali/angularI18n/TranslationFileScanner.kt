package com.silali.angularI18n

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor

class TranslationFileScanner(private val project: Project) {

    fun loadKeys(): List<String> {
        val jsonFiles = findTranslationFiles()
        if (jsonFiles.isEmpty()) {
            LOG.info("No translation files found in project")
            return emptyList()
        }

        val allKeys = linkedSetOf<String>()
        for (file in jsonFiles) {
            LOG.info("HtmlTranslationKeyCompletionContributor: Parsing translation file: ${file.path}")
            allKeys += parseKeys(file)
        }

        return allKeys.toList()
    }

    private fun findTranslationFiles(): List<VirtualFile> {
        val baseDir = project.guessProjectDir()
        if (baseDir == null) {
            LOG.warn("HtmlTranslationKeyCompletionContributor: Could not resolve project base directory")
            return emptyList()
        }

        LOG.info("HtmlTranslationKeyCompletionContributor: Resolved baseDir: $baseDir")

        val jsonFiles = mutableListOf<VirtualFile>()

        VfsUtilCore.visitChildrenRecursively(baseDir, object : VirtualFileVisitor<Unit>() {
            override fun visitFile(file: VirtualFile): Boolean {
                if (file.isDirectory && file.name in EXCLUDED_DIRS) return false
                if (!file.isDirectory && file.extension == "json") {
                    val isUnderI18n = generateSequence(file.parent) { it.parent }
                        .takeWhile { it != baseDir }
                        .any { it.name.equals("i18n", ignoreCase = true) }
                    if (isUnderI18n) jsonFiles += file
                }
                return true
            }
        })

        LOG.info("HtmlTranslationKeyCompletionContributor: Found ${jsonFiles.size} translation file(s)")
        return jsonFiles
    }

    private fun parseKeys(file: VirtualFile): List<String> = try {
        val text = VfsUtilCore.loadText(file)
        val json = JsonParser.parseString(text).asJsonObject
        flatten(json)
    } catch (e: Exception) {
        LOG.warn("HtmlTranslationKeyCompletionContributor: Failed to parse translation file: ${file.path}", e)
        emptyList()
    }

    private fun flatten(obj: JsonObject, prefix: String = ""): List<String> {
        val result = mutableListOf<String>()
        for ((key, value) in obj.entrySet()) {
            val fullKey = if (prefix.isEmpty()) key else "$prefix.$key"
            if (value.isJsonObject) {
                result += flatten(value.asJsonObject, fullKey)
            } else {
                result.add(fullKey)
            }
        }
        return result
    }

    companion object {
        private val LOG = Logger.getInstance(TranslationFileScanner::class.java)

        private val EXCLUDED_DIRS = setOf(
            "node_modules",
            "dist",
            ".git",
            ".angular",
            "build",
            "coverage",
            ".idea"
        )
    }
}