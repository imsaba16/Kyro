package com.kyro.util

import com.kyro.lexer.Position

class KyroException(
    val position: Position,
    message: String
) : RuntimeException(message)
