package com.kyro.emit

import com.kyro.ast.*

class ShellEmitter {

    fun emit(program: Program): String =
        program.statements.joinToString("\n") { emitStmt(it) }

    private fun emitStmt(stmt: Stmt): String =
        when (stmt) {
            is VarDecl -> emitVarDecl(stmt)
            is Run -> emitRun(stmt)
            is If -> emitIf(stmt)
            is For -> emitFor(stmt)
            is Pipe -> emitPipe(stmt)
            is ExprStmt -> emitExprStmt(stmt)
            else -> "# unsupported"
        }



    // ---- NEW ----
    private fun emitVarDecl(v: VarDecl): String {
        val value = emitExpr(v.expr)
        return "${v.name}=$value"
    }

    private fun emitRun(run: Run): String {
        val cmd = emitCommand(run.args.first())
        val rest = run.args.drop(1)
            .joinToString(" ") { emitExpr(it) }

        return listOf(cmd, rest)
            .filter { it.isNotBlank() }
            .joinToString(" ")
    }

    private fun emitCommand(expr: Expr): String =
        when (expr) {
            is StringLit -> expr.value
            is Var -> "\$${expr.name}"
            else -> error("Invalid command: $expr")
        }

    private fun emitExpr(expr: Expr): String =
        when (expr) {
            is StringLit -> "\"${expr.value}\""
            is NumberLit -> expr.value
            is Var -> "\"\$${expr.name}\""
            else -> error("Unsupported expr: $expr")
        }

    private fun emitIf(stmt: If): String {
        val cond = emitCondition(stmt.cond)
        val body = stmt.body.joinToString("\n") { emitStmt(it) }

        return """
        if $cond; then
        $body
        fi
    """.trimIndent()
    }

    private fun emitFor(stmt: For): String {
        val iterable = emitIterable(stmt.iterable)
        val body = stmt.body.joinToString("\n") { emitStmt(it) }

        return """
        for ${stmt.name} in $iterable; do
        $body
        done
    """.trimIndent()
    }

    private fun emitIterable(expr: Expr): String =
        when (expr) {
            is Call -> when (expr.name) {
                "glob" -> {
                    require(expr.args.size == 1) {
                        "glob() expects exactly one argument"
                    }
                    // glob expands in shell, so NO quotes
                    val arg = expr.args[0]
                    when (arg) {
                        is StringLit -> arg.value
                        is Var -> "\$${arg.name}"
                        else -> error("Invalid glob argument")
                    }
                }
                else -> error("Unsupported iterable function: ${expr.name}")
            }
            else -> error("Invalid iterable expression: $expr")
        }


    private fun emitCondition(expr: Expr): String =
        when (expr) {
            is Call -> when (expr.name) {
                "exists" -> {
                    require(expr.args.size == 1) {
                        "exists() expects exactly one argument"
                    }
                    val arg = emitExpr(expr.args[0])
                    "[ -e $arg ]"
                }
                else -> error("Unknown condition function: ${expr.name}")
            }
            else -> error("Invalid condition: $expr")
        }

    private fun emitPipe(pipe: Pipe): String {
        return pipe.commands.joinToString(" | ") { emitRun(it) }
    }

    private fun emitExprStmt(stmt: ExprStmt): String {
        val expr = stmt.expr

        if (expr is Call && expr.name == "print") {
            require(expr.args.size == 1) {
                "print() expects exactly one argument"
            }
            return "echo ${emitExpr(expr.args[0])}"
        }

        error("Unsupported expression statement: $expr")
    }


}
