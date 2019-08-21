(ns eth.macros)

(defmacro defeth [name args body]
  `(def ~name (fn ~args (system-check ~body))))
