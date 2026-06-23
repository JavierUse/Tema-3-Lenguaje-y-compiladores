🧬 DNA-Turtle Graphics ApplicationEste módulo contiene la implementación práctica de una aplicación de escritorio en Java que fusiona las Gramáticas Libres de Contexto (GLC) con los Gráficos de Tortuga (Turtle Graphics) para validar, procesar y renderizar figuras geométricas a partir de cadenas de texto basadas en un alfabeto genómico.

🏗️ Arquitectura del SistemaEl código fuente está estructurado de manera modular en cuatro componentes principales:

🔍 DnaParser (Validador Sintáctico): Evalúa los tokens de la cadena en tiempo de ejecución. Verifica que pertenezcan estrictamente al alfabeto $\Sigma = \{a, c, g, t, [, ]\}$ y controla que los corchetes estén perfectamente balanceados. Lanza una excepción personalizada (DnaSyntaxException) con la posición exacta ante cualquier fallo.

📚 GrammarFigures (Repositorio de Gramáticas): Almacena las cadenas de ADN predefinidas por defecto y encapsula las secuencias de las derivaciones formales por la izquierda para mostrarlas de forma interactiva en la interfaz de usuario.

🎨 TurtleCanvas (Motor Gráfico): Procesa secuencialmente cada nucleótido sobre un plano bidimensional. Utiliza una estructura de Pila (Stack) para el manejo de bifurcaciones (operaciones Push y Pop con los corchetes) y calcula dinámicamente el escalado y centrado automático de la figura en el lienzo.

💻 Interfaz Gráfica (GUI): Desarrollada con Swing, proporciona un entorno interactivo limpio con un menú desplegable para cargar las figuras básicas, un campo editable para pruebas experimentales y un panel lateral dedicado a visualizar las derivaciones matemáticas.

🛠️ Requisitos de Ejecución

☕ Java Development Kit (JDK): Versión 17 o superior.

📦 Dependencias: Ninguna externa. Utiliza exclusivamente las bibliotecas nativas estándar del ecosistema Java (AWT, Swing y colecciones de utilidad).

🚀 Instrucciones de Uso

Iniciar: Ejecute la clase principal desde su IDE o entorno de desarrollo.

Seleccionar: Use el menú desplegable para cargar cualquiera de las estructuras morfológicas (Cuadrado, Cubo, Árbol, Escalera o Cruz).

Experimentar: Modifique el campo "Cadena ADN" de forma manual si desea probar la tolerancia a fallos del validador ante caracteres inválidos o corchetes rotos.

Compilar: Presione el botón "Dibujar / Procesar" para evaluar sintácticamente el flujo de datos y renderizar el resultado en tiempo real.
