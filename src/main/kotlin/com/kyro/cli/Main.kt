package com.kyro.cli

import com.kyro.lexer.Lexer
import com.kyro.parser.Parser
import com.kyro.emit.ShellEmitter
import com.kyro.util.KyroException
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        printHelp()
        exitProcess(1)
    }

    val mode = args[0]
    val file = args.getOrNull(1)

    if (file == null) {
        error("Missing input file")
    }

    val source = File(file).readText()

    try {
        val tokens = Lexer(source).lex()
        val ast = Parser(tokens).parse()
        val shell = ShellEmitter().emit(ast)

        when (mode) {
            "compile" -> {
                println(shell)
            }

            "run" -> {
                runShell(shell)
            }

            "help" -> {
                printHelp()
            }

            else -> {
                error("Unknown command: $mode")
            }
        }
    }catch (e: KyroException) {
        System.err.println(
            "Kyro error at ${e.position.line}:${e.position.column} → ${e.message}"
        )
        exitProcess(1)
    }catch (e: Exception) {
        System.err.println("Internal Kyro error: ${e.message}")
        exitProcess(1)
    }


}

private fun runShell(script: String) {
    val process = ProcessBuilder("sh", "-c", script)
        .inheritIO()
        .start()

    val exitCode = process.waitFor()
    exitProcess(exitCode)
}

private fun printHelp() {
    println(
        """
        Kyro — structured shell scripting language

        Usage:
          kyro compile <file.ky>   Compile to POSIX shell
          kyro run <file.ky>       Compile and execute
          kyro help                Show this help
        """.trimIndent()
    )
}
