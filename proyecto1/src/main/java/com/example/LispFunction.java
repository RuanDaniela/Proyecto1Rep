package com.example;

import java.util.List;

/**
 * Interfaz funcional que representa una función de Lisp
 * que puede ser aplicada a una lista de argumentos.
 */
@FunctionalInterface
public interface LispFunction {

    /**
     * Aplica la función a la lista de argumentos proporcionada.
     *
     * @param args Lista de argumentos para la función.
     * @return El resultado de la evaluación de la función.
     * @throws EvaluatorException Si ocurre un error durante la evaluación.
     */
    Object apply(List<Object> args) throws EvaluatorException;
}
