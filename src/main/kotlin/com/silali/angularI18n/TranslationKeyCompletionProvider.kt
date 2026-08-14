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
                || position.parent is com.intellij.lang.javascript.psi.JSLiteralExpression
        val insideInterpolation =
            PsiTreeUtil.getParentOfType(position, Angular2Interpolation::class.java) != null

        val project = parameters.position.project
        val fileExtension = parameters.originalFile.virtualFile?.extension?.lowercase(getDefault())
        val translationKeysService = project.getService(TranslationKeysService::class.java)


        if(fileExtension != "html" && fileExtension != "ts") return

        LOG.info("TranslationKeyCompletionContributor extenstion: $fileExtension and is string literal $isStringLiteral")

        if (!isStringLiteral) return

        translationKeysService.getKeys().forEach { key ->
            result.addElement(LookupElementBuilder.create(key).withInsertHandler { insertionContext, _ ->
                val document = insertionContext.document
                val openingQuote = document.getText(
                    com.intellij.openapi.util.TextRange(
                        insertionContext.startOffset - 1,
                        insertionContext.startOffset
                    )
                )
                val charAfter = document.getText(
                    com.intellij.openapi.util.TextRange(
                        insertionContext.tailOffset,
                        insertionContext.tailOffset + 1
                    )
                )
                if (charAfter != openingQuote) {
                    document.insertString(insertionContext.tailOffset, openingQuote)
                }
                insertionContext.editor.caretModel.moveToOffset(insertionContext.tailOffset)
            })
        }
    }

    companion object {
        private val LOG = Logger.getInstance(TranslationKeyCompletionProvider::class.java)
    }
}