/**
 * @Author: turk
 * @Description: Leksikalni analizator.
 */

package compiler.lexer;

import static common.RequireNonNull.requireNonNull;
import static compiler.lexer.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // Spremenljivke za grajenje leksemov
        boolean comment = false;
        String tempString = "";

        for (int i = 0, line_index = 1, inline_start_index = 1,
                inline_stop_index = 1; i < source.length(); i++, inline_start_index++, inline_stop_index++) {

            // CR char spregledamo
            if ((int) source.charAt(i) == 0xd) {
                continue;
            }
            // Newline karakter
            else if ((int) source.charAt(i) == 0xa) {
                line_index++;
                // Te dva indexa dam na 0, ker se newline porabi kot char in naslednjo
                // loop iteracijo se povečata za 1
                inline_start_index = 0;
                inline_stop_index = 0;
                comment = false;
                continue;
            }

            else if (source.charAt(i) == '$') {
                symbols.add(new Symbol(new Position(line_index, inline_start_index, line_index, inline_start_index),
                        EOF, "$"));
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
                        inline_stop_index += 2;
                    } else if (source.charAt(i + 1) == '\n') {
                        Report.error(new Position(line_index, inline_start_index, line_index, inline_stop_index),
                                "String, se ne zaključi z končnim \' znakom!");
                    } else {
                        tempString += source.charAt(i + 1);
                        i++;
                        inline_stop_index++;
                    }
                }
                tempString += "\'";
                i++;
                inline_stop_index++;
                symbols.add(new Symbol(new Position(line_index, inline_start_index, line_index, inline_stop_index),
                        C_STRING, tempString));
            }

            // Belo besedilo vržemo ven
            else if (Character.isWhitespace(source.charAt(i))) {
                continue;
            }

            // Operatorji
            else if (multi_char_op.containsKey(Character.toString(source.charAt(i)))) {
                if (source.charAt(i + 1) == '=') {
                    StringBuffer token_key = new StringBuffer().append(source.charAt(i)).append(source.charAt(i + 1));
                    i++;
                    inline_stop_index++;
                    symbols.add(new Symbol(new Position(line_index, inline_start_index, line_index, inline_stop_index),
                            multi_char_op.get(token_key.toString()), token_key.toString()));
                } else {
                    symbols.add(new Symbol(new Position(line_index, inline_start_index, line_index, inline_stop_index),
                            multi_char_op.get(Character.toString(source.charAt(i))),
                            Character.toString(source.charAt(i))));
                }
            }

            // Ujami vse znake, ki so lahko samo en char
            else if (single_char_lexems.containsKey(source.charAt(i))) {
                symbols.add(new Symbol(new Position(line_index, inline_start_index, line_index, inline_start_index),
                        single_char_lexems.get(source.charAt(i)),
                        Character.toString(source.charAt(i))));
            }

            // Števila
            else if (Character.isDigit(source.charAt(i))) {
                tempString = "";
                tempString += source.charAt(i);
                while (Character.isDigit(source.charAt(i + 1))) {
                    tempString += source.charAt(i + 1);
                    i++;
                    inline_stop_index++;
                }
                symbols.add(new Symbol(new Position(line_index, inline_start_index, line_index, inline_stop_index),
                        C_INTEGER, tempString));
            }

            // Keywords in imena
            else {
                tempString = "";
                tempString += source.charAt(i);
                while (Character.isLetterOrDigit(source.charAt(i + 1)) || (int) source.charAt(i + 1) == 95) {
                    tempString += source.charAt(i + 1);
                    i++;
                    inline_stop_index++;
                }
                TokenType tkn_Type = assignTokenType(tempString);

                symbols.add(new Symbol(new Position(line_index, inline_start_index, line_index, inline_stop_index), tkn_Type,
                        tempString));
            }
            inline_start_index = inline_stop_index;
        }
        return symbols;
    }

    private static TokenType assignTokenType(String tempString) {
        // Preveri ali je keyword
        if (keywordMapping.containsKey(tempString)) {
            return keywordMapping.get(tempString);
        }
        // Preveri ali je podatkovni tip
        else if (data_types.containsKey(tempString)) {
            return data_types.get(tempString);
        }
        // Preveri če je true ali false vrednost
        else if (tempString.equals("true") || tempString.equals("false")) {
            return C_LOGICAL;
        }
        // Če ne, je ime
        else {
            return IDENTIFIER;
        }
    }
}