package com.kyro.ast

sealed interface Stmt
sealed interface Expr

data class Program(val statements: List<Stmt>)

data class VarDecl(
    val mutable: Boolean,
    val name: String,
    val expr: Expr
) : Stmt

data class Run(val args: List<Expr>) : Stmt
data class If(val cond: Expr, val body: List<Stmt>) : Stmt
data class For(val name: String, val iterable: Expr, val body: List<Stmt>) : Stmt
data class Pipe(val commands: List<Run>) : Stmt

data class StringLit(val value: String) : Expr
data class NumberLit(val value: String) : Expr
data class Var(val name: String) : Expr
data class Call(val name: String, val args: List<Expr>) : Expr
data class ExprStmt(val expr: Expr) : Stmt

