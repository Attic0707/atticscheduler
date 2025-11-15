package com.attic.scheduler.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.*;

import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CalendarView extends JPanel {

    private static final long serialVersionUID = 1814142941291706135L;

    private final JFrame frame;
    private final JPanel mainPanel;

    private JComboBox<String> monthBox;
    private JComboBox<Integer> yearBox;

    private JPanel calendarPanel;
    private JPanel upperPanel;
    private JPanel lowerPanel;

    private JTextArea tasksArea;
    private JLabel tasksTitle;

    private Calendar currentCal;
    private final Calendar today;

    // key = "yyyy-MM-dd"
    private final Map<String, List<String>> tasksByDate = new HashMap<>();

    public CalendarView() {
        setLayout(new BorderLayout());

        // Window + Panels
        frame = new JFrame("Attic Calendarinator");
        mainPanel = new JPanel(new BorderLayout());
        upperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        lowerPanel = new JPanel(new BorderLayout());

        // Calendar state
        currentCal = Calendar.getInstance();
        today = Calendar.getInstance();

        String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        monthBox = new JComboBox<>(months);
        monthBox.setSelectedIndex(currentCal.get(Calendar.MONTH));

        yearBox = new JComboBox<>();
        for (int y = 1980; y <= 2050; y++) {
            yearBox.addItem(y);
        }
        yearBox.setSelectedItem(currentCal.get(Calendar.YEAR));

        upperPanel.add(monthBox);
        upperPanel.add(yearBox);

        tasksTitle = new JLabel("Tasks for today:");
        tasksArea = new JTextArea(3, 30);
        tasksArea.setEditable(false);
        tasksArea.setEnabled(false);
        tasksArea.setLineWrap(true);
        tasksArea.setWrapStyleWord(true);
        tasksArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        lowerPanel.add(tasksTitle, BorderLayout.NORTH);
        lowerPanel.add(tasksArea, BorderLayout.CENTER);
        lowerPanel.setPreferredSize(new Dimension(mainPanel.getWidth(), 80));

        calendarPanel = new JPanel();
        calendarPanel.setBorder(BorderFactory.createTitledBorder("Calendar"));

        mainPanel.setOpaque(false);
        upperPanel.setOpaque(false);
        lowerPanel.setOpaque(false);
        calendarPanel.setOpaque(false);

        mainPanel.add(upperPanel, BorderLayout.NORTH);
        mainPanel.add(calendarPanel, BorderLayout.CENTER);
        mainPanel.add(lowerPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // INITIAL GRID
        buildCalendarGrid();
        showTasksForCalendarDate(today.get(Calendar.YEAR),
                                 today.get(Calendar.MONTH),
                                 today.get(Calendar.DAY_OF_MONTH));

        monthBox.addActionListener(e -> updateCalendarFromCombos());
        yearBox.addActionListener(e -> updateCalendarFromCombos());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        GradientPaint gp = new GradientPaint(
                0, 0, new Color(52, 143, 80),
                180, height, new Color(86, 180, 211)
        );
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
    }

    private void updateCalendarFromCombos() {
        int selectedMonth = monthBox.getSelectedIndex();
        int selectedYear = (int) yearBox.getSelectedItem();

        currentCal.set(Calendar.MONTH, selectedMonth);
        currentCal.set(Calendar.YEAR, selectedYear);

        buildCalendarGrid();
    }

    private void buildCalendarGrid() {
        calendarPanel.removeAll();

        calendarPanel.setLayout(new GridLayout(7, 7));

        String[] daysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String d : daysOfWeek) {
            JLabel lbl = new JLabel(d, JLabel.CENTER);
            lbl.setBorder(BorderFactory.createLineBorder(Color.BLUE));
            calendarPanel.add(lbl);
        }

        Calendar cal = (Calendar) currentCal.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int javaDow = cal.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        int firstDayColumn = (javaDow + 5) % 7;
        int day = 1;

        for (int cell = 0; cell < 42; cell++) {
            JLabel lbl;

            if (cell < firstDayColumn || day > daysInMonth) {
                // Empty cell
                lbl = new JLabel("", JLabel.CENTER);
            } else {
                int dom = day;
                lbl = new JLabel(String.valueOf(dom), JLabel.CENTER);
                lbl.setBorder(BorderFactory.createLineBorder(Color.BLUE));

                // Highlight today
                if (year == today.get(Calendar.YEAR)
                        && month == today.get(Calendar.MONTH)
                        && dom == today.get(Calendar.DAY_OF_MONTH)) {
                    lbl.setOpaque(true);
                    lbl.setBackground(new Color(255, 255, 153)); // light yellow
                }

                // Click listener → show tasks for that date
                lbl.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showTasksForCalendarDate(year, month, dom);
                    }
                });

                day++;
            }

            if (lbl.getBorder() == null) {
                lbl.setBorder(BorderFactory.createLineBorder(Color.BLUE));
            }

            calendarPanel.add(lbl);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    // Build "yyyy-MM-dd" key
    private String buildKey(int year, int monthZero, int day) {
        return String.format("%04d-%02d-%02d", year, monthZero + 1, day);
    }

    private void showTasksForCalendarDate(int year, int monthZero, int day) {
        String key = buildKey(year, monthZero, day);

        tasksTitle.setText("Tasks for " + key + ":");
        List<String> tasks = tasksByDate.get(key);

        if (tasks == null || tasks.isEmpty()) {
            tasksArea.setText("No tasks for this day.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String t : tasks) {
                sb.append("• ").append(t).append("\n");
            }
            tasksArea.setText(sb.toString());
        }
    }

    // Public API for MainFrame / Controller
    public void setTasksForDate(int year, int monthZero, int day, List<String> tasks) {
        tasksByDate.put(buildKey(year, monthZero, day), tasks);
    }

    public void initialize() {
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(this);
        frame.setVisible(true);
    }
}
