# 2. Preguntas de investigación y desarrollo

## 2.1.- Fundamentos y Jerarquía (Teoría Aplicada)

Este apartado detalla el marco matemático que sustenta la teoría de lenguajes formales y la jerarquía de gramáticas propuesta por Noam Chomsky, estableciendo los cimientos teóricos necesarios para la construcción de traductores, analizadores y compiladores.

---

### 2.2.1.- Relación Gramática-Lenguaje

#### Concepto de la Relación entre Gramáticas y Lenguajes Formales

En ciencias de la computación y lingüística matemática, la relación entre una gramática y un lenguaje formal es de carácter **generativo y descriptivo**: la gramática es el conjunto finito de reglas (la especificación sintáctica) que define y produce la totalidad de las cadenas (habitualmente un conjunto infinito) que componen el lenguaje.

Para comprender esta relación formalmente, definimos:
1.  **Alfabeto (Σ):** Un conjunto finito y no vacío de símbolos (por ejemplo, `Σ = {a, b}`).
2.  **Palabra o Cadena (w):** Una secuencia finita de símbolos elegidos del alfabeto `Σ`. La cadena vacía se representa tradicionalmente con la letra griega `ε` (épsilon) o `λ` (lambda).
3.  **Clausura de Kleene (Σ*):** El conjunto de todas las palabras posibles de cualquier longitud (incluyendo la palabra vacía) que se pueden construir con los símbolos de `Σ`.
4.  **Lenguaje Formal (L):** Un subconjunto de la Clausura de Kleene (`L ⊆ Σ*`).

Una **Gramática Formal (G)** se define matemáticamente como una cuádrupla:

`G = (V, Σ, P, S)`

Donde cada uno de los elementos representa:
*   **V (Conjunto de Variables o Símbolos No Terminales):** Un conjunto finito de elementos que actúan como variables intermedias y son sustituidos durante la derivación. Convencionalmente se escriben con letras mayúsculas.
*   **Σ (Alfabeto o Símbolos Terminales):** Un conjunto finito de caracteres elementales que forman las cadenas del lenguaje final. Se cumple estrictamente la restricción de que ambos conjuntos son disjuntos: `V ∩ Σ = ∅`.
*   **P (Reglas de Producción):** Conjunto finito de reglas que definen las sustituciones permitidas. Tienen la forma general `α → β`, donde la cadena de la izquierda `α` pertenece a `(V ∪ Σ)* V (V ∪ Σ)*` (es decir, contiene obligatoriamente al menos un no terminal) y la cadena de la derecha `β` pertenece a `(V ∪ Σ)*`.
*   **S (Axioma o Símbolo Inicial):** Es el símbolo no terminal distinguido por el cual se inicia obligatoriamente todo proceso de generación (`S ∈ V`).

La relación entre ambos conceptos se establece a través del conjunto de palabras formadas exclusivamente por símbolos terminales que la gramática es capaz de generar. Este conjunto constituye el **Lenguaje Generado por G**, denotado como `L(G)`:

`L(G) = { w ∈ Σ* | S ⇒* w }`

Donde `⇒*` representa una secuencia de sustituciones (derivaciones) que parten de `S` y terminan en la cadena de terminales `w`.

#### Ejemplo Práctico de la Relación

Consideremos el lenguaje de expresiones lógicas booleanas simples compuestas por literales de verdad (`t` y `f`) y el operador de conjunción (`&`).
*   **Alfabeto Terminal (Σ):** `Σ = {t, f, &}`
*   **Gramática (G):**
    *   Variables (V): `{E, Vval}`
    *   Axioma (S): `E`
    *   Reglas de Producción (P):
        *   `E → Vval`
        *   `E → E & E`
        *   `Vval → t`
        *   `Vval → f`
*   **Lenguaje generado L(G):** El conjunto de cadenas sintácticamente correctas que se derivan de las reglas:
    `L(G) = {t, f, t & t, t & f, f & t & t, ...}`
    Cadenas mal formadas como `t &` o `& f` no pueden ser generadas por ninguna combinación de las reglas de `P`, por lo que quedan excluidas del lenguaje.

#### Mecanismo de Generación (Derivación) Detallado

Una gramática genera un lenguaje formal mediante un proceso iterativo de reescritura de cadenas conocido como **derivación**:

1.  **Inicialización:** El proceso comienza obligatoriamente con el axioma inicial `S`.
2.  **Sustitución Directa (⇒):** Si en una cadena actual `xαy` existe una subcadena `α` que coincide con el lado izquierdo de una regla `α → β` del conjunto `P`, se reemplaza `α` por `β`, obteniendo la nueva cadena `xβy`. Esto se denota como:
    `xαy ⇒ xβy`
3.  **Sustitución en Múltiples Pasos (⇒*):** Se aplican sucesivas reglas de producción de la forma:
    `S ⇒ w1 ⇒ w2 ⇒ ... ⇒ wn`
    Cuando se realizan cero o más pasos de derivación para pasar de una cadena a otra, se denota con el operador transitivo `⇒*` (ej. `S ⇒* wn`).
4.  **Finalización:** El proceso de generación de una palabra concluye con éxito cuando la cadena obtenida contiene exclusivamente símbolos del alfabeto terminal (`wn ∈ Σ*`). Si aún quedan no terminales en la cadena, el proceso debe continuar hasta eliminarlos todos.

---

### 2.2.2.- Jerarquía de Chomsky

La Jerarquía de Chomsky clasifica las gramáticas formales en cuatro niveles o tipos (de 0 a 3) en función del grado de restricción aplicado a las reglas de producción `α → β`. A mayor tipo, las reglas son más restrictivas y el autómata necesario para su procesamiento es más sencillo.

#### Los 4 Tipos de Gramáticas

1.  **Tipo 0: Gramáticas Sin Restricciones (Recursivamente Enumerables):**
    *   **Formato de Reglas:** `α → β`, donde `α` debe contener al menos un no terminal (`α ∈ (V ∪ Σ)* V (V ∪ Σ)*`) y `β` es una cadena cualquiera (`β ∈ (V ∪ Σ)*`). No existen limitaciones en la longitud de las cadenas a ambos lados de la regla.
    *   **Autómata asociado:** Máquina de Turing.
2.  **Tipo 1: Gramáticas Sensibles al Contexto (Context-Sensitive):**
    *   **Formato de Reglas:** `α A β → α γ β`, donde `A` es un símbolo no terminal, `α` y `β` representan el contexto circundante de variables o terminales, y `γ` es una cadena de símbolos no vacía (`γ ≠ ε`).
    *   **Restricción alternativa (No contracción):** Para toda regla `δ → η`, la longitud del lado izquierdo es menor o igual a la del derecho (`|δ| ≤ |η|`), garantizando que la cadena no se reduzca de tamaño durante el proceso.
    *   **Autómata asociado:** Autómata Linealmente Acotado (LBA).
3.  **Tipo 2: Gramáticas Libres de Contexto (Context-Free):**
    *   **Formato de Reglas:** `A → β`, donde el lado izquierdo contiene única y estrictamente un símbolo no terminal (`A ∈ V`) y el lado derecho es cualquier cadena (`β ∈ (V ∪ Σ)*`). La sustitución se realiza sin importar el contexto en el que se encuentre la variable.
    *   **Autómata asociado:** Autómata de Pila (PDA).
4.  **Tipo 3: Gramáticas Regulares:**
    *   **Formato de Reglas:** Reglas que siguen un orden lineal estricto. En el caso lineal derecho, las reglas permitidas son `A → a`, `A → aB` y `A → ε` (donde `A, B` son variables, `a` es un terminal y `ε` representa la cadena vacía).
    *   **Autómata asociado:** Autómata Finito (Determinista o No Determinista - DFA o NFA).

---

#### Ejemplos Prácticos en Notación BNF (y adaptaciones formales)

> [!IMPORTANT]
> La notación **BNF (Backus-Naur Form)** es por definición un formalismo diseñado para Gramáticas Libres de Contexto (Tipo 2). Las gramáticas de Tipo 1 y Tipo 0, al requerir la evaluación o alteración del contexto circundante de los símbolos no terminales, no pueden expresarse de forma nativa en la sintaxis estándar de BNF. Para resolver esto y proveer ejemplos prácticos en notación compatible, se utiliza la sintaxis BNF estándar para los Tipos 2 y 3, y representaciones de reglas de reescritura de múltiples símbolos (BNF contextual extendido) para los Tipos 1 y 0.

##### 1. Tipo 3: Gramática Regular (Estructura de una Dirección de Correo Electrónico Simplificada)
Este ejemplo define una versión simplificada de un correo electrónico (`usuario@dominio.com`), con reglas puramente lineales derechas (donde a lo sumo hay un no terminal en el extremo derecho).

```bnf
<email>         ::= <char> <email_rest>
<email_rest>    ::= <char> <email_rest> | "@" <domain>
<domain>        ::= <char> <domain_rest>
<domain_rest>   ::= <char> <domain_rest> | "." <tld>
<tld>           ::= "c" <tld_o>
<tld_o>         ::= "o" <tld_m>
<tld_m>         ::= "m"
<char>          ::= "a" | "b" | "c" | "d" | "x" | "y" | "z"
```

##### 2. Tipo 2: Gramática Libre de Contexto (Estructura de Bloques Anidados con Llaves `{}`)
Permite anidamiento arbitrario y balance simétrico de llaves, estructura común en lenguajes de programación.

```bnf
<block>          ::= "{" <statement_list> "}"
<statement_list> ::= <statement> <statement_list> | ""
<statement>      ::= <block> | "id" "=" "expr" ";"
```

##### 3. Tipo 1: Gramática Sensible al Contexto (Lenguaje no libre de contexto a^n b^n c^n)
El lenguaje `a^n b^n c^n` no puede describirse mediante gramáticas libres de contexto tradicionales debido a la necesidad de coordinar el número de símbolos de tres grupos distintos. En una representación BNF extendida contextualmente, las reglas se definen permitiendo patrones en el lado izquierdo:

```bnf
/* Reglas iniciales de expansión */
S               ::= "a" S B C | "a" B C

/* Regla sensible al contexto: reordenamiento de variables */
/* Permite que la variable B transite a través de C */
C B             ::= B C

/* Reglas de resolución terminal bajo contexto específico */
/* B se convierte en 'b' solo si está precedido por 'a' o 'b' */
"a" B           ::= "a" "b"
"b" B           ::= "b" "b"

/* C se convierte en 'c' solo si está precedido por 'b' o 'c' */
"b" C           ::= "b" "c"
"c" C           ::= "c" "c"
```

##### 4. Tipo 0: Gramática Sin Restricciones (Cómputo General: Lenguaje a^(2^n))
Este lenguaje genera cadenas de longitud potencia de 2 (`a`, `aa`, `aaaa`, `aaaaaaaa`, etc.). Requiere una gramática que actúe como un algoritmo completo de duplicación de cadenas.

```bnf
/* Configuración inicial con delimitadores de frontera L (izq) y R (der) */
S               ::= L "a" R

/* Generación de marcadores de duplicación D */
L               ::= L D

/* Viaje del duplicador D hacia la derecha duplicando cada terminal 'a' */
D "a"           ::= "a" "a" D

/* Absorción y eliminación de marcadores cuando el proceso de copia termina */
D R             ::= R
L               ::= ""
R               ::= ""
```
