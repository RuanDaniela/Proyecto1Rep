package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase responsable de dividir una expresión LISP en tokens y verificar el balance de paréntesis.
 */
public class LispLexer {
    /**
     * Expresión regular que identifica los tokens válidos en LISP:
     * paréntesis, números enteros y decimales (con notación científica),
     * cadenas entre comillas y símbolos (identificadores y operadores).
     */
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
        "\\(|\\)|-?\\d+(\\.\\d+)?(e[+-]?\\d+)?|\"(\\\\.|[^\"])*\"|[\\w+\\-*/!?=<>.]+"
    );

    /**
     * Divide una expresión LISP en una lista de tokens válidos.
     * Lanza excepción si encuentra caracteres inválidos o no reconocidos entre tokens.
     *
     * @param expr expresión LISP como cadena
     * @return lista de tokens extraídos de la expresión
     * @throws LexerException si se encuentran caracteres no válidos en la expresión
     */
    public static List<String> dividirEnTokens(String expr) throws LexerException {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(expr.trim());
        int lastMatchEnd = 0;
        while (matcher.find()) {
            if (matcher.start() != lastMatchEnd) {
                String skipped = expr.substring(lastMatchEnd, matcher.start());
                if (!skipped.trim().isEmpty()) {
                    throw new LexerException("Carácter no válido encontrado: " + skipped.trim());
                }
            }
            tokens.add(matcher.group());
            lastMatchEnd = matcher.end();
        }
        if (lastMatchEnd != expr.length()) {
            String trailing = expr.substring(lastMatchEnd);
            if (!trailing.trim().isEmpty()) {
                throw new LexerException("Carácter no válido encontrado: " + trailing.trim());
            }
        }
        return tokens;
    }

    /**
     * Verifica si los paréntesis en la expresión están balanceados correctamente.
     *
     * @param expr expresión LISP como cadena
     * @return true si los paréntesis están balanceados; false en caso contrario
     */
    public static boolean parentesisBalanceados(String expr) {
        int contador = 0;
        for (char c : expr.toCharArray()) {
            if (c == '(') contador++;
            else if (c == ')') contador--;
            if (contador < 0) return false;
        }
        return contador == 0;
    }

    /**
     * Excepción lanzada cuando el lexer encuentra caracteres inválidos o errores en el análisis léxico.
     */
    public static class LexerException extends Exception {
        /**
         * Constructor de la excepción con mensaje personalizado.
         *
         * @param message mensaje descriptivo del error
         */
        public LexerException(String message) {
            super(message);
        }
    }
}
