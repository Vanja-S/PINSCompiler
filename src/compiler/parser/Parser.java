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
import compiler.parser.ast.expr.*;
import compiler.parser.ast.type.*;

public class Parser {
    /**
     * Seznam leksikalnih simbolov.
     */
    @SuppressWarnings("unused")
    private final List<Symbol> symbols;
    private final ListIterator<Symbol> symbol_iterator;

    /**
     * Ciljni tok, kamor izpisujemo produkcije. Če produkcij ne želimo izpisovati,
     * vrednost opcijske spremenljivke nastavimo na Optional.empty().
     */
    private final Optional<PrintStream> productionsOutputStream;

    public Parser(List<Symbol> symbols, Optional<PrintStream> productionsOutputStream) {
        requireNonNull(symbols, productionsOutputStream);
        this.symbols = symbols;
        this.productionsOutputStream = productionsOutputStream;
        this.symbol_iterator = symbols.listIterator();
    }

    /**
     * Izvedi sintaksno analizo.
     */
    public Ast parse() {
        return parseSource();
    }

    private Ast parseSource() {
        dump("source -> definitions");
        var tempDefs = parseDefs();
        Symbol curr_sym = symbol_iterator.next();
        if (symbol_iterator.hasNext() && curr_sym.tokenType != TokenType.EOF) {
            Report.error(curr_sym.position, "There is no EOF at the end of file");
            return null;
        } else
            return tempDefs;
    }

    private Defs parseDefs() {
        Location start = symbol_iterator.next().position.start;
        symbol_iterator.previous();
        List<Def> defsList = new ArrayList<Def>();
        dump("definitions -> definition definitions_1");
        defsList.add(parseDef());
        return parseDefs_1(start, defsList);
    }

    private Defs parseDefs_1(Location start, List<Def> defsList) {
        Symbol currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType == TokenType.OP_SEMICOLON) {
            dump("definitions_1 -> ; definition definitions_1");
            defsList.add(parseDef());
            return parseDefs_1(start, defsList);
        } else {
            dump("definitions_1 -> ε");
            var tempDefs = new Defs(new Position(start, defsList.get(defsList.size() - 1).position.end), defsList);
            symbol_iterator.previous();
            return tempDefs;
        }
    }

    private Def parseDef() {
        Symbol currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType == TokenType.KW_TYP) {
            dump("definition -> type_definition");
            return parseTypeDef(currentLexicalSym.position.start);
        } else if (currentLexicalSym.tokenType == TokenType.KW_FUN) {
            dump("definition -> function_definition");
            return parseFunDef(currentLexicalSym.position.start);
        } else if (currentLexicalSym.tokenType == TokenType.KW_VAR) {
            dump("definition -> variable_definition");
            return parseVarDef(currentLexicalSym.position.start);
        } else {
            Report.error(currentLexicalSym.position, "Wrong definition statment");
            return null;
        }
    }

    private TypeDef parseTypeDef(Location start) {
        String tmp = "";
        dump("type_definition -> typ id : type");
        Symbol currentLexicalSym = symbol_iterator.next();
        tmp += currentLexicalSym.lexeme;
        if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
            Report.error(currentLexicalSym.position, "Type definition identifier is wrong");
        currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType != TokenType.OP_COLON)
            Report.error(currentLexicalSym.position, "After type definition identifier a colon \':\' must follow");
        Type tempType = parseType();
        return new TypeDef(new Position(start, tempType.position.end), tmp.toString(), tempType);
    }

    private Type parseType() {
        Symbol currentLexicalSym = symbol_iterator.next();
        Location start = currentLexicalSym.position.start;
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
            currentLexicalSym = symbol_iterator.next();
            if (currentLexicalSym.tokenType != TokenType.OP_LBRACKET)
                Report.error(currentLexicalSym.position, "Specifying arr lenght must be enclosed in square brackets");
            currentLexicalSym = symbol_iterator.next();
            if (currentLexicalSym.tokenType != TokenType.C_INTEGER)
                Report.error(currentLexicalSym.position, "Array arr lenght must be an integer");
            int size = Integer.parseInt(currentLexicalSym.lexeme);
            currentLexicalSym = symbol_iterator.next();
            if (currentLexicalSym.tokenType != TokenType.OP_RBRACKET)
                Report.error(currentLexicalSym.position, "Specifying arr lenght must be enclosed in square brackets");
            var tempType = parseType();
            return new Array(new Position(start, tempType.position.end), size, tempType);
        } else {
            dump("type -> id");
            return new TypeName(currentLexicalSym.position, currentLexicalSym.lexeme);
        }
    }

    private FunDef parseFunDef(Location start) {
        dump("function_definition -> fun id \'(\' parameters \')\' \':\' type \'=\' expression");
        String tmp = "";
        Symbol currentLexicalSym = symbol_iterator.next();
        tmp += currentLexicalSym.lexeme;
        if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
            Report.error(currentLexicalSym.position,
                    "After the keyword fun, an identifier is required to name the function");
        currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType != TokenType.OP_LPARENT)
            Report.error(currentLexicalSym.position,
                    "Function parameters should be enclosed in paranthesis, the left one is missing or misplaced");
        var tempParams = parseParams();

        currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType != TokenType.OP_RPARENT)
            Report.error(currentLexicalSym.position,
                    "Function parameters should be enclosed in paranthesis, the right one is missing or misplaced");

        currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType != TokenType.OP_COLON)
            Report.error(currentLexicalSym.position,
                    "Following function declaration a colon is required to denote the body");

        var tempType = parseType();
        currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType != TokenType.OP_ASSIGN)
            Report.error(currentLexicalSym.position,
                    "Following the type in a function declaration an assignment operator is required");
        var tempExpr = parseExpression();
        return new FunDef(new Position(start, tempExpr.position.end), tmp, tempParams, tempType, tempExpr);
    }

    private Expr parseExpression() {
        dump("expression -> logical_ior_expression expression_1");
        var logicalExpr = parseLogicalOrExpression();
        Symbol currentLexicalSym = symbol_iterator.next();
        Location start = currentLexicalSym.position.start;
        if (currentLexicalSym.tokenType == TokenType.OP_LBRACE) {
            dump("expression_1 -> { WHERE definitions }");
            return parseExpression_1(start, logicalExpr);
        } else {
            dump("expression_1 -> ε");
            symbol_iterator.previous();
            return logicalExpr;
        }
    }

    private Expr parseExpression_1(Location start, Expr logicalExpr) {
        Symbol currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType != TokenType.KW_WHERE)
            Report.error(currentLexicalSym.position,
                    "The keyword where is required after left brace in expression following a logical or expression");
        var tempDefs = parseDefs();
        currentLexicalSym = symbol_iterator.next();
        Location end = currentLexicalSym.position.end;
        if (currentLexicalSym.tokenType != TokenType.OP_RBRACE)
            Report.error(currentLexicalSym.position,
                    "After definitions in a where definitions statemetn a right brace should close the block");
        return new Where(new Position(logicalExpr.position.start, end), logicalExpr, tempDefs);
    }

    private Expr parseLogicalOrExpression() {
        dump("logical_ior_expression -> logical_and_expression logical_ior_expression_1");
        var tempLogical = parseLogicalAndExpression();
        Symbol currentLexicalSym = symbol_iterator.next();
        Location start = currentLexicalSym.position.start;
        if (currentLexicalSym.tokenType == TokenType.OP_OR) {
            dump("logical_ior_expression_1 -> \'|\' logical_and_expression logical_ior_expression_1");
            return parseLogicalOrExpression_1(start, tempLogical);
        } else {
            dump("logical_ior_expression_1 -> ε");
            symbol_iterator.previous();
            return tempLogical;
        }
    }

    private Binary parseLogicalOrExpression_1(Location start, Expr left) {
        var right = parseLogicalAndExpression();
        var tempBinary = new Binary(new Position(left.position.start, right.position.end), left, Binary.Operator.OR,
                right);
        Symbol currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType == TokenType.OP_OR) {
            dump("logical_ior_expression_1 -> \'|\' logical_and_expression logical_ior_expression_1");
            return parseLogicalOrExpression_1(start, tempBinary);
        } else {
            dump("logical_ior_expression_1 -> ε");
            currentLexicalSym = symbol_iterator.previous();
            return tempBinary;
        }
    }

    private Expr parseLogicalAndExpression() {
        dump("logical_and_expression -> compare_expression logical_and_expression_1");
        var tempComp = parseCompareExpression();
        Symbol currentLexicalSym = symbol_iterator.next();
        Location start = tempComp.position.start;
        if (currentLexicalSym.tokenType == TokenType.OP_AND) {
            dump("logical_and_expression_1 -> \'&\' compare_expression logical_and_expression_1");
            return parseLogicalAndExpression_1(start, tempComp);
        } else {
            dump("logical_and_expression_1 -> ε");
            currentLexicalSym = symbol_iterator.previous();
            return tempComp;
        }
    }

    private Binary parseLogicalAndExpression_1(Location start, Expr left) {
        var right = parseCompareExpression();
        var tempBinary = new Binary(new Position(left.position.start, right.position.end), left, Binary.Operator.AND,
                right);
        Symbol currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType == TokenType.OP_AND) {
            dump("logical_and_expression_1 -> \'&\' compare_expression logical_and_expression_1");
            return parseLogicalAndExpression_1(start, tempBinary);
        } else {
            dump("logical_and_expression_1 -> ε");
            currentLexicalSym = symbol_iterator.previous();
            return tempBinary;
        }
    }

    private Expr parseCompareExpression() {
        dump("compare_expression -> additive_expr compare_expression_1");
        var tempAdditive = parseAdditiveExpression();
        Symbol currentLexicalSym = symbol_iterator.next();
        switch (currentLexicalSym.tokenType) {
            case OP_EQ:
                dump("compare_expression_1 -> \'==\' additive_expr");
                return parseCompareExpression_1(tempAdditive.position.start, tempAdditive,
                        Binary.Operator.EQ);
            case OP_LEQ:
                dump("compare_expression_1 -> \'<=\' additive_expr");
                return parseCompareExpression_1(tempAdditive.position.start, tempAdditive,
                        Binary.Operator.LEQ);
            case OP_GEQ:
                dump("compare_expression_1 -> \'>=\' additive_expr");
                return parseCompareExpression_1(tempAdditive.position.start, tempAdditive,
                        Binary.Operator.GEQ);
            case OP_NEQ:
                dump("compare_expression_1 -> \'!=\' additive_expr");
                return parseCompareExpression_1(tempAdditive.position.start, tempAdditive,
                        Binary.Operator.NEQ);
            case OP_GT:
                dump("compare_expression_1 -> \'>\' additive_expr");
                return parseCompareExpression_1(tempAdditive.position.start, tempAdditive,
                        Binary.Operator.GT);
            case OP_LT:
                dump("compare_expression_1 -> \'<\' additive_expr");
                return parseCompareExpression_1(tempAdditive.position.start, tempAdditive,
                        Binary.Operator.LT);
            default:
                dump("compare_expression_1 -> ε");
                symbol_iterator.previous();
                return tempAdditive;
        }
    }

    private Binary parseCompareExpression_1(Location start, Expr left, Binary.Operator op) {
        var tempAdditive = parseAdditiveExpression();
        return new Binary(new Position(start, tempAdditive.position.end), left, op, tempAdditive);
    }

    private Expr parseAdditiveExpression() {
        dump("additive_expr -> multiplicative_expression additive_expression_1");
        var tempMult = parseMultiplicativeExpression();
        Symbol currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType == TokenType.OP_ADD) {
            dump("additive_expression_1 -> \'+\' multiplicative_expression additive_expression_1");
            return parseAdditiveExpression_1(tempMult.position.start, tempMult, Binary.Operator.ADD);
        } else if (currentLexicalSym.tokenType == TokenType.OP_SUB) {
            dump("additive_expression_1 -> \'-\' multiplicative_expression additive_expression_1");
            return parseAdditiveExpression_1(tempMult.position.start, tempMult, Binary.Operator.SUB);
        } else {
            dump("additive_expression_1 -> ε");
            currentLexicalSym = symbol_iterator.previous();
            return tempMult;
        }
    }

    private Binary parseAdditiveExpression_1(Location start, Expr left, Binary.Operator op) {
        var right = parseMultiplicativeExpression();
        var tempBinary = new Binary(new Position(left.position.start, right.position.end), left, op, right);
        Symbol currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType == TokenType.OP_ADD) {
            dump("additive_expression_1 -> \'+\' multiplicative_expression additive_expression_1");
            op = Binary.Operator.ADD;
            return parseAdditiveExpression_1(start, tempBinary, op);
        } else if (currentLexicalSym.tokenType == TokenType.OP_SUB) {
            dump("additive_expression_1 -> \'-\' multiplicative_expression additive_expression_1");
            op = Binary.Operator.SUB;
            return parseAdditiveExpression_1(start, tempBinary, op);
        } else {
            dump("additive_expression_1 -> ε");
            symbol_iterator.previous();
            return tempBinary;
        }
    }

    private Expr parseMultiplicativeExpression() {
        dump("multiplicative_expression -> prefix_expression multiplicative_expression_1");
        var tempPrefix = parsePrefixExpression();
        Symbol currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType == TokenType.OP_MUL) {
            dump("multiplicative_expression_1 -> \'*\' prefix_expression multiplicative_expression_1");
            return parseMultiplicativeExpression_1(tempPrefix.position.start, tempPrefix,
                    Binary.Operator.MUL);
        } else if (currentLexicalSym.tokenType == TokenType.OP_DIV) {
            dump("multiplicative_expression_1 -> \'/\' prefix_expression multiplicative_expression_1");
            return parseMultiplicativeExpression_1(tempPrefix.position.start, tempPrefix,
                    Binary.Operator.DIV);
        } else if (currentLexicalSym.tokenType == TokenType.OP_MOD) {
            dump("multiplicative_expression_1 -> \'%\' prefix_expression multiplicative_expression_1");
            return parseMultiplicativeExpression_1(tempPrefix.position.start, tempPrefix,
                    Binary.Operator.MOD);
        } else {
            dump("multiplicative_expression_1 -> ε");
            symbol_iterator.previous();
            return tempPrefix;
        }
    }

    private Binary parseMultiplicativeExpression_1(Location start, Expr left, Binary.Operator op) {
        var right = parsePrefixExpression();
        var tempBinary = new Binary(new Position(left.position.start, right.position.end), left, op, right);
        Symbol currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType == TokenType.OP_MUL) {
            dump("multiplicative_expression_1 -> \'*\' prefix_expression multiplicative_expression_1");
            return parseMultiplicativeExpression_1(start, tempBinary, Binary.Operator.MUL);
        } else if (currentLexicalSym.tokenType == TokenType.OP_DIV) {
            dump("multiplicative_expression_1 -> \'/\' prefix_expression multiplicative_expression_1");
            return parseMultiplicativeExpression_1(start, tempBinary, Binary.Operator.DIV);
        } else if (currentLexicalSym.tokenType == TokenType.OP_MOD) {
            dump("multiplicative_expression_1 -> \'%\' prefix_expression multiplicative_expression_1");
            return parseMultiplicativeExpression_1(start, tempBinary, Binary.Operator.MOD);
        } else {
            dump("multiplicative_expression_1 -> ε");
            symbol_iterator.previous();
            return tempBinary;
        }
    }

    private Expr parsePrefixExpression() {
        Symbol currentLexicalSym = symbol_iterator.next();
        Location start = currentLexicalSym.position.start;
        Unary.Operator op = null;
        Expr tempPrefix;
        if (currentLexicalSym.tokenType == TokenType.OP_ADD) {
            dump("prefix_expression -> \'+\' prefix_expression");
            tempPrefix = parsePrefixExpression();
            op = Unary.Operator.ADD;
        } else if (currentLexicalSym.tokenType == TokenType.OP_SUB) {
            dump("prefix_expression -> \'-\' prefix_expression");
            tempPrefix = parsePrefixExpression();
            op = Unary.Operator.SUB;
        } else if (currentLexicalSym.tokenType == TokenType.OP_NOT) {
            dump("prefix_expression -> \'!\' prefix_expression");
            tempPrefix = parsePrefixExpression();
            op = Unary.Operator.NOT;
        } else {
            dump("prefix_expression -> postfix_expression");
            symbol_iterator.previous();
            tempPrefix = parsePostfixExpression();
            return tempPrefix;
        }
        return new Unary(new Position(start, tempPrefix.position.end), tempPrefix, op);
    }

    private Expr parsePostfixExpression() {
        dump("postfix_expression -> atom_expression postfix_expression_1");
        Expr tempAtom = parseAtomExpression();
        Symbol currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType == TokenType.OP_LBRACKET) {
            dump("postfix_expression_1 -> \'[\' expression \']\' postfix_expression_1");
            symbol_iterator.previous();
            return parsePostfixExpression_1(tempAtom);
        } else {
            dump("postfix_expression_1 -> ε");
            symbol_iterator.previous();
            return tempAtom;
        }
    }

    private Expr parsePostfixExpression_1(Expr leftExpr) {
        Symbol currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType != TokenType.OP_LBRACKET)
            Report.error(currentLexicalSym.position, "Before expressions a left opening square bracket is required");
        var tempExpr = parseExpression();
        currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType != TokenType.OP_RBRACKET)
            Report.error(currentLexicalSym.position,
                    "Following expressions a right closing square bracket is required");
        currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType == TokenType.OP_LBRACKET) {
            var tempBinary = new Binary(new Position(leftExpr.position.start, tempExpr.position.end), leftExpr,Binary.Operator.ARR, tempExpr);
            return parsePostfixExpression_1(tempBinary);
        } else {
            symbol_iterator.previous();
            return new Binary(new Position(leftExpr.position.start, tempExpr.position.end), leftExpr,
                    Binary.Operator.ARR, tempExpr);
        }
    }

    private Expr parseAtomExpression() {
        Symbol currentLexicalSym = symbol_iterator.next();
        Symbol id = currentLexicalSym;
        switch (currentLexicalSym.tokenType) {
            case C_LOGICAL:
                dump("atom_expression -> logical_const");
                return new Literal(new Position(currentLexicalSym.position.start, currentLexicalSym.position.end),
                        currentLexicalSym.lexeme, Atom.Type.LOG);
            case C_INTEGER:
                dump("atom_expression -> integer_const");
                return new Literal(new Position(currentLexicalSym.position.start, currentLexicalSym.position.end),
                        currentLexicalSym.lexeme, Atom.Type.INT);
            case C_STRING:
                dump("atom_expression -> string_const");
                return new Literal(new Position(currentLexicalSym.position.start, currentLexicalSym.position.end),
                        currentLexicalSym.lexeme, Atom.Type.STR);
            case IDENTIFIER:
                dump("atom_expression -> id atom_expression_id");
                currentLexicalSym = symbol_iterator.next();
                if (currentLexicalSym.tokenType == TokenType.OP_LPARENT) {
                    dump("atom_expression_id -> \'(\' expressions \')\'");
                    var tempExprs = parseExpressions(currentLexicalSym.position.start);
                    currentLexicalSym = symbol_iterator.next();
                    if (currentLexicalSym.tokenType != TokenType.OP_RPARENT)
                        Report.error(currentLexicalSym.position,
                                "Expressions should be closed with a closing right paranthesis");
                    return new Call(new Position(id.position.start, currentLexicalSym.position.end),
                            tempExprs.expressions,
                            id.lexeme);
                } else {
                    dump("atom_expression_id -> ε");
                    symbol_iterator.previous();
                    return new Name(new Position(id.position.start, id.position.end), id.lexeme);
                }
            case OP_LPARENT:
                dump("atom_expression -> \'(\' expressions \')\'");
                var tempExprs = parseExpressions(currentLexicalSym.position.start);
                currentLexicalSym = symbol_iterator.next();
                if (currentLexicalSym.tokenType != TokenType.OP_RPARENT)
                    Report.error(currentLexicalSym.position,
                            "Expressions should be closed with a closing right paranthesis");
                return tempExprs;
            case OP_LBRACE:
                dump("atom_expression -> \'{\' atom_expression_lbrace_1");
                return parseAtomExpressionLBrace(currentLexicalSym.position.start);
            default:
                Report.error(currentLexicalSym.position, "Unexpected symbol");
                return null;
        }
    }

    private Expr parseAtomExpressionLBrace(Location start) {
        Symbol currentLexicalSym = symbol_iterator.next();
        switch (currentLexicalSym.tokenType) {
            case KW_IF:
                dump("atom_expression_lbrace_1 -> if expression then expression if_else \'}\'");
                var tempExpr1 = parseExpression();
                currentLexicalSym = symbol_iterator.next();
                Optional<Expr> _else = null;
                if (currentLexicalSym.tokenType != TokenType.KW_THEN)
                    Report.error(currentLexicalSym.position,
                            "In the if statement after condition a then keyword should follow");
                var tempExpr2 = parseExpression();
                currentLexicalSym = symbol_iterator.next();
                if (currentLexicalSym.tokenType != TokenType.OP_RBRACE) {
                    dump("if_else -> else expression");
                    symbol_iterator.previous();
                    _else = parseIfElse();
                    currentLexicalSym = symbol_iterator.next();
                } else {
                    dump("if_else -> ε");
                    _else = Optional.empty();
                }
                if (currentLexicalSym.tokenType != TokenType.OP_RBRACE)
                    Report.error(currentLexicalSym.position, "The if statment should be closed with a right brace");
                return new IfThenElse(new Position(start, currentLexicalSym.position.end), tempExpr1, tempExpr2, _else);
            case KW_WHILE:
                dump("atom_expression_lbrace_1 -> while expression \':\' expression \'}\' .");
                var tempExprWhileCond = parseExpression();
                currentLexicalSym = symbol_iterator.next();
                if (currentLexicalSym.tokenType != TokenType.OP_COLON)
                    Report.error(currentLexicalSym.position, "After while condition a colon should follow");
                var tempExprWhileExpr = parseExpression();
                currentLexicalSym = symbol_iterator.next();
                if (currentLexicalSym.tokenType != TokenType.OP_RBRACE)
                    Report.error(currentLexicalSym.position,
                            "After while body expression a right brace should close it");
                return new While(new Position(start, currentLexicalSym.position.end), tempExprWhileCond,
                        tempExprWhileExpr);
            case KW_FOR:
                dump("atom_expression_lbrace_1 -> for id \'=\' expression \',\' expression \',\' expression \':\' expression \'}\'");
                currentLexicalSym = symbol_iterator.next();
                var tempCounter = currentLexicalSym;
                if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
                    Report.error(currentLexicalSym.position, "Following a for keyword counter identifier is required");
                currentLexicalSym = symbol_iterator.next();
                if (currentLexicalSym.tokenType != TokenType.OP_ASSIGN)
                    Report.error(currentLexicalSym.position,
                            "Following a for identifier an assignment operator is required");
                var tempExprFor1 = parseExpression();
                currentLexicalSym = symbol_iterator.next();
                if (currentLexicalSym.tokenType != TokenType.OP_COMMA)
                    Report.error(currentLexicalSym.position,
                            "After the first expression in for loop statement a comma is required");
                var tempExprFor2 = parseExpression();
                currentLexicalSym = symbol_iterator.next();
                if (currentLexicalSym.tokenType != TokenType.OP_COMMA)
                    Report.error(currentLexicalSym.position,
                            "After the second expression in for loop statement a comma is required");
                var tempExprFor3 = parseExpression();
                currentLexicalSym = symbol_iterator.next();
                if (currentLexicalSym.tokenType != TokenType.OP_COLON)
                    Report.error(currentLexicalSym.position,
                            "After the third expression in for loop statement a colon is required");
                var tempForBody = parseExpression();
                currentLexicalSym = symbol_iterator.next();
                if (currentLexicalSym.tokenType != TokenType.OP_RBRACE)
                    Report.error(currentLexicalSym.position,
                            "After the for loop statement body a closing right curly brace is required");
                return new For(new Position(start, currentLexicalSym.position.end),
                        new Name(tempCounter.position, tempCounter.lexeme), tempExprFor1, tempExprFor2, tempExprFor3,
                        tempForBody);
            default:
                dump("atom_expression_lbrace_1 -> expression \'=\' expression \'}\'");
                symbol_iterator.previous();
                var tempExprAss = parseExpression();
                currentLexicalSym = symbol_iterator.next();
                if (currentLexicalSym.tokenType != TokenType.OP_ASSIGN)
                    Report.error(currentLexicalSym.position,
                            "Following an expression an assignment operator is required in this statment");
                var tempExprAss2 = parseExpression();
                currentLexicalSym = symbol_iterator.next();
                if (currentLexicalSym.tokenType != TokenType.OP_RBRACE)
                    Report.error(currentLexicalSym.position,
                            "After the expression assignemnt statment a closing right curly brace is required");
                return new Binary(new Position(start, currentLexicalSym.position.end), tempExprAss,
                        Binary.Operator.ASSIGN, tempExprAss2);
        }
    }

    private Optional<Expr> parseIfElse() {
        Symbol currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType != TokenType.KW_ELSE)
            Report.error(currentLexicalSym.position, "Either else statement or closing right brace expected");
        return Optional.of(parseExpression());
    }

    private Block parseExpressions(Location start) {
        dump("expressions -> expression expressions_1");
        List<Expr> expressions = new ArrayList<Expr>();
        expressions.add(parseExpression());
        Symbol currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType == TokenType.OP_COMMA) {
            dump("expressions_1 -> \',\' expression expressions_1");
            return parseExpressions_1(expressions, start);
        } else {
            dump("expressions_1 -> ε");
            currentLexicalSym = symbol_iterator.previous();
            return new Block(new Position(start, currentLexicalSym.position.end), expressions);
        }
    }

    private Block parseExpressions_1(List<Expr> expressions, Location start) {
        expressions.add(parseExpression());
        Symbol currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType == TokenType.OP_COMMA) {
            dump("expressions_1 -> \',\' expression expressions_1");
            return parseExpressions_1(expressions, start);
        } else {
            dump("expressions_1 -> ε");
            currentLexicalSym = symbol_iterator.previous();
            return new Block(new Position(start, currentLexicalSym.position.end), expressions);
        }
    }

    private List<FunDef.Parameter> parseParams() {
        List<FunDef.Parameter> paramsList = new ArrayList<FunDef.Parameter>();
        dump("parameters -> parameter parameters_1");
        paramsList.add(parseParam());
        return parseParams_1(paramsList);
    }

    private List<FunDef.Parameter> parseParams_1(List<FunDef.Parameter> paramsList) {
        Symbol currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType == TokenType.OP_COMMA) {
            dump("parameters_1 -> , parameter parameters_1");
            paramsList.add(parseParam());
            return parseParams_1(paramsList);
        } else {
            dump("parameters_1 -> ε");
            symbol_iterator.previous();
            return paramsList;
        }
    }

    private FunDef.Parameter parseParam() {
        dump("parameter -> id : type");
        Symbol currentLexicalSym = symbol_iterator.next();
        Location start = currentLexicalSym.position.start;
        String tmp = currentLexicalSym.lexeme;
        if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
            Report.error(currentLexicalSym.position, "Parameter declaration must begin with its identifier");
        currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType != TokenType.OP_COLON)
            Report.error(currentLexicalSym.position, "After parameter identificator a colon is required");
        var tempType = parseType();
        return new FunDef.Parameter(new Position(start, tempType.position.end), tmp.toString(), tempType);
    }

    private VarDef parseVarDef(Location start) {
        String tmp = "";
        Symbol currentLexicalSym = symbol_iterator.next();
        tmp += currentLexicalSym.lexeme;
        dump("variable_definition -> var id : type");
        if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
            Report.error(currentLexicalSym.position, "Variable definition identifier is wrong");
        currentLexicalSym = symbol_iterator.next();
        if (currentLexicalSym.tokenType != TokenType.OP_COLON)
            Report.error(currentLexicalSym.position, "After variable definition identifier a colon \':\' must follow");
        var tempType = parseType();
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