/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.jsontool;

import bundled.jsontool.org.json.JSONException;
import bundled.jsontool.org.json.JSONObject;
import bundled.jsontool.org.json.JSONTokener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class Main extends javax.swing.JFrame {
    JSONObjectTreeNode jsonRoot;
    JSONObjectTreeNode workingJSONObject;
    Object[][] dataTable;

    /**
     * Creates the JFrame form.
     */
    public Main() {
        //jsonRoot = new JSONObjectTreeNode();

        try {
            jsonRoot = new JSONObjectTreeNode(String.format("root: no file"), new JSONObject());
        } catch (JSONException e) {
            throw new Error(e);
        }

        dataTable = new Object[][]{};

        initComponents();

        FILE_DIALOG = new JFileChooser() {
            final String CONFIRM_MESSAGE = "The file exists.  Overwrite?",
                    CONFIRM_TITLE = "JSONToolView";

            /**
             * Subclass to add a confirm message.
             */
            @Override
            public void approveSelection() {
                File file = getSelectedFile();
                if (file.exists()
                        && this.getDialogType() == JFileChooser.SAVE_DIALOG) {

                    int result = JOptionPane.showConfirmDialog(this,
                            CONFIRM_MESSAGE, CONFIRM_TITLE,
                            JOptionPane.YES_NO_OPTION);

                    switch (result) {
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.NO_OPTION:
                            cancelSelection();
                            return;
                        case JOptionPane.CLOSED_OPTION:
                            return;
                        default:
                            return;
                    }
                }

                // If not the save dialog. then assume it's been approved.
                super.approveSelection();
            }
        };
    }

    /**
     * Loads a file and attempts to parse it as JSON.
     *
     * @param jsonFile The file to load. Assumed JSON; a message will be
     * displayed with the exception message if a JSONException is caught.
     */
    void loadFile(File jsonFile) {
        try {
            JSONObject jsonData = new JSONObject(new JSONTokener(new FileInputStream(jsonFile)));

            // Remove previous JSON object tree and reload GUI.
            jsonRoot.removeAllChildren();

            // Reload tree.
            ((DefaultTreeModel) jsonTree.getModel()).reload();

            // Generate tree structure.
            jsonRoot.buildKeyValues(jsonData);

            // Expand root, select root node.
            jsonTree.expandRow(0);
            jsonTree.setSelectionPath(jsonTree.getPathForRow(0));
        } catch (JSONException | FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    /**
     * Builds a table of key / type / value objects for a selected JSONReference
     * instance.
     *
     * @param ref The JSONReference instance to build a table from.
     */
    void buildTableElements(JSONObjectTreeNode ref) {
        DefaultTableModel jsonTable = ((DefaultTableModel) jsonObjectTable.getModel());

        // Fastest way to clear the table.
        jsonTable.setNumRows(0);

        for (Map.Entry keyValues : ref.keyValues.entrySet()) {
            // TODO patch up addrow to support JSONArrays?
            jsonTable.addRow(new Object[]{keyValues.getKey(), keyValues.getValue().getClass().getSimpleName(), keyValues.getValue().toString() });
        }
    }

    /**
     * Updates the working JSONObjectTreeNode instance with a new key / value
     * pair.
     *
     * @param key
     * @param value
     */
    void updateWorkingKeyValue(String key, Object value) {
        workingJSONObject.keyValues.put(key, value);

        // Rebuild table just for the value being changed.
        buildTableElements(workingJSONObject);
    }

    /**
     * Saves the current working file as JSON.
     *
     * @param jsonFile The file to save.
     */
    void saveFile(File jsonFile) {
        try {
            JSONObject export = exportJSONTree(jsonRoot);

            try (FileOutputStream fOut = new FileOutputStream(jsonFile, false);
                    BufferedOutputStream bOut = new BufferedOutputStream(fOut);
                    PrintStream pOut = new PrintStream(bOut)) {
                pOut.print(export.toString(4));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recursively exports a tree represented by JSONObjectTreeNode instances
     * containing JSONReference objects as a JSONObject.
     *
     * @param treeNode The node to use as the root to export from.
     * @return A JSONObject
     * @throws JSONException
     */
    JSONObject exportJSONTree(JSONObjectTreeNode treeNode) throws JSONException {
        JSONObject rootObject = new JSONObject();

        Enumeration<JSONObjectTreeNode> childNodes = treeNode.children();

        while (childNodes.hasMoreElements()) {
            JSONObjectTreeNode childNode = childNodes.nextElement();
            JSONObject innerObject = exportJSONTree(childNode);

            rootObject.put(childNode.toString(), innerObject);
        }

        for (Map.Entry<String, Object> entry : treeNode.keyValues.entrySet()) {
            rootObject.put(entry.getKey(), entry.getValue());
        }

        return rootObject;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jsonObjectModify = new javax.swing.JPopupMenu();
        jsonKeyDelete = new javax.swing.JMenuItem();
        jsonMainPane = new javax.swing.JSplitPane();
        jsonTreeScrollPane = new javax.swing.JScrollPane();
        jsonTree = new javax.swing.JTree(jsonRoot);
        jsonTableScrollPane = new javax.swing.JScrollPane();
        jsonObjectTable = new javax.swing.JTable();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuFileOpen = new javax.swing.JMenuItem();
        menuFileSave = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();

        jsonKeyDelete.setText("Delete key / value pairs");
        jsonKeyDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jsonKeyDeleteActionPerformed(evt);
            }
        });
        jsonObjectModify.add(jsonKeyDelete);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JSONToolView pre-alpha");

        jsonMainPane.setDividerLocation(192);

        jsonTree.setShowsRootHandles(true);
        jsonTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jsonTreeValueChanged(evt);
            }
        });
        jsonTreeScrollPane.setViewportView(jsonTree);

        jsonMainPane.setLeftComponent(jsonTreeScrollPane);

        jsonObjectTable.setModel(new javax.swing.table.DefaultTableModel(
            dataTable,
            new String [] {
                "Name", "Type", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                //Only the third column
                return false;
            }
        });
        jsonObjectTable.setFillsViewportHeight(true);
        jsonObjectTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jsonObjectTable.setShowHorizontalLines(false);
        jsonObjectTable.setShowVerticalLines(false);
        jsonObjectTable.getTableHeader().setReorderingAllowed(false);
        jsonObjectTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jsonObjectTableMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jsonObjectTableMouseClicked(evt);
            }
        });
        jsonObjectTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jsonObjectTableKeyPressed(evt);
            }
        });
        jsonTableScrollPane.setViewportView(jsonObjectTable);

        jsonMainPane.setRightComponent(jsonTableScrollPane);

        menuFile.setText("File");

        menuFileOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menuFileOpen.setText("Open");
        menuFileOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFileOpenActionPerformed(evt);
            }
        });
        menuFile.add(menuFileOpen);

        menuFileSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuFileSave.setText("Save");
        menuFileSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFileSaveActionPerformed(evt);
            }
        });
        menuFile.add(menuFileSave);

        menuBar.add(menuFile);

        menuEdit.setText("Edit");
        menuBar.add(menuEdit);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jsonMainPane, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jsonMainPane)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    final JFileChooser FILE_DIALOG;

    private void menuFileOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileOpenActionPerformed
        int fileDialogReturnValue = FILE_DIALOG.showOpenDialog(this);

        // Load file.
        if (fileDialogReturnValue == JFileChooser.APPROVE_OPTION) {
            File file = FILE_DIALOG.getSelectedFile();
            loadFile(file);
        }
    }//GEN-LAST:event_menuFileOpenActionPerformed

    private void jsonTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jsonTreeValueChanged
        // Switched to a different JSONObject tree node.
        JSONObjectTreeNode node = (JSONObjectTreeNode) jsonTree.getLastSelectedPathComponent();

        if (node == null) {
            return;
        }

        // Set selected node as working if non-null.
        workingJSONObject = node;

        // Build the table from the node's key / value pairs.
        buildTableElements(node);
    }//GEN-LAST:event_jsonTreeValueChanged

    private void menuFileSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileSaveActionPerformed
        int fileDialogReturnValue = FILE_DIALOG.showSaveDialog(this);

        if (fileDialogReturnValue == JFileChooser.APPROVE_OPTION) {
            File file = FILE_DIALOG.getSelectedFile();

            saveFile(file);
        }
    }//GEN-LAST:event_menuFileSaveActionPerformed

    private void jsonObjectTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jsonObjectTableMouseClicked
        int targetRow = jsonObjectTable.getSelectedRow();

        if (evt.getClickCount() == 2
                && evt.getButton() == MouseEvent.BUTTON1) {
            if (targetRow >= 0) {
                // Modify the selected key/value pair.
                String key = (String) jsonObjectTable.getModel().getValueAt(targetRow, 0);
                Object object = workingJSONObject.keyValues.get(key);

                // Pop-up a modal dialog box to edit the key/value.
                JSONValueEditDialog.JSONValueDialogResponse returnValue =
                        (new JSONValueEditDialog(this, key, object))
                        .getReturnValue();

                String newKey = returnValue.key;
                Object newValue = returnValue.value;

                /**
                 * Remove previous key/value pair if the new one does not use
                 * the same key.
                 */
                workingJSONObject.keyValues.remove(key);

                updateWorkingKeyValue(newKey, newValue);
            }
        }
    }//GEN-LAST:event_jsonObjectTableMouseClicked

    private void jsonObjectTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jsonObjectTableMouseReleased
        int targetRow = jsonObjectTable.rowAtPoint(evt.getPoint());

        // Show exactly which row we're acting on.
        if (targetRow >= 0 && targetRow < jsonObjectTable.getRowCount()) {
            jsonObjectTable.setRowSelectionInterval(targetRow, targetRow);
        } else {
            jsonObjectTable.clearSelection();
        }

        // Show menu for object.
        if (evt.getButton() == MouseEvent.BUTTON3 && targetRow >= 0) {
            jsonObjectModify.show(jsonObjectTable, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jsonObjectTableMouseReleased

    private void jsonKeyDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jsonKeyDeleteActionPerformed
        int targetRow = jsonObjectTable.getSelectedRow();

        // Delete the selected key/value pair.
        if (targetRow >= 0) {
            String key = (String) jsonObjectTable.getModel().getValueAt(targetRow, 0);

            workingJSONObject.keyValues.remove(key);
            buildTableElements(workingJSONObject);
        }
    }//GEN-LAST:event_jsonKeyDeleteActionPerformed

    private void jsonObjectTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jsonObjectTableKeyPressed
        // TODO proper implementation
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            int targetRow = jsonObjectTable.getSelectedRow();

            if (targetRow >= 0) {
                String key = (String) jsonObjectTable.getModel().getValueAt(targetRow, 0);

                workingJSONObject.keyValues.remove(key);
                buildTableElements(workingJSONObject);
            }
        }
    }//GEN-LAST:event_jsonObjectTableKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the operating system look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem jsonKeyDelete;
    private javax.swing.JSplitPane jsonMainPane;
    private javax.swing.JPopupMenu jsonObjectModify;
    private javax.swing.JTable jsonObjectTable;
    private javax.swing.JScrollPane jsonTableScrollPane;
    private javax.swing.JTree jsonTree;
    private javax.swing.JScrollPane jsonTreeScrollPane;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuFileOpen;
    private javax.swing.JMenuItem menuFileSave;
    // End of variables declaration//GEN-END:variables
}
