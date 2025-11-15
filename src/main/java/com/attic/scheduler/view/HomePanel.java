package com.attic.scheduler.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public class HomePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7685689743747881331L;
	private JTextArea textArea;

	private boolean darkMode;
	private HomeListener homeListener;

	public void setHomeListener(HomeListener listener) {
		this.homeListener = listener;
	}

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
		if(!darkMode) {
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

	public HomePanel() {
		setFocusable(true);
		requestFocusInWindow();
		setLayout(new BorderLayout()); 
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topPanel.setOpaque(false);
		add(topPanel, BorderLayout.NORTH);
		
		textArea = new JTextArea(5, 50);
		textArea.setEnabled(false);
		textArea.setDisabledTextColor(Color.WHITE);
		textArea.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		textArea.setText("Welcome to Attic Scheduler\nDesigned and developed by Attic Software Inc.\n2020 Istanbul\nAll Rights Reserved");
		textArea.setOpaque(false);
		textArea.setPreferredSize(new Dimension(500, 100));

		JButton extend = new JButton("<");
		extend.setBackground(new Color(101, 152, 240));
		extend.setForeground(Color.BLACK);
		extend.setHorizontalAlignment(JButton.CENTER);
		extend.setVerticalAlignment(JButton.CENTER);
		extend.setPreferredSize(new Dimension(50, 25));
		extend.addActionListener(new ActionListener() {
			private boolean isFormVisible = false;
			private String buttonText;

			public void actionPerformed(ActionEvent e) {
				isFormVisible = !isFormVisible;
				buttonText = isFormVisible ? ">" : "<";
				HomeEvent he = new HomeEvent(this, isFormVisible);
				if (homeListener != null) {
					homeListener.homeEventOccurred(he);
					extend.setText(buttonText);
				}
			}
		});
		add(textArea);
		topPanel.add(extend);
	}
}