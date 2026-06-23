## 3.1. Introducción y Fundamentos de la Higiene Gramatical

En la teoría de autómatas y lenguajes formales, una Gramática Libre de Contexto (GLC) es un modelo matemático robusto para definir la sintaxis de los lenguajes de programación. Formalmente, una GLC se define como una tétrada $G = (V, \Sigma, P, S)$, donde $V$ es un conjunto finito de variables o símbolos no terminales, $\Sigma$ es un alfabeto finito de símbolos terminales disjunto de $V$ ($V \cap \Sigma = \emptyset$), $P$ es un conjunto finito de producciones de la forma $A \rightarrow \alpha$ (con $A \in V$ y $\alpha \in (V \cup \Sigma)^*$), y $S \in V$ es el símbolo inicial.

Sin embargo, la mera validez generativa de una gramática no garantiza su viabilidad computacional en la fase de análisis sintáctico (_parsing_). Una gramática puede reconocer con precisión un lenguaje $L(G)$ y, al mismo tiempo, poseer taras estructurales que degradan la eficiencia del compilador o la vuelven incompatible con los algoritmos de análisis deterministas (como los analizadores predictivos LL o los basados en tablas LR). La **higiene gramatical** constituye el conjunto de transformaciones algebraicas y estructurales sobre el conjunto de producciones $P$ destinadas a erradicar estas patologías sin alterar el lenguaje indexado, garantizando que $L(G) = L(G_{optimizada})$.
## 3.2. CASO A: Ambigüedad en Gramáticas Formales y Multiplicidad Estructural

### 3.2.1. Definición Formal de Ambigüedad Sintáctica

Sea una gramática G=(V,Σ,P,S). Se define una derivación por la izquierda (_leftmost derivation_) como aquella donde en cada paso de sustitución se reemplaza el no terminal situado más a la izquierda en la forma de frase. Una gramática G se clasifica matemáticamente como **ambigua** si existe al menos una cadena de terminales ω∈L(G) que admite dos o más árboles de derivación sintáctica estructurados de forma no isomorfa, o de manera equivalente, dos o más derivaciones por la izquierda diferenciadas.

La ambigüedad es una propiedad de la gramática, no del lenguaje. Existen lenguajes ambiguos que poseen gramáticas equivalentes no ambiguas (ambigüedad inherente vs. ambigüedad gramatical).


### 3.2.2. Formalización de la Gramática Enferma ($G_{amb}$)

Definimos la gramática recursiva simétrica para expresiones aritméticas que carece de restricciones jerárquicas:

$$V = \{E\}, \quad \Sigma = \{+, \times, \text{id}\}, \quad S = E$$

El conjunto de producciones $P$ viene dado por:

$$E \rightarrow E + E \quad \mid \quad E \times E \quad \mid \quad \text{id}$$

### 3.2.3. Selección de la Cadena de Prueba e Inducción de Conflicto

Para demostrar la patología, se somete la gramática a la cadena de tokens:

$$\omega = \text{id}_1 + \text{id}_2 \times \text{id}_3$$
### 3.2.4. Demostración Algebraica por Doble Derivación por la Izquierda

Un analizador sintáctico procesa la cadena de izquierda a derecha. A continuación se exponen las dos secuencias de derivación divergentes que demuestran la ambigüedad:

#### Derivación por la Izquierda 1 ($D_1$):

Esta secuencia prioriza la estructura de la suma en la raíz del árbol, lo que desplaza la multiplicación hacia las hojas (mayor precedencia semántica).

1.  $E \Rightarrow E + E$ (Por sustitución de $E \rightarrow E + E$)
    
2.  $E \Rightarrow \text{id}_1 + E$ (Por sustitución del no terminal extremo izquierdo $E \rightarrow \text{id}$)
    
3.  $E \Rightarrow \text{id}_1 + E \times E$ (Por expansión del segundo no terminal $E \rightarrow E \times E$)
    
4.  $E \Rightarrow \text{id}_1 + \text{id}_2 \times E$ (Por sustitución del no terminal intermedio $E \rightarrow \text{id}$)
    
5.  $E \Rightarrow \text{id}_1 + \text{id}_2 \times \text{id}_3$ (Por sustitución del no terminal final $E \rightarrow \text{id}$)
    

#### Derivación por la Izquierda 2 ($D_2$):

Esta secuencia anida la suma dentro de la rama izquierda de la multiplicación, invirtiendo la precedencia algebraica estándar.

1.  $E \Rightarrow E \times E$ (Por sustitución de $E \rightarrow E \times E$)
    
2.  $E \Rightarrow E + E \times E$ (Por expansión del no terminal extremo izquierdo $E \rightarrow E + E$)
    
3.  $E \Rightarrow \text{id}_1 + E \times E$ (Por sustitución del no terminal extremo izquierdo $E \rightarrow \text{id}$)
    
4.  $E \Rightarrow \text{id}_1 + \text{id}_2 \times E$ (Por sustitución del no terminal intermedio $E \rightarrow \text{id}$)
    
5.  $E \Rightarrow \text{id}_1 + \text{id}_2 \times \text{id}_3$ (Por sustitución del no terminal final $E \rightarrow \text{id}$)

### 3.2.5. Topologías de Árboles Sintácticos Abstractos (AST)

Como consecuencia directa de las derivaciones $D_1$ y $D_2$, el sistema genera dos estructuras jerárquicas incompatibles:

```
    Árbol Sintáctico S1 (Asociatividad Correcta)     Árbol Sintáctico S2 (Asociatividad Incorrecta)
               [ E ]                                              [ E ]
              /  |  \                                            /  |  \
           [E]   +   [ E ]                                    [ E ] ×   [E]
            |       /  |  \                                  /  |  \     |
           id1   [E]   ×   [E]                            [E]   +   [E] id3
                  |         |                              |         |
                 id2       id3                            id1       id2
```

### 3.2.6. Impacto Crítico en la Arquitectura del Compilador

En la fase de **Análisis Semántico**, el compilador recorre el árbol sintáctico de abajo hacia arriba (_post-order traversal_) para emitir el código intermedio o ejecutar las instrucciones.

-   En **S1**, se evalúa primero $\text{id}_2 \times \text{id}_3$ y el resultado se suma a $\text{id}_1$, preservando el orden matemático usual: $\text{id}_1 + (\text{id}_2 \times \text{id}_3)$.
    
-   En **S2**, se fuerza la evaluación de $\text{id}_1 + \text{id}_2$ en primer lugar, multiplicando el resultado por $\text{id}_3$, lo que equivale a: $(\text{id}_1 + \text{id}_2) \times \text{id}_3$.
    

La ambigüedad causa que un programa semánticamente unívoco genere resultados binarios variables e impredecibles dependiendo del recorrido algorítmico que efectúe el _parser_.

## 3.3. CASO B: Eliminación de Recursividad por la Izquierda

### 3.3.1. Fundamentación del Bucle Infinito en Parsers Top-Down

Un analizador sintáctico descendente (como los de descenso recíproco o LL(1)) construye el árbol de arriba hacia abajo, mapeando cada no terminal a una función en código. Si la gramática contiene una regla recursiva por la izquierda de la forma $A \rightarrow A\alpha$, la función asociada al no terminal $A$ se invocará a sí misma de manera inmediata antes de consumir cualquier token de la entrada $\Sigma$. Al carecer de una condición de parada basada en el avance del flujo de tokens, el autómata sintáctico incurre en una **divergencia recursiva infinita** (_stack overflow_).

### 3.3.2. Presentación de la Gramática Patológica

Tomemos el modelo recursivo inmediato por la izquierda:

$$\begin{aligned} E &\rightarrow E + T \\ E &\rightarrow T \end{aligned}$$

Donde la variable $E$ inicia su propia derivación en su primera alternativa de producción.

### 3.3.3. Formalización del Algoritmo de Transformación

Para el caso general de recursividad izquierda inmediata asociada a un no terminal $A$:

$$A \rightarrow A\alpha_1 \mid A\alpha_2 \mid \dots \mid A\alpha_m \mid \beta_1 \mid \beta_2 \mid \dots \mid \beta_n$$

Donde ningún $\beta_i$ comienza con el símbolo $A$.

Este esquema genera cadenas que consisten en un caso base $\beta$ seguido de un número finito de sufijos $\alpha$. Se puede reescribir algebraicamente el sistema mutando la recursividad a la derecha mediante la introducción de una variable artificial extendida $A'$:

$$A \rightarrow \beta_1 A' \mid \beta_2 A' \mid \dots \mid \beta_n A'$$

$$A' \rightarrow \alpha_1 A' \mid \alpha_2 A' \mid \dots \mid \alpha_m A' \mid \epsilon$$

### 3.3.4. Aplicación Rigurosa Paso a Paso sobre la Gramática

1.  **Identificación de Parámetros:** Comparando las reglas de $E$ con la forma general:
    
    -   Símbolo crítico: $A = E$
        
    -   Cadena de cola recursiva ($\alpha_1$): $+ T$
        
    -   Factor de escape o caso base ($\beta_1$): $T$
        
2.  **Construcción de la Regla Principal de Transición:** Se define la producción para la variable original $E$, obligándola a iniciar con el caso base no recursivo $\beta_1$ y sufijando la variable de extensión:
    
    $$E \rightarrow T E'$$
    
3.  **Construcción de las Reglas del Símbolo Auxiliar:** Se definen las transiciones para la nueva variable $E'$, encargada de absorber los operadores aditivos secuenciales de forma recursiva por la derecha o finalizar mediante la producción de la cadena vacía $\epsilon$:
    
    $$E' \rightarrow + T E' \quad \mid \quad \epsilon$$


### 3.3.5. Gramática Limpia Equivalente ($G_{limpia}$)

El conjunto de producciones higienizado, libre de recursividad por la izquierda e idóneo para _parsers_ LL(1), se consolida como:

$$\begin{aligned} E &\rightarrow T E' \\ E' &\rightarrow + T E' \quad \mid \quad \epsilon \\ T &\rightarrow \text{id} \end{aligned}$$


## 3.4. CASO C: Factorización por la Izquierda

### 3.4.1. El Problema del No-Determinismo en la Lectura Anticipada (_Lookahead_)

Los analizadores predictivos deterministas evalúan el flujo de entrada apoyándose en un número fijo de tokens de lectura anticipada (típicamente $k=1$, denotado como LL(1)). Si un no terminal $A$ posee múltiples alternativas de derivación que comparten un prefijo común en $\Sigma^*$, el cálculo de los conjuntos de predicción o de selección arrojará una intersección no vacía. Ante esto, el _parser_ no cuenta con suficiente información sintáctica para discriminar de forma unívoca qué rama tomar basándose únicamente en el token actual, rompiendo el determinismo del autómata.

### 3.4.2. Presentación de la Gramática No-Factorizada

El conflicto se manifiesta de forma clásica en la ambigüedad estructural de las sentencias condicionales anidadas o de opción única (_Dangling Else_):

$$\begin{aligned} S &\rightarrow \textbf{if } C \textbf{ then } S \textbf{ else } S \\ S &\rightarrow \textbf{if } C \textbf{ then } S \\ S &\rightarrow \textbf{assignment} \end{aligned}$$

Donde se evidencia que las dos primeras producciones de la sentencia $S$ comparten el prefijo exacto:

$$\alpha = \textbf{if } C \textbf{ then } S$$


### 3.4.3. Proceso Matemático de Extracción de Factor Común

El lema de factorización sintáctica establece que para un conjunto de producciones con factor común:

$$A \rightarrow \alpha\beta_1 \mid \alpha\beta_2 \mid \gamma$$

Se debe aislar el prefijo $\alpha$ retardando la decisión de los sufijos mediante un nuevo no terminal $A'$:

$$A \rightarrow \alpha A' \mid \gamma$$

$$A' \rightarrow \beta_1 \mid \beta_2$$

Efectuando la sustitución formal para nuestro sistema de producciones de $S$:

-   Prefijo común extraído ($\alpha$): $\textbf{if } C \textbf{ then } S$
    
-   Residuo indexado de la producción 1 ($\beta_1$): $\textbf{else } S$
    
-   Residuo indexado de la producción 2 ($\beta_2$): $\epsilon$ (producción nula)
    
-   Componente independiente invariable ($\gamma$): $\textbf{assignment}$
    

Reestructurando el sistema lineal de producciones de la sentencia $S$ y derivando los residuos hacia una variable complementaria $S'$, se posterga la ramificación condicional del `else`.

### 3.4.4. Gramática Resultante Optimizada y Análisis Predictivo

El conjunto final de producciones higienizado estructuralmente se define como:

$$\begin{aligned} S &\rightarrow \textbf{if } C \textbf{ then } S \, S' \quad \mid \quad \textbf{assignment} \\ S' &\rightarrow \textbf{else } S \quad \mid \quad \epsilon \end{aligned}$$

### 3.4.5. Resolución Dinámica del Conflicto de Decisión

Bajo esta nueva configuración gramatical, el comportamiento del analizador sintáctico se torna determinista:

1.  Al recibir en el flujo el token inicial `if`, el _parser_ selecciona unívocamente la producción $S \rightarrow \textbf{if } C \textbf{ then } S \, S'$.
    
2.  Consume la expresión de control $C$, la palabra clave `then` y la sub-sentencia sintáctica interna $S$.
    
3.  Una vez completado este subárbol, el estado de evaluación se posiciona sobre la variable diferida $S'$.
    
4.  En este punto, el _lookahead_ de un token inspecciona el flujo de entrada:
    
    -   Si el siguiente token es exactamente `else`, se expande la producción determinista $S' \rightarrow \textbf{else } S$.
        
    -   Si el token corresponde a cualquier otro símbolo de la gramática (por ejemplo, el inicio de otra asignación o un delimitador de bloque), se asume el cierre de la condicional simple mediante la regla de anulación $S' \rightarrow \epsilon$.
        

La factorización por la izquierda preserva el determinismo sintáctico sin alterar las restricciones morfológicas o semánticas del lenguaje original.
### Conclusión Técnica de la Factorización

A través de esta reestructuración, cuando el analizador predictivo procesa el token `if`, selecciona directamente la primera producción de $S$ de forma determinista. Una vez consumida la expresión condicional y la sentencia interna, el estado del _parser_ se sitúa sobre el no terminal $S'$. En este punto, el evaluador consulta el flujo de entrada: si el siguiente token es `else`, derivará de forma inequívoca en $\textbf{else } S$; si se encuentra cualquier otro token, aplicará la producción vacía $\epsilon$, resolviendo el conflicto de decisión.
