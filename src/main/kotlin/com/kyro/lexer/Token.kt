import com.kyro.lexer.Position

sealed interface Token {
    val pos: Position

    data class Ident(val value: String, override val pos: Position) : Token
    data class StringLit(val value: String, override val pos: Position) : Token
    data class NumberLit(val value: String, override val pos: Position) : Token
    data class Keyword(val value: String, override val pos: Position) : Token
    data class Symbol(val value: Char, override val pos: Position) : Token

    data class Eof(override val pos: Position) : Token
}
