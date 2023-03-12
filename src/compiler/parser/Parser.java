/**
 * @Author: turk
 * @Description: Sintaksni analizator.
 */

package compiler.parser;

import static compiler.lexer.TokenType.*;
import static common.RequireNonNull.requireNonNull;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.lang.model.util.ElementScanner14;

import common.Report;
import compiler.lexer.Position;
import compiler.lexer.Symbol;
import compiler.lexer.TokenType;

public class Parser {
    /**
     * Seznam leksikalnih simbolov.
     */
    private final List<Symbol> symbols;

    /**
     * Ciljni tok, kamor izpisujemo produkcije. Če produkcij ne želimo izpisovati,
     * vrednost opcijske spremenljivke nastavimo na Optional.empty().
     */
    private final Optional<PrintStream> productionsOutputStream;

    public Parser(List<Symbol> symbols, Optional<PrintStream> productionsOutputStream) {
        requireNonNull(symbols, productionsOutputStream);
        this.symbols = symbols;
        this.productionsOutputStream = productionsOutputStream;
    }

    /**
     * Izvedi sintaksno analizo.
     */
    public void parse() {
        parseSource();
    }

    private void parseSource() {
        dump("source -> definitions");
        Iterator<Symbol> symbol_iterator = symbols.iterator();
        parseDefs(symbol_iterator);
    }

    private void parseDefs(Iterator<Symbol> lexicalSymbol) {
        dump("definitions -> definition definitions_1");
        while (lexicalSymbol.hasNext()) {
            parseDef(lexicalSymbol);
            if (lexicalSymbol.next().tokenType == TokenType.OP_SEMICOLON) {
                parseDefs_1(lexicalSymbol);
            }
        }
    }

    private void parseDefs_1(Iterator<Symbol> lexicalSymbol) {
        dump("definitions_1 -> ; definition definitions_1 | ");
        while (lexicalSymbol.hasNext()) {
            parseDef(lexicalSymbol);
            if (lexicalSymbol.next().tokenType == TokenType.OP_SEMICOLON) {
                parseDefs_1(lexicalSymbol);
            }
        }
    }

    private void parseDef(Iterator<Symbol> lexicalSymbol) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType == TokenType.KW_TYP) {
            dump("definition -> type_definition");
            parseTypeDef(lexicalSymbol);
        } else if (currentLexicalSym.tokenType == TokenType.KW_FUN) {
            dump("definition -> function_definition");
            parseFunDef(lexicalSymbol);
        } else if (currentLexicalSym.tokenType == TokenType.KW_VAR) {
            dump("definition -> variable_definition");
            parseVarDef(lexicalSymbol);
        } else
            Report.error(currentLexicalSym.position, "Wrong definition statment");
    }

    private void parseTypeDef(Iterator<Symbol> lexicalSymbol) {
        dump("type_definition -> typ id : type");
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
            Report.error(currentLexicalSym.position, "Type definition identifier is wrong");
        currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_COLON)
            Report.error(currentLexicalSym.position, "After type definition identifier a colon \':\' must follow");
        parseType(lexicalSymbol);
    }

    private void parseType(Iterator<Symbol> lexicalSymbol) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType == TokenType.IDENTIFIER)
            dump("type -> id");
        else if (currentLexicalSym.tokenType == TokenType.AT_LOGICAL)
            dump("type -> logical");
        else if (currentLexicalSym.tokenType == TokenType.AT_INTEGER)
            dump("type -> integer");
        else if (currentLexicalSym.tokenType == TokenType.AT_STRING)
            dump("type -> string");
        else if (currentLexicalSym.tokenType == TokenType.KW_ARR) {
            dump("type -> arr [ integer_const ] type");
            currentLexicalSym = lexicalSymbol.next();
            if (currentLexicalSym.tokenType != TokenType.OP_LBRACKET)
                Report.error(currentLexicalSym.position, "Specifying arr lenght must be enclosed in square brackets");
            currentLexicalSym = lexicalSymbol.next();
            if (currentLexicalSym.tokenType != TokenType.C_INTEGER)
                Report.error(currentLexicalSym.position, "Array arr lenght must be an integer");
            currentLexicalSym = lexicalSymbol.next();
            if (currentLexicalSym.tokenType != TokenType.OP_RBRACKET)
                Report.error(currentLexicalSym.position, "Specifying arr lenght must be enclosed in square brackets");
            parseType(lexicalSymbol);
        }
    }

    private void parseFunDef(Iterator<Symbol> lexicalSymbol) {
        dump("function_definition -> fun id ( parameters ) : type = expression");
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
            Report.error(currentLexicalSym.position,
                    "After the keyword fun, an identifier is required to name the function");
        currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_LPARENT)
            Report.error(currentLexicalSym.position,
                    "Function parameters should be enclosed in paranthesis, the left one is missing or misplaced");
        parseParams(lexicalSymbol);
        currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_RPARENT)
            Report.error(currentLexicalSym.position,
                    "Function parameters should be enclosed in paranthesis, the right one is missing or misplaced");
        currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_COLON)
            Report.error(currentLexicalSym.position,
                    "Following function declaration a colon is required to denote the body");
        parseType(lexicalSymbol);
        currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_ASSIGN)
            Report.error(currentLexicalSym.position,
                    "Following the type in a function declaration an assignment operator is required");
        parseExpression(lexicalSymbol);
    }

    private void parseParams(Iterator<Symbol> lexicalSymbol) {
        dump("parameters -> parameter parameters_1");
        parseParam(lexicalSymbol);
        // TODO: parse params_1 production
    }

    private void parseParam(Iterator<Symbol> lexicalSymbol) {
        dump("parameter -> id : type");
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
            Report.error(currentLexicalSym.position, "Parameter declaration must begin with its identifier");
        currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_COLON)
            Report.error(currentLexicalSym.position, "After parameter identificator a colon is required");
        parseType(lexicalSymbol);
    }

    private void parseVarDef(Iterator<Symbol> lexicalSymbol) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        dump("variable_definition -> var id : type");
        if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
            Report.error(currentLexicalSym.position, "Variable definition identifier is wrong");
        currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_COLON)
            Report.error(currentLexicalSym.position, "After variable definition identifier a colon \':\' must follow");
        parseType(lexicalSymbol);
    }

    /**
     * Izpiše produkcijo na izhodni tok.
     */
    private void dump(String production) {
        if (productionsOutputStream.isPresent()) {
            productionsOutputStream.get().println(production);
        }
    }
}
