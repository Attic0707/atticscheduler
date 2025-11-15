package com.attic.scheduler.view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;

public class DatePickerDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private LocalDate selectedDate;
    private final JComboBox<String> monthBox;
    private final JComboBox<Integer> yearBox;
    private final JPanel daysPanel;

    private YearMonth currentMonth;

    private DatePickerDialog(Window parent, LocalDate initialDate) {
        super(parent, "Select Date", ModalityType.APPLICATION_MODAL);

        if (initialDate == null) {
            initialDate = LocalDate.now();
        }
        this.currentMonth = YearMonth.of(initialDate.getYear(), initialDate.getMonthValue());

        setLayout(new BorderLayout());
        setSize(300, 250);
        setLocationRelativeTo(parent);
        setResizable(false);

        // Top: month/year
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        monthBox = new JComboBox<>(months);
        monthBox.setSelectedIndex(currentMonth.getMonthValue() - 1);

        yearBox = new JComboBox<>();
        for (int y = initialDate.getYear() - 10; y <= initialDate.getYear() + 10; y++) {
            yearBox.addItem(y);
        }
        yearBox.setSelectedItem(initialDate.getYear());

        topPanel.add(monthBox);
        topPanel.add(yearBox);

        add(topPanel, BorderLayout.NORTH);

        // Center: days grid
        daysPanel = new JPanel(new GridLayout(7, 7)); // 1 row headers + 6 weeks
        add(daysPanel, BorderLayout.CENTER);

        // Bottom: cancel button
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> {
            selectedDate = null;
            dispose();
        });
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(cancel);
        add(bottomPanel, BorderLayout.SOUTH);

        // listeners
        monthBox.addActionListener(e -> updateMonthFromCombo());
        yearBox.addActionListener(e -> updateMonthFromCombo());

        buildDaysGrid();

        pack();
    }

    private void updateMonthFromCombo() {
        int monthIndex = monthBox.getSelectedIndex(); // 0-11
        int year = (int) yearBox.getSelectedItem();
        currentMonth = YearMonth.of(year, monthIndex + 1);
        buildDaysGrid();
    }

    private void buildDaysGrid() {
        daysPanel.removeAll();

        // headers (Mon-Sun)
        String[] headers = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String h : headers) {
            JLabel lbl = new JLabel(h, SwingConstants.CENTER);
            lbl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            daysPanel.add(lbl);
        }

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1=Mon .. 7=Sun
        int daysInMonth = currentMonth.lengthOfMonth();

        int day = 1;
        for (int cell = 0; cell < 42; cell++) {
            JButton btn;

            if (cell < firstDayOfWeek - 1 || day > daysInMonth) {
                btn = new JButton("");
                btn.setEnabled(false);
            } else {
                int dayOfMonth = day;
                btn = new JButton(String.valueOf(dayOfMonth));
                btn.addActionListener(e -> {
                    selectedDate = currentMonth.atDay(dayOfMonth);
                    dispose();
                });
                day++;
            }

            btn.setMargin(new Insets(0, 0, 0, 0));
            daysPanel.add(btn);
        }

        daysPanel.revalidate();
        daysPanel.repaint();
        pack();
    }

    public static LocalDate showDatePicker(Component parent, LocalDate initialDate) {
        Window window = SwingUtilities.getWindowAncestor(parent);
        DatePickerDialog dialog = new DatePickerDialog(window, initialDate);
        dialog.setVisible(true); // blocks until dispose()
        return dialog.selectedDate;
    }
}
