package app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainGui {
    private JFrame frame;
    private DefaultTableModel infoTableModel;
    private DefaultTableModel tableModel2;
    private List<JButton> panelButtonList;
    private List<JButton> buchungsButtonList;
    private JButton loginBtn;
    private static JTable buchungsTable;
    private int selectedRowIndex = -1;

    public MainGui() {
        this.frame = new JFrame();
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setLayout(new BorderLayout());
        renderFrame();

    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());

        infoTableModel = new DefaultTableModel() {
            // Override the isCellEditable method to make the cells non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(infoTableModel);

        // Füge die Spaltennamen hinzu
        infoTableModel.addColumn("Buchung");
        infoTableModel.addColumn("Name");
        infoTableModel.addColumn("Buchungszeitraum");
        infoTableModel.addColumn("Status");

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createBottomTablePanel() {
        JPanel buchungsTablePanel = new JPanel(new GridBagLayout());
        buchungsTablePanel.setPreferredSize(new Dimension(300, 150));
        tableModel2 = new DefaultTableModel() {
            // Override the isCellEditable method to make the cells non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        buchungsTable = new JTable(tableModel2);

        // Füge die Spaltennamen hinzu
        tableModel2.addColumn("Name");
        tableModel2.addColumn("Vorname");
        tableModel2.addColumn("Anreise");
        tableModel2.addColumn("Abreise");
        tableModel2.addColumn("Platznummer");
        tableModel2.addColumn("E-Mail");
        tableModel2.addColumn("Telefon");

        JScrollPane scrollPane2 = new JScrollPane(buchungsTable);

        // Konfigurieren Sie die GridBagConstraints für die Tabelle
        GridBagConstraints tableConstraints = new GridBagConstraints();
        tableConstraints.gridx = 2;
        tableConstraints.gridy = 0;
        tableConstraints.gridwidth = 1;
        tableConstraints.gridheight = 1;
        tableConstraints.fill = GridBagConstraints.BOTH; // Füllen Sie den verfügbaren Platz
        tableConstraints.weightx = 2.0;
        tableConstraints.weighty = 0.2;

        buchungsTablePanel.add(scrollPane2, tableConstraints);

        // Fügen Sie einen Seitenabstand hinzu (10 Pixel) - Ändern Sie dies nach Bedarf
        buchungsTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        return buchungsTablePanel;
    }

    private void renderFrame() {
        try {
            // Set Nimbus Look and Feel
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        JLabel imageLabel = new JLabel();
        ImageIcon imageIcon = new ImageIcon("./Platzplan.png");
        Image scaledImage = imageIcon.getImage().getScaledInstance(150, -1, Image.SCALE_SMOOTH);
        imageLabel.setIcon(imageIcon);

        // Fügen Sie einen Seitenabstand für das Bild hinzu (10 Pixel) - Ändern Sie dies nach Bedarf
        imageLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        contentPanel.add(imageLabel, BorderLayout.WEST);

        JPanel buchungsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20)); // Erhöhter vertikaler Abstand hier
        String[] buchungsButtonLabels = {
                "Neue Buchung", "Buchung bearbeiten",
                "Buchung löschen", "Info"
        };

        buchungsButtonList = new ArrayList<JButton>();

        for (String label : buchungsButtonLabels) {
            JButton button = createIdentifiedButton(label); // Button mit Identifier erstellen

            button.setPreferredSize(new Dimension(150, 40)); // Button-Größe anpassen
            button.setEnabled(false);
            buchungsButtonList.add(button);
            buchungsPanel.add(button);
        }
        contentPanel.add(buchungsPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.WEST); // Hier wird das Bild links von der Tabelle platziert
        contentPanel.add(createTablePanel(), BorderLayout.EAST); // Hier wird die erste Tabelle rechts vom Bild platziert
        mainPanel.add(createBottomTablePanel(), BorderLayout.SOUTH); // Hier wird die zweite Tabelle unterhalb der unteren Buttonreihe platziert

        this.frame.add(mainPanel);
        this.frame.setSize(1300, 700); // Größe auf 900x500 festlegen
        this.frame.setResizable(false);
        this.frame.setVisible(true);

        // Daten aus CSV-Datei in Tabelle zwei laden
        loadCSVDataToTable((DefaultTableModel) buchungsTable.getModel());
    }

    private JButton createIdentifiedButton(String label) {
        JButton button = new JButton(label);
        button.setPreferredSize(new Dimension(140, 40)); // Button-Größe anpassen
        button.setActionCommand(label); // Verwende das Label als Identifier
        button.addActionListener(buttonListener); // Füge den ActionListener hinzu
        return button;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));

        String[] buttonLabels = {
                "Buchungen", "Freie Plätze", "Neuer Platz",
                "Platz bearbeiten", "Platz löschen", "Platz Buchen",
                "Export/Import"
        };

        panelButtonList = new ArrayList<JButton>();

        for (String label : buttonLabels) {
            JButton button = createIdentifiedButton(label); // Button mit Identifier erstellen
            button.setEnabled(false);
            panelButtonList.add(button);
            buttonPanel.add(button);
        }

        loginBtn = createIdentifiedButton("Login"); // Button mit Identifier erstellen
        buttonPanel.add(loginBtn);

        return buttonPanel;
    }

    private ActionListener buttonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String identifier = e.getActionCommand(); // Hole den Identifier des geklickten Buttons

            // Hier kannst du basierend auf dem Identifier die entsprechende Aktion ausführen
            switch (identifier) {
                case "Neue Buchung":
                    new BuchungsGui(MainGui.this, null);
                    break;
                case "Buchung bearbeiten":
                    selectedRowIndex = buchungsTable.getSelectedRow();
                    if (selectedRowIndex >= 0) {
                        // Laden Sie die Daten aus der ausgewählten Zeile
                        String[] selectedData = loadSelectedBookingData(selectedRowIndex);

                        // Übergeben Sie die Daten an die BuchungsGui
                        if (selectedData != null) {
                            new BuchungsGui(MainGui.this, selectedData);
                        }
                    }
                    break;
                case "Login":
                    new Login(MainGui.this);
                    break;
                case "Neuer Platz": // Hinzugefügt: Aktion für "Neuer Platz" Button
                    new PlatzAnlegenGui();
                    break;
            }
        }
    };

    // Neue Methode zum Laden der Daten aus der ausgewählten Zeile der Tabelle
    private String[] loadSelectedBookingData(int rowIndex) {
        DefaultTableModel model = (DefaultTableModel) buchungsTable.getModel();
        if (rowIndex >= 0 && rowIndex < model.getRowCount()) {
            String[] data = new String[7];
            for (int i = 0; i < 7; i++) {
                data[i] = (String) model.getValueAt(rowIndex, i);
            }
            return data;
        }
        return null;
    }

    // Methode, um alle Buttons zu aktivieren
    private void enableAllButtons() {
        for (JButton button : buchungsButtonList) {
            button.setEnabled(true);
        }
    }

    private void disableButtonsExcept(int indexToExclude) {
        for (int i = 0; i < buchungsButtonList.size(); i++) {
            if (i != indexToExclude) {
                buchungsButtonList.get(i).setEnabled(false);
            }
        }
    }

    public void enable() {
        for (JButton button : panelButtonList) {
            button.setEnabled(true);
        }

        for (JButton button : buchungsButtonList) {
            button.setEnabled(true);
        }
    }

    private void saveEditedData() {
        DefaultTableModel model = (DefaultTableModel) buchungsTable.getModel();
        if (selectedRowIndex >= 0 && selectedRowIndex < model.getRowCount()) {
            // Nehmen Sie die Änderungen an den Daten vor
            String name = (String) model.getValueAt(selectedRowIndex, 0);
            String vorname = (String) model.getValueAt(selectedRowIndex, 1);
            String anreise = (String) model.getValueAt(selectedRowIndex, 2);
            String abreise = (String) model.getValueAt(selectedRowIndex, 3);
            String platznummer = (String) model.getValueAt(selectedRowIndex, 4);
            String email = (String) model.getValueAt(selectedRowIndex, 5);
            String telefon = (String) model.getValueAt(selectedRowIndex, 6);

            // Hier können Sie den Code zum Speichern der Daten in Ihrer CSV-Datei hinzufügen
            // Verwenden Sie die oben erfassten Daten
            // ...

            // Nach dem Speichern können Sie eine Erfolgsmeldung anzeigen
            JOptionPane.showMessageDialog(frame, "Änderungen erfolgreich gespeichert.", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static void loadCSVDataToTable(DefaultTableModel tableModel) {
        // Pfad zur CSV-Datei ändern, falls erforderlich
        String csvFilePath = "./BuchungsCSV.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Überspringe die erste Zeile
                }
                String[] data = line.split(",");

                // Annahme: Die CSV-Datei hat die gewünschte Reihenfolge der Spalten
                if (data.length >= 7) { // Überprüfen Sie, ob genügend Spalten vorhanden sind
                    String[] rowData = new String[7]; // Erstellen Sie ein Array für die gewünschten Spalten
                    rowData[0] = data[0]; // Name
                    rowData[1] = data[1]; // Vorname
                    rowData[2] = data[2]; // Anreise
                    rowData[3] = data[3]; // Abreise
                    rowData[4] = data[4]; // Platznummer
                    rowData[5] = data[5]; // E-Mail
                    rowData[6] = data[6]; // Telefon

                    tableModel.addRow(rowData); // Fügen Sie die Daten in die Tabelle ein
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void updateTable() {
        // Löschen Sie alle Zeilen aus der Tabelle
        DefaultTableModel model = (DefaultTableModel) buchungsTable.getModel();
        model.setRowCount(0);

        // Laden Sie die Daten erneut aus Ihrer CSV-Datei
        loadCSVDataToTable(model);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGui());
    }
}
