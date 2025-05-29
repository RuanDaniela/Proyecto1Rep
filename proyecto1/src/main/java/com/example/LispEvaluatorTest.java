package com.example;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class LispEvaluatorTest {

    private LispEvaluator evaluator;

    @BeforeEach
    public void setup() {
        evaluator = new LispEvaluator();
    }

    @Test
    public void testSetqAndVariable() throws EvaluatorException {
        // (setq x 10)
        List<Object> setqExpr = Arrays.asList("setq", "x", 10);
        Object result = evaluator.evaluar(setqExpr);
        assertEquals(10, result);

        // Evaluar x retorna 10
        Object xVal = evaluator.evaluar("x");
        assertEquals(10, xVal);
    }

    @Test
    public void testFactorialFunction() throws EvaluatorException {
        // Definir factorial
        List<Object> factorialDef = Arrays.asList(
            "defun", "factorial", Arrays.asList("n"),
            Arrays.asList(
                "cond",
                Arrays.asList("=", "n", 0), 1,
                "t", Arrays.asList("*", "n", Arrays.asList("factorial", Arrays.asList("-", "n", 1)))
            )
        );
        Object defResult = evaluator.evaluar(factorialDef);
        assertEquals("factorial", defResult);

        // Calcular factorial(5) → 120
        List<Object> fact5 = Arrays.asList("factorial", 5);
        Object res = evaluator.evaluar(fact5);
        assertEquals(120, res);
    }

    @Test
    public void testCondTrueBranch() throws EvaluatorException {
        // (cond ((= 1 0) "nope") ((= 2 2) "sí") (t "nunca llega acá"))
        List<Object> condExpr = Arrays.asList(
            "cond",
            Arrays.asList(Arrays.asList("=", 1, 0), "nope"),
            Arrays.asList(Arrays.asList("=", 2, 2), "sí"),
            Arrays.asList("t", "nunca llega acá")
        );
        Object res = evaluator.evaluar(condExpr);
        assertEquals("sí", res);
    }

    @Test
    public void testArithmetic() throws EvaluatorException {
        // (+ 3 (* 4 2)) = 11
        List<Object> expr = Arrays.asList("+", 3, Arrays.asList("*", 4, 2));
        Object res = evaluator.evaluar(expr);
        assertEquals(11, res);
    }

    // Más pruebas según tus métodos y casos...
}
