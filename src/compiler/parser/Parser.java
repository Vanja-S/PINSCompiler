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

    private Defs parseDefs_1(ListIterator<Symbol> lexicalSymbol, Location start, List<Def> defsList,
            String string) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType == TokenType.OP_SEMICOLON) {
            dump("definitions_1 -> ; definition definitions_1");
            defsList.add(parseDef(lexicalSymbol, string));
            return parseDefs_1(lexicalSymbol, start, defsList, string);
        } else {
            dump("definitions_1 -> ε");
            var tempDefs = new Defs(new Position(start, defsList.get(defsList.size() - 1).position.end), defsList);
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
        String tmp = "";
        dump("type_definition -> typ id : type");
        Symbol currentLexicalSym = lexicalSymbol.next();
        tmp += currentLexicalSym.lexeme;
        if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
            Report.error(currentLexicalSym.position, "Type definition identifier is wrong");
        currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_COLON)
            Report.error(currentLexicalSym.position, "After type definition identifier a colon \':\' must follow");
        string += tmp + " ";
        Type tempType = parseType(lexicalSymbol, string);
        return new TypeDef(new Position(start, tempType.position.end), tmp.toString(), tempType);
    }

    private Type parseType(ListIterator<Symbol> lexicalSymbol, String string) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        Location start = currentLexicalSym.position.start;
        string += currentLexicalSym.lexeme;
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
            string += currentLexicalSym.lexeme + " ";
            currentLexicalSym = lexicalSymbol.next();
            if (currentLexicalSym.tokenType != TokenType.C_INTEGER)
                Report.error(currentLexicalSym.position, "Array arr lenght must be an integer");
            int size = Integer.parseInt(currentLexicalSym.lexeme);
            string += currentLexicalSym.lexeme + " ";
            currentLexicalSym = lexicalSymbol.next();
            if (currentLexicalSym.tokenType != TokenType.OP_RBRACKET)
                Report.error(currentLexicalSym.position, "Specifying arr lenght must be enclosed in square brackets");
            string += currentLexicalSym.lexeme + " ";
            var tempType = parseType(lexicalSymbol, string);
            return new Array(new Position(start, tempType.position.end), size, tempType);
        } else {
            dump("type -> id");
            string += currentLexicalSym.lexeme + " ";
            return new TypeName(currentLexicalSym.position, currentLexicalSym.lexeme);
        }
    }

    private FunDef parseFunDef(ListIterator<Symbol> lexicalSymbol, Location start, String string) {
        dump("function_definition -> fun id \'(\' parameters \')\' \':\' type \'=\' expression");
        String tmp = "";
        Symbol currentLexicalSym = lexicalSymbol.next();
        tmp += currentLexicalSym.lexeme;
        if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
            Report.error(currentLexicalSym.position,
                    "After the keyword fun, an identifier is required to name the function");
        currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_LPARENT)
            Report.error(currentLexicalSym.position,
                    "Function parameters should be enclosed in paranthesis, the left one is missing or misplaced");
        var tempParams = parseParams(lexicalSymbol, tmp);

        currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_RPARENT)
            Report.error(currentLexicalSym.position,
                    "Function parameters should be enclosed in paranthesis, the right one is missing or misplaced");

        currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_COLON)
            Report.error(currentLexicalSym.position,
                    "Following function declaration a colon is required to denote the body");

        string += tmp + " ";
        var tempType = parseType(lexicalSymbol, string);
        currentLexicalSym = lexicalSymbol.next();
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

    private Expr parseLogicalOrExpression(ListIterator<Symbol> lexicalSymbol) {
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

    private Binary parseLogicalOrExpression_1(ListIterator<Symbol> lexicalSymbol, Location start, Expr left) {
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

    private Expr parseLogicalAndExpression(ListIterator<Symbol> lexicalSymbol) {
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
            return tempComp;
        }
    }

    private Binary parseLogicalAndExpression_1(ListIterator<Symbol> lexicalSymbol, Location start, Expr left) {
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

    private Expr parseCompareExpression(ListIterator<Symbol> lexicalSymbol) {
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
                return tempAdditive;
        }
    }

    private Binary parseCompareExpression_1(ListIterator<Symbol> lexicalSymbol, Location start, Expr left,
            Binary.Operator op) {
        var tempAdditive = parseAdditiveExpression(lexicalSymbol);
        return new Binary(new Position(start, tempAdditive.position.end), left, op, tempAdditive);
    }

    private Expr parseAdditiveExpression(ListIterator<Symbol> lexicalSymbol) {
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
            return tempMult;
        }
    }

    private Binary parseAdditiveExpression_1(ListIterator<Symbol> lexicalSymbol, Location start, Expr left,
            Binary.Operator op) {
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

    private Expr parseMultiplicativeExpression(ListIterator<Symbol> lexicalSymbol) {
        dump("multiplicative_expression -> prefix_expression multiplicative_expression_1");
        var tempPrefix = parsePrefixExpression(lexicalSymbol);
        Symbol currentLexicalSym = lexicalSymbol.next();
        Location start = currentLexicalSym.position.start;
        if (currentLexicalSym.tokenType == TokenType.OP_MUL) {
            dump("multiplicative_expression_1 -> \'*\' prefix_expression multiplicative_expression_1");
            return parseMultiplicativeExpression_1(lexicalSymbol, start, tempPrefix, Binary.Operator.MUL);
        } else if (currentLexicalSym.tokenType == TokenType.OP_DIV) {
            dump("multiplicative_expression_1 -> \'/\' prefix_expression multiplicative_expression_1");
            return parseMultiplicativeExpression_1(lexicalSymbol, start, tempPrefix, Binary.Operator.DIV);
        } else if (currentLexicalSym.tokenType == TokenType.OP_MOD) {
            dump("multiplicative_expression_1 -> \'%\' prefix_expression multiplicative_expression_1");
            return parseMultiplicativeExpression_1(lexicalSymbol, start, tempPrefix, Binary.Operator.MOD);
        } else {
            dump("multiplicative_expression_1 -> ε");
            currentLexicalSym = lexicalSymbol.previous();
            return tempPrefix;
        }
    }

    private Binary parseMultiplicativeExpression_1(ListIterator<Symbol> lexicalSymbol, Location start, Expr left,
            Binary.Operator op) {
        var right = parsePrefixExpression(lexicalSymbol);
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType == TokenType.OP_MUL) {
            dump("multiplicative_expression_1 -> \'*\' prefix_expression multiplicative_expression_1");
            return parseMultiplicativeExpression_1(lexicalSymbol, start, right, Binary.Operator.MUL);
        } else if (currentLexicalSym.tokenType == TokenType.OP_DIV) {
            dump("multiplicative_expression_1 -> \'/\' prefix_expression multiplicative_expression_1");
            return parseMultiplicativeExpression_1(lexicalSymbol, start, right, Binary.Operator.DIV);
        } else if (currentLexicalSym.tokenType == TokenType.OP_MOD) {
            dump("multiplicative_expression_1 -> \'%\' prefix_expression multiplicative_expression_1");
            return parseMultiplicativeExpression_1(lexicalSymbol, start, right, Binary.Operator.MOD);
        } else {
            dump("multiplicative_expression_1 -> ε");
            currentLexicalSym = lexicalSymbol.previous();
            return new Binary(new Position(start, currentLexicalSym.position.end), left, op, right);
        }
    }

    private Expr parsePrefixExpression(ListIterator<Symbol> lexicalSymbol) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        Location start = currentLexicalSym.position.start;
        Unary.Operator op = null;
        Expr tempPrefix;
        if (currentLexicalSym.tokenType == TokenType.OP_ADD) {
            dump("prefix_expression -> \'+\' prefix_expression");
            tempPrefix = parsePrefixExpression(lexicalSymbol);
            op = Unary.Operator.ADD;
        } else if (currentLexicalSym.tokenType == TokenType.OP_SUB) {
            dump("prefix_expression -> \'-\' prefix_expression");
            tempPrefix = parsePrefixExpression(lexicalSymbol);
            op = Unary.Operator.SUB;
        } else if (currentLexicalSym.tokenType == TokenType.OP_NOT) {
            dump("prefix_expression -> \'!\' prefix_expression");
            tempPrefix = parsePrefixExpression(lexicalSymbol);
            op = Unary.Operator.NOT;
        } else {
            dump("prefix_expression -> postfix_expression");
            lexicalSymbol.previous();
            tempPrefix = parsePostfixExpression(lexicalSymbol);
            return tempPrefix;
        }
        return new Unary(new Position(start, currentLexicalSym.position.end), tempPrefix, op);
    }

    private Expr parsePostfixExpression(ListIterator<Symbol> lexicalSymbol) {
        dump("postfix_expression -> atom_expression postfix_expression_1");
        Expr tempAtom = parseAtomExpression(lexicalSymbol);
        Symbol currentLexicalSym = lexicalSymbol.next();
        Location start = currentLexicalSym.position.start;
        Expr tempPosfix = null;
        if (currentLexicalSym.tokenType == TokenType.OP_LBRACKET) {
            dump("postfix_expression_1 -> \'[\' expression \']\' postfix_expression_1");
            lexicalSymbol.previous();
            tempPosfix = parsePostfixExpression_1(lexicalSymbol);
            return new Binary(new Position(start, currentLexicalSym.position.end), tempAtom, Binary.Operator.ARR,
                    tempPosfix);
        } else {
            dump("postfix_expression_1 -> ε");
            currentLexicalSym = lexicalSymbol.previous();
            return tempAtom;
        }
    }

    private Expr parsePostfixExpression_1(ListIterator<Symbol> lexicalSymbol) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_LBRACKET)
            Report.error(currentLexicalSym.position, "Before expressions a left opening square bracket is required");
        var tempExpr = parseExpression(lexicalSymbol, "");
        currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_RBRACKET)
            Report.error(currentLexicalSym.position,
                    "Following expressions a right closing square bracket is required");
        return parsePostfixExpression(lexicalSymbol);
    }

    private Expr parseAtomExpression(ListIterator<Symbol> lexicalSymbol) {
        Symbol currentLexicalSym = lexicalSymbol.next();
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
                currentLexicalSym = lexicalSymbol.next();
                Location start = currentLexicalSym.position.start;
                if (currentLexicalSym.tokenType == TokenType.OP_LPARENT) {
                    dump("atom_expression_id -> \'(\' expressions \')\'");
                    var tempExprs = parseExpressions(lexicalSymbol);
                    currentLexicalSym = lexicalSymbol.next();
                    if (currentLexicalSym.tokenType != TokenType.OP_RPARENT)
                        Report.error(currentLexicalSym.position,
                                "Expressions should be closed with a closing right paranthesis");
                    return new Call(new Position(start, currentLexicalSym.position.end), tempExprs.expressions,
                            currentLexicalSym.lexeme);
                } else {
                    dump("atom_expression_id -> ε");
                    lexicalSymbol.previous();
                    return new Name(new Position(id.position.start, id.position.end), id.lexeme);
                }
            case OP_LPARENT:
                dump("atom_expression -> \'(\' expressions \')\'");
                var tempExprs = parseExpressions(lexicalSymbol);
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_RPARENT)
                    Report.error(currentLexicalSym.position,
                            "Expressions should be closed with a closing right paranthesis");
                return new Block(currentLexicalSym.position, tempExprs.expressions);
            case OP_LBRACE:
                dump("atom_expression -> \'{\' atom_expression_lbrace_1");
                return parseAtomExpressionLBrace(lexicalSymbol);
            default:
                lexicalSymbol.previous();
                return null;
        }
    }

    private Expr parseAtomExpressionLBrace(ListIterator<Symbol> lexicalSymbol) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        Location start = currentLexicalSym.position.start;
        switch (currentLexicalSym.tokenType) {
            case KW_IF:
                dump("atom_expression_lbrace_1 -> if expression then expression if_else \'}\'");
                var tempExpr1 = parseExpression(lexicalSymbol, "");
                currentLexicalSym = lexicalSymbol.next();
                Optional<Expr> _else = null;
                if (currentLexicalSym.tokenType != TokenType.KW_THEN)
                    Report.error(currentLexicalSym.position,
                            "In the if statement after condition a then keyword should follow");
                var tempExpr2 = parseExpression(lexicalSymbol, "");
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_RBRACE) {
                    dump("if_else -> else expression");
                    lexicalSymbol.previous();
                    _else = parseIfElse(lexicalSymbol);
                    currentLexicalSym = lexicalSymbol.next();
                } else {
                    dump("if_else -> ε");
                    _else = Optional.empty();
                }
                if (currentLexicalSym.tokenType != TokenType.OP_RBRACE)
                    Report.error(currentLexicalSym.position, "The if statment should be closed with a right brace");
                return new IfThenElse(new Position(start, currentLexicalSym.position.end), tempExpr1, tempExpr2, _else);
            case KW_WHILE:
                dump("atom_expression_lbrace_1 -> while expression \':\' expression \'}\' .");
                var tempExprWhileCond = parseExpression(lexicalSymbol, "");
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_COLON)
                    Report.error(currentLexicalSym.position, "After while condition a colon should follow");
                var tempExprWhileExpr = parseExpression(lexicalSymbol, "");
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_RBRACE)
                    Report.error(currentLexicalSym.position,
                            "After while body expression a right brace should close it");
                return new While(new Position(start, currentLexicalSym.position.end), tempExprWhileCond,
                        tempExprWhileExpr);
            case KW_FOR:
                dump("atom_expression_lbrace_1 -> for id \'=\' expression \',\' expression \',\' expression \':\' expression \'}\'");
                currentLexicalSym = lexicalSymbol.next();
                var tempCounter = currentLexicalSym;
                if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
                    Report.error(currentLexicalSym.position, "Following a for keyword counter identifier is required");
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_ASSIGN)
                    Report.error(currentLexicalSym.position,
                            "Following a for identifier an assignment operator is required");
                var tempExprFor1 = parseExpression(lexicalSymbol, "");
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_COMMA)
                    Report.error(currentLexicalSym.position,
                            "After the first expression in for loop statement a comma is required");
                var tempExprFor2 = parseExpression(lexicalSymbol, "");
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_COMMA)
                    Report.error(currentLexicalSym.position,
                            "After the second expression in for loop statement a comma is required");
                var tempExprFor3 = parseExpression(lexicalSymbol, "");
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_COLON)
                    Report.error(currentLexicalSym.position,
                            "After the third expression in for loop statement a colon is required");
                var tempForBody = parseExpression(lexicalSymbol, "");
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_RBRACE)
                    Report.error(currentLexicalSym.position,
                            "After the for loop statement body a closing right curly brace is required");
                return new For(new Position(start, currentLexicalSym.position.end),
                        new Name(tempCounter.position, tempCounter.lexeme), tempExprFor1, tempExprFor2, tempExprFor3,
                        tempForBody);
            default:
                dump("atom_expression_lbrace_1 -> expression \'=\' expression \'}\'");
                lexicalSymbol.previous();
                var tempExprAss = parseExpression(lexicalSymbol, "");
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_ASSIGN)
                    Report.error(currentLexicalSym.position,
                            "Following an expression an assignment operator is required in this statment");
                var tempExprAss2 = parseExpression(lexicalSymbol, "");
                currentLexicalSym = lexicalSymbol.next();
                if (currentLexicalSym.tokenType != TokenType.OP_RBRACE)
                    Report.error(currentLexicalSym.position,
                            "After the expression assignemnt statment a closing right curly brace is required");
                return new Binary(new Position(start, currentLexicalSym.position.end), tempExprAss,
                        Binary.Operator.ASSIGN, tempExprAss2);
        }
    }

    private Optional<Expr> parseIfElse(ListIterator<Symbol> lexicalSymbol) {
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.KW_ELSE)
            Report.error(currentLexicalSym.position, "Either else statement or closing right brace expected");
        return Optional.of(parseExpression(lexicalSymbol, ""));
    }

    private Block parseExpressions(ListIterator<Symbol> lexicalSymbol) {
        dump("expressions -> expression expressions_1");
        List<Expr> expressions = new ArrayList<Expr>();
        expressions.add(parseExpression(lexicalSymbol, ""));
        Symbol currentLexicalSym = lexicalSymbol.next();
        Location start = currentLexicalSym.position.start;
        if (currentLexicalSym.tokenType == TokenType.OP_COMMA) {
            dump("expressions_1 -> \',\' expression expressions_1");
            return parseExpressions_1(lexicalSymbol, start, expressions);
        } else {
            dump("expressions_1 -> ε");
            currentLexicalSym = lexicalSymbol.previous();
            return new Block(new Position(start, currentLexicalSym.position.end), expressions);
        }
    }

    private Block parseExpressions_1(ListIterator<Symbol> lexicalSymbol, Location start, List<Expr> expressions) {
        expressions.add(parseExpression(lexicalSymbol, ""));
        Symbol currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType == TokenType.OP_COMMA) {
            dump("expressions_1 -> \',\' expression expressions_1");
            return parseExpressions_1(lexicalSymbol, start, expressions);
        } else {
            dump("expressions_1 -> ε");
            currentLexicalSym = lexicalSymbol.previous();
            return new Block(new Position(start, currentLexicalSym.position.end), expressions);
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
        String tmp = currentLexicalSym.lexeme;
        if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
            Report.error(currentLexicalSym.position, "Parameter declaration must begin with its identifier");
        currentLexicalSym = lexicalSymbol.next();
        if (currentLexicalSym.tokenType != TokenType.OP_COLON)
            Report.error(currentLexicalSym.position, "After parameter identificator a colon is required");
        var tempType = parseType(lexicalSymbol, tmp);
        string += tmp;
        return new FunDef.Parameter(new Position(start, tempType.position.end), tmp.toString(), tempType);
    }

    private VarDef parseVarDef(ListIterator<Symbol> lexicalSymbol, Location start, String string) {
        String tmp = "";
        Symbol currentLexicalSym = lexicalSymbol.next();
        tmp += currentLexicalSym.lexeme;
        dump("variable_definition -> var id : type");
        if (currentLexicalSym.tokenType != TokenType.IDENTIFIER)
            Report.error(currentLexicalSym.position, "Variable definition identifier is wrong");
        currentLexicalSym = lexicalSymbol.next();
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
