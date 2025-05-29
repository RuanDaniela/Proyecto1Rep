package com.example;

/**
 * Excepción personalizada que representa errores ocurridos durante
 * la evaluación de expresiones Lisp en el intérprete.
 */
public class EvaluatorException extends Exception {

    /**
     * Construye una nueva EvaluatorException con un mensaje detallado.
     *
     * @param message Mensaje que describe la causa de la excepción.
     */
    public EvaluatorException(String message) {
        super(message);
    }
}
