package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Evaluador para expresiones Lisp.
 * Soporta evaluación de expresiones, definición de funciones y variables,
 * y ejecución de funciones básicas como +, -, *, /, y condicionales.
 */
public class LispEvaluator {
    private Map<String, Object> environment;

    /**
     * Crea un nuevo evaluador con el entorno inicial que contiene funciones básicas.
     */
    public LispEvaluator() {
        environment = new HashMap<>();
        cargarFuncionesBasicas();
    }

    /**
     * Carga las funciones básicas de Lisp en el entorno.
     * Incluye operadores aritméticos, comparadores, y funciones tipo ATOM, LIST, EQUAL.
     */
    private void cargarFuncionesBasicas() {
        environment.put("+", (LispFunction) args -> {
            double sum = 0;
            for (Object arg : args) {
                sum += toDouble(evaluar(arg));
            }
            return sum;
        });

        environment.put("-", (LispFunction) args -> {
            if (args.isEmpty()) throw new EvaluatorException("'-' requiere al menos un argumento.");
            double result = toDouble(evaluar(args.get(0)));
            if (args.size() == 1) return -result;
            for (int i = 1; i < args.size(); i++) {
                result -= toDouble(evaluar(args.get(i)));
            }
            return result;
        });

        environment.put("*", (LispFunction) args -> {
            double result = 1;
            for (Object arg : args) {
                result *= toDouble(evaluar(arg));
            }
            return result;
        });

        environment.put("/", (LispFunction) args -> {
            if (args.size() < 2) throw new EvaluatorException("'/' requiere al menos dos argumentos.");
            double result = toDouble(evaluar(args.get(0)));
            for (int i = 1; i < args.size(); i++) {
                double divisor = toDouble(evaluar(args.get(i)));
                if (divisor == 0) throw new EvaluatorException("División por cero.");
                result /= divisor;
            }
            return result;
        });

        environment.put(">", (LispFunction) args ->
                toDouble(evaluar(args.get(0))) > toDouble(evaluar(args.get(1))));

        environment.put("<", (LispFunction) args ->
                toDouble(evaluar(args.get(0))) < toDouble(evaluar(args.get(1))));

        environment.put("=", (LispFunction) args ->
                Objects.equals(evaluar(args.get(0)), evaluar(args.get(1))));

        environment.put("ATOM", (LispFunction) args -> {
            Object val = evaluar(args.get(0));
            return (val instanceof String) || (val instanceof Number);
        });

        environment.put("LIST", (LispFunction) args ->
                evaluar(args.get(0)) instanceof List);

        environment.put("EQUAL", (LispFunction) args ->
                Objects.equals(evaluar(args.get(0)), evaluar(args.get(1))));
    }

    /**
     * Convierte un objeto a double, si es posible.
     *
     * @param num objeto a convertir
     * @return valor double
     * @throws EvaluatorException si el objeto no es numérico
     */
    private double toDouble(Object num) throws EvaluatorException {
        if (num instanceof Number) return ((Number) num).doubleValue();
        throw new EvaluatorException("Se esperaba un número, pero se obtuvo: " + num);
    }

    /**
     * Evalúa una expresión representada por un AST.
     *
     * @param ast expresión a evaluar (puede ser String, Number, List)
     * @return resultado de la evaluación
     * @throws EvaluatorException si ocurre algún error en la evaluación
     */
    public Object evaluar(Object ast) throws EvaluatorException {
        if (ast instanceof String) {
            String simbolo = (String) ast;
            if (environment.containsKey(simbolo)) return environment.get(simbolo);
            throw new EvaluatorException("Símbolo no definido: " + simbolo);
        } else if (ast instanceof Number) {
            return ast;
        } else if (ast instanceof List) {
            List<Object> list = (List<Object>) ast;
            if (list.isEmpty()) throw new EvaluatorException("Lista vacía.");
            Object primero = list.get(0);
            if (!(primero instanceof String)) throw new EvaluatorException("Se esperaba nombre de función.");
            String fn = (String) primero;

            if (fn.equals("defun")) return definirFuncionLisp(list);
            if (fn.equals("setq")) return definirVariableLisp(list);
            if (fn.equals("quote")) {
                if (list.size() != 2) throw new EvaluatorException("quote requiere un argumento.");
                return list.get(1);
            }
            if (fn.equals("cond")) return evaluarCond(list);

            Object funcion = environment.get(fn);
            if (!(funcion instanceof LispFunction)) throw new EvaluatorException("Función no definida: " + fn);

            LispFunction lf = (LispFunction) funcion;
            List<Object> args = new ArrayList<>();
            for (int i = 1; i < list.size(); i++) args.add(evaluar(list.get(i)));
            return lf.apply(args);
        }
        throw new EvaluatorException("Expresión no válida.");
    }

    /**
     * Evalúa una expresión condicional (cond).
     *
     * @param list lista que contiene cond y sus cláusulas
     * @return resultado de la primera cláusula verdadera o null si ninguna lo es
     * @throws EvaluatorException si la sintaxis es incorrecta
     */
    private Object evaluarCond(List<Object> list) throws EvaluatorException {
        for (int i = 1; i < list.size(); i++) {
            List<Object> cond = (List<Object>) list.get(i);
            if (cond.size() != 2) throw new EvaluatorException("Cada cláusula cond debe tener 2 elementos.");
            Object condicion = evaluar(cond.get(0));
            if ((condicion instanceof Boolean && (Boolean) condicion)
                    || (condicion instanceof Number && ((Number) condicion).doubleValue() != 0)) {
                return evaluar(cond.get(1));
            }
        }
        return null;
    }

    /**
     * Define una nueva función Lisp y la agrega al entorno.
     *
     * @param list lista que contiene defun, nombre, parámetros y cuerpo
     * @return nombre de la función definida
     * @throws EvaluatorException si la definición es inválida
     */
    private Object definirFuncionLisp(List<Object> list) throws EvaluatorException {
        if (list.size() < 4) throw new EvaluatorException("defun requiere nombre, parámetros y cuerpo.");
        if (!(list.get(1) instanceof String)) throw new EvaluatorException("Nombre inválido.");
        String nombre = (String) list.get(1);
        if (!(list.get(2) instanceof List)) throw new EvaluatorException("Parámetros inválidos.");

        List<?> paramList = (List<?>) list.get(2);
        List<String> params = new ArrayList<>();
        for (Object p : paramList) {
            if (!(p instanceof String)) throw new EvaluatorException("Parámetro inválido.");
            params.add((String) p);
        }

        List<Object> cuerpo = list.subList(3, list.size());
        environment.put(nombre, (LispFunction) args -> {
            if (args.size() != params.size())
                throw new EvaluatorException("Argumentos incorrectos en llamada a: " + nombre);
            Map<String, Object> localEnv = new HashMap<>(environment);
            for (int i = 0; i < params.size(); i++) localEnv.put(params.get(i), args.get(i));
            Object res = null;
            for (Object expr : cuerpo) res = evaluarEnEntorno(expr, localEnv);
            return res;
        });

        return nombre;
    }

    /**
     * Define o reasigna una variable en el entorno.
     *
     * @param list lista que contiene setq, nombre y valor
     * @return valor asignado
     * @throws EvaluatorException si la sintaxis es inválida
     */
    private Object definirVariableLisp(List<Object> list) throws EvaluatorException {
        if (list.size() != 3) throw new EvaluatorException("setq requiere nombre y valor.");
        if (!(list.get(1) instanceof String)) throw new EvaluatorException("Nombre inválido.");
        String nombre = (String) list.get(1);
        Object valor = evaluar(list.get(2));
        environment.put(nombre, valor);
        return valor;
    }

    /**
     * Evalúa una expresión en un entorno local.
     *
     * @param ast     expresión a evaluar
     * @param entorno entorno local
     * @return resultado de la evaluación
     * @throws EvaluatorException si ocurre error en la evaluación
     */
    private Object evaluarEnEntorno(Object ast, Map<String, Object> entorno) throws EvaluatorException {
        LispEvaluator local = new LispEvaluator();
        local.environment = entorno;
        return local.evaluar(ast);
    }
}
