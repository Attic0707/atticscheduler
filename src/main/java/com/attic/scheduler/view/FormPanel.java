package com.attic.scheduler.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class FormPanel extends JPanel {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private JTextField taskName;
	private JTextField description;
	private JComboBox<String> assignee;
	private JComboBox<String> priorities;
	private JComboBox<String> stage;
	private JComboBox<Integer> effort;
	private JButton submit;
	private JButton cancel;
	private JRadioButton bug;
	private JRadioButton feature;
	private JRadioButton story;
	private ButtonGroup buttonGroup;
	private JCheckBox subTask;
	private JLabel subLabel;
	private JTextField subField;
	private Boolean isUserLoggedIn;
	private JButton dueDate;
	private static final DateTimeFormatter DUE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private FormListener formListener;

	public void setFormListener(FormListener listener) {
		this.formListener = listener;
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

	public FormPanel() {
		isUserLoggedIn = false;
		setFocusable(true);
		requestFocusInWindow(true);
		setBackground(new Color(0, 156, 99));
		setPreferredSize(new Dimension(300, 800));
		setMinimumSize(new Dimension(250, 600));

		Border outerBorder = BorderFactory.createTitledBorder("Create a Task");
		Border innerBorder = BorderFactory.createEmptyBorder();
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

		// Default values
		String[] userList = {"Admin", "User 1", "User 2", "User 3", "User 4"};
		String [] priorityList = {"Low", "Medium", "High", "Urgent"};
		String[] stageList = {"Backlog", "Selected for Development", "In Progress", "Development Done", "Peer Review", "Finished"};
		Integer[] effortList = {1,3,5,8,13,21};
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		// fields
		taskName = new JTextField(30);
		description = new JTextField(60);
		assignee = new JComboBox<>(userList);
		dueDate = new JButton();
		priorities = new JComboBox<>(priorityList);
		stage = new JComboBox<>(stageList);
		effort = new JComboBox<>(effortList);
		subTask = new JCheckBox("Is there a sub-task?");
		submit = new JButton("Submit");
		cancel = new JButton("Cancel");

		// field config
		assignee.setSelectedIndex(0);
		priorities.setSelectedIndex(0);
		stage.setSelectedIndex(0);
		effort.setSelectedIndex(0);
		dueDate.setText(dtf.format(LocalDateTime.now()));
		dueDate.setHorizontalAlignment(SwingConstants.CENTER);
		dueDate.setCursor(new Cursor(Cursor.HAND_CURSOR));
		submit.setCursor(new Cursor(Cursor.HAND_CURSOR));
		cancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		subTask.setCursor(new Cursor(Cursor.HAND_CURSOR));

		subTask.setBackground(null);

		subLabel = new JLabel("Subtask : ");
		subField = new JTextField(10);
		subLabel.setVisible(false);
		subField.setVisible(false);
		subTask.setSelected(false);

		subTask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean isTicked = subTask.isSelected();
				subLabel.setVisible(isTicked);
				subField.setVisible(isTicked);
				subField.setText(null);
			}
		});
		buttonParams();
		layoutParams();

		submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String taskNameValue = taskName.getText();
				String descValue = description.getText();
				String dueDateText = dueDate.getText();
				int stageId = stage.getSelectedIndex();
				String stageName = (String) stage.getSelectedItem();

				List<String> validationMessages = validateForm(taskNameValue, descValue, dueDateText);

				if (!validationMessages.isEmpty()) {
					String joined = String.join("\n", validationMessages);
					JOptionPane.showMessageDialog( FormPanel.this, joined, "Form Errors", JOptionPane.ERROR_MESSAGE );
					return;
				}

				String assigneeName = (String) assignee.getSelectedItem();
				String type = (buttonGroup.getSelection() == null) ? "Bug" : buttonGroup.getSelection().getActionCommand();
				int typeId = "Bug".equals(type) ? 0 : "Feature".equals(type) ? 1 : 2;

				String priorityName = (String) priorities.getSelectedItem();
				int effortValue = effort.getItemAt(effort.getSelectedIndex());
				String subId = subField.getText();

				String formattedDueDate = dueDateText;

				FormEvent fe = new FormEvent(
						this,
						taskNameValue,
						stageId,
						stageName,
						descValue,
						formattedDueDate,
						assigneeName,
						type,
						typeId,
						priorityName,
						effortValue,
						subId
				);

				if (formListener != null) {
					formListener.formEventOccurred(fe);

					// reset form only on success
					taskName.setText(null);
					stage.setSelectedIndex(1);
					description.setText(null);
					assignee.setSelectedIndex(0);
				}
			}
		});

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				taskName.setText(null);
				stage.setSelectedItem(1);
				description.setText(null);
				assignee.setSelectedIndex(0);
				priorities.setSelectedIndex(0);
				effort.setSelectedIndex(0);
				subTask.setSelected(false);
				subField.setVisible(false);
				subLabel.setVisible(false);
				buttonGroup.clearSelection();
			}
		});

		dueDate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LocalDate initial = LocalDate.now();
				try {
					initial = LocalDate.parse(dueDate.getText(), DUE_DATE_FORMATTER);
				} catch (Exception ex) {
					// ignore, fallback to today
				}

				LocalDate selected = DatePickerDialog.showDatePicker( FormPanel.this, initial);

				if (selected != null) {
					dueDate.setText(selected.format(DUE_DATE_FORMATTER));
				}
			}
		});
	}

	private List<String> validateForm(String taskNameVal, String descVal, String dueDateText) {
		List<String> errorMessages = new ArrayList<>();

		if (taskNameVal == null || taskNameVal.trim().length() < 5) {
			errorMessages.add("Task name should be at least 5 characters long.");
		}

		if (descVal == null || descVal.trim().length() < 20) {
			errorMessages.add("Description must be at least 20 characters long.");
		}

		// Due date validation
		if (dueDateText == null || dueDateText.trim().isEmpty()) {
			errorMessages.add("Due date is required.");
		} else {
			try {
				LocalDate due = LocalDate.parse(dueDateText.trim(), DUE_DATE_FORMATTER);
				if (due.isBefore(LocalDate.now())) {
					errorMessages.add("Due date cannot be in the past.");
				}
			} catch (DateTimeParseException ex) {
				errorMessages.add("Due date format must be dd/MM/yyyy.");
			}
		}

		return errorMessages;
	}

	public void setLoggedInUser(boolean isLoggedIn) {
		this.isUserLoggedIn = isLoggedIn;
		if (submit != null) {
			submit.setEnabled(isLoggedIn);
		}
		revalidate();
	}

	private void buttonParams() {
		bug = new JRadioButton("Bug");
		feature = new JRadioButton("Feature");
		story = new JRadioButton("Story");
		buttonGroup = new ButtonGroup();
		buttonGroup.add(bug);
		buttonGroup.add(feature);
		buttonGroup.add(story);

		bug.setOpaque(false);
		bug.setActionCommand("Bug");

		feature.setActionCommand("Feature");
		feature.setOpaque(false);

		story.setActionCommand("Story");
		story.setOpaque(false);

		submit.setBackground(new Color(101, 152, 240));
		submit.setForeground(Color.BLACK);

		subTask.setOpaque(false);
		submit.setDefaultCapable(true);
		submit.setEnabled(this.isUserLoggedIn);

		ImageIcon submitIcon = new ImageIcon(getClass().getResource("/images/submit.png"));
		Image scaledSub = submitIcon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);

		submitIcon = new ImageIcon(scaledSub);
		submit.setIcon(submitIcon);
		submit.setHorizontalAlignment(JButton.CENTER);
		submit.setVerticalAlignment(JButton.CENTER);
		submit.setPreferredSize(new Dimension(50, 25));

		cancel.setBackground(new Color(101, 152, 248));
		cancel.setForeground(Color.BLACK);
		ImageIcon cancelIcon = new ImageIcon(getClass().getResource("/images/cancel.png"));
		Image scaledCan = cancelIcon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);

		cancelIcon = new ImageIcon(scaledCan);
		cancel.setIcon(cancelIcon);
		cancel.setHorizontalAlignment(JButton.CENTER);
		cancel.setVerticalAlignment(JButton.CENTER);
		cancel.setPreferredSize(new Dimension(50, 25));

		InputMap im = submit.getInputMap();
		im.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
		im.put(KeyStroke.getKeyStroke("released ENTER"), "released");

		InputMap imp = cancel.getInputMap();
		imp.put(KeyStroke.getKeyStroke("SPACE"), "pressed");
		imp.put(KeyStroke.getKeyStroke("released SPACE"), "released");

	}

	private void layoutParams() {
		setLayout(new GridBagLayout());

		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = new Insets(10, 5, 0, 0);
		add(new JLabel("Task Name: "), gc);

		gc.gridx = 1;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(10, 0, 0, 10);
		add(taskName, gc);

		gc.gridx = 0;
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = new Insets(0, 5, 0, 0);
		add(new JLabel("Description: "), gc);

		gc.gridx = 1;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(0, 0, 0, 10);
		add(description, gc);

		gc.gridx = 0;
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = new Insets(0, 5, 0, 0);
		add(new JLabel("Status: "), gc);

		gc.gridx = 1;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(0, 0, 0, 10);
		add(stage, gc);

		gc.gridx = 0;
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = new Insets(0, 5, 0, 0);
		add(new JLabel("Due Date: "), gc);

		gc.gridx = 1;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(0, 0, 0, 10);
		add(dueDate, gc);

		gc.gridx = 0;
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = new Insets(0, 5, 0, 0);
		add(new JLabel("Assignee: "), gc);

		gc.gridx = 1;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(0, 0, 0, 10);
		add(assignee, gc);

		gc.gridx = 0;
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = new Insets(0, 5, 0, 0);
		add(new JLabel("Type: "), gc);

		gc.gridx = 1;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		add(bug, gc);

		gc.gridx = 1;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.insets = new Insets(0, 50, 0, 0);
		add(feature, gc);

		gc.gridx = 1;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.insets = new Insets(0, 120, 0, 0);
		add(story, gc);

		gc.gridx = 0;
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = new Insets(0, 5, 0, 0);
		add(new JLabel("Priority: "), gc);

		gc.gridx = 1;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(0, 0, 0, 10);
		add(priorities, gc);

		gc.gridx = 0;
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = new Insets(0, 5, 0, 0);
		add(new JLabel("Effort: "), gc);

		gc.gridx = 1;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(0, 0, 0, 10);
		add(effort, gc);

		gc.gridx = 0;
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = new Insets(0, 5, 0, 0);
		add(new JLabel("Any Subtask?: "), gc);

		gc.gridx = 1;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(0, 0, 0, 10);
		add(subTask, gc);

		gc.gridx = 0;
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = new Insets(0, 5, 0, 0);
		add(subLabel, gc);

		gc.gridx = 1;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(0, 0, 0, 10);
		add(subField, gc);

		gc.gridx = 1;
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 20;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 100, 0, 5);
		add(submit, gc);

		gc.gridx = 1;
		gc.weightx = 1;
		gc.weighty = 20;
		gc.insets = new Insets(0, 1, 0, 10);
		add(cancel, gc);
	}
}