package com.attic.scheduler.view;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

public class KanbanPanel extends JPanel implements java.awt.event.ActionListener {

    private static final long serialVersionUID = 1L;

    private String[] paneNames;
    private HashMap<Integer, JLayeredPane> paneMap;
    private HashMap<JLayeredPane, Integer> paneIdByLayer;

    private JPopupMenu popupMenu;
    private TaskCardListener listener;
    private JScrollPane scroll;
    private Dimension area, scrollArea;
    private JMenuItem editTask, removeTask;
    private boolean darkMode;

    // Drag & Drop state
    private JTextArea draggedCard;
    private JLayeredPane dragSourcePane;
    private CardTransferHandler cardTransferHandler;

    public void setCardListener(TaskCardListener listen) {
        this.listener = listen;
    }

    @Override
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

    public KanbanPanel() {
        setLayout(new FlowLayout());
        setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        setPreferredSize(new Dimension());

        popupMenu = new JPopupMenu();
        editTask = new JMenuItem("Edit Task");
        editTask.addActionListener(this);
        removeTask = new JMenuItem("Remove Task");
        removeTask.addActionListener(this);
        popupMenu.add(editTask);
        popupMenu.add(removeTask);

        area = new Dimension(0, 0);
        scrollArea = new Dimension(180, 480);
        paneMap = new HashMap<>();
        paneIdByLayer = new HashMap<>();
        cardTransferHandler = new CardTransferHandler();

        // NOTE: original code was from 1 to 5, which skips "Backlog" at index 0.
        // If your stageId is 0..5, you might want to start from 0. For now I keep your 1..5 pattern.
        paneNames = new String[]{
                "Backlog",
                "Selected for Development",
                "In Progress",
                "Development Done",
                "Peer Review",
                "Finished"
        };

        for (int i = 1; i < paneNames.length; i++) {
            JLayeredPane stages = new JLayeredPane();
            stages.setPreferredSize(area);
            stages.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder(paneNames[i]),
                    BorderFactory.createEmptyBorder()
            ));
            stages.setLayout(new FlowLayout());
            stages.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

            // each column is a drop target for cards
            stages.setTransferHandler(cardTransferHandler);

            scroll = new JScrollPane(stages);
            scroll.setPreferredSize(scrollArea);
            scroll.setWheelScrollingEnabled(true);
            scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scroll.getVerticalScrollBar().setUnitIncrement(10);

            // Invisible slots (your original structure, left as-is)
            for (int j = 0; j < 100; j++) {
                JLabel slot = new JLabel("Slot " + j);
                slot.setPreferredSize(new Dimension(135, 125));
                slot.setOpaque(true);
                slot.setVisible(false);
                stages.add(slot);
            }

            paneMap.put(i, stages);
            paneIdByLayer.put(stages, i);
            add(scroll, 0);
        }
    }

    /**
     * Creates a card from textValues.
     * textValues mapping (from your previous code):
     * 0: Task Name
     * 1: Description
     * 2: Status
     * 3: Due Date
     * 4: Assignee
     * 5: Type
     * 6: Priority
     * 7: Difficulty
     * 8: Stage / Pane Id
     * 9: Type Id (for color)
     */
    public void createCard(ArrayList<String> textValues) {

        int paneId = Integer.parseInt(textValues.get(8));
        int typeId = Integer.parseInt(textValues.get(9));

        Point origin = new Point(10, 20);
        JTextArea card = new JTextArea(textValues.get(0));
        card.setPreferredSize(new Dimension(130, 120));
        Color colorToSet = typeId == 0 ? new Color(242, 24, 86)
                : typeId == 1 ? new Color(6, 50, 140) : new Color(14, 153, 51);
        card.setBackground(colorToSet);
        card.setForeground(Color.WHITE);
        card.setLineWrap(true);
        card.setEditable(false);
        card.setBounds(origin.x, origin.y, 100, 100);
        card.setComponentPopupMenu(popupMenu);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Attach DnD handler to the card (for drag)
        card.setTransferHandler(cardTransferHandler);

        // Card mouse behavior: double-click = edit, drag = DnD
        MouseAdapter cardMouseAdapter = new MouseAdapter() {

            private Point pressPoint;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTextArea clickedCard = (JTextArea) e.getSource();
                    HashMap<String, String> changeMap = createCardEditView(
                            350,
                            550,
                            clickedCard.getBackground(),
                            textValues
                    );

                    if (changeMap != null && !changeMap.isEmpty()) {
                        // For now, just append the changed values to the text.
                        // You may want to apply them properly to textValues and controller.
                        clickedCard.setText(textValues.get(0)); // reset to original title
                        changeMap.forEach((key, value) -> {
                            clickedCard.append("\n" + value);
                        });
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressPoint = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (pressPoint == null) return;

                int dx = Math.abs(e.getX() - pressPoint.x);
                int dy = Math.abs(e.getY() - pressPoint.y);

                // small threshold to avoid accidental drags
                if (dx > 5 || dy > 5) {
                    TransferHandler th = ((JTextArea) e.getSource()).getTransferHandler();
                    if (th != null) {
                        th.exportAsDrag((JTextArea) e.getSource(), e, TransferHandler.MOVE);
                    }
                    pressPoint = null;
                }
            }
        };

        card.addMouseListener(cardMouseAdapter);
        card.addMouseMotionListener(cardMouseAdapter);

        if (paneId != 0 && paneMap.containsKey(paneId)) {
            JLayeredPane targetPane = paneMap.get(paneId);

            area.height =
                    (card.getHeight() * targetPane.getComponentCountInLayer(JLayeredPane.PALETTE_LAYER))
                            + (40 * targetPane.getComponentCountInLayer(JLayeredPane.PALETTE_LAYER));

            targetPane.add(card, JLayeredPane.PALETTE_LAYER);
            targetPane.setPreferredSize(new Dimension(190, area.height));
            targetPane.revalidate();
            targetPane.repaint();
        }
    }

    public void tasksLoaded() {
        // Hook for when tasks are loaded from file.
        System.out.println("Tasks loaded");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem clicked = (JMenuItem) e.getSource();
        if (clicked == editTask) {
            // Right-click "Edit Task" is currently not wired to a specific card.
            // You can later wire this to the selected card if you keep track of it.
        }

        if (clicked == removeTask) {
            if (listener != null && draggedCard != null && dragSourcePane != null) {
                // Optional: You can add a "Delete" action that uses draggedCard reference
                System.out.println("remove task clicked");
            }
        }
    }

    private HashMap<String, String> createCardEditView(int panelWidth, int panelHeight, Color colorToSet,
                                                       ArrayList<String> textStrings) {
        JPanel editPanel;
        HashMap<String, String> changeMap = new HashMap<>();

        if (listener != null) {
            editPanel = new JPanel();
            GridBagConstraints gc = new GridBagConstraints();
            int editPanelResult;

            editPanel.setLayout(new GridBagLayout());
            editPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
            editPanel.setBackground(colorToSet);
            ArrayList<JTextField> fieldArray = new ArrayList<>();
            String[] fieldNames = new String[]{
                    "Task Name", "Description", "Status", "Due Date",
                    "Assignee", "Type", "Priority", "Difficulty"
            };
            for (String s : fieldNames) {
                JTextField field = new JTextField(10);
                field.setName(s);
                fieldArray.add(field);
            }

            for (int i = 0; i < fieldNames.length; i++) {
                fieldArray.get(i).setText(textStrings.get(i));
            }

            gc.gridx = 0;
            gc.gridy = 0;
            gc.weightx = 1;
            gc.weighty = 1;
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.FIRST_LINE_START;
            gc.insets = new Insets(10, 5, 0, 0);
            editPanel.add(new JLabel(fieldNames[0]), gc);

            gc.gridx++;
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(10, 0, 0, 10);
            editPanel.add(fieldArray.get(0), gc);

            gc.gridx = 0;
            gc.gridy++;
            gc.weightx = 1;
            gc.weighty = 1;
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.FIRST_LINE_START;
            gc.insets = new Insets(10, 5, 0, 0);
            editPanel.add(new JLabel(fieldNames[1]), gc);

            gc.gridx++;
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(10, 0, 0, 10);
            editPanel.add(fieldArray.get(1), gc);

            gc.gridx = 0;
            gc.gridy++;
            gc.weightx = 1;
            gc.weighty = 1;
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.FIRST_LINE_START;
            gc.insets = new Insets(10, 5, 0, 0);
            editPanel.add(new JLabel(fieldNames[2]), gc);

            gc.gridx++;
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(10, 0, 0, 10);
            editPanel.add(fieldArray.get(2), gc);

            gc.gridx = 0;
            gc.gridy++;
            gc.weightx = 1;
            gc.weighty = 1;
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.FIRST_LINE_START;
            gc.insets = new Insets(10, 5, 0, 0);
            editPanel.add(new JLabel(fieldNames[3]), gc);

            gc.gridx++;
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(10, 0, 0, 10);
            editPanel.add(fieldArray.get(3), gc);

            gc.gridx = 0;
            gc.gridy++;
            gc.weightx = 1;
            gc.weighty = 1;
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.FIRST_LINE_START;
            gc.insets = new Insets(10, 5, 0, 0);
            editPanel.add(new JLabel(fieldNames[4]), gc);

            gc.gridx++;
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(10, 0, 0, 10);
            editPanel.add(fieldArray.get(4), gc);

            gc.gridx = 0;
            gc.gridy++;
            gc.weightx = 1;
            gc.weighty = 1;
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.FIRST_LINE_START;
            gc.insets = new Insets(10, 5, 0, 0);
            editPanel.add(new JLabel(fieldNames[5]), gc);

            gc.gridx++;
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(10, 0, 0, 10);
            editPanel.add(fieldArray.get(5), gc);

            gc.gridx = 0;
            gc.gridy++;
            gc.weightx = 1;
            gc.weighty = 1;
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.FIRST_LINE_START;
            gc.insets = new Insets(10, 5, 0, 0);
            editPanel.add(new JLabel(fieldNames[6]), gc);

            gc.gridx++;
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(10, 0, 0, 10);
            editPanel.add(fieldArray.get(6), gc);

            gc.gridx = 0;
            gc.gridy++;
            gc.weightx = 1;
            gc.weighty = 20;
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.FIRST_LINE_START;
            gc.insets = new Insets(10, 5, 0, 0);
            editPanel.add(new JLabel(fieldNames[7]), gc);

            gc.gridx++;
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(10, 0, 0, 10);
            editPanel.add(fieldArray.get(7), gc);

            do {
                editPanelResult = JOptionPane.showConfirmDialog(
                        null,
                        editPanel,
                        "Edit Task",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

                changeMap.clear();
                for (int i = 0; i < fieldNames.length; i++) {
                    if (!fieldArray.get(i).getText().equals(textStrings.get(i))) {
                        changeMap.put(String.valueOf(i), fieldArray.get(i).getText());
                    }
                }

                if (editPanelResult == -1 || editPanelResult == 2) {
                    break;
                } else if (!changeMap.isEmpty()) {
                    int confirmDialogResult = JOptionPane.showConfirmDialog(
                            null,
                            "Are you sure you want to make these changes?",
                            "Confirm",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );
                    if (confirmDialogResult == 0) {
                        // apply changes back into textStrings
                        changeMap.forEach((idx, val) -> {
                            int i = Integer.parseInt(idx);
                            textStrings.set(i, val);
                        });
                        break;
                    }
                }
            } while (!changeMap.isEmpty());
        }
        return !changeMap.isEmpty() ? changeMap : null;
    }

    /**
     * Inner TransferHandler that moves a card (JTextArea) between JLayeredPane columns.
     */
    private class CardTransferHandler extends TransferHandler {

        private static final long serialVersionUID = 1L;

        @Override
        protected Transferable createTransferable(javax.swing.JComponent c) {
            if (c instanceof JTextArea) {
                draggedCard = (JTextArea) c;
                dragSourcePane = (JLayeredPane) draggedCard.getParent();
                // we don't really care about the data; we just return something
                return new StringSelection(draggedCard.getText());
            }
            return null;
        }

        @Override
        public int getSourceActions(javax.swing.JComponent c) {
            return MOVE;
        }

        @Override
        public boolean canImport(TransferSupport support) {
            // We only accept drops on JLayeredPane (our columns)
            return support.getComponent() instanceof JLayeredPane && support.isDrop();
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support) || draggedCard == null || dragSourcePane == null) {
                return false;
            }

            JLayeredPane targetPane = (JLayeredPane) support.getComponent();
            if (targetPane == dragSourcePane) {
                return false; // no-op if same column
            }

            // Move the card from source to target visually
            dragSourcePane.remove(draggedCard);
            targetPane.add(draggedCard, JLayeredPane.PALETTE_LAYER);

            // Optional: update textValues' stage index if you stored it
            Integer newPaneId = paneIdByLayer.get(targetPane);
            if (newPaneId != null) {
                // If you keep a map from card -> textValues, you can update its stage id here.
                // Example: textValues.set(8, String.valueOf(newPaneId));
                // You would need to store that mapping when creating the card.
            }

            dragSourcePane.revalidate();
            dragSourcePane.repaint();
            targetPane.revalidate();
            targetPane.repaint();

            return true;
        }

        @Override
        protected void exportDone(javax.swing.JComponent source, Transferable data, int action) {
            // clear drag state
            draggedCard = null;
            dragSourcePane = null;
        }
    }
}