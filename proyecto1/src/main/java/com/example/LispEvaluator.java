package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluador de expresiones tipo Lisp. Permite la evaluación de ASTs (árboles de sintaxis abstracta)
 * representados como listas anidadas.
 */
public class LispEvaluator {

    private final Map<String, Object> environment;

    /**
     * Constructor por defecto. Inicializa un entorno vacío.
     */
    public LispEvaluator() {
        this.environment = new HashMap<>();
    }

    /**
     * Constructor con entorno predefinido.
     *
     * @param env Mapa con variables y funciones iniciales.
     */
    public LispEvaluator(Map<String, Object> env) {
        this.environment = new HashMap<>(env);
    }

    /**
     * Evalúa un AST (expresión en forma de objeto).
     *
     * @param ast Expresión a evaluar.
     * @return Resultado de la evaluación.
     * @throws EvaluatorException Si hay errores de sintaxis o símbolos indefinidos.
     */
    public Object evaluar(Object ast) throws EvaluatorException {
        if (ast instanceof String) {
            String token = (String) ast;
            if (token.startsWith("\"") && token.endsWith("\"")) {
                return token.substring(1, token.length() - 1);
            }
            if (environment.containsKey(token)) {
                return environment.get(token);
            }
            try {
                if (token.contains(".")) {
                    return Double.parseDouble(token);
                } else {
                    return Integer.parseInt(token);
                }
            } catch (NumberFormatException e) {
                throw new EvaluatorException("Símbolo no definido: " + token);
            }
        } else if (ast instanceof Number) {
            return ast;
        } else if (ast instanceof List) {
            List<?> lista = (List<?>) ast;
            if (lista.isEmpty()) {
                throw new EvaluatorException("Lista vacía no es una expresión válida");
            }
            Object primerElemento = lista.get(0);
            if (!(primerElemento instanceof String)) {
                throw new EvaluatorException("Operador inválido: " + primerElemento);
            }
            String operador = (String) primerElemento;

            switch (operador) {
                case "setq":
                    if (lista.size() != 3) throw new EvaluatorException("setq requiere 2 argumentos");
                    String variable = (String) lista.get(1);
                    Object valor = evaluar(lista.get(2));
                    environment.put(variable, valor);
                    return valor;

                case "defun":
                    if (lista.size() < 4) throw new EvaluatorException("defun requiere nombre, parámetros y cuerpo");
                    String nombreFuncion = (String) lista.get(1);
                    Object params = lista.get(2);
                    List<?> cuerpo = lista.subList(3, lista.size());
                    environment.put(nombreFuncion, new LispFunction() {
                        @Override
                        public Object apply(List<Object> args) throws EvaluatorException {
                            if (!(params instanceof List))
                                throw new EvaluatorException("Parámetros deben ser una lista");
                            List<?> paramsLista = (List<?>) params;
                            if (args.size() != paramsLista.size())
                                throw new EvaluatorException("Número de argumentos incorrecto para " + nombreFuncion);
                            Map<String, Object> localEnv = new HashMap<>(environment);
                            for (int i = 0; i < paramsLista.size(); i++) {
                                localEnv.put((String) paramsLista.get(i), args.get(i));
                            }
                            LispEvaluator localEval = new LispEvaluator(localEnv);
                            Object resultado = null;
                            for (Object expr : cuerpo) {
                                resultado = localEval.evaluar(expr);
                            }
                            return resultado;
                        }
                    });
                    return nombreFuncion;

                case "quote":
                    if (lista.size() != 2)
                        throw new EvaluatorException("quote requiere exactamente 1 argumento");
                    return lista.get(1);

                case "cond":
                    for (int i = 1; i < lista.size(); i++) {
                        Object condExpr = lista.get(i);
                        if (!(condExpr instanceof List))
                            throw new EvaluatorException("Cada cláusula cond debe ser una lista");
                        List<?> clausula = (List<?>) condExpr;
                        if (clausula.isEmpty()) continue;
                        Object condicion = clausula.get(0);
                        if ("t".equals(condicion)) {
                            Object resultado = null;
                            for (int j = 1; j < clausula.size(); j++) {
                                resultado = evaluar(clausula.get(j));
                            }
                            return resultado;
                        } else {
                            Object valCond = evaluar(condicion);
                            if (valCond instanceof Boolean && (Boolean) valCond) {
                                Object resultado = null;
                                for (int j = 1; j < clausula.size(); j++) {
                                    resultado = evaluar(clausula.get(j));
                                }
                                return resultado;
                            }
                        }
                    }
                    return null;

                case "+":
                case "-":
                case "*":
                case "/":
                case "=":
                case "<":
                case ">":
                    if (lista.size() < 3)
                        throw new EvaluatorException(operador + " requiere al menos 2 argumentos");
                    List<Object> argsEval = new ArrayList<>();
                    for (int i = 1; i < lista.size(); i++) {
                        argsEval.add(evaluar(lista.get(i)));
                    }
                    return evaluarOperador(operador, argsEval);

                case "equal":
                    if (lista.size() != 3)
                        throw new EvaluatorException("equal requiere exactamente 2 argumentos");
                    Object arg1 = evaluar(lista.get(1));
                    Object arg2 = evaluar(lista.get(2));
                    return equalLisp(arg1, arg2);

                case "atom":
                    if (lista.size() != 2)
                        throw new EvaluatorException("atom requiere exactamente 1 argumento");
                    Object arg = evaluar(lista.get(1));
                    return !(arg instanceof List);

                case "list":
                    if (lista.size() != 2)
                        throw new EvaluatorException("list requiere exactamente 1 argumento");
                    Object argList = evaluar(lista.get(1));
                    return argList instanceof List;

                default:
                    Object func = environment.get(operador);
                    if (func == null) throw new EvaluatorException("Función no definida: " + operador);
                    if (!(func instanceof LispFunction))
                        throw new EvaluatorException(operador + " no es una función");
                    List<Object> argumentos = new ArrayList<>();
                    for (int i = 1; i < lista.size(); i++) {
                        argumentos.add(evaluar(lista.get(i)));
                    }
                    return ((LispFunction) func).apply(argumentos);
            }
        } else {
            return ast;
        }
    }

    /**
     * Evalúa operaciones matemáticas y lógicas.
     *
     * @param operador Operador como "+", "-", etc.
     * @param args     Argumentos ya evaluados.
     * @return Resultado de la operación.
     * @throws EvaluatorException Si hay errores de tipo o argumentos.
     */
    private Object evaluarOperador(String operador, List<Object> args) throws EvaluatorException {
        switch (operador) {
            case "+":
                double suma = 0;
                for (Object o : args) suma += toDouble(o);
                if (suma == (int) suma) return (int) suma;
                return suma;

            case "-":
                double resta = toDouble(args.get(0));
                for (int i = 1; i < args.size(); i++) resta -= toDouble(args.get(i));
                if (resta == (int) resta) return (int) resta;
                return resta;

            case "*":
                double prod = 1;
                for (Object o : args) prod *= toDouble(o);
                if (prod == (int) prod) return (int) prod;
                return prod;

            case "/":
                double div = toDouble(args.get(0));
                for (int i = 1; i < args.size(); i++) div /= toDouble(args.get(i));
                return div;

            case "=":
                if (args.size() != 2) throw new EvaluatorException("= requiere exactamente 2 argumentos");
                return toDouble(args.get(0)) == toDouble(args.get(1));

            case "<":
                if (args.size() != 2) throw new EvaluatorException("< requiere exactamente 2 argumentos");
                return toDouble(args.get(0)) < toDouble(args.get(1));

            case ">":
                if (args.size() != 2) throw new EvaluatorException("> requiere exactamente 2 argumentos");
                return toDouble(args.get(0)) > toDouble(args.get(1));

            default:
                throw new EvaluatorException("Operador no soportado: " + operador);
        }
    }

    /**
     * Convierte un objeto a número decimal.
     *
     * @param o Objeto a convertir.
     * @return Valor decimal.
     * @throws EvaluatorException Si no es convertible a número.
     */
    private double toDouble(Object o) throws EvaluatorException {
        if (o instanceof Integer) return ((Integer) o).doubleValue();
        if (o instanceof Double) return (Double) o;
        throw new EvaluatorException("No se pudo convertir a número: " + o);
    }

    /**
     * Compara dos objetos al estilo Lisp.
     *
     * @param a Primer objeto.
     * @param b Segundo objeto.
     * @return true si son iguales, false si no.
     */
    private boolean equalLisp(Object a, Object b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a instanceof List && b instanceof List) {
            List<?> listaA = (List<?>) a;
            List<?> listaB = (List<?>) b;
            if (listaA.size() != listaB.size()) return false;
            for (int i = 0; i < listaA.size(); i++) {
                if (!equalLisp(listaA.get(i), listaB.get(i))) return false;
            }
            return true;
        }
        return a.equals(b);
    }

    /**
     * Interfaz funcional para definir funciones Lisp.
     */
    public interface LispFunction {
        Object apply(List<Object> args) throws EvaluatorException;
    }

    /**
     * Excepción personalizada para errores de evaluación Lisp.
     */
    public static class EvaluatorException extends Exception {
        public EvaluatorException(String message) {
            super(message);
        }
    }
}
