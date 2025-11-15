package com.attic.scheduler.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

import com.attic.scheduler.model.Issue;

public class TablePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3329721466713659240L;
	private JTable table;
	private IssueTableModel tableModel;
	private JPopupMenu popup;
	private IssueTableListener tableListener;
	private JTextField searchBar;
	private JComboBox stageLookup;
	private TableRowSorter<IssueTableModel> sorter;
	private JPanel upperPanel;
	private final JPanel mainPanel;

	public void setIssueTableListener(IssueTableListener listener) {
		this.tableListener = listener;
	}

	private boolean darkMode;

	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		int width = getWidth();
		int height = getHeight();
		Color color1 = new Color(52, 143, 80);
		Color color2 = new Color(86, 180, 211);
		Color darkGray = Color.DARK_GRAY;
		Color gray = Color.GRAY;
		GradientPaint gp = new GradientPaint(0, 0, color1, 180, height, color2);
		GradientPaint dm = new GradientPaint(0, 0, gray, 100, height, darkGray);
		if (!darkMode) {
			g2d.setPaint(gp);
			g2d.fillRect(0, 0, width, height);
		} else {
			g2d.setPaint(dm);
			g2d.fillRect(0, 0, width, height);
		}
	}

	public void setDarkMode(boolean turnDark) {
		this.darkMode = turnDark;
		repaint();
		revalidate();
	}

	public TablePanel() {
		upperPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		mainPanel = new JPanel(new BorderLayout());

		// search bar
		searchBar = new JTextField(30);
		upperPanel.add(searchBar);

		// stage lookup
		String[] stageList = {"All", "Backlog", "Selected for Development", "In Progress", "Development Done", "Peer Review", "Finished"};
		stageLookup = new JComboBox<>(stageList);
		upperPanel.add(stageLookup);

		Border outerBorder = BorderFactory.createTitledBorder("Lookup Filter");
		Border innerBorder = BorderFactory.createEmptyBorder();
		searchBar.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

		// table
		tableModel = new IssueTableModel();
		table = new JTable(tableModel);

		sorter = new TableRowSorter<>(tableModel);
		table.setRowSorter(sorter);

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		mainPanel.add(upperPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

		add(mainPanel, BorderLayout.CENTER);

		searchBar.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				applyFilters();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				applyFilters();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				applyFilters();
			}
		});

		stageLookup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				applyFilters();
			}
		});

		popup = new JPopupMenu();
		JMenuItem editItem = new JMenuItem("Edit Task");
		JMenuItem removeItem = new JMenuItem("Remove Row");
		popup.add(editItem);
		popup.add(removeItem);

		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				table.getSelectionModel().setSelectionInterval(row, row);

				if (e.getButton() == MouseEvent.BUTTON3) {
					popup.show(table, e.getX(), e.getY());
				}
			}

		});

		editItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				int col = table.getSelectedColumn();
				Object editedvalue = table.getValueAt(row, col);
				if (tableListener != null) {
					tableListener.cellEdited(row, col, editedvalue);
					tableModel.fireTableDataChanged();
					table.revalidate();
					table.repaint();
				}

			}
		});

		removeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if (tableListener != null) {
					tableListener.rowDeleted(row);
					tableModel.fireTableRowsDeleted(row, row);
				}
			}
		});

	}

	public void setData(List<Issue> db) {
		tableModel.setData(db);
	}

	public void refresh() {
		tableModel.fireTableDataChanged();
	}

	private void applyFilters() {
		if (sorter == null) return;

		String searchText = searchBar.getText();
		String selectedStage = (String) stageLookup.getSelectedItem();

		// List of RowFilters to combine with AND
		java.util.List<RowFilter<IssueTableModel, Object>> filters = new java.util.ArrayList<>();

		// 1) Text filter (task name, description, assignee)
		if (searchText != null && !searchText.trim().isEmpty()) {
			String pattern = "(?i)" + java.util.regex.Pattern.quote(searchText.trim());
			// columns: 1 = Task Name, 3 = Description, 5 = Assignee
			RowFilter<IssueTableModel, Object> textFilter =
					RowFilter.regexFilter(pattern, 1, 3, 5);
			filters.add(textFilter);
		}

		// 2) Stage filter (Status column = index 2)
		if (selectedStage != null && !"All".equals(selectedStage)) {
			RowFilter<IssueTableModel, Object> stageFilter =
					RowFilter.regexFilter("^" + java.util.regex.Pattern.quote(selectedStage) + "$", 2);
			filters.add(stageFilter);
		}

		// Apply combined filter or clear
		if (filters.isEmpty()) {
			sorter.setRowFilter(null);
		} else {
			sorter.setRowFilter(RowFilter.andFilter(filters));
		}
	}
}
