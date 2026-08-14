package com.silali.angularI18n

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.javascript.JSTokenTypes
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.angular2.lang.expr.psi.Angular2Interpolation
import java.util.Locale
import java.util.Locale.getDefault

class TranslationKeyCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position

        val isStringLiteral = position.node?.elementType == JSTokenTypes.STRING_LITERAL
        val insideInterpolation =
            PsiTreeUtil.getParentOfType(position, Angular2Interpolation::class.java) != null

        val project = parameters.position.project
        val fileExtension = parameters.originalFile.virtualFile?.extension?.lowercase(getDefault())
        val translationKeysService = project.getService(TranslationKeysService::class.java)


        if(fileExtension != "html" && fileExtension != "ts") return

        if (!isStringLiteral) return

        translationKeysService.getKeys().forEach { key ->
            result.addElement(LookupElementBuilder.create(key).withInsertHandler { insertionContext, _ ->
                insertionContext.document.insertString(insertionContext.tailOffset, "\"")
                insertionContext.editor.caretModel.moveToOffset(insertionContext.tailOffset)
            })
        }
    }

    companion object {
        private val LOG = Logger.getInstance(TranslationKeyCompletionProvider::class.java)
    }
}