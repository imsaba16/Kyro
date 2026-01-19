package com.kyro.parser

import Token
import com.kyro.ast.*
import com.kyro.lexer.*
import com.kyro.util.KyroException

class Parser(private val tokens: List<Token>) {
    private var pos = 0

    fun parse(): Program {
        val stmts = mutableListOf<Stmt>()
        while (!match<Token.Eof>()) {
            if (peek() is Token.Symbol) {
                advance()
                continue
            }
            stmts += parseStmt()
        }

        return Program(stmts)
    }

    private fun parseStmt(): Stmt {
        return when (val token = peek()) {
            is Token.Keyword -> when (token.value) {
                "run" -> parseRun()
                "val" -> parseVarDecl(mutable = false)
                "var" -> parseVarDecl(mutable = true)
                "if"  -> parseIf()
                "for" -> parseFor()
                "pipe" -> parsePipe()
                else -> error("Unknown keyword: ${token.value}")
            }

            is Token.Ident -> {
                val expr = parseExpr()
                if (expr is Call) {
                    ExprStmt(expr)
                } else {
                    error("Invalid statement: $expr")
                }
            }

            else -> error("Unexpected token: $token")
        }
    }


    // ---- NEW ----
    private fun parseVarDecl(mutable: Boolean): Stmt {
        advance() // val | var

        val name = expect<Token.Ident>().value
        expectSymbol('=')

        val expr = parseExpr()
        return VarDecl(mutable, name, expr)
    }

    private fun parseRun(): Stmt {
        advance() // run

        val args = mutableListOf<Expr>()
        args += parseExpr()

        while (checkSymbol(',')) {
            advance()
            args += parseExpr()
        }

        return Run(args)
    }

    private fun parseExpr(): Expr {
        val token = advance()

        return when (token) {
            is Token.StringLit -> StringLit(token.value)
            is Token.NumberLit -> NumberLit(token.value)
            is Token.Ident -> {
                if (checkSymbol('(')) {
                    parseCall(token.value)
                } else {
                    Var(token.value)
                }
            }
            else -> error("Expected expression, got $token")
        }
    }


    private fun parseIf(): Stmt {
        advance()

        val condition = parseExpr()
        val body = parseBlock()

        return If(condition, body)
    }

    private fun parseBlock(): List<Stmt> {
        expectSymbol('{')

        val stmts = mutableListOf<Stmt>()
        while (!checkSymbol('}')) {
            stmts += parseStmt()
        }

        expectSymbol('}')
        return stmts
    }

    private fun parseCall(name: String): Expr {
        expectSymbol('(')

        val args = mutableListOf<Expr>()
        if (!checkSymbol(')')) {
            args += parseExpr()
            while (checkSymbol(',')) {
                advance()
                args += parseExpr()
            }
        }

        expectSymbol(')')
        return Call(name, args)
    }

    private fun parseFor(): Stmt {
        advance() // consume 'for'

        val name = expect<Token.Ident>().value

        val inKw = advance()
        if (inKw !is Token.Keyword || inKw.value != "in") {
            error("Expected 'in' after for-variable")
        }

        val iterable = parseExpr()
        val body = parseBlock()

        return For(name, iterable, body)
    }

    private fun parsePipe(): Stmt {
        advance() // consume 'pipe'
        expectSymbol('{')

        val commands = mutableListOf<Run>()

        while (!checkSymbol('}')) {
            val stmt = parseStmt()
            if (stmt !is Run) {
                error("Only 'run' statements are allowed inside pipe block")
            }
            commands += stmt
        }

        expectSymbol('}')
        return Pipe(commands)
    }



    // ---- helpers ----
    private fun peek(): Token = tokens[pos]
    private fun advance(): Token = tokens[pos++]

    private inline fun <reified T> match(): Boolean =
        tokens[pos] is T

    private inline fun <reified T> expect(): T {
        val token = advance()
        if (token !is T) {
            error(token, "Expected ${T::class.simpleName}, got $token")
        }
        return token
    }


    private fun expectSymbol(c: Char) {
        val t = advance()
        if (t !is Token.Symbol || t.value != c) {
            error(t, "Expected '$c'")
        }
    }


    private fun checkSymbol(c: Char): Boolean =
        peek() is Token.Symbol && (peek() as Token.Symbol).value == c

    private fun error(token: Token, msg: String): Nothing {
        throw KyroException(token.pos, msg)
    }

}
