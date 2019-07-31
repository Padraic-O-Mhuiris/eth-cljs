(ns eth.macros)


(defmacro <?
  [ch]
  `(throw-err (cljs.core.async/<! ~ch)))
