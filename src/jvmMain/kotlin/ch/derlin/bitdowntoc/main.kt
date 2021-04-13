package ch.derlin.bitdowntoc

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path

class Cli : CliktCommand() {

    init {
        context { helpFormatter = CliktHelpFormatter(showRequiredTag = true) }
    }

    private val inputFile: Path by argument(help = "Input Markdown File")
        .path(mustExist = true, canBeDir = false)

    private val indentChars: String by BitOptions.indentChars.cliOption()
    private val concatSpaces: Boolean by BitOptions.concatSpaces.cliOption()
    private val generateAnchors: Boolean by BitOptions.generateAnchors.cliOption()
    private val trimToIndent: Boolean by BitOptions.trimTocIndent.cliOption()
    private val oneshot: Boolean by BitOptions.oneShot.cliOption()
    private val maxLevel: Int by BitOptions.maxLevel.cliOptionInt()

    private val inplace: Boolean by option("--inplace", "-i", help = "Overwrite input file")
        .flag(default = false)

    private val outputFile: Path? by option("-o", "--output-file")
        .path(mustExist = false, canBeDir = false)

    override fun run() {
        require(!(inplace && outputFile != null)) {
            "--inplace and --output are mutually exclusive options"
        }

        val inputText = inputFile.toFile().readText()
        val output = if (inplace) inputFile else outputFile

        BitGenerator.generate(
            inputText,
            indentCharacters = indentChars,
            generateAnchors = generateAnchors,
            concatSpaces = concatSpaces,
            trimTocIndent = trimToIndent,
            oneShot = oneshot,
            maxLevel = maxLevel
        ).let {
            (output?.toFile()?.writeText(it)) ?: println(it)
        }
    }

    private fun BitOption<String>.cliOption() = option("--$id", help = "$help (default: '$default')")
        .default(default)

    private fun BitOption<Int>.cliOptionInt() = option("--$id", help = "$help (default: '$default')")
        .int()
        .default(default)

    private fun BitOption<Boolean>.cliOption() = option("--$id", help = "$help (default: $default)")
        .flag("--no-$id", default = default)
}


fun main(args: Array<String>) = Cli().main(args)