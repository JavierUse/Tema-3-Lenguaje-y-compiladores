package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * =====================================================================================
 *  DNA-TURTLE APP
 *  Derivación y Modelado de Figuras mediante Cadenas ADN
 * =====================================================================================
 */
public class Main {

    // =================================================================================
    // 1) EXCEPCIÓN DE ERROR SINTÁCTICO
    // =================================================================================
    static class DnaSyntaxException extends Exception {
        public DnaSyntaxException(String message) {
            super(message);
        }
    }

    // =================================================================================
    // 2) MÓDULO PARSER / VALIDADOR
    // =================================================================================
    static class DnaParser {
        public static void validate(String code) throws DnaSyntaxException {
            if (code == null || code.isEmpty()) {
                throw new DnaSyntaxException("La cadena está vacía. Σ = {a, c, g, t, [, ]}");
            }

            int balance = 0;
            for (int i = 0; i < code.length(); i++) {
                char ch = code.charAt(i);
                switch (ch) {
                    case 'a': case 'c': case 'g': case 't':
                        break;
                    case '[':
                        balance++;
                        break;
                    case ']':
                        balance--;
                        if (balance < 0) {
                            throw new DnaSyntaxException(
                                    "Error sintáctico en la posición " + (i + 1) +
                                            ": se encontró ']' sin un '[' previo que lo abra. " +
                                            "La gramática exige que los corchetes estén balanceados."
                            );
                        }
                        break;
                    default:
                        throw new DnaSyntaxException(
                                "Error sintáctico en la posición " + (i + 1) +
                                        ": el símbolo '" + ch + "' no pertenece al alfabeto Σ = {a, c, g, t, [, ]}."
                        );
                }
            }

            if (balance != 0) {
                throw new DnaSyntaxException(
                        "Error sintáctico: hay " + balance + " corchete(s) '[' sin cerrar. " +
                                "Toda apertura '[' debe tener su correspondiente cierre ']'."
                );
            }
        }
    }

    // =================================================================================
    // 3) GENERADOR DE FIGURAS Y DERIVACIONES FORMALES
   
    // =================================================================================
    static class GrammarFigures {

        public enum Figura { CUADRADO, CUBO, ARBOL, ESCALERA, CRUZ }

        public static String generar(Figura figura) {
            if (figura == null) return "";
            switch (figura) {
                case CUADRADO:
                    // Cuadrado 5x5
                    return "aaaaacccccgggggttttt";

                case CUBO:
                    // Usaste: aaaaacccccgggggttttt[aacaaaaacccccgggggttttt]cc
                    return "aaaaacccccgggggttttt[aacaaaaacccccgggggttttt]cc";

                case ARBOL:
                    // Usaste:
                    // aaaaa[tttttaaaa[ttaa[t][c][a]][ccaa[t][c][a]][a]]cccccaaaa[ttaa[t][c][a]][ccaa[t][c][a]][a]
                    return "aaaaa[tttttaaaa[ttaa[t][c][a]][ccaa[t][c][a]][a]]cccccaaaa[ttaa[t][c][a]][ccaa[t][c][a]][a]";

                case ESCALERA:
                    // Usaste:
                    // aaaa[ttt]aaa[ttt]aaa[ttt]aaatttggggggggggggg
                    return "aaaa[ttt]aaa[ttt]aaa[ttt]aaatttggggggggggggg";

                case CRUZ:
                    // Cruz: cuatro brazos
                    return "[aaaaa][ggggg][ccccc][ttttt]";

                default:
                    throw new IllegalArgumentException("Figura no soportada");
            }
        }

        public static List<String> derivacion(Figura figura) {
            List<String> pasos = new ArrayList<>();
            if (figura == null) return pasos;

            switch (figura) {
                case CUADRADO:
                    pasos.add("Gramática: S -> aaaaa ccccc ggggg ttttt");
                    pasos.add("S => aaaaa ccccc ggggg ttttt");
                    break;

                case CUBO:
                    pasos.add("S => CUBO");
                    pasos.add("CUBO => [ CUADRADO ] aac [ CUADRADO ] [ac][ac][ac][ac]  (representación formal)");
                    pasos.add("CUADRADO => a a a a a c c c c c g g g g g t t t t t");
                    pasos.add("Cadena terminal: " + generar(Figura.CUBO));
                    break;

                case ARBOL:
                    pasos.add("S => ARBOL");
                    pasos.add("ARBOL => a RAMA");
                    pasos.add("RAMA => a [ t RAMA ] [ c RAMA ] a | aa");
                    pasos.add("Se presenta una expansión hasta hojas (estructura ramificada)");
                    pasos.add("Cadena terminal: " + generar(Figura.ARBOL));
                    break;

                case ESCALERA:
                    pasos.add("Gramática:\n  S -> aaaa [ ttt ] E g g g g g g g g g g g g g\n  E -> aaaa [ ttt ] E | ε");
                    pasos.add("S => aaaa[ttt] E ggggggggggggg");
                    pasos.add("  => aaaa[ttt] aaaa[ttt] E ggggggggggggg");
                    pasos.add("  => aaaa[ttt] aaaa[ttt] aaaa[ttt] ggggggggggggg"); // Reemplazando E por ε
                    break;

                case CRUZ:
                    pasos.add("S => CRUZ");
                    pasos.add("CRUZ => [ aaaaa ] [ ggggg ] [ ccccc ] [ ttttt ]");
                    pasos.add("Cada corchete guarda el centro y traza un brazo independiente");
                    pasos.add("Cadena terminal: " + generar(Figura.CRUZ));
                    break;
            }
            return pasos;
        }
    }

    // =================================================================================
    // 4) MÓDULO INTÉRPRETE / MOTOR GRÁFICO
    // =================================================================================
    static class TurtleCanvas extends JPanel {

        private record Segmento(double x1, double y1, double x2, double y2, char origen) {}

        private List<Segmento> segmentos = new ArrayList<>();

        public TurtleCanvas() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(800, 600));
        }

        public void interpretar(String codigo) {
            this.segmentos = new ArrayList<>();

            double x = 0, y = 0;
            final double PASO = 10.0;
            Stack<Point2D.Double> pila = new Stack<>();

            for (char ch : codigo.toCharArray()) {
                double nx = x, ny = y;
                switch (ch) {
                    case 'a': ny = y - PASO; break;
                    case 'c': nx = x + PASO; break;
                    case 'g': ny = y + PASO; break;
                    case 't': nx = x - PASO; break;
                    case '[':
                        pila.push(new Point2D.Double(x, y));
                        continue;
                    case ']':
                        if (!pila.isEmpty()) {
                            Point2D.Double p = pila.pop();
                            x = p.x; y = p.y;
                        }
                        continue;
                    default:
                        continue;
                }
                segmentos.add(new Segmento(x, y, nx, ny, ch));
                x = nx; y = ny;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            if (segmentos.isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.drawString("Seleccione una figura del dropdown y presione \"Dibujar / Procesar\"",
                        20, getHeight() / 2);
                return;
            }

            double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
            double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
            for (Segmento s : segmentos) {
                minX = Math.min(minX, Math.min(s.x1(), s.x2()));
                maxX = Math.max(maxX, Math.max(s.x1(), s.x2()));
                minY = Math.min(minY, Math.min(s.y1(), s.y2()));
                maxY = Math.max(maxY, Math.max(s.y1(), s.y2()));
            }
            double anchoDibujo = Math.max(1, maxX - minX);
            double altoDibujo = Math.max(1, maxY - minY);

            double margen = 40;
            double escalaX = (getWidth() - 2 * margen) / anchoDibujo;
            double escalaY = (getHeight() - 2 * margen) / altoDibujo;
            double escala = Math.min(1.0, Math.min(escalaX, escalaY));

            double centroX = (minX + maxX) / 2.0;
            double centroY = (minY + maxY) / 2.0;

            double offsetX = getWidth() / 2.0 - centroX * escala;
            double offsetY = getHeight() / 2.0 - centroY * escala;

            for (Segmento s : segmentos) {
                g2.setColor(colorPorNucleotido(s.origen()));
                double sx1 = s.x1() * escala + offsetX;
                double sy1 = s.y1() * escala + offsetY;
                double sx2 = s.x2() * escala + offsetX;
                double sy2 = s.y2() * escala + offsetY;
                g2.draw(new Line2D.Double(sx1, sy1, sx2, sy2));
            }
        }

        private Color colorPorNucleotido(char ch) {
            switch (ch) {
                case 'a': return new Color(0x1B6FB5);  // azul
                case 'c': return new Color(0xD9534F);  // rojo
                case 'g': return new Color(0x2E8B57);  // verde
                case 't': return new Color(0xE08E26);  // naranja
                default:  return Color.BLACK;
            }
        }
    }

    // =================================================================================
    // 5) INTERFAZ GRÁFICA DE USUARIO (GUI) 
    // =================================================================================
    private JFrame frame;
    private JComboBox<GrammarFigures.Figura> comboFiguras;
    private JTextField campoCadena;
    private JTextArea areaDerivacion;
    private TurtleCanvas canvas;

    private void crearYMostrarGUI() {
        frame = new JFrame("ADN-Tortuga | GLC sobre Σ = {a, c, g, t, [, ]}");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(8, 8));

        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBorder(new EmptyBorder(10, 10, 5, 10));

        // Fila simplificada: solo ComboBox y botón Dibujar
        JPanel filaControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaControles.add(new JLabel("Seleccionar figura:"));

        comboFiguras = new JComboBox<>(GrammarFigures.Figura.values());
        filaControles.add(comboFiguras);

        JButton btnDibujar = new JButton("Dibujar / Procesar");
        btnDibujar.setFont(btnDibujar.getFont().deriveFont(Font.BOLD));
        filaControles.add(btnDibujar);

        panelSuperior.add(filaControles);

        // Campo de cadena ADN (mostrando la cadena cargada)
        JPanel filaCadena = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaCadena.add(new JLabel("Cadena ADN:"));
        campoCadena = new JTextField(50);
        campoCadena.setEditable(true);  // permite editar manualmente si lo desea
        filaCadena.add(campoCadena);
        panelSuperior.add(filaCadena);

        frame.add(panelSuperior, BorderLayout.NORTH);

        canvas = new TurtleCanvas();
        canvas.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        frame.add(canvas, BorderLayout.CENTER);

        areaDerivacion = new JTextArea();
        areaDerivacion.setEditable(false);
        areaDerivacion.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        areaDerivacion.setLineWrap(true);
        areaDerivacion.setWrapStyleWord(true);
        JScrollPane scrollDerivacion = new JScrollPane(areaDerivacion);
        scrollDerivacion.setPreferredSize(new Dimension(330, 600));
        scrollDerivacion.setBorder(BorderFactory.createTitledBorder("Derivación formal (S => ... => cadena)"));
        frame.add(scrollDerivacion, BorderLayout.EAST);

        // Acción del ComboBox: al seleccionar, auto-carga la cadena
        comboFiguras.addActionListener(e -> actualizarCadena());

        // Acción del botón: dibuja/procesa
        btnDibujar.addActionListener(e -> procesarYDibujar());

        // Inicializar con la primera figura
        actualizarCadena();
        procesarYDibujar();

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void actualizarCadena() {
        GrammarFigures.Figura figura = (GrammarFigures.Figura) comboFiguras.getSelectedItem();
        if (figura != null) {
            String cadena = GrammarFigures.generar(figura);
            campoCadena.setText(cadena);
            mostrarDerivacion(figura);
        }
    }

    private void mostrarDerivacion(GrammarFigures.Figura figura) {
        StringBuilder sb = new StringBuilder();
        sb.append("Figura: ").append(figura).append("\n");
        sb.append("--------------------------------------\n");
        for (String paso : GrammarFigures.derivacion(figura)) {
            sb.append(paso).append("\n\n");
        }
        areaDerivacion.setText(sb.toString());
        areaDerivacion.setCaretPosition(0);
    }

    private void procesarYDibujar() {
        String cadena = campoCadena.getText().trim();
        try {
            DnaParser.validate(cadena);
            canvas.interpretar(cadena);
        } catch (DnaSyntaxException ex) {
            JOptionPane.showMessageDialog(
                    frame,
                    ex.getMessage(),
                    "Error sintáctico en la cadena ADN",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =================================================================================
    // 6) PUNTO DE ENTRADA
    // =================================================================================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new Main().crearYMostrarGUI());
    }
}
