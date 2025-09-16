package ch.derlin.bitdowntoc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

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

    @Test
    fun testSingleLineHtmlCommentsAreIgnored() {
        val input = """
        # Example

        [TOC]

        ## Section A

        <!-- ### Commented out heading -->

        ## Section B
        """.trimIndent()

        val expected = """
        # Example

        <!-- TOC start (generated with $BITDOWNTOC_URL) -->

        - [Section A](#section-a)
        - [Section B](#section-b)

        <!-- TOC end -->

        ## Section A

        <!-- ### Commented out heading -->

        ## Section B
        """.trimIndent()

        assertEquals(
            expected,
            BitGenerator.generate(input, BitGenerator.Params(generateAnchors = false))
        )
    }

    @Test
    fun testMultipleHeadingsInHtmlCommentsAreIgnored() {
        val input = """
        # Example

        [TOC]

        ## Section A

        <!--
        ### First commented heading
        #### Second commented heading
        ##### Third commented heading
        -->

        ## Section B
        """.trimIndent()

        val expected = """
        # Example

        <!-- TOC start (generated with $BITDOWNTOC_URL) -->

        - [Section A](#section-a)
        - [Section B](#section-b)

        <!-- TOC end -->

        ## Section A

        <!--
        ### First commented heading
        #### Second commented heading
        ##### Third commented heading
        -->

        ## Section B
        """.trimIndent()

        assertEquals(
            expected,
            BitGenerator.generate(input, BitGenerator.Params(generateAnchors = false))
        )
    }

    @Test
    fun testBitDownTocCommentsAreNotIgnored() {
        val input = """
        # Example

        [TOC]

        ## Section A

        <!--
        ### Placeholder SubSection
        -->

        ## Section B
        """.trimIndent()

        val result = BitGenerator.generate(input, BitGenerator.Params(generateAnchors = true))
        
        // Check that HTML comments are preserved in output
        assertTrue(result.contains("<!--"), "HTML comments should be preserved in output")
        assertTrue(result.contains("### Placeholder SubSection"), "HTML comment content should be preserved in output")
        assertTrue(result.contains("-->"), "HTML comments should be preserved in output")
        
        // Check that TOC only contains Section A and Section B, not Placeholder SubSection
        val tocLines = result.lines().dropWhile { !it.contains("TOC start") }.takeWhile { !it.contains("TOC end") }
        val tocContent = tocLines.joinToString("\n")
        assertTrue(tocContent.contains("Section A"), "TOC should contain Section A")
        assertTrue(tocContent.contains("Section B"), "TOC should contain Section B")
        assertFalse(tocContent.contains("Placeholder SubSection"), "TOC should NOT contain Placeholder SubSection")
        
        // Check that BitDownToc comments are preserved
        assertTrue(result.contains("<!-- TOC -->"), "BitDownToc comments should be preserved")
    }
}