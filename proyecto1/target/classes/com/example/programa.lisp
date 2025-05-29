(setq t 1)

(defun factorial (n)
  (cond ((= n 0) 1)
        (t (* n (factorial (- n 1))))))

(defun fibonacci (n)
  (cond ((< n 2) 1)
        (t (+ (fibonacci (- n 1)) (fibonacci (- n 2))))))

(defun fahrenheit (c)
  (+ (* c 1.8) 32))

