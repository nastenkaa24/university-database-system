import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GUI extends JFrame {

    private static String DB_URL="jdbc:postgresql://localhost:5432/tema3_vara?currentSchema=training";
    private static String DB_USER= "postgres";
    private static String DB_PASS= "postgres";

    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel statusLabel = new JLabel("Ready.");
    private final JTextField paramField = new JTextField();

    public GUI() {
        super("Tema 3");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildMain(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JComponent buildMain() {
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                buildLeftPanel(),
                buildRightPanel()
        );
        split.setDividerLocation(380);
        split.setResizeWeight(0);
        split.setBorder(null);
        return split;
    }

    private JComponent buildLeftPanel() {
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(14, 14, 14, 14));
        left.setPreferredSize(new Dimension(380, 600));

        JLabel section = new JLabel("Queries");
        section.setFont(section.getFont().deriveFont(Font.BOLD, 14f));

        JLabel paramLbl = new JLabel("Parametrii (optional):");
        paramLbl.setBorder(new EmptyBorder(10, 0, 6, 0));

        paramField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        paramField.setToolTipText("Ex: 2 | 5 | %@student.utcluj.ro");

        JLabel hint = new JLabel("Used for 3a / 3b / 5b. Defaults: 2 / 5 / %@student.utcluj.ro");
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 11f));
        hint.setForeground(Color.DARK_GRAY);

        JButton b3a = new JButton("3a) Students by year");
        JButton b3b = new JButton("3b) Courses min credits");
        JButton b4a = new JButton("4a) JOIN credits > 4");
        JButton b4b = new JButton("4b) LEFT JOIN courses+students");
        JButton b5a = new JButton("5a) Students without credits > 6");
        JButton b5b = new JButton("5b) Courses with email domain");
        JButton b6a = new JButton("6a) Count courses per semester");
        JButton b6b = new JButton("6b) Total credits per course (passed)");
        JButton b7b = new JButton("procedura 1 - Total cursuri student");
        JButton b8b=new JButton("procesura 2-Afisare studenti inscrisi curs");
        JButton clear = new JButton("Clear table");

        JButton[] buttons = {b3a, b3b, b4a, b4b, b5a, b5b, b6a, b6b, clear};
        for (JButton b : buttons) {
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        }

        b3a.addActionListener(e ->
                executeQuery(Queries.studentsByYear(parseIntOrDefault(paramField.getText(), 2)))
        );

        b3b.addActionListener(e ->
                executeQuery(Queries.coursesByMinCredits(parseIntOrDefault(paramField.getText(), 5)))
        );

        b4a.addActionListener(e -> executeQuery(Queries.joinStudentsCoursesCreditsGt4));
        b4b.addActionListener(e -> executeQuery(Queries.leftJoinCoursesStudents));
        b5a.addActionListener(e -> executeQuery(Queries.studentsWithoutCourseCreditsGt6));

        b5b.addActionListener(e -> {
            String like = paramField.getText().trim();
            if (like.isEmpty()) like = "%@student.utcluj.ro";
            executeQuery(Queries.coursesWithStudentEmailDomainLike(like));
        });

        b6a.addActionListener(e -> executeQuery(Queries.coursesCountPerSemester));
        b6b.addActionListener(e -> executeQuery(Queries.totalCreditsPerCoursePassed));

        b7b.addActionListener(e -> {
            int studentId = parseIntOrDefault(paramField.getText(), 1);
            executeCountCoursesForStudent(studentId);
        });


        b8b.addActionListener(e ->
                executeQuery(
                        Queries.studentsForCourse(
                                parseIntOrDefault(paramField.getText(), 1)
                        )
                )
        );

        clear.addActionListener(e -> {
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);
            statusLabel.setText("Cleared.");
        });

        left.add(section);
        left.add(paramLbl);
        left.add(paramField);
        left.add(Box.createVerticalStrut(4));
        left.add(hint);
        left.add(Box.createVerticalStrut(14));

        left.add(b3a);
        left.add(Box.createVerticalStrut(8));
        left.add(b3b);
        left.add(Box.createVerticalStrut(12));
        left.add(new JSeparator());
        left.add(Box.createVerticalStrut(12));

        left.add(b4a);
        left.add(Box.createVerticalStrut(8));
        left.add(b4b);
        left.add(Box.createVerticalStrut(12));
        left.add(new JSeparator());
        left.add(Box.createVerticalStrut(12));

        left.add(b5a);
        left.add(Box.createVerticalStrut(8));
        left.add(b5b);
        left.add(Box.createVerticalStrut(12));
        left.add(new JSeparator());
        left.add(Box.createVerticalStrut(12));

        left.add(b6a);
        left.add(Box.createVerticalStrut(8));
        left.add(b6b);
        left.add(Box.createVerticalStrut(14));

        left.add(b7b);
        left.add(Box.createVerticalStrut(8));

        left.add(b8b);
        left.add(Box.createVerticalStrut(8));


        left.add(clear);
        left.add(Box.createVerticalGlue());

        return left;
    }

    private JComponent buildRightPanel() {
        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Tabel Tema 3");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));


        header.add(title);
        header.add(Box.createVerticalStrut(4));

        JLabel resultsTitle = new JLabel("Rezultate");
        resultsTitle.setFont(resultsTitle.getFont().deriveFont(Font.BOLD, 14f));
        resultsTitle.setBorder(new EmptyBorder(10, 0, 8, 0));

        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(table);

        JPanel center = new JPanel(new BorderLayout());
        center.add(resultsTitle, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        right.add(header, BorderLayout.NORTH);
        right.add(center, BorderLayout.CENTER);

        return right;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(new EmptyBorder(8, 14, 8, 14));
        footer.add(statusLabel, BorderLayout.WEST);
        return footer;
    }

    private int parseIntOrDefault(String text, int def) {
        if (text == null || text.trim().isEmpty()) return def;
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception e) {
            statusLabel.setText("Invalid number, using default=" + def);
            return def;
        }
    }

    private void executeQuery(String sql) {
        statusLabel.setText("Running...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            for (int i = 1; i <= colCount; i++) {
                tableModel.addColumn(meta.getColumnName(i));
            }

            int rows = 0;
            while (rs.next()) {
                Object[] row = new Object[colCount];
                for (int i = 1; i <= colCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(row);
                rows++;
            }

            statusLabel.setText("Done. Rows: " + rows);

        } catch (SQLException ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }

    }
    private void executeCountCoursesForStudent(int studentId) {
        String sql = "SELECT training.count_courses_for_student(?) AS total";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                tableModel.setRowCount(0);
                tableModel.setColumnCount(0);
                tableModel.addColumn("total");

                if (rs.next()) {
                    tableModel.addRow(new Object[]{rs.getInt("total")});
                }
            }

            statusLabel.setText("Done.");
        } catch (SQLException ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }


}