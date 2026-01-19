package com.kyro.lexer

import Token
import com.kyro.util.KyroException

class Lexer(private val src: String) {

    private var pos = 0
    private var line = 1
    private var column = 1

    val KEYWORDS = setOf(
        "run",
        "if",
        "for",
        "in",
        "pipe",
        "val",
        "var"
    )


    fun lex(): List<Token> {
        val tokens = mutableListOf<Token>()

        while (!eof()) {
            skipWhitespace()
            if (eof()) break   // 🔑 CRITICAL

            when {
                peek().isLetter() ->
                    tokens.add(readIdent())

                peek() == '"' ->
                    tokens.add(readString())

                peek().isDigit() ->
                    tokens.add(readNumber())

                else -> {
                    val c = peek()

                    // 🔑 Ignore stray newlines / whitespace
                    if (c.isWhitespace()) {
                        advance()
                    } else {
                        tokens.add(Token.Symbol(advance(), position()))
                    }
                }
            }
        }

        tokens.add(Token.Eof(position()))
        return tokens
    }


    private fun readIdent(): Token {
        val startPos = position()
        val start = pos
        while (!eof() && peek().isLetterOrDigit()) advance()
        val text = src.substring(start, pos)

        return if (text in KEYWORDS)
            Token.Keyword(text, startPos)
        else
            Token.Ident(text, startPos)
    }


    private fun readString(): Token {
        val startPos = position()
        advance() // "

        val start = pos
        while (!eof() && peek() != '"') advance()

        if (eof()) {
            errorAt(startPos, "Unterminated string literal")
        }

        val value = src.substring(start, pos)
        advance() // closing "
        return Token.StringLit(value, startPos)
    }



    private fun readNumber(): Token {
        val startPos = position()
        val start = pos
        while (!eof() && peek().isDigit()) advance()
        return Token.NumberLit(src.substring(start, pos), startPos)
    }


    private fun skipWhitespace() {
        while (!eof() && peek().isWhitespace()) advance()
    }


    private fun peek(): Char =
        if (pos >= src.length) '\u0000' else src[pos]

    private fun advance(): Char {
        if (pos >= src.length) {
            return '\u0000'
        }

        val c = src[pos++]
        if (c == '\n') {
            line++
            column = 1
        } else {
            column++
        }
        return c
    }

    private fun position() = Position(line, column)
    private fun eof() = pos >= src.length

    private fun errorAt(pos: Position, msg: String): Nothing {
        throw KyroException(pos, msg)
    }

}
