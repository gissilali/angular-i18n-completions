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

        LOG.info(
            "FIRED offset=${parameters.offset} elementType=${position.node?.elementType} " +
                    "isStringLiteral=$isStringLiteral insideInterpolation=$insideInterpolation"
        )

        if (!isStringLiteral || !insideInterpolation) return

        FAKE_TRANSLATION_KEYS.forEach { key ->
            result.addElement(LookupElementBuilder.create(key))
        }
    }

    companion object {
        private val LOG = Logger.getInstance(TranslationKeyCompletionProvider::class.java)

        private val FAKE_TRANSLATION_KEYS = listOf(
            "AUTH.SIGN-IN",
            "AUTH.SIGN-OUT",
            "AUTH.FORGOT-PASSWORD",
            "CARDS.OPERATIONS.COMPLETE-ISSUANCE",
            "CARDS.OPERATIONS.BLOCK-CARD",
            "CARDS.OPERATIONS.REISSUE-CARD",
            "ACCOUNTS.SUMMARY.BALANCE",
            "ACCOUNTS.SUMMARY.RECENT-TRANSACTIONS",
            "COMMON.ACTIONS.SAVE",
            "COMMON.ACTIONS.CANCEL"
        )
    }
}