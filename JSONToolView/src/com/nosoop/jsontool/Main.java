/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.jsontool;

import bundled.jsontool.org.json.JSONException;
import bundled.jsontool.org.json.JSONObject;
import bundled.jsontool.org.json.JSONTokener;
import java.awt.EventQueue;
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
import java.util.Set;
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
     * Recursively builds a tree of JSONObjectTreeNodes containing JSONReference
     * values using the given JSONObject and JSONObjectTreeNode.
     *
     * @param treeNode Tree node to add JSONObject values to.
     * @param jsonData The JSONObject with values to add.
     * @throws JSONException
     */
    void buildJSONTree(JSONObjectTreeNode treeNode, JSONObject jsonData)
            throws JSONException {
        for (String key : (Set<String>) jsonData.keySet()) {
            if (jsonData.optJSONObject(key) != null) {
                JSONObjectTreeNode innerObject =
                        new JSONObjectTreeNode(key, jsonData.getJSONObject(key));
                buildJSONTree(innerObject, jsonData.getJSONObject(key));

                treeNode.add(innerObject);
            }
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

        for (int i = jsonTable.getRowCount() - 1; i >= 0; i--) {
            jsonTable.removeRow(i);
        }

        for (Map.Entry keyValues : ref.keyValues.entrySet()) {
            jsonTable.addRow(new Object[]{keyValues.getKey(), keyValues.getValue().getClass().getSimpleName(), keyValues.getValue()});
        }
    }

    /**
     * Updates the working JSONReference instance with a new key / value pair.
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
            // TODO persist changes made in editor
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

        jsonMainPane = new javax.swing.JSplitPane();
        jsonTreeScrollPane = new javax.swing.JScrollPane();
        jsonTree = new javax.swing.JTree(jsonRoot);
        jsonTableScrollPane = new javax.swing.JScrollPane();
        jsonObjectTable = new javax.swing.JTable();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuItemOpen = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();

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
        jsonObjectTable.setFocusable(false);
        jsonObjectTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jsonObjectTable.setShowHorizontalLines(false);
        jsonObjectTable.setShowVerticalLines(false);
        jsonObjectTable.getTableHeader().setReorderingAllowed(false);
        jsonObjectTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jsonObjectTableMouseClicked(evt);
            }
        });
        jsonTableScrollPane.setViewportView(jsonObjectTable);

        jsonMainPane.setRightComponent(jsonTableScrollPane);

        menuFile.setText("File");

        menuItemOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menuItemOpen.setText("Open");
        menuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemOpenActionPerformed(evt);
            }
        });
        menuFile.add(menuItemOpen);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Save");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        menuFile.add(jMenuItem1);

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

    private void menuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOpenActionPerformed
        int fileDialogReturnValue = FILE_DIALOG.showOpenDialog(this);

        if (fileDialogReturnValue == JFileChooser.APPROVE_OPTION) {
            File file = FILE_DIALOG.getSelectedFile();

            loadFile(file);
        }
    }//GEN-LAST:event_menuItemOpenActionPerformed

    private void jsonTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jsonTreeValueChanged
        JSONObjectTreeNode node = (JSONObjectTreeNode) jsonTree.getLastSelectedPathComponent();

        if (node == null) {
            return;
        }

        workingJSONObject = node;
        buildTableElements(node);
    }//GEN-LAST:event_jsonTreeValueChanged

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        int fileDialogReturnValue = FILE_DIALOG.showSaveDialog(this);

        if (fileDialogReturnValue == JFileChooser.APPROVE_OPTION) {
            File file = FILE_DIALOG.getSelectedFile();

            saveFile(file);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jsonObjectTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jsonObjectTableMouseClicked
        int targetRow = jsonObjectTable.getSelectedRow();

        if (evt.getClickCount() == 2
                && evt.getButton() == MouseEvent.BUTTON1) {
            if (targetRow >= 0) {
                String key = (String) jsonObjectTable.getModel().getValueAt(targetRow, 0);
                Object object = workingJSONObject.keyValues.get(key);

                JSONValueEditDialog.JSONValueDialogResponse returnValue =
                        (new JSONValueEditDialog(this, key, object))
                        .getReturnValue();

                String newKey = returnValue.key;
                Object newValue = returnValue.value;

                workingJSONObject.keyValues.remove(key);

                updateWorkingKeyValue(newKey, newValue);
            }
        }
    }//GEN-LAST:event_jsonObjectTableMouseClicked

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
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JSplitPane jsonMainPane;
    private javax.swing.JTable jsonObjectTable;
    private javax.swing.JScrollPane jsonTableScrollPane;
    private javax.swing.JTree jsonTree;
    private javax.swing.JScrollPane jsonTreeScrollPane;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuItemOpen;
    // End of variables declaration//GEN-END:variables
}
