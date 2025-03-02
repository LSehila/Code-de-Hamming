package hamming;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HammingInterface extends JFrame {
    private JTextField textField;
    private JTextArea resultArea;
    private JButton encodeButton, verifyButton;
    private JPanel hammingPanel;

    public HammingInterface() {
        super("Code de Hamming");
        init();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Définition d'un fond sombre pour un style moderne
        Color backgroundColor = new Color(45, 45, 45);
        Color buttonColor = new Color(30, 136, 229);
        Color textColor = Color.WHITE;

        // === Panel pour l'entrée ===
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBackground(backgroundColor);
        JLabel label = new JLabel("Entrez une trame binaire :");
        label.setForeground(textColor);
        textField = new JTextField(20);
        textField.setBackground(Color.DARK_GRAY);
        textField.setForeground(Color.WHITE);
        inputPanel.add(label);
        inputPanel.add(textField);

        // === Zone de texte (résultats) ===
        resultArea = new JTextArea(8, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setBackground(Color.BLACK);
        resultArea.setForeground(Color.GREEN);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // === Panel pour les boutons ===
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(backgroundColor);
        encodeButton = createStyledButton("Calculer Code de Hamming", buttonColor, textColor);
        verifyButton = createStyledButton("Vérifier", buttonColor, textColor);
        buttonPanel.add(encodeButton);
        buttonPanel.add(verifyButton);

        // === Panel pour afficher la table Hamming ===
        hammingPanel = new JPanel();
        hammingPanel.setPreferredSize(new Dimension(600, 150));
        hammingPanel.setBackground(backgroundColor);

        // === Panel regroupant l'affichage et la JTextArea en dessous ===
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BorderLayout());
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        displayPanel.setBackground(backgroundColor);
        displayPanel.add(hammingPanel, BorderLayout.NORTH);
        displayPanel.add(scrollPane, BorderLayout.CENTER);

        // === Ajout des composants à la fenêtre ===
        add(inputPanel, BorderLayout.NORTH);
        add(displayPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addEventListeners();
        setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void addEventListeners() {
      encodeButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = textField.getText().trim();

            if (!input.matches("[01]+")) {
                JOptionPane.showMessageDialog(HammingInterface.this, 
                    "Veuillez entrer une trame binaire valide.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Vérifier si la longueur du message respecte la règle (2^i - 1) - i
            if (!HammingCode.isMessageValid(input)) {
                JOptionPane.showMessageDialog(HammingInterface.this, 
                    "La taille du message ne respecte pas la règle de Hamming.\n"
                    + "Longueur attendue : (2^i - 1) - i.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            resultArea.setText("=== Encodage du message en code de Hamming ===\n");
            resultArea.append("Message original : " + input + "\n");

            int[] encoded = HammingCode.encodeHamming(input);
            resultArea.append("Message encodé :\n" + Arrays.toString(encoded) + "\n");
            updateHammingTable(encoded);
        }
    });


      verifyButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = textField.getText().trim();

            if (!input.matches("[01]+")) {
                JOptionPane.showMessageDialog(HammingInterface.this, 
                    "❌ Veuillez entrer une trame binaire valide.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Vérifier si la longueur de la trame respecte la règle x = 2^i - 1
            if (!HammingCode.isHammingCodeValid(input)) {
                JOptionPane.showMessageDialog(HammingInterface.this, 
                    "⚠️ La taille du mot de Hamming ne respecte pas la règle.\n"
                    + "Longueur attendue : 2^i - 1.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            resultArea.setText("🔍 === Vérification et correction ===\n\n");
            resultArea.append("📥 Message reçu : " + input + "\n\n");

            Integer[] syndromeBits = HammingCode.decodeHamming(input);
            resultArea.append("🛠 Bits de contrôle : " + Arrays.toString(syndromeBits) + "\n");

            // Calcul de la position de l'erreur en décimal
            int errorPosition = HammingCode.binaryToDecimal(syndromeBits);
            String binaryPosition = Arrays.stream(syndromeBits)
                                          .map(String::valueOf)
                                          .collect(Collectors.joining(""));

            if (errorPosition == 0) {
                resultArea.append("\n✅ Aucune erreur détectée.\n");
            } else {
                resultArea.append("\n⚠️ Erreur détectée à la position : " + errorPosition + " (Décimal) \n");
                resultArea.append("   ➡ Position binaire : " + binaryPosition + "\n");

                // Correction du message
                List<Integer> correctedMessage = HammingCode.correctHamming(input);
                String correctedMessageStr = correctedMessage.stream()
                                                             .map(String::valueOf)
                                                             .collect(Collectors.joining(""));
                resultArea.append("\n✔️ Message corrigé : " + correctedMessageStr + "\n");

                updateHammingTable(correctedMessage.stream().mapToInt(i -> i).toArray());
            }
        }
    });

    }

    private void updateHammingTable(int[] encoded) {
        List<Integer> messageList = Arrays.stream(encoded).boxed().collect(Collectors.toList());
        Integer[] controlBits = HammingCode.getControlBitsPositions(encoded.length);

        hammingPanel.removeAll();
        hammingPanel.add(new HammingTableComponent(messageList, controlBits));
        hammingPanel.revalidate();
        hammingPanel.repaint();
    }

    static class HammingTableComponent extends JComponent {
        private List<Integer> message;
        private Integer[] bitsControle;

        public HammingTableComponent(List<Integer> message, Integer[] bitsControle) {
            this.message = message;
            this.bitsControle = bitsControle;
            setPreferredSize(new Dimension(500, 100));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));

            int width = getWidth();
            int cellWidth = width / message.size();
            int cellHeight = 40;
            int startX = 10;
            int startY = 30;

            List<Integer> controlPositions = Arrays.asList(bitsControle);
            int totalBits = message.size();
            int numControlBits = bitsControle.length;
            int numDataBits = totalBits - numControlBits;

            List<String> bitLabels = new ArrayList<>();
            int dataIndex = numDataBits - 1;
            int controlIndex = numControlBits - 1;

            for (int i = totalBits - 1; i >= 0; i--) {
                if (controlPositions.contains(i)) {
                    bitLabels.add("C" + controlIndex);
                    controlIndex--;
                } else {
                    bitLabels.add("D" + dataIndex);
                    dataIndex--;
                }
            }

            for (int i = 0; i < totalBits; i++) {
                g2.drawRect(startX + i * cellWidth, startY, cellWidth, cellHeight);
                g2.drawString(bitLabels.get(i), startX + i * cellWidth + cellWidth / 3, startY - 5);
                g2.drawString(String.valueOf(message.get(i)), startX + i * cellWidth + cellWidth / 2, startY + 20);
            }
        }
    }

    public static void main(String[] args) {
        new HammingInterface();
    }
}
