import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * GradeTrackerGUI.java
 * The graphical (Swing + FlatLaf) interface for the Student Grade Tracker.
 * All data logic still lives in GradeTracker/Student — this class
 * is only responsible for drawing the window and reacting to clicks.
 */
public class GradeTrackerGUI extends JFrame {

    // ---------- Palette (soft indigo accent on a neutral canvas) ----------
    private static final Color BG = new Color(0xF3, 0xF4, 0xF8);
    private static final Color CARD = Color.WHITE;
    private static final Color BORDER = new Color(0xE4, 0xE6, 0xEE);
    private static final Color PRIMARY = new Color(0x4F, 0x46, 0xE5);
    private static final Color PRIMARY_SOFT = new Color(0xEE, 0xEC, 0xFD);
    private static final Color DANGER = new Color(0xE1, 0x1D, 0x48);
    private static final Color MUTED = new Color(0x6B, 0x72, 0x80);
    private static final Color TEXT = new Color(0x11, 0x18, 0x27);
    private static final Font FONT_BASE = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TITLE = new Font("Segoe UI Semibold", Font.PLAIN, 26);
    private static final Font FONT_HEADING = new Font("Segoe UI Semibold", Font.PLAIN, 16);

    private final GradeTracker tracker = new GradeTracker();

    private JTextField nameField, rollField, marksField, searchField;
    private JButton submitBtn, cancelEditBtn;
    private JLabel formTitleLabel, formErrorLabel;
    private JLabel totalValue, avgValue, highValue, lowValue;
    private DefaultTableModel tableModel;
    private JTable table;

    private Integer editingRoll = null; // null = adding, non-null = editing that roll number

    public GradeTrackerGUI() {
        super("Student Grade Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 760);
        setMinimumSize(new Dimension(760, 560));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(28, 32, 28, 32));

        root.add(buildTitle());
        root.add(Box.createVerticalStrut(20));
        root.add(buildFormCard());
        root.add(Box.createVerticalStrut(18));
        root.add(buildSearchCard());
        root.add(Box.createVerticalStrut(18));
        root.add(buildTableCard());
        root.add(Box.createVerticalStrut(18));
        root.add(buildStatsCard());

        JScrollPane scroll = new JScrollPane(root);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        loadSampleData();
    }

    // ---------- UI builders ----------

    private JComponent buildTitle() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Student Grade Tracker");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Add, edit, search and analyze student marks");
        subtitle.setFont(FONT_BASE);
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subtitle);
        return panel;
    }

    private JPanel card(String heading) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 18");
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(20, 22, 20, 22)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (heading != null) {
            JLabel h = new JLabel(heading);
            h.setFont(FONT_HEADING);
            h.setForeground(TEXT);
            h.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(h);
            card.add(Box.createVerticalStrut(14));
        }
        return card;
    }

    private JComponent buildFormCard() {
        JPanel card = card("Add Student");
        formTitleLabel = (JLabel) card.getComponent(0);

        JPanel fields = new JPanel(new GridLayout(1, 3, 14, 0));
        fields.setBackground(CARD);
        fields.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameField = styledTextField("e.g. Priya Singh");
        rollField = styledTextField("e.g. 101");
        marksField = styledTextField("e.g. 85");

        fields.add(labeledField("Name", nameField));
        fields.add(labeledField("Roll Number", rollField));
        fields.add(labeledField("Marks (0-100)", marksField));

        card.add(fields);
        card.add(Box.createVerticalStrut(16));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setBackground(CARD);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);

        submitBtn = flatButton("Add Student", PRIMARY, Color.WHITE, false);
        submitBtn.addActionListener(e -> onSubmit());

        cancelEditBtn = flatButton("Cancel", BORDER, TEXT, false);
        cancelEditBtn.setVisible(false);
        cancelEditBtn.addActionListener(e -> cancelEdit());

        JButton sampleBtn = flatButton("Load Sample Data", BORDER, TEXT, false);
        sampleBtn.addActionListener(e -> loadSampleData());

        buttons.add(submitBtn);
        buttons.add(cancelEditBtn);
        buttons.add(sampleBtn);
        card.add(buttons);

        formErrorLabel = new JLabel(" ");
        formErrorLabel.setFont(FONT_BASE);
        formErrorLabel.setForeground(DANGER);
        formErrorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(Box.createVerticalStrut(10));
        card.add(formErrorLabel);

        return card;
    }

    private JComponent buildSearchCard() {
        JPanel card = card("Search Student");

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(CARD);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        searchField = styledTextField("Search by name or roll number");
        searchField.addActionListener(e -> doSearch());

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btns.setBackground(CARD);
        JButton searchBtn = flatButton("Search", PRIMARY, Color.WHITE, false);
        searchBtn.addActionListener(e -> doSearch());
        JButton clearBtn = flatButton("Clear", BORDER, TEXT, false);
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            filterTable(null);
        });
        btns.add(searchBtn);
        btns.add(clearBtn);

        row.add(searchField, BorderLayout.CENTER);
        row.add(btns, BorderLayout.EAST);

        card.add(row);
        return card;
    }

    private JComponent buildTableCard() {
        JPanel card = card("All Students");
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));

        String[] columns = {"Roll", "Name", "Marks", "Grade", ""};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFont(FONT_BASE);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.putClientProperty(FlatClientProperties.STYLE,
                "selectionBackground: " + colorHex(PRIMARY_SOFT) + "; selectionForeground: " + colorHex(TEXT));
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setForeground(MUTED);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "separatorColor: " + colorHex(BORDER));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(stripedRenderer(SwingConstants.CENTER));
        table.getColumnModel().getColumn(1).setCellRenderer(stripedRenderer(SwingConstants.LEFT));
        table.getColumnModel().getColumn(2).setCellRenderer(stripedRenderer(SwingConstants.CENTER));
        table.getColumnModel().getColumn(3).setCellRenderer(new GradeCellRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new ActionsCellRenderer());
        table.getColumnModel().getColumn(4).setPreferredWidth(160);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());
                if (col != 4 || row < 0) return;

                int roll = (int) table.getValueAt(row, col);
                Rectangle cellRect = table.getCellRect(row, col, false);
                int relativeX = e.getX() - cellRect.x;

                if (relativeX < cellRect.width / 2) {
                    startEdit(roll);
                } else {
                    removeStudent(roll);
                }
            }
        });
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(BORDER, 1, true));
        sp.getViewport().setBackground(CARD);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sp);
        return card;
    }

    private DefaultTableCellRenderer stripedRenderer(int alignment) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                             boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(alignment);
                setFont(FONT_BASE);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD : new Color(0xFA, 0xFA, 0xFC));
                    c.setForeground(TEXT);
                }
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return c;
            }
        };
    }

    private JComponent buildStatsCard() {
        JPanel card = card("Statistics");

        JPanel grid = new JPanel(new GridLayout(1, 4, 14, 0));
        grid.setBackground(CARD);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        totalValue = new JLabel("0");
        avgValue = new JLabel("0.00");
        highValue = new JLabel("-");
        lowValue = new JLabel("-");

        grid.add(statBox("Total Students", totalValue));
        grid.add(statBox("Average Marks", avgValue));
        grid.add(statBox("Highest Marks", highValue));
        grid.add(statBox("Lowest Marks", lowValue));

        card.add(grid);
        return card;
    }

    private JComponent statBox(String label, JLabel valueLabel) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(PRIMARY_SOFT);
        box.putClientProperty(FlatClientProperties.STYLE, "arc: 14");
        box.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel l = new JLabel(label);
        l.setFont(FONT_BASE);
        l.setForeground(MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 17));
        valueLabel.setForeground(PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        box.add(l);
        box.add(Box.createVerticalStrut(6));
        box.add(valueLabel);
        return box;
    }

    // ---------- small styled component helpers ----------

    private JComponent labeledField(String label, JTextField field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD);

        JLabel l = new JLabel(label);
        l.setFont(FONT_BASE);
        l.setForeground(MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(l);
        panel.add(Box.createVerticalStrut(6));
        panel.add(field);
        return panel;
    }

    private JTextField styledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(FONT_BASE);
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 1.5; focusWidth: 2");
        field.setBorder(new EmptyBorder(8, 10, 8, 10));
        return field;
    }

    private JButton flatButton(String text, Color bg, Color fg, boolean outline) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        return btn;
    }

    private static String colorHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    // ---------- behavior ----------

    private void onSubmit() {
        formErrorLabel.setText(" ");

        String name = nameField.getText().trim();
        String rollStr = rollField.getText().trim();
        String marksStr = marksField.getText().trim();

        if (name.isEmpty()) {
            showError("Name cannot be empty.");
            return;
        }

        int roll;
        try {
            roll = Integer.parseInt(rollStr);
        } catch (NumberFormatException e) {
            showError("Roll number must be a whole number.");
            return;
        }

        boolean duplicate = tracker.getStudents().stream()
                .anyMatch(s -> s.getRollNumber() == roll && (editingRoll == null || roll != editingRoll));
        if (duplicate) {
            showError("A student with this roll number already exists.");
            return;
        }

        double marks;
        try {
            marks = Double.parseDouble(marksStr);
        } catch (NumberFormatException e) {
            showError("Marks must be a number.");
            return;
        }
        if (marks < 0 || marks > 100) {
            showError("Marks must be between 0 and 100.");
            return;
        }

        Student student = new Student(name, roll, marks);
        if (editingRoll != null) {
            tracker.updateStudent(editingRoll, student);
            cancelEdit();
        } else {
            tracker.addStudent(student);
            clearForm();
        }
        refreshTable();
        refreshStats();
    }

    private void showError(String msg) {
        formErrorLabel.setText(msg);
    }

    private void clearForm() {
        nameField.setText("");
        rollField.setText("");
        marksField.setText("");
        formErrorLabel.setText(" ");
    }

    private void startEdit(int roll) {
        Student s = tracker.searchByRollNumber(roll);
        if (s == null) return;
        editingRoll = roll;
        nameField.setText(s.getName());
        rollField.setText(String.valueOf(s.getRollNumber()));
        marksField.setText(String.valueOf(s.getMarks()));
        formTitleLabel.setText("Edit Student");
        submitBtn.setText("Update Student");
        cancelEditBtn.setVisible(true);
        formErrorLabel.setText(" ");
    }

    private void cancelEdit() {
        editingRoll = null;
        clearForm();
        formTitleLabel.setText("Add Student");
        submitBtn.setText("Add Student");
        cancelEditBtn.setVisible(false);
    }

    private void removeStudent(int roll) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove this student from the list?", "Confirm Remove",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            tracker.removeStudent(roll);
            if (editingRoll != null && editingRoll == roll) {
                cancelEdit();
            }
            refreshTable();
            refreshStats();
        }
    }

    private void doSearch() {
        String query = searchField.getText().trim();
        filterTable(query.isEmpty() ? null : query);
    }

    private void filterTable(String query) {
        if (query == null) {
            refreshTable();
            return;
        }
        String lower = query.toLowerCase();
        List<Student> matches = tracker.getStudents().stream()
                .filter(s -> s.getName().toLowerCase().contains(lower)
                        || String.valueOf(s.getRollNumber()).equals(query))
                .toList();
        populateTable(matches);
        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No matching student found.",
                    "Search", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshTable() {
        populateTable(tracker.getStudents());
    }

    private void populateTable(List<Student> list) {
        tableModel.setRowCount(0);
        for (Student s : list) {
            tableModel.addRow(new Object[]{
                    s.getRollNumber(), s.getName(),
                    String.format("%.2f", s.getMarks()), s.getGrade(),
                    s.getRollNumber() // used by the actions column to find the row's student
            });
        }
    }

    private void refreshStats() {
        if (tracker.isEmpty()) {
            totalValue.setText("0");
            avgValue.setText("-");
            highValue.setText("-");
            lowValue.setText("-");
            return;
        }
        totalValue.setText(String.valueOf(tracker.getTotalStudents()));
        avgValue.setText(String.format("%.2f", tracker.getAverageMarks()));
        Student high = tracker.getHighestScorer();
        Student low = tracker.getLowestScorer();
        highValue.setText(String.format("%.2f (%s)", high.getMarks(), high.getName()));
        lowValue.setText(String.format("%.2f (%s)", low.getMarks(), low.getName()));
    }

    private void loadSampleData() {
        Object[][] sample = {
                {"Aarav Sharma", 101, 95.0}, {"Diya Mehta", 102, 91.0},
                {"Kabir Singh", 103, 88.0}, {"Ishita Rao", 104, 84.0},
                {"Vihaan Kapoor", 105, 80.0}, {"Ananya Gupta", 106, 79.0},
                {"Reyansh Patel", 107, 75.0}, {"Saanvi Nair", 108, 71.0},
                {"Arjun Verma", 109, 70.0}, {"Myra Joshi", 110, 68.0},
                {"Vivaan Malhotra", 111, 64.0}, {"Aadhya Iyer", 112, 60.0},
                {"Kian Chopra", 113, 59.0}, {"Navya Bansal", 114, 55.0},
                {"Aryan Desai", 115, 51.0}, {"Riya Kulkarni", 116, 50.0},
                {"Ayaan Khanna", 117, 47.0}, {"Zara Ahmed", 118, 42.0},
                {"Dev Choudhary", 119, 38.0}, {"Pari Agarwal", 120, 30.0},
                {"Rohan Bhatt", 121, 93.0}, {"Anika Menon", 122, 87.0},
                {"Yuvraj Saxena", 123, 73.0}, {"Ishaan Trivedi", 124, 62.0},
                {"Sara Kohli", 125, 45.0}
        };
        for (Object[] row : sample) {
            int roll = (int) row[1];
            if (tracker.searchByRollNumber(roll) == null) {
                tracker.addStudent(new Student((String) row[0], roll, (double) row[2]));
            }
        }
        refreshTable();
        refreshStats();
    }

    // ---------- custom table cell rendering ----------

    /** Colors the Grade column like a pill badge, based on the letter grade. */
    private class GradeCellRenderer implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            String grade = String.valueOf(value);
            Color[] colors = switch (grade) {
                case "A+", "A" -> new Color[]{new Color(0xDC, 0xFC, 0xE7), new Color(0x15, 0x80, 0x3D)};
                case "B" -> new Color[]{new Color(0xDB, 0xEA, 0xFE), new Color(0x1D, 0x4E, 0xD8)};
                case "C" -> new Color[]{new Color(0xFE, 0xF3, 0xC7), new Color(0xA1, 0x62, 0x07)};
                case "D" -> new Color[]{new Color(0xFF, 0xE4, 0xD5), new Color(0xC2, 0x41, 0x0C)};
                default -> new Color[]{new Color(0xFE, 0xE2, 0xE2), new Color(0xB9, 0x1C, 0x1C)};
            };
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            wrapper.setOpaque(true);
            wrapper.setBackground(row % 2 == 0 ? CARD : new Color(0xFA, 0xFA, 0xFC));

            JLabel badge = new JLabel(grade);
            badge.setOpaque(true);
            badge.setBackground(colors[0]);
            badge.setForeground(colors[1]);
            badge.setFont(FONT_BOLD);
            badge.setHorizontalAlignment(SwingConstants.CENTER);
            badge.setBorder(new EmptyBorder(3, 12, 3, 12));
            badge.putClientProperty(FlatClientProperties.STYLE, "arc: 999");

            wrapper.add(badge);
            return wrapper;
        }
    }

    /**
     * Draws Edit / Remove buttons inside the last table column.
     * These are purely visual — actual clicks are handled by the
     * MouseListener on the table (see buildTableCard), since a
     * JTable cell editor would otherwise require two clicks to
     * register a button press.
     */
    private class ActionsCellRenderer implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            return buildActionsPanel(row);
        }
    }

    private JPanel buildActionsPanel(int row) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        panel.setOpaque(true);
        panel.setBackground(row % 2 == 0 ? CARD : new Color(0xFA, 0xFA, 0xFC));

        JButton editBtn = new JButton("Edit");
        editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        editBtn.setForeground(PRIMARY);
        editBtn.setBackground(PRIMARY_SOFT);
        editBtn.setBorder(new EmptyBorder(4, 10, 4, 10));
        editBtn.setFocusPainted(false);
        editBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 8");

        JButton removeBtn = new JButton("Remove");
        removeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        removeBtn.setForeground(DANGER);
        removeBtn.setBackground(new Color(0xFE, 0xE2, 0xE2));
        removeBtn.setBorder(new EmptyBorder(4, 10, 4, 10));
        removeBtn.setFocusPainted(false);
        removeBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 8");

        panel.add(editBtn);
        panel.add(removeBtn);
        return panel;
    }
}
