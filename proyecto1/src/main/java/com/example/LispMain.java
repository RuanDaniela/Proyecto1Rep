package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LispMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bienvenido al intérprete de LISP.");
        System.out.println("Comandos:\n  salir → cierra\n  archivo <ruta> → carga archivo .lisp\n");

        LispEvaluator evaluator = new LispEvaluator();

        while (true) {
            System.out.print("LISP> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("salir")) break;

            if (input.toLowerCase().startsWith("archivo ")) {
                String[] parts = input.split("\\s+", 2);
                if (parts.length < 2) {
                    System.out.println("Debes escribir una ruta.");
                    continue;
                }

                try {
                    List<String> expresiones = leerExpresionesDesdeArchivo(parts[1]);
                    for (String expr : expresiones) {
                        ejecutarExpresion(expr, evaluator);
                    }
                } catch (IOException e) {
                    System.out.println("Error al leer archivo: " + e.getMessage());
                }

                continue;
            }

            ejecutarExpresion(input, evaluator);
        }

        System.out.println("Intérprete finalizado.");
        scanner.close();
    }

    private static void ejecutarExpresion(String input, LispEvaluator evaluator) {
        try {
            if (!LispLexer.parentesisBalanceados(input)) {
                System.out.println("Paréntesis desbalanceados.");
                return;
            }
            List<String> tokens = LispLexer.dividirEnTokens(input);
            LispParser parser = new LispParser(tokens);
            Object ast = parser.parse();
            Object resultado = evaluator.evaluar(ast);
            System.out.println("Resultado: " + resultado);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static List<String> leerExpresionesDesdeArchivo(String ruta) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(ruta));
        StringBuilder expresion = new StringBuilder();
        List<String> expresiones = new ArrayList<>();
        int balance = 0;
        String linea;

        while ((linea = br.readLine()) != null) {
            for (char c : linea.toCharArray()) {
                if (c == '(') balance++;
                if (c == ')') balance--;
            }
            expresion.append(linea).append(" ");
            if (balance == 0 && expresion.toString().trim().length() > 0) {
                expresiones.add(expresion.toString().trim());
                expresion.setLength(0);
            }
        }
        br.close();
        return expresiones;
    }
}
