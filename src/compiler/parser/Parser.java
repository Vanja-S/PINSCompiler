/**
 * @Author: turk
 * @Description: Sintaksni analizator.
 */

package compiler.parser;

import static common.RequireNonNull.requireNonNull;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import common.Report;
import compiler.lexer.Position;
import compiler.lexer.Symbol;
import compiler.lexer.TokenType;
import compiler.lexer.Position.Location;
import compiler.parser.ast.Ast;
import compiler.parser.ast.def.Defs;
import compiler.parser.ast.def.Def;
import compiler.parser.ast.def.FunDef;
import compiler.parser.ast.def.TypeDef;
import compiler.parser.ast.def.VarDef;
import compiler.parser.ast.expr.Binary;
import compiler.parser.ast.expr.Block;
import compiler.parser.ast.expr.Expr;
import compiler.parser.ast.expr.IfThenElse;
import compiler.parser.ast.expr.Unary;
import compiler.parser.ast.expr.Where;
import compiler.parser.ast.type.*;

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
    public Ast parse() {
        return parseSource();
    }

    private Ast parseSource() {
        dump("source -> definitions");
        ListIterator<Symbol> symbol_iterator = symbols.listIterator();
        String string = "";
        var tempDefs = parseDefs(symbol_iterator, string);
        Symbol curr_sym = symbol_iterator.next();
        if (symbol_iterator.hasNext() && curr_sym.tokenType != TokenType.EOF) {
            Report.error(curr_sym.position, "There is no EOF at the end of file");
            return null;
        } else
            return tempDefs;
    }

    private Defs parseDefs(ListIterator<Symbol> lexicalSymbol, String string) {
        Location start = lexicalSymbol.next().position.start;
        lexicalSymbol.previous();
        List<Def> defsList = new ArrayList<Def>();
        dump("definitions -> definition definitions_1");
        defsList.add(parseDef(lexicalSymbol, string));
        return parseDefs_1(lexicalSymbol, start, defsList, string);
    }

    private Defs parseDefs_1(ListIterator<Symbol> lexicalSymbol, Location start, List<Def> defsList, String string) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType == TokenType.OP_SEMICOLON) {
            dump("definitions_1 -> ; definition definitions_1");
            defsList.add(parseDef(lexicalSymbol, string));
            return parseDefs_1(lexicalSymbol, start, defsList, string);
        } else {
            dump("definitions_1 -> ε");
            var tempDefs = new Defs(new Position(start, currentLexicalSym.position.end), defsList);
            lexicalSymbol.previous();
            return tempDefs;
        }
    }

    private Def parseDef(ListIterator<Symbol> lexicalSymbol, String string) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType == TokenType.KW_TYP) {
            dump("definition -> type_definition");
            return parseTypeDef(lexicalSymbol, currentLexicalSym.position.start, string);
        } else if (currentLexicalSym.tokenType == TokenType.KW_FUN) {
            dump("definition -> function_definition");
            return parseFunDef(lexicalSymbol, currentLexicalSym.position.start, string);
        } else if (currentLexicalSym.tokenType == TokenType.KW_VAR) {
            dump("definition -> variable_definition");
            return parseVarDef(lexicalSymbol, currentLexicalSym.position.start, string);
        } else {
            Report.error(currentLexicalSym.position, "Wrong definition statment");
            return null;
        }
    }

    private TypeDef parseTypeDef(ListIterator<Symbol> lexicalSymbol, Location start, String string) {
        String tmp = "typ ";
        dump("type_definition -> typ id : type");
        Symbol currentLexicalSym = lexicalSymbol.next();
        tmp += currentLexicalSym.lexeme + " ";
        if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
            Report.error(currentLexicalSym.position, "Type definition identifier is wrong");
        currentLexicalSym = lexicalSymbol.next();
        tmp += currentLexicalSym.lexeme + " ";
        if (currentLexicalSym.tokenType != TokenType.OP_COLON)
            Report.error(currentLexicalSym.position, "After type definition identifier a colon \':\' must follow");
        string += tmp + " ";
        Type tempType = parseType(lexicalSymbol, string);
        return new TypeDef(new Position(start, tempType.position.end), tmp, tempType);
    }

    private Type parseType(ListIterator<Symbol> lexicalSymbol, String string) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        Location start = currentLexicalSym.position.start;
        String tmp = currentLexicalSym.lexeme + " ";
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
            tmp = currentLexicalSym.lexeme + " ";
            currentLexicalSym = lexicalSymbol.next();
            if (currentLexicalSym.tokenType != TokenType.C_INTEGER)
                Report.error(currentLexicalSym.position, "Array arr lenght must be an integer");
            int size = Integer.parseInt(currentLexicalSym.lexeme);
            tmp = currentLexicalSym.lexeme + " ";
            currentLexicalSym = lexicalSymbol.next();
            if (currentLexicalSym.tokenType != TokenType.OP_RBRACKET)
                Report.error(currentLexicalSym.position, "Specifying arr lenght must be enclosed in square brackets");
            tmp = currentLexicalSym.lexeme + " ";
            string += tmp + " ";
            return new Array(new Position(start, currentLexicalSym.position.end), size,
                    parseType(lexicalSymbol, tmp));
        } else {
            dump("type -> id");
            string += currentLexicalSym.lexeme + " ";
            return new TypeName(currentLexicalSym.position, currentLexicalSym.lexeme);
        }
    }

    private FunDef parseFunDef(ListIterator<Symbol> lexicalSymbol, Location start, String string) {
        dump("function_definition -> fun id \'(\' parameters \')\' \':\' type \'=\' expression");
        String tmp = "fun ";
        Symbol currentLexicalSym = lexicalSymbol.next();
        tmp += currentLexicalSym.lexeme + " ";
        if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
            Report.error(currentLexicalSym.position,
                    "After the keyword fun, an identifier is required to name the function");
        currentLexicalSym = lexicalSymbol.next();
        tmp += currentLexicalSym.lexeme + " ";
        if (currentLexicalSym.tokenType != TokenType.OP_LPARENT)
            Report.error(currentLexicalSym.position,
                    "Function parameters should be enclosed in paranthesis, the left one is missing or misplaced");
        currentLexicalSym = lexicalSymbol.next();
        tmp += currentLexicalSym.lexeme + " ";
        string += tmp + " ";
        var tempParams = parseParams(lexicalSymbol, tmp);
        if (currentLexicalSym.tokenType != TokenType.OP_RPARENT)
            Report.error(currentLexicalSym.position,
                    "Function parameters should be enclosed in paranthesis, the right one is missing or misplaced");
        currentLexicalSym = lexicalSymbol.next();
        tmp += currentLexicalSym.lexeme + " ";
        if (currentLexicalSym.tokenType != TokenType.OP_COLON)
            Report.error(currentLexicalSym.position,
                    "Following function declaration a colon is required to denote the body");
        string += tmp + " ";
        var tempType = parseType(lexicalSymbol, string);
        currentLexicalSym = lexicalSymbol.next();
        tmp += currentLexicalSym.lexeme + " ";
        if (currentLexicalSym.tokenType != TokenType.OP_ASSIGN)
            Report.error(currentLexicalSym.position,
                    "Following the type in a function declaration an assignment operator is required");
        var tempExpr = parseExpression(lexicalSymbol, string);
        return new FunDef(new Position(start, tempExpr.position.end), tmp, tempParams, tempType, tempExpr);
    }

    private Expr parseExpression(ListIterator<Symbol> lexicalSymbol, String string) {
        dump("expression -> logical_ior_expression expression_1");
        var logicalExpr = parseLogicalOrExpression(lexicalSymbol);
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType == TokenType.OP_LBRACE) {
            dump("expression_1 -> { WHERE definitions }");
            Location start = currentLexicalSym.position.start;
            return parseExpression_1(lexicalSymbol, start, logicalExpr, string);
        } else {
            dump("expression_1 -> ε");
            lexicalSymbol.previous();
            return logicalExpr;
        }
    }

    private Expr parseExpression_1(ListIterator<Symbol> lexicalSymbol, Location start, Expr logicalExpr,
            String string) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.KW_WHERE)
            Report.error(currentLexicalSym.position,
                    "The keyword where is required after left brace in expression following a logical or expression");
        var tempDefs = parseDefs(lexicalSymbol, string);
        currentLexicalSym = lexicalSymbol.next();
        Location end = currentLexicalSym.position.end;
        if (currentLexicalSym.tokenType != TokenType.OP_RBRACE)
            Report.error(currentLexicalSym.position,
                    "After definitions in a where definitions statemetn a right brace should close the block");
        return new Where(new Position(start, end), logicalExpr, tempDefs);
    }

    private Binary parseLogicalOrExpression(ListIterator<Symbol> lexicalSymbol) {
        dump("logical_ior_expression -> logical_and_expression logical_ior_expression_1");
        var tempLogical = parseLogicalAndExpression(lexicalSymbol);
        Symbol currentLexicalSym = lexicalSymbol.next();
        Location start = currentLexicalSym.position.start;
        if (currentLexicalSym.tokenType == TokenType.OP_OR) {
            dump("logical_ior_expression_1 -> \'|\' logical_and_expression logical_ior_expression_1");
            return parseLogicalOrExpression_1(lexicalSymbol, start, tempLogical);
        } else {
            dump("logical_ior_expression_1 -> ε");
            lexicalSymbol.previous();
            return tempLogical;
        }
    }

    private Binary parseLogicalOrExpression_1(ListIterator<Symbol> lexicalSymbol, Location start, Binary left) {
        var right = parseLogicalAndExpression(lexicalSymbol);
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType == TokenType.OP_OR) {
            dump("logical_ior_expression_1 -> \'|\' logical_and_expression logical_ior_expression_1");
            return parseLogicalOrExpression_1(lexicalSymbol, start, right);
        } else {
            dump("logical_ior_expression_1 -> ε");
            currentLexicalSym = lexicalSymbol.previous();
            return new Binary(new Position(start, currentLexicalSym.position.end), left, Binary.Operator.OR, right);
        }
    }

    private Binary parseLogicalAndExpression(ListIterator<Symbol> lexicalSymbol) {
        dump("logical_and_expression -> compare_expression logical_and_expression_1");
        var tempComp = parseCompareExpression(lexicalSymbol);
        Symbol currentLexicalSym = lexicalSymbol.next();
        Location start = currentLexicalSym.position.start;
        if (currentLexicalSym.tokenType == TokenType.OP_AND) {
            dump("logical_and_expression_1 -> \'&\' compare_expression logical_and_expression_1");
            return parseLogicalAndExpression_1(lexicalSymbol, start, tempComp);
        } else {
            dump("logical_and_expression_1 -> ε");
            currentLexicalSym = lexicalSymbol.previous();
            return new Binary(new Position(start, currentLexicalSym.position.end), tempComp, Binary.Operator.AND, null);
        }
    }

    private Binary parseLogicalAndExpression_1(ListIterator<Symbol> lexicalSymbol, Location start, Binary left) {
        var right = parseCompareExpression(lexicalSymbol);
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType == TokenType.OP_AND) {
            dump("logical_and_expression_1 -> \'&\' compare_expression logical_and_expression_1");
            return parseLogicalAndExpression_1(lexicalSymbol, start, right);
        } else {
            dump("logical_and_expression_1 -> ε");
            currentLexicalSym = lexicalSymbol.previous();
            return new Binary(new Position(start, currentLexicalSym.position.end), left, Binary.Operator.AND, right);
        }
    }

    private Binary parseCompareExpression(ListIterator<Symbol> lexicalSymbol) {
        dump("compare_expression -> additive_expr compare_expression_1");
        var tempAdditive = parseAdditiveExpression(lexicalSymbol);
        Symbol currentLexicalSym = lexicalSymbol.next();
        Location start = currentLexicalSym.position.start;
        switch (currentLexicalSym.tokenType) {
            case OP_EQ:
                dump("compare_expression_1 -> \'==\' additive_expr");
                return parseCompareExpression_1(lexicalSymbol, start, tempAdditive, Binary.Operator.EQ);
            case OP_LEQ:
                dump("compare_expression_1 -> \'<=\' additive_expr");
                return parseCompareExpression_1(lexicalSymbol, start, tempAdditive, Binary.Operator.LEQ);
            case OP_GEQ:
                dump("compare_expression_1 -> \'>=\' additive_expr");
                return parseCompareExpression_1(lexicalSymbol, start, tempAdditive, Binary.Operator.GEQ);
            case OP_NEQ:
                dump("compare_expression_1 -> \'!=\' additive_expr");
                return parseCompareExpression_1(lexicalSymbol, start, tempAdditive, Binary.Operator.NEQ);
            case OP_GT:
                dump("compare_expression_1 -> \'>\' additive_expr");
                return parseCompareExpression_1(lexicalSymbol, start, tempAdditive, Binary.Operator.GT);
            case OP_LT:
                dump("compare_expression_1 -> \'<\' additive_expr");
                return parseCompareExpression_1(lexicalSymbol, start, tempAdditive, Binary.Operator.LT);
            default:
                dump("compare_expression_1 -> ε");
                currentLexicalSym = lexicalSymbol.previous();
                return new Binary(new Position(start, currentLexicalSym.position.end), tempAdditive, null, null);
                break;
        }
    }

    private Binary parseCompareExpression_1(ListIterator<Symbol> lexicalSymbol, Location start, Expr left, Binary.Operator op) {
        var tempAdditive = parseAdditiveExpression(lexicalSymbol);
        return new Binary(new Position(start, tempAdditive.position.end), left, op, tempAdditive);
    }

    private Binary parseAdditiveExpression(ListIterator<Symbol> lexicalSymbol) {
        dump("additive_expr -> multiplicative_expression additive_expression_1");
        var tempMult = parseMultiplicativeExpression(lexicalSymbol);
        Symbol currentLexicalSym = lexicalSymbol.next();
        Location start = currentLexicalSym.position.start;
        if (currentLexicalSym.tokenType == TokenType.OP_ADD) {
            dump("additive_expression_1 -> \'+\' multiplicative_expression additive_expression_1");
            return parseAdditiveExpression_1(lexicalSymbol, start, tempMult, Binary.Operator.ADD);
        } else if (currentLexicalSym.tokenType == TokenType.OP_SUB) {
            dump("additive_expression_1 -> \'-\' multiplicative_expression additive_expression_1");
            return parseAdditiveExpression_1(lexicalSymbol, start, tempMult, Binary.Operator.SUB);
        } else {
            dump("additive_expression_1 -> ε");
            currentLexicalSym = lexicalSymbol.previous();
            return new Binary(new Position(start, currentLexicalSym.position.end), tempMult, null, null)
        }
    }

    private Binary parseAdditiveExpression_1(ListIterator<Symbol> lexicalSymbol, Location start, Binary left, Binary.Operator op) {
        var right = parseMultiplicativeExpression(lexicalSymbol);
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType == TokenType.OP_ADD) {
            dump("additive_expression_1 -> \'+\' multiplicative_expression additive_expression_1");
            op = Binary.Operator.ADD;
            return parseAdditiveExpression_1(lexicalSymbol, start, right, op);
        } else if (currentLexicalSym.tokenType == TokenType.OP_SUB) {
            dump("additive_expression_1 -> \'-\' multiplicative_expression additive_expression_1");
            op = Binary.Operator.SUB;
            return parseAdditiveExpression_1(lexicalSymbol, start, right, op);
        } else {
            dump("additive_expression_1 -> ε");
            currentLexicalSym = lexicalSymbol.previous();
            return new Binary(new Position(start, currentLexicalSym.position.end), left, op, right);
        }
    }

    private Binary parseMultiplicativeExpression(ListIterator<Symbol> lexicalSymbol) {
        dump("multiplicative_expression -> prefix_expression multiplicative_expression_1");
        var tempPrefix = parsePrefixExpression(lexicalSymbol);
        Symbol currentLexicalSym = lexicalSymbol.next();
        Location start = currentLexicalSym.position.start;
        if (currentLexicalSym.tokenType == TokenType.OP_MUL) {
            dump("multiplicative_expression_1 -> \'*\' prefix_expression multiplicative_expression_1");
            parseMultiplicativeExpression_1(lexicalSymbol);
        } else if (currentLexicalSym.tokenType == TokenType.OP_DIV) {
            dump("multiplicative_expression_1 -> \'/\' prefix_expression multiplicative_expression_1");
            parseMultiplicativeExpression_1(lexicalSymbol);
        } else if (currentLexicalSym.tokenType == TokenType.OP_MOD) {
            dump("multiplicative_expression_1 -> \'%\' prefix_expression multiplicative_expression_1");
            parseMultiplicativeExpression_1(lexicalSymbol);
        } else {
            dump("multiplicative_expression_1 -> ε");
            lexicalSymbol.previous();
        }
    }

    private Binary parseMultiplicativeExpression_1(ListIterator<Symbol> lexicalSymbol) {
        parsePrefixExpression(lexicalSymbol);
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType == TokenType.OP_MUL) {
            dump("multiplicative_expression_1 -> \'*\' prefix_expression multiplicative_expression_1");
            parseMultiplicativeExpression_1(lexicalSymbol);
        } else if (currentLexicalSym.tokenType == TokenType.OP_DIV) {
            dump("multiplicative_expression_1 -> \'/\' prefix_expression multiplicative_expression_1");
            parseMultiplicativeExpression_1(lexicalSymbol);
        } else if (currentLexicalSym.tokenType == TokenType.OP_MOD) {
            dump("multiplicative_expression_1 -> \'%\' prefix_expression multiplicative_expression_1");
            parseMultiplicativeExpression_1(lexicalSymbol);
        } else {
            dump("multiplicative_expression_1 -> ε");
            lexicalSymbol.previous();
        }
    }

    private Unary parsePrefixExpression(ListIterator<Symbol> lexicalSymbol) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType == TokenType.OP_ADD) {
            dump("prefix_expression -> \'+\' prefix_expression");
            parsePrefixExpression(lexicalSymbol);
        } else if (currentLexicalSym.tokenType == TokenType.OP_SUB) {
            dump("prefix_expression -> \'-\' prefix_expression");
            parsePrefixExpression(lexicalSymbol);
        } else if (currentLexicalSym.tokenType == TokenType.OP_NOT) {
            dump("prefix_expression -> \'!\' prefix_expression");
            parsePrefixExpression(lexicalSymbol);
        } else {
            dump("prefix_expression -> postfix_expression");
            lexicalSymbol.previous();
            parsePostfixExpression(lexicalSymbol);
        }
    }

    private Unary parsePostfixExpression(ListIterator<Symbol> lexicalSymbol) {
        dump("postfix_expression -> atom_expression postfix_expression_1");
        parseAtomExpression(lexicalSymbol);
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType == TokenType.OP_LBRACKET) {
            dump("postfix_expression_1 -> \'[\' expression \']\' postfix_expression_1");
            lexicalSymbol.previous();
            parsePostfixExpression_1(lexicalSymbol);
        } else {
            dump("postfix_expression_1 -> ε");
            lexicalSymbol.previous();
        }
    }

    private Unary parsePostfixExpression_1(ListIterator<Symbol> lexicalSymbol) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_LBRACKET)
            Report.error(currentLexicalSym.position, "Before expressions a left opening square bracket is required");
        parseExpression(lexicalSymbol);
        currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_RBRACKET)
            Report.error(currentLexicalSym.position,
                    "Following expressions a right closing square bracket is required");
        parsePostfixExpression(lexicalSymbol);
    }

    private Atom parseAtomExpression(ListIterator<Symbol> lexicalSymbol) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        switch (currentLexicalSym.tokenType) {
            case C_LOGICAL:
                dump("atom_expression -> logical_const");
                break;
            case C_INTEGER:
                dump("atom_expression -> integer_const");
                break;
            case C_STRING:
                dump("atom_expression -> string_const");
                break;
            case IDENTIFIER:
                dump("atom_expression -> id atom_expression_id");
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType == TokenType.OP_LPARENT) {
                    dump("atom_expression_id -> \'(\' expressions \')\'");
                    parseExpressions(lexicalSymbol);
                    currentLexicalSym = lexicalSymbol.next();
                    if (currentLexicalSym.tokenType != TokenType.OP_RPARENT)
                        Report.error(currentLexicalSym.position,
                                "Expressions should be closed with a closing right paranthesis");
                } else {
                    dump("atom_expression_id -> ε");
                    lexicalSymbol.previous();
                }
                break;
            case OP_LPARENT:
                dump("atom_expression -> \'(\' expressions \')\'");
                parseExpressions(lexicalSymbol);
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_RPARENT)
                    Report.error(currentLexicalSym.position,
                            "Expressions should be closed with a closing right paranthesis");
                break;
            case OP_LBRACE:
                dump("atom_expression -> \'{\' atom_expression_lbrace_1");
                parseAtomExpressionLBrace(lexicalSymbol);
                break;
            default:
                lexicalSymbol.previous();
                break;
        }
    }

    private Expr parseAtomExpressionLBrace(ListIterator<Symbol> lexicalSymbol) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        switch (currentLexicalSym.tokenType) {
            case KW_IF:
                dump("atom_expression_lbrace_1 -> if expression then expression if_else \'}\'");
                parseExpression(lexicalSymbol);
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.KW_THEN)
                    Report.error(currentLexicalSym.position,
                            "In the if statement after condition a then keyword should follow");
                parseExpression(lexicalSymbol);
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_RBRACE) {
                    dump("if_else -> else expression");
                    lexicalSymbol.previous();
                    parseIfElse(lexicalSymbol);
                    currentLexicalSym = lexicalSymbol.next();
                } else
                    dump("if_else -> ε");
                if (currentLexicalSym.tokenType != TokenType.OP_RBRACE)
                    Report.error(currentLexicalSym.position, "The if statment should be closed with a right brace");
                break;
            case KW_WHILE:
                dump("atom_expression_lbrace_1 -> while expression \':\' expression \'}\' .");
                parseExpression(lexicalSymbol);
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_COLON)
                    Report.error(currentLexicalSym.position, "After while condition a colon should follow");
                parseExpression(lexicalSymbol);
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_RBRACE)
                    Report.error(currentLexicalSym.position,
                            "After while body expression a right brace should close it");
                break;
            case KW_FOR:
                dump("atom_expression_lbrace_1 -> for id \'=\' expression \',\' expression \',\' expression \':\' expression \'}\'");
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
                    Report.error(currentLexicalSym.position, "Following a for keyword an identifier is required");
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_ASSIGN)
                    Report.error(currentLexicalSym.position,
                            "Following a for identifier an assignment operator is required");
                parseExpression(lexicalSymbol);
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_COMMA)
                    Report.error(currentLexicalSym.position,
                            "After the first expression in for loop statement a comma is required");
                parseExpression(lexicalSymbol);
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_COMMA)
                    Report.error(currentLexicalSym.position,
                            "After the second expression in for loop statement a comma is required");
                parseExpression(lexicalSymbol);
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_COLON)
                    Report.error(currentLexicalSym.position,
                            "After the third expression in for loop statement a colon is required");
                parseExpression(lexicalSymbol);
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_RBRACE)
                    Report.error(currentLexicalSym.position,
                            "After the for loop statement body a closing right curly brace is required");
                break;
            default:
                dump("atom_expression_lbrace_1 -> expression \'=\' expression \'}\'");
                lexicalSymbol.previous();
                parseExpression(lexicalSymbol);
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_ASSIGN)
                    Report.error(currentLexicalSym.position,
                            "Following an expression an assignment operator is required in this statment");
                parseExpression(lexicalSymbol);
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_RBRACE)
                    Report.error(currentLexicalSym.position,
                            "After the expression assignemnt statment a closing right curly brace is required");
                break;
        }
    }

    private Expr parseIfElse(ListIterator<Symbol> lexicalSymbol) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.KW_ELSE)
            Report.error(currentLexicalSym.position, "Either else statement or closing right brace expected");
        parseExpression(lexicalSymbol);
    }

    private Block parseExpressions(ListIterator<Symbol> lexicalSymbol) {
        dump("expressions -> expression expressions_1");
        parseExpression(lexicalSymbol);
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType == TokenType.OP_COMMA) {
            dump("expressions_1 -> \',\' expression expressions_1");
            parseExpressions_1(lexicalSymbol);
        } else {
            dump("expressions_1 -> ε");
            lexicalSymbol.previous();
        }
    }

    private Block parseExpressions_1(ListIterator<Symbol> lexicalSymbol) {
        parseExpression(lexicalSymbol);
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType == TokenType.OP_COMMA) {
            dump("expressions_1 -> \',\' expression expressions_1");
            parseExpressions_1(lexicalSymbol);
        } else {
            dump("expressions_1 -> ε");
            lexicalSymbol.previous();
        }
    }

    private List<FunDef.Parameter> parseParams(ListIterator<Symbol> lexicalSymbol, String string) {
        List<FunDef.Parameter> paramsList = new ArrayList<FunDef.Parameter>();
        dump("parameters -> parameter parameters_1");
        paramsList.add(parseParam(lexicalSymbol, string));
        return parseParams_1(lexicalSymbol, paramsList, string);
    }

    private List<FunDef.Parameter> parseParams_1(ListIterator<Symbol> lexicalSymbol, List<FunDef.Parameter> paramsList,
            String string) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType == TokenType.OP_COMMA) {
            dump("parameters_1 -> , parameter parameters_1");
            paramsList.add(parseParam(lexicalSymbol, string));
            return parseParams_1(lexicalSymbol, paramsList, string);
        } else {
            dump("parameters_1 -> ε");
            lexicalSymbol.previous();
            return paramsList;
        }
    }

    private FunDef.Parameter parseParam(ListIterator<Symbol> lexicalSymbol, String string) {
        dump("parameter -> id : type");
        Symbol currentLexicalSym = lexicalSymbol.next();
        Location start = currentLexicalSym.position.start;
        String tmp = currentLexicalSym.lexeme + " ";
        if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
            Report.error(currentLexicalSym.position, "Parameter declaration must begin with its identifier");
        currentLexicalSym = lexicalSymbol.next();
        tmp = currentLexicalSym.lexeme + " ";
        if (currentLexicalSym.tokenType != TokenType.OP_COLON)
            Report.error(currentLexicalSym.position, "After parameter identificator a colon is required");
        var tempType = parseType(lexicalSymbol, string);
        string += tmp + " ";
        return new FunDef.Parameter(new Position(start, tempType.position.end), tmp, tempType);
    }

    private VarDef parseVarDef(ListIterator<Symbol> lexicalSymbol, Location start, String string) {
        String tmp = "var ";
        Symbol currentLexicalSym = lexicalSymbol.next();
        tmp += currentLexicalSym.lexeme;
        dump("variable_definition -> var id : type");
        if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
            Report.error(currentLexicalSym.position, "Variable definition identifier is wrong");
        currentLexicalSym = lexicalSymbol.next();
        tmp += currentLexicalSym.lexeme;
        if (currentLexicalSym.tokenType != TokenType.OP_COLON)
            Report.error(currentLexicalSym.position, "After variable definition identifier a colon \':\' must follow");
        string += tmp + " ";
        var tempType = parseType(lexicalSymbol, string);
        return new VarDef(new Position(start, tempType.position.end), tmp, tempType);
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
