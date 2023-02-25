/**
 * @Author: turk
 * @Description: Leksikalni analizator.
 */

package compiler.lexer;

import static common.RequireNonNull.requireNonNull;
import static compiler.lexer.TokenType.*;
import compiler.lexer.Position.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import common.Report;

public class Lexer {
    /**
     * Izvorna koda.
     */
    private final String source;

    /**
     * Preslikava iz ključnih besed v vrste simbolov.
     */
    private final static Map<String, TokenType> keywordMapping;

    static {
        keywordMapping = new HashMap<>();
        for (var token : TokenType.values()) {
            var str = token.toString();
            if (str.startsWith("KW_")) {
                keywordMapping.put(str.substring("KW_".length()).toLowerCase(), token);
            }
            if (str.startsWith("AT_")) {
                keywordMapping.put(str.substring("AT_".length()).toLowerCase(), token);
            }
        }
    }

    /**
     * Preslikava iz enojnega karakterja v TokenType enum
     */
    private final static HashMap<Character, TokenType> single_char_lexems;

    static {
        single_char_lexems = new HashMap<Character, TokenType>();
        single_char_lexems.put('+', TokenType.OP_ADD);
        single_char_lexems.put('-', TokenType.OP_SUB);
        single_char_lexems.put('*', TokenType.OP_MUL);
        single_char_lexems.put('/', TokenType.OP_DIV);
        single_char_lexems.put('%', TokenType.OP_MOD);
        single_char_lexems.put('&', TokenType.OP_AND);
        single_char_lexems.put('|', TokenType.OP_OR);
        single_char_lexems.put('(', TokenType.OP_LPARENT);
        single_char_lexems.put(')', TokenType.OP_RPARENT);
        single_char_lexems.put('[', TokenType.OP_LBRACKET);
        single_char_lexems.put(']', TokenType.OP_RBRACKET);
        single_char_lexems.put('{', TokenType.OP_LBRACE);
        single_char_lexems.put('}', TokenType.OP_RBRACE);
        single_char_lexems.put(':', TokenType.OP_COLON);
        single_char_lexems.put(';', TokenType.OP_SEMICOLON);
        single_char_lexems.put('.', TokenType.OP_DOT);
        single_char_lexems.put(',', TokenType.OP_COMMA);
    }

    /**
     * Preslikava iz več-simbolnih operatorjev v TokenType enum
     */
    private final static HashMap<String, TokenType> multi_char_op;

    static {
        multi_char_op = new HashMap<String, TokenType>();
        // One char op
        multi_char_op.put("!", TokenType.OP_NOT);
        multi_char_op.put("=", TokenType.OP_ASSIGN);
        multi_char_op.put("<", TokenType.OP_LT);
        multi_char_op.put(">", TokenType.OP_GT);
        // Two char op
        multi_char_op.put("!=", TokenType.OP_NEQ);
        multi_char_op.put("==", TokenType.OP_EQ);
        multi_char_op.put("<=", TokenType.OP_LEQ);
        multi_char_op.put(">=", TokenType.OP_GEQ);
    }

    /**
     * Preslikava podatkovnih tipov v TokenType enum
     */
    private final static HashMap<String, TokenType> data_types;

    static {
        data_types = new HashMap<String, TokenType>();
        data_types.put("integer", TokenType.AT_INTEGER);
        data_types.put("logical", TokenType.AT_LOGICAL);
        data_types.put("string", TokenType.AT_STRING);
    }

    /**
     * Ustvari nov analizator.
     * 
     * @param source Izvorna koda programa.
     */
    public Lexer(String source) {
        requireNonNull(source);
        this.source = source;
    }

    /**
     * Izvedi leksikalno analizo.
     * 
     * @return seznam leksikalnih simbolov.
     */
    public List<Symbol> scan() {
        var symbols = new ArrayList<Symbol>();
        // TODO: implementacija leksikalne analize
        boolean comment = false;
        String tempString = "";

        for (int i = 0, j = 0, k = 0; i < source.length(); i++, k++) {
            // Newline karakterji
            if ((int) source.charAt(i) == 10 || (int) source.charAt(i) == 13) {
                j++;
                k = 0;
                comment = false;
                continue;
            }

            else if (source.charAt(i) == '$') {
                symbols.add(new Symbol(new Position(k, j, k, j), EOF, "$"));
            }

            else if (comment)
                continue;

            else if (source.charAt(i) == '#') {
                comment = true;
                continue;
            }

            // Največja prednost gre string-om
            // Tukaj jih sparsamo
            else if (source.charAt(i) == '\'') {
                tempString = "";
                tempString += source.charAt(i);
                while (source.charAt(i + 1) != '\'' || (source.charAt(i + 1) == '\'' && source.charAt(i + 2) == '\'')) {
                    if (source.charAt(i + 1) == '\'') {
                        tempString += source.charAt(i + 1);
                        i += 2;
                    } else {
                        tempString += source.charAt(i + 1);
                        i++;
                    }
                }
                symbols.add(new Symbol(new Position(k, j, i, j), C_STRING, tempString));
                k = i;
                tempString = "";
            }

            // Belo besedilo vržemo ven
            else if ((int) source.charAt(i) == 32 || (int) source.charAt(i) == 9) {
                continue;
            }

            // Operatorji
            else if (multi_char_op.containsKey(Character.toString(source.charAt(i)))) {
                if (source.charAt(i + 1) == '=') {
                    i++;
                    k++;
                    String token_key = Character.toString(source.charAt(i)) + Character.toString(source.charAt(i + 1));
                    symbols.add(new Symbol(new Position(k - 1, j, k, j), multi_char_op.get(token_key), token_key));
                } else {
                    symbols.add(new Symbol(new Position(k, j, k, j),
                            multi_char_op.get(Character.toString(source.charAt(i))),
                            Character.toString(source.charAt(i))));
                }
            }

            // Ujami vse znake, ki so lahko samo en char
            else if (single_char_lexems.containsKey(source.charAt(i))) {
                symbols.add(new Symbol(new Position(k, j, k, j), single_char_lexems.get(source.charAt(i)),
                        Character.toString(source.charAt(i))));
            }

            // Števila
            else if (Character.isDigit(source.charAt(i))) {
                tempString = "";
                tempString += source.charAt(i);
                while (Character.isDigit(source.charAt(i + 1))) {
                    tempString += source.charAt(i + 1);
                    i++;
                }
                symbols.add(new Symbol(new Position(k, j, i, j), C_INTEGER, tempString));
                k = i;
                tempString = "";
            }

            // Keywords in imena
            else {
                tempString = "";
                tempString += source.charAt(i);
                while (Character.isLetterOrDigit(source.charAt(i + 1)) || (int) source.charAt(i) == 95) {
                    tempString += source.charAt(i + 1);
                    i++;
                }
                TokenType t;

                // Preveri ali je keyword
                if (keywordMapping.containsKey(tempString)) {
                    t = keywordMapping.get(tempString);
                }

                // Preveri ali je podatkovni tip
                if (data_types.containsKey(tempString)) {
                    t = data_types.get(tempString);
                }

                // Preveri če je true ali false vrednost
                if (tempString.equals("true") || tempString.equals("false"))
                    t = C_LOGICAL;

                // Če ne, je ime
                else
                    t = IDENTIFIER;

                symbols.add(new Symbol(new Position(k, j, i, j), t, tempString));
                k = i;
                tempString = "";
            }

        }
        return symbols;
    }
}
