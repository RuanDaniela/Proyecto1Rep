package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LispLexer {
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
        "\\(|\\)|-?\\d+(\\.\\d+)?(e[+-]?\\d+)?|\"(\\\\.|[^\"])*\"|[\\w+\\-*/!?=<>.]+"
    );

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

    public static boolean parentesisBalanceados(String expr) {
        int contador = 0;
        for (char c : expr.toCharArray()) {
            if (c == '(') contador++;
            else if (c == ')') contador--;
            if (contador < 0) return false;
        }
        return contador == 0;
    }

    public static class LexerException extends Exception {
        public LexerException(String message) {
            super(message);
        }
    }
}
