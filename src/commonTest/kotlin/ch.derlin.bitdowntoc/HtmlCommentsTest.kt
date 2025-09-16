package ch.derlin.bitdowntoc

import kotlin.test.Test
import kotlin.test.assertEquals

class HtmlCommentsTest {

    @Test
    fun testHtmlCommentsAreIgnored() {
        val input = """
        # Example

        [TOC]

        ## Section A

        TODO

        <!--
        ### Placeholder SubSection
        -->

        ## Section B

        TODO
        """.trimIndent()

        val expected = """
        # Example

        <!-- TOC start (generated with $BITDOWNTOC_URL) -->

        - [Section A](#section-a)
        - [Section B](#section-b)

        <!-- TOC end -->

        ## Section A

        TODO

        <!--
        ### Placeholder SubSection
        -->

        ## Section B

        TODO
        """.trimIndent()

        assertEquals(
            expected,
            BitGenerator.generate(input, BitGenerator.Params(generateAnchors = false))
        )
    }
}