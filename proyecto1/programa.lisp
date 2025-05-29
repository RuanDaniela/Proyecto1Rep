(defun factorial (n)
  (cond ((= n 0) 1)
        (t (* n (factorial (- n 1))))))

(factorial 5)

(defun fibonacci (n)
  (cond ((< n 2) 1)
        (t (+ (fibonacci (- n 1)) (fibonacci (- n 2))))))

(fibonacci 6)

(setq a 3)
(setq b 4)
(+ a (* b 2))

