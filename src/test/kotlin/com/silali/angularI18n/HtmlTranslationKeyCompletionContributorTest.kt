package com.silali.angularI18n

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.lang.html.HTMLLanguage
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests the current behavior of [HtmlTranslationKeyCompletionContributor]:
 * it is registered for the HTML language and, while its data is still fake,
 * offers the hard-coded translation keys with the "i18n key" type text when
 * completing inside an HTML file.
 */
class HtmlTranslationKeyCompletionContributorTest : BasePlatformTestCase() {

    /** Mirror of the fake keys currently offered by the contributor. */
    private val expectedKeys = listOf(
        "AUTH.SIGN-IN",
        "AUTH.SIGN-OUT",
        "AUTH.FORGOT-PASSWORD",
        "CARDS.OPERATIONS.COMPLETE-ISSUANCE",
        "CARDS.OPERATIONS.BLOCK-CARD",
        "CARDS.OPERATIONS.REISSUE-CARD",
        "ACCOUNTS.SUMMARY.BALANCE",
        "ACCOUNTS.SUMMARY.RECENT-TRANSACTIONS",
        "COMMON.ACTIONS.SAVE",
        "COMMON.ACTIONS.CANCEL",
    )

    fun testContributorIsRegisteredForHtmlLanguage() {
        val contributors = CompletionContributor.forLanguage(HTMLLanguage.INSTANCE)

        assertTrue(
            "HtmlTranslationKeyCompletionContributor should be registered for the HTML language",
            contributors.any { it is HtmlTranslationKeyCompletionContributor },
        )
    }

    fun testCompletesFakeTranslationKeysInHtmlFile() {
        myFixture.configureByText("test.html", "<div>|</div>")

        val lookupStrings = myFixture.completeBasic().map { it.lookupString }

        assertTrue(
            "Completion should offer all fake translation keys, but was: $lookupStrings",
            lookupStrings.containsAll(expectedKeys),
        )
    }

    fun testTranslationKeySuggestionsCarryTypeText() {
        myFixture.configureByText("test.html", "<div>|</div>")

        val suggestions = myFixture.completeBasic()
            .filter { it.lookupString in expectedKeys }
            .map { LookupElementPresentation.renderElement(it).typeText }

        assertFalse("At least one translation key suggestion should be present", suggestions.isEmpty())
        assertTrue(
            "Translation key suggestions should carry the 'i18n key' type text, but was: $suggestions",
            suggestions.all { it == "i18n key" },
        )
    }
}
