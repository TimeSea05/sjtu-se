import javax.swing.*;
import java.awt.*;

public class CuckooGUIApp extends JFrame {
    private CuckooHashTable table;
    private HashTableEntry[][] entries;
    public CuckooGUIApp(CuckooHashTable table) {
        this.table = table;
        entries = table.GetInnerTables();

        setTitle("Cuckoo Hash Table");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        // layout of the GUI App
        /*
          1: label(键): ____(input area); label(值): ____(input area); button(插入); button(删除);
          2: array panel of table1; array panel of table2
         */
        GridBagConstraints constraints = new GridBagConstraints();
        int gap = 10;
        constraints.insets = new Insets(gap, gap, gap, gap);

        setGridBagConstraints(constraints, 0, 0);
        add(new JLabel("键："), constraints);

        setGridBagConstraints(constraints, 1, 0);
        JTextField keyInputArea = new JTextField(10);
        add(keyInputArea, constraints);

        setGridBagConstraints(constraints, 2, 0);
        add(new JLabel("值:  "), constraints);

        setGridBagConstraints(constraints, 3, 0);
        JTextField valueInputArea = new JTextField(10);
        add(valueInputArea, constraints);

        setGridBagConstraints(constraints, 4, 0);
        JButton insertButton = new JButton("插入");
        add(insertButton, constraints);

        setGridBagConstraints(constraints, 5, 0);
        JButton deleteButton = new JButton("删除");
        add(deleteButton, constraints);

        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(50, 50, 50, 50);
        setGridBagConstraints(constraints, 2, 1);
        JPanel array1Panel = createArrayPanel(entries[0]);
        add(array1Panel, constraints);

        JPanel array2Panel = createArrayPanel(entries[1]);
        setGridBagConstraints(constraints, 3, 1);
        add(array2Panel, constraints);

        // add an `ActionListener` to the button, which listens for button click events
        // and then call the desired function
        insertButton.addActionListener(e -> {
            String keyText = keyInputArea.getText();
            String valueText = valueInputArea.getText();
            if (keyText.equals("") || valueText.equals("")) {
                return;
            }
            int key = Integer.parseInt(keyText);
            int value = Integer.parseInt(valueText);
            table.Set(key, value);
            entries = table.GetInnerTables();

            updateArrayPanels(array1Panel, array2Panel);
            keyInputArea.setText("");
            valueInputArea.setText("");
        });

        deleteButton.addActionListener(e -> {
            String keyText = keyInputArea.getText();
            if (keyText.equals("")) {
                return;
            }
            int key = Integer.parseInt(keyText);
            table.Delete(key);
            entries = table.GetInnerTables();

            updateArrayPanels(array1Panel, array2Panel);
            keyInputArea.setText("");
            valueInputArea.setText("");
        });
    }

    private JPanel createArrayPanel(HashTableEntry[] entries) {
        JPanel panel = new JPanel(new GridLayout(entries.length, 1));
        int cellWidth = 40, cellHeight = 40;

        for (HashTableEntry entry : entries) {
            String labelStr = (entry == null) ? null : String.valueOf(entry.key);
            JLabel cell = new JLabel(labelStr, SwingConstants.CENTER);
            cell.setPreferredSize(new Dimension(cellWidth, cellHeight));
            cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            panel.add(cell);
        }
        return panel;
    }

    private void updateArrayPanels(JPanel array1Panel, JPanel array2Panel) {
        for (int i = 0; i < CuckooHashTable.INITIAL_CAP; i++) {
            JLabel cell1 = (JLabel)array1Panel.getComponent(i);
            JLabel cell2 = (JLabel)array2Panel.getComponent(i);
            String newText1 = (entries[0][i] == null) ? null : String.valueOf(entries[0][i].key);
            String newText2 = (entries[1][i] == null) ? null : String.valueOf(entries[1][i].key);
            cell1.setText(newText1);
            cell2.setText(newText2);
        }
    }
    private static void setGridBagConstraints(GridBagConstraints constraints, int gridx, int gridy) {
        constraints.gridx = gridx;
        constraints.gridy = gridy;
    }
}
