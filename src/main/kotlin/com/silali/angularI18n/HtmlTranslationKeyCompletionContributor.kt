package com.silali.angularI18n

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.javascript.JSTokenTypes
import com.intellij.openapi.diagnostic.Logger
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import org.angular2.lang.expr.psi.Angular2Interpolation


class HtmlTranslationKeyCompletionContributor : CompletionContributor() {

    init {

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(JSTokenTypes.STRING_LITERAL)
                .inside(Angular2Interpolation::class.java),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    LOG.info("TranslationKeyCompletionContributor fired at offset ${parameters.offset}")

                    FAKE_TRANSLATION_KEYS.forEach { key ->
                        result.addElement(
                            LookupElementBuilder.create(key)
                                .withTypeText("i18n key")
                        )
                    }
                }
            },
        )
    }

    companion object {
        private val LOG = Logger.getInstance(HtmlTranslationKeyCompletionContributor::class.java)

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