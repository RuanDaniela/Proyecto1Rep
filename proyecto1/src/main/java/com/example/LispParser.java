package com.example;

import java.util.ArrayList;
import java.util.List;

public class LispParser {
    private List<String> tokens;
    private int index;

    public LispParser(List<String> tokens) {
        this.tokens = tokens;
        this.index = 0;
    }

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
            index++; // cerrar ')'
            return list;
        } else if (token.equals(")")) {
            throw new RuntimeException("Error: paréntesis inesperado.");
        } else {
            try {
                return Integer.parseInt(token);
            } catch (NumberFormatException e) {
                return token;
            }
        }
    }
}
