import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class SearchSystemGUIApp extends JFrame {
    private TextSearchSystem system = new TextSearchSystem();
    public SearchSystemGUIApp() {
        setTitle("全文搜索系统");
        setSize(800, 600);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        initComponents();
    }

    private void initComponents() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        setGridBagConstraints(constraints, 0, 0);
        add(new JLabel("请选择要搜索的文本文件"), constraints);

        setGridBagConstraints(constraints, 1, 0);
        JButton browseButton = new JButton("浏览");
        add(browseButton, constraints);

        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(SearchSystemGUIApp.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String selectedFilePath = fileChooser.getSelectedFile().getPath();
                System.out.println("Selected file: " + selectedFilePath);
                system.readTextFile(selectedFilePath);

                changeFrameLayout();
            }
            changeFrameLayout();
        });
    }

    private void changeFrameLayout() {
        getContentPane().removeAll();   // Remove all components from the frame

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        setGridBagConstraints(constraints, 0, 0);
        add(new JLabel("请输入搜索项："), constraints);

        setGridBagConstraints(constraints, 1, 0);
        JTextField booleanExprInputField = new JTextField(30);
        add(booleanExprInputField, constraints);

        setGridBagConstraints(constraints, 2, 0);
        JButton searchButton = new JButton("确定");
        add(searchButton, constraints);

        setGridBagConstraints(constraints, 0, 1);
        add(new JLabel("搜索结果："), constraints);

        setGridBagConstraints(constraints, 1, 2);
        JTextArea searchResultArea = new JTextArea();
        searchResultArea.setPreferredSize(new Dimension(400, 200));
        searchResultArea.setBorder(new LineBorder(Color.BLACK));
        add(searchResultArea, constraints);

        searchButton.addActionListener(e -> {
            String booleanExpr = booleanExprInputField.getText();
            if (booleanExpr.length() == 0) {
                return;
            }

            searchResultArea.setText(system.fullTextSearchWithStringResult(booleanExpr));
        });

        revalidate();           // Revalidate the frame to update its layout
        repaint();              // Repaint the frame to reflect the changes
    }
    private static void setGridBagConstraints(GridBagConstraints constraints, int x, int y) {
        constraints.gridx = x;
        constraints.gridy = y;
    }
}
