package com.attic.scheduler.view;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileTableModel extends AbstractTableModel {
    private List<String> userNames;
    private List<String> loginDates;
    // private List<String> columnNames;
    private String[] columnNames = {"UserName", "Login Date / Time"};

    public ProfileTableModel() {
        userNames = new ArrayList<>();
        loginDates = new ArrayList<>();

    }

    public void setData(HashMap<String, ArrayList<String>> loginHistoryMap) {
        // Clear existing data
        userNames.clear();
        loginDates.clear();

        // Populate data from the map
        for (Map.Entry<String, ArrayList<String>> entry : loginHistoryMap.entrySet()) {
            String userName = entry.getKey();
            ArrayList<String> loginHistory = entry.getValue();
            
            for (String loginDate : loginHistory) {
                userNames.add(userName);
                loginDates.add(loginDate);
            }
        }
        // Notify the table that the data has changed
        fireTableDataChanged();
    }

    public void clearData() {
        userNames.clear();
        loginDates.clear();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return userNames.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public String getColumnName(int index) {
        return columnNames[index];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columnIndex == 0 ? userNames.get(rowIndex) : columnIndex == 1 ? loginDates.get(rowIndex) : null;
    }
}
