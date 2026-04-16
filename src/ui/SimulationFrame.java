package ui;

import carrefour.Carrefour;
import carrefour.VoitureCarrefour;
import common.Direction;
import common.Utils;
import parking.Parking;
import parking.VoitureParking;
import tunnel.Tunnel;
import tunnel.VoitureTunnel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationFrame extends JFrame {

    private JComboBox<String> simulationCombo;
    private JTextField nbVoituresField;
    private JTextField capaciteField;
    private JTextField nbPortesField;
    private JTextPane logPane;
    private JLabel statusLabel;

    private JPanel capaciteBlock;
    private JPanel portesBlock;

    private final Random random = new Random();

    public SimulationFrame() {
        setTitle("Simulation de circulation - Projet Java");
        setSize(1180, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(AppColors.BACKGROUND);

        Utils.setLogConsumer(this::appendLog);

        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.setBackground(AppColors.BACKGROUND);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(root);
        refreshStatus("Prêt à lancer une simulation.");
    }

    private JPanel buildHeader() {
        RoundedPanel header = new RoundedPanel(new BorderLayout(15, 10), AppColors.PRIMARY, 28);
        header.setBorder(new EmptyBorder(22, 28, 22, 28));

        JLabel title = new JLabel("Simulation concurrente de circulation et de ressources partagées");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));

        JLabel subtitle = new JLabel("Projet Java - Interface intuitive pour le carrefour, le tunnel et le parking");
        subtitle.setForeground(new Color(230, 235, 255));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.CENTER);
        return header;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new GridLayout(1, 2, 20, 20));
        center.setOpaque(false);

        center.add(buildControlPanel());
        center.add(buildLogPanel());

        return center;
    }

    private JPanel buildControlPanel() {
        RoundedPanel controlPanel = new RoundedPanel(new BorderLayout(), AppColors.CARD, 24);
        controlPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel panelTitle = new JLabel("Paramètres de simulation");
        panelTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        panelTitle.setForeground(AppColors.TEXT_PRIMARY);

        JLabel panelSubtitle = new JLabel("Configurez la simulation puis lancez l'exécution.");
        panelSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panelSubtitle.setForeground(AppColors.TEXT_SECONDARY);

        content.add(panelTitle);
        content.add(Box.createVerticalStrut(6));
        content.add(panelSubtitle);
        content.add(Box.createVerticalStrut(22));

        simulationCombo = new JComboBox<>(new String[]{
                "Carrefour avec priorité à droite",
                "Tunnel à une voie",
                "Parking à n places"
        });
        styleCombo(simulationCombo);

        nbVoituresField = new JTextField("6");
        capaciteField = new JTextField("2");
        nbPortesField = new JTextField("1");

        styleTextField(nbVoituresField);
        styleTextField(capaciteField);
        styleTextField(nbPortesField);

        simulationCombo.addActionListener(e -> updateFieldsVisibility());

        content.add(createFieldBlock("Type de simulation", simulationCombo));
        content.add(Box.createVerticalStrut(16));
        content.add(createFieldBlock("Nombre de voitures", nbVoituresField));
        content.add(Box.createVerticalStrut(16));

        capaciteBlock = createFieldBlock("Capacité du parking", capaciteField);
        portesBlock = createFieldBlock("Nombre de portes", nbPortesField);

        content.add(capaciteBlock);
        content.add(Box.createVerticalStrut(16));
        content.add(portesBlock);
        content.add(Box.createVerticalStrut(20));

        JLabel note = new JLabel(
                "<html><div style='width:430px;'>"
                        + "Conseil : pour le parking, renseignez la capacité et le nombre de portes. "
                        + "Pour le tunnel et le carrefour, seul le nombre de voitures est utilisé."
                        + "</div></html>"
        );
        note.setForeground(AppColors.TEXT_SECONDARY);
        note.setFont(new Font("SansSerif", Font.PLAIN, 13));

        content.add(note);
        content.add(Box.createVerticalStrut(22));

        RoundedButton launchButton = new RoundedButton("Lancer", AppColors.ACCENT, Color.WHITE);
        RoundedButton clearButton = new RoundedButton("Effacer", AppColors.SUCCESS, Color.WHITE);
        RoundedButton quitButton = new RoundedButton("Quitter", AppColors.DANGER, Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(launchButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(quitButton);

        content.add(buttonPanel);
        content.add(Box.createVerticalStrut(22));

        statusLabel = new JLabel("Prêt à lancer une simulation.");
        statusLabel.setForeground(AppColors.PRIMARY_DARK);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        content.add(statusLabel);

        launchButton.addActionListener(e -> lancerSimulation());
        clearButton.addActionListener(e -> {
            if (logPane != null) {
                logPane.setText("");
            }
            refreshStatus("Zone d'événements réinitialisée.");
        });
        quitButton.addActionListener(e -> System.exit(0));

        controlPanel.add(content, BorderLayout.NORTH);

        updateFieldsVisibility();
        return controlPanel;
    }

    private JPanel buildLogPanel() {
        RoundedPanel logPanel = new RoundedPanel(new BorderLayout(0, 18), AppColors.CARD, 24);
        logPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel panelTitle = new JLabel("Journal des événements");
        panelTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        panelTitle.setForeground(AppColors.TEXT_PRIMARY);

        JLabel panelSubtitle = new JLabel("Affichage enrichi avec catégories, couleurs et chronologie.");
        panelSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panelSubtitle.setForeground(AppColors.TEXT_SECONDARY);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(panelTitle);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(panelSubtitle);

        logPane = new JTextPane();
        logPane.setEditable(false);
        logPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        logPane.setBackground(new Color(248, 249, 252));
        logPane.setForeground(AppColors.TEXT_PRIMARY);
        logPane.setMargin(new Insets(14, 14, 14, 14));

        JScrollPane scrollPane = new JScrollPane(logPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.BORDER, 1, true));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel legendPanel = buildLegendPanel();

        logPanel.add(titlePanel, BorderLayout.NORTH);
        logPanel.add(scrollPane, BorderLayout.CENTER);
        logPanel.add(legendPanel, BorderLayout.SOUTH);

        return logPanel;
    }

    private JPanel buildLegendPanel() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        legend.setOpaque(false);

        legend.add(createLegendItem("INFO", new Color(25, 118, 210)));
        legend.add(createLegendItem("ATTENTE", new Color(245, 124, 0)));
        legend.add(createLegendItem("PASSAGE", new Color(46, 125, 50)));
        legend.add(createLegendItem("SORTIE", new Color(198, 40, 40)));
        legend.add(createLegendItem("DÉBLOCAGE", new Color(123, 31, 162)));
        legend.add(createLegendItem("FIN", new Color(69, 90, 100)));

        return legend;
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        item.setOpaque(false);

        JLabel dot = new JLabel("●");
        dot.setForeground(color);
        dot.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel label = new JLabel(text);
        label.setForeground(AppColors.TEXT_SECONDARY);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));

        item.add(dot);
        item.add(label);
        return item;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JLabel info = new JLabel("Application réalisée en Java Swing avec synchronisation des threads.");
        info.setFont(new Font("SansSerif", Font.PLAIN, 13));
        info.setForeground(AppColors.TEXT_SECONDARY);

        footer.add(info, BorderLayout.WEST);
        return footer;
    }

    private JPanel createFieldBlock(String labelText, JComponent field) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(AppColors.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        block.add(label);
        block.add(Box.createVerticalStrut(8));
        block.add(field);

        return block;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
        field.setPreferredSize(new Dimension(480, 44));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setMinimumSize(new Dimension(200, 44));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(AppColors.TEXT_PRIMARY);
        field.setCaretColor(AppColors.PRIMARY_DARK);
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 15));
        combo.setPreferredSize(new Dimension(480, 44));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        combo.setMinimumSize(new Dimension(200, 44));
        combo.setBackground(Color.WHITE);
        combo.setForeground(AppColors.TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createLineBorder(AppColors.BORDER, 1, true));

        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        renderer.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        combo.setRenderer(renderer);
    }

    private void updateFieldsVisibility() {
        String choix = (String) simulationCombo.getSelectedItem();
        boolean isParking = "Parking à n places".equals(choix);

        if (capaciteBlock != null) {
            capaciteBlock.setVisible(isParking);
        }
        if (portesBlock != null) {
            portesBlock.setVisible(isParking);
        }

        revalidate();
        repaint();
    }

    private void lancerSimulation() {
        int nbVoitures;

        try {
            nbVoitures = Integer.parseInt(nbVoituresField.getText().trim());
            if (nbVoitures <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Veuillez saisir un nombre de voitures valide supérieur à 0.",
                    "Saisie invalide",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String choix = (String) simulationCombo.getSelectedItem();
        logPane.setText("");

        new Thread(() -> {
            if ("Carrefour avec priorité à droite".equals(choix)) {
                lancerCarrefour(nbVoitures);
            } else if ("Tunnel à une voie".equals(choix)) {
                lancerTunnel(nbVoitures);
            } else {
                int capacite;
                int nbPortes;

                try {
                    capacite = Integer.parseInt(capaciteField.getText().trim());
                    nbPortes = Integer.parseInt(nbPortesField.getText().trim());

                    if (capacite <= 0 || nbPortes <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                            this,
                            "Veuillez saisir une capacité de parking et un nombre de portes valides.",
                            "Saisie invalide",
                            JOptionPane.WARNING_MESSAGE
                    ));
                    return;
                }

                lancerParking(nbVoitures, capacite, nbPortes);
            }
        }).start();
    }

    private void lancerCarrefour(int n) {
        refreshStatus("Simulation du carrefour en cours...");
        Utils.log("[INFO] Début de la simulation du carrefour avec priorité à droite.");

        Carrefour carrefour = new Carrefour();
        List<Thread> voitures = new ArrayList<>();
        Direction[] directions = {Direction.A, Direction.B, Direction.C, Direction.D};

        for (int i = 1; i <= n; i++) {
            Direction d = directions[random.nextInt(directions.length)];
            Thread v = new VoitureCarrefour(i, d, carrefour);
            voitures.add(v);
        }

        demarrerEtAttendre(voitures);

        Utils.log("[FIN] La simulation du carrefour est terminée.");
        refreshStatus("Simulation du carrefour terminée.");
    }

    private void lancerTunnel(int n) {
        refreshStatus("Simulation du tunnel en cours...");
        Utils.log("[INFO] Début de la simulation du tunnel à une voie.");

        Tunnel tunnel = new Tunnel();
        List<Thread> voitures = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            Direction sens = random.nextBoolean() ? Direction.GAUCHE_DROITE : Direction.DROITE_GAUCHE;
            Thread v = new VoitureTunnel(i, sens, tunnel);
            voitures.add(v);
        }

        demarrerEtAttendre(voitures);

        Utils.log("[FIN] La simulation du tunnel est terminée.");
        refreshStatus("Simulation du tunnel terminée.");
    }

    private void lancerParking(int n, int capacite, int nbPortes) {
        refreshStatus("Simulation du parking en cours...");
        Utils.log("[INFO] Début de la simulation du parking.");
        Utils.log("[INFO] Capacité du parking : " + capacite + " place(s).");
        Utils.log("[INFO] Nombre de portes : " + nbPortes + ".");

        Parking parking = new Parking(capacite, nbPortes);
        List<Thread> voitures = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            int porteEntree = 1 + random.nextInt(nbPortes);
            int porteSortie = 1 + random.nextInt(nbPortes);
            long duree = 1200 + random.nextInt(2500);

            Thread v = new VoitureParking(i, porteEntree, porteSortie, parking, duree);
            voitures.add(v);
        }

        demarrerEtAttendre(voitures);

        Utils.log("[FIN] La simulation du parking est terminée.");
        refreshStatus("Simulation du parking terminée.");
    }

    private void demarrerEtAttendre(List<Thread> threads) {
        for (Thread t : threads) {
            t.start();
            Utils.sleep(250);
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Utils.log("[ERREUR] Le thread principal a été interrompu.");
            }
        }
    }

    private void appendLog(String message) {
        if (logPane == null) return;

        StyledDocument doc = logPane.getStyledDocument();
        Style style = logPane.addStyle("style", null);

        StyleConstants.setFontFamily(style, "Monospaced");
        StyleConstants.setFontSize(style, 14);
        StyleConstants.setBold(style, false);

        Color color = AppColors.TEXT_PRIMARY;

        if (message.contains("[INFO]")) {
            color = new Color(25, 118, 210);
            StyleConstants.setBold(style, true);
        } else if (message.contains("[ATTENTE]")) {
            color = new Color(245, 124, 0);
        } else if (message.contains("[PASSAGE]")) {
            color = new Color(46, 125, 50);
            StyleConstants.setBold(style, true);
        } else if (message.contains("[SORTIE]")) {
            color = new Color(198, 40, 40);
        } else if (message.contains("[DÉBLOCAGE]")) {
            color = new Color(123, 31, 162);
            StyleConstants.setBold(style, true);
        } else if (message.contains("[ERREUR]")) {
            color = new Color(183, 28, 28);
            StyleConstants.setBold(style, true);
        } else if (message.contains("[FIN]")) {
            color = new Color(69, 90, 100);
            StyleConstants.setBold(style, true);
        }

        StyleConstants.setForeground(style, color);

        try {
            doc.insertString(doc.getLength(), message + "\n", style);
            logPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void refreshStatus(String text) {
        if (statusLabel != null) {
            SwingUtilities.invokeLater(() -> statusLabel.setText(text));
        }
    }
}