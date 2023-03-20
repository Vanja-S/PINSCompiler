private Type parseType(ListIterator<Symbol> lexicalSymbol) {
    Symbol currentLexicalSym = lexicalSymbol.next();
    if (currentLexicalSym.tokenType == TokenType.AT_LOGICAL) {
        dump("type -> logical");
        return Atom.LOG(currentLexicalSym.position);
    } else if (currentLexicalSym.tokenType == TokenType.AT_INTEGER) {
        dump("type -> integer");
        return Atom.INT(currentLexicalSym.position);
    } else if (currentLexicalSym.tokenType == TokenType.AT_STRING) {
        dump("type -> string");
        return Atom.STR(currentLexicalSym.position);
    } else if (currentLexicalSym.tokenType == TokenType.KW_ARR) {
        dump("type -> arr [ integer_const ] type");
        currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_LBRACKET)
            Report.error(currentLexicalSym.position, "Specifying arr lenght must be enclosed in square brackets");
        currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.C_INTEGER)
            Report.error(currentLexicalSym.position, "Array arr lenght must be an integer");
        int size = Integer.parseInt(currentLexicalSym.lexeme);
        currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_RBRACKET)
            Report.error(currentLexicalSym.position, "Specifying arr lenght must be enclosed in square brackets");
        return new Array(currentLexicalSym.position, size, parseType(lexicalSymbol));
    } else {
        dump("type -> id");
        return 
    }
}