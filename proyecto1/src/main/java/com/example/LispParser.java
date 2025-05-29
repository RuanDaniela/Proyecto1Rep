package com.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser para expresiones LISP.
 * Convierte una lista de tokens en una estructura anidada de listas y valores.
 */
public class LispParser {
    private List<String> tokens;
    private int index;

    /**
     * Crea un nuevo parser con la lista de tokens a procesar.
     *
     * @param tokens lista de tokens generada por el lexer
     */
    public LispParser(List<String> tokens) {
        this.tokens = tokens;
        this.index = 0;
    }

    /**
     * Parsea los tokens y construye la estructura de datos que representa
     * la expresión LISP. Esta estructura es recursiva: puede ser una lista de objetos
     * o un token simple (número o símbolo).
     *
     * @return la expresión parseada como Object (puede ser Integer, String o List)
     * @throws RuntimeException si encuentra errores de sintaxis como paréntesis desbalanceados o inesperados
     */
    public Object parse() {
        if (index >= tokens.size()) {
            throw new RuntimeException("Error: expresión inesperada al final.");
        }

        String token = tokens.get(index++);
        if (token.equals("(")) {
            List<Object> list = new ArrayList<>();
            while (index < tokens.size() && !tokens.get(index).equals(")")) {
                list.add(parse());
            }
            if (index >= tokens.size() || !tokens.get(index).equals(")")) {
                throw new RuntimeException("Error: paréntesis desbalanceados.");
            }
            index++; // consumir ')'
            return list;
        } else if (token.equals(")")) {
            throw new RuntimeException("Error: paréntesis inesperado.");
        } else {
            try {
                return Integer.parseInt(token);
            } catch (NumberFormatException e) {
                // No es número, devolver como símbolo (String)
                return token;
            }
        }
    }
}
