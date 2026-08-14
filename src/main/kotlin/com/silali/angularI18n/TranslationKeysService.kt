package com.silali.angularI18n

import com.intellij.openapi.components.Service

@Service(Service.Level.PROJECT)
class TranslationKeysService {
    private val keys = mutableListOf<String>()

    fun setKeys(keys: List<String>) {
        this.keys.apply {
            addAll(keys)
        }
    }

    fun getKeys(): List<String> {
        return this.keys.toList()
    }
}