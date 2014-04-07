/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.jsontool;

import bundled.jsontool.org.json.JSONException;
import bundled.jsontool.org.json.JSONObject;
import bundled.jsontool.org.json.JSONTokener;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class JSONToolWindow extends javax.swing.JFrame {
    File workingFile;
    JSONObjectTreeNode jsonRoot;
    JSONObjectTreeNode workingJSONObject;
    Object[][] dataTable;

    /**
     * Creates the JFrame form.
     */
    public JSONToolWindow() {
        workingFile = null;
        try {
            jsonRoot = new JSONObjectTreeNode(String.format("root: new file"), new JSONObject());
            workingJSONObject = jsonRoot;
        } catch (JSONException e) {
            throw new Error(e);
        }

        dataTable = new Object[][]{};

        initComponents();

        FILE_DIALOG = new JFileChooser() {
            /**
             * Subclass to add a confirm message.
             */
            final String CONFIRM_MESSAGE = "The file exists.  Overwrite?",
                    CONFIRM_TITLE = "JSONToolView";

            @Override
            public void approveSelection() {
                File file = getSelectedFile();
                if (this.getDialogType() == JFileChooser.SAVE_DIALOG
                        && file.exists()) {

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

                // If not the save dialog. then just forward the approval.
                super.approveSelection();
            }
        };

        // TODO add support to drag-and-drop for non-leaf tree nodes.
    }

    /**
     * Loads a file and attempts to parse it as JSON.
     *
     * @param jsonFile The file to load. Assumed JSON; a message will be
     * displayed with the exception message if a JSONException is caught.
     */
    void loadFile(final File jsonFile) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try (FileInputStream fis = new FileInputStream(jsonFile)) {
                    JSONObject jsonData = new JSONObject(new JSONTokener(fis));

                    // Remove previous JSON object tree and reload GUI.
                    jsonRoot.removeAllChildren();

                    // Reload tree.
                    ((DefaultTreeModel) jsonTree.getModel()).reload();

                    // Generate tree structure.
                    jsonRoot.buildKeyValues(jsonData);

                    // Expand root, select root node.
                    jsonTree.expandRow(0);
                    jsonTree.setSelectionPath(jsonTree.getPathForRow(0));

                    JSONToolWindow.this.workingFile = jsonFile;
                    jsonRoot.setName(
                            String.format("root: %s", jsonFile.getName()));
                } catch (JSONException | IOException e) {
                    JOptionPane.showMessageDialog(JSONToolWindow.this, e.getMessage());
                }
            }
        });
    }

    /**
     * Builds a table of key / type / value objects for a selected JSONReference
     * instance.
     *
     * @param referenceNode The JSONReference instance to build a table from.
     */
    void buildTableElements(final JSONObjectTreeNode referenceNode) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                DefaultTableModel jsonTable = ((DefaultTableModel) jsonObjectTable.getModel());

                // Fastest way to clear the table.
                jsonTable.setNumRows(0);

                for (Map.Entry keyValues : referenceNode.keyValues.entrySet()) {
                    // TODO patch up addrow to support JSONArrays?
                    Object value = keyValues.getValue();
                    String textValue, classValue;
                    if (value != null) {
                        textValue = value.toString();
                        classValue = value.getClass().getSimpleName();
                    } else {
                        classValue = "Null";
                        textValue = "";
                    }

                    jsonTable.addRow(new Object[]{ (String) keyValues.getKey(),
                        classValue, textValue});
                }
            }
        });
    }

    /**
     * Updates the working JSONObjectTreeNode instance with a new key / value
     * pair.
     *
     * @param key
     * @param value
     */
    void putKeyValueAndRebuild(String key, Object value) {
        workingJSONObject.keyValues.put(key, value);

        // Rebuild table just for the value being changed.
        buildTableElements(workingJSONObject);
    }

    /**
     * Saves the current working file as JSON.
     *
     * @param jsonFile The file to save.
     */
    void saveFile(final File jsonFile) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set waiting cursor.
                    JSONToolWindow.this.setCursor(
                            Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    
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
                } finally {
                    // Unset waiting cursor.
                    JSONToolWindow.this.setCursor(
                            Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
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
            // Wrap values. Mainly for null.
            rootObject.put(entry.getKey(), JSONObject.wrap(entry.getValue()));
        }

        return rootObject;
    }

    /**
     * Checks whether or not a node has an existing key in its object table or
     * as children nodes.
     *
     * @param node The JSONObjectTreeNode instance to check a key name for.
     * @param inputKey A key to check the existence of.
     * @return
     */
    boolean keyExistsInNode(JSONObjectTreeNode node, String inputKey) {
        for (String key : node.keyValues.keySet()) {
            if (key.equals(inputKey)) {
                return true;
            }
        }

        Enumeration<JSONObjectTreeNode> children = node.children();
        while (children.hasMoreElements()) {
            if (children.nextElement().getName().equals(inputKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jsonKeyValueModify = new javax.swing.JPopupMenu();
        jsonKeyCreate = new javax.swing.JMenuItem();
        jsonKeyDelete = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jsonKeyDuplicate = new javax.swing.JMenuItem();
        jsonObjectTreeModify = new javax.swing.JPopupMenu();
        jsonTreeNewChildNode = new javax.swing.JMenuItem();
        jsonTreeRenameNode = new javax.swing.JMenuItem();
        jsonTreeRemoveNode = new javax.swing.JMenuItem();
        jsonMainPane = new javax.swing.JSplitPane();
        jsonTreeScrollPane = new javax.swing.JScrollPane();
        jsonTree = new javax.swing.JTree(jsonRoot);
        jsonTableScrollPane = new javax.swing.JScrollPane();
        jsonObjectTable = new javax.swing.JTable();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuFileOpen = new javax.swing.JMenuItem();
        menuFileSave = new javax.swing.JMenuItem();
        menuFileSaveAs = new javax.swing.JMenuItem();
        menuView = new javax.swing.JMenu();
        menuViewRawJSON = new javax.swing.JMenuItem();
        menuViewSelectionRawJSON = new javax.swing.JMenuItem();

        jsonKeyCreate.setText("Create key / valur pair...");
        jsonKeyCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jsonKeyCreateActionPerformed(evt);
            }
        });
        jsonKeyValueModify.add(jsonKeyCreate);

        jsonKeyDelete.setText("Delete key / value pair...");
        jsonKeyDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jsonKeyDeleteActionPerformed(evt);
            }
        });
        jsonKeyValueModify.add(jsonKeyDelete);
        jsonKeyValueModify.add(jSeparator1);

        jsonKeyDuplicate.setText("Duplicate selected key / value pair...");
        jsonKeyDuplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jsonKeyDuplicateActionPerformed(evt);
            }
        });
        jsonKeyValueModify.add(jsonKeyDuplicate);

        jsonTreeNewChildNode.setText("Create new child node...");
        jsonTreeNewChildNode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jsonTreeNewChildNodeActionPerformed(evt);
            }
        });
        jsonObjectTreeModify.add(jsonTreeNewChildNode);

        jsonTreeRenameNode.setText("Rename selected node...");
        jsonTreeRenameNode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jsonTreeRenameNodeActionPerformed(evt);
            }
        });
        jsonObjectTreeModify.add(jsonTreeRenameNode);

        jsonTreeRemoveNode.setText("Delete selected node...");
        jsonTreeRemoveNode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jsonTreeRemoveNodeActionPerformed(evt);
            }
        });
        jsonObjectTreeModify.add(jsonTreeRemoveNode);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JSONToolView pre-alpha");

        jsonMainPane.setDividerLocation(192);
        jsonMainPane.setResizeWeight(0.25);

        jsonTree.setDragEnabled(true);
        jsonTree.setTransferHandler(new JSONObjectTreeTransferHandler());
        jsonTree.setDropMode(javax.swing.DropMode.ON_OR_INSERT);
        jsonTree.setShowsRootHandles(true);
        jsonTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jsonTreeMouseReleased(evt);
            }
        });
        jsonTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jsonTreeValueChanged(evt);
            }
        });
        jsonTreeScrollPane.setViewportView(jsonTree);

        jsonMainPane.setLeftComponent(jsonTreeScrollPane);

        jsonObjectTable.setAutoCreateRowSorter(true);
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

        menuFileSaveAs.setText("Save As...");
        menuFileSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFileSaveAsActionPerformed(evt);
            }
        });
        menuFile.add(menuFileSaveAs);

        menuBar.add(menuFile);

        menuView.setText("View");

        menuViewRawJSON.setText("View raw JSON text ...");
        menuViewRawJSON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewRawJSONActionPerformed(evt);
            }
        });
        menuView.add(menuViewRawJSON);

        menuViewSelectionRawJSON.setText("View raw JSON text (selection)...");
        menuViewSelectionRawJSON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewSelectionRawJSONActionPerformed(evt);
            }
        });
        menuView.add(menuViewSelectionRawJSON);

        menuBar.add(menuView);

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
        /**
         * Loads a file into the current window.
         */
        int fileDialogReturnValue = FILE_DIALOG.showOpenDialog(JSONToolWindow.this);

        if (fileDialogReturnValue == JFileChooser.APPROVE_OPTION) {
            File file = FILE_DIALOG.getSelectedFile();
            loadFile(file);
        }
    }//GEN-LAST:event_menuFileOpenActionPerformed

    private void jsonTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jsonTreeValueChanged
        /**
         * Switched to a different JSON object node. Rebuild the key / value
         * pairs table to show the items from the new node.
         */
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
        if (workingFile == null) {
            int fileDialogReturnValue = FILE_DIALOG.showSaveDialog(this);

            if (fileDialogReturnValue == JFileChooser.APPROVE_OPTION) {
                workingFile = FILE_DIALOG.getSelectedFile();

                saveFile(workingFile);
            }
        } else {
            saveFile(workingFile);
        }
    }//GEN-LAST:event_menuFileSaveActionPerformed

    private void jsonObjectTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jsonObjectTableMouseClicked
        /**
         * Edits a selected key / value pair.
         */
        int targetRow = jsonObjectTable.getSelectedRow();

        if (evt.getClickCount() == 2
                && evt.getButton() == MouseEvent.BUTTON1) {
            if (targetRow >= 0) {
                int selectedRow = 
                        jsonObjectTable.convertRowIndexToModel(targetRow);
                
                // Modify the selected key/value pair.
                String key = (String) jsonObjectTable.getModel().getValueAt(selectedRow, 0);
                Object object = workingJSONObject.keyValues.get(key);

                // Pop-up a modal dialog box to edit the key/value.
                JSONValueEditDialog.JSONValueDialogResponse returnValue =
                        (new JSONValueEditDialog(JSONToolWindow.this, key, object))
                        .getReturnValue();

                if (returnValue.dialogResponse
                        == JSONValueEditDialog.ReturnValue.SAVE) {
                    String newKey = returnValue.key;
                    Object newValue = returnValue.value;

                    /**
                     * Remove previous key/value pair if the new one does not
                     * use the same key.
                     */
                    workingJSONObject.keyValues.remove(key);

                    putKeyValueAndRebuild(newKey, newValue);
                }
            }
        }
    }//GEN-LAST:event_jsonObjectTableMouseClicked

    private void jsonObjectTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jsonObjectTableMouseReleased
        /**
         * Opens the menu for the key / value.
         */
        int targetRow = jsonObjectTable.rowAtPoint(evt.getPoint());

        // Show exactly which row we're acting on.
        if (targetRow >= 0 && targetRow < jsonObjectTable.getRowCount()) {
            jsonObjectTable.setRowSelectionInterval(targetRow, targetRow);
        } else {
            jsonObjectTable.clearSelection();
        }

        // Show menu for object.
        if (evt.getButton() == MouseEvent.BUTTON3) {
            // Set menu option as enabled if we have a key / value selected.
            jsonKeyDelete.setEnabled(targetRow >= 0);
            jsonKeyDuplicate.setEnabled(targetRow >= 0);

            jsonKeyValueModify.show(jsonObjectTable, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jsonObjectTableMouseReleased

    private void jsonKeyDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jsonKeyDeleteActionPerformed
        /**
         * Deletes the selected key / value pair from the right-click menu.
         */
        int targetRow = jsonObjectTable.getSelectedRow();

        // Delete the selected key/value pair.
        if (targetRow >= 0) {
            int selectedRow = 
                        jsonObjectTable.convertRowIndexToModel(targetRow);
            
            String key = (String) 
                    jsonObjectTable.getModel().getValueAt(selectedRow, 0);

            workingJSONObject.keyValues.remove(key);
            buildTableElements(workingJSONObject);
        }
    }//GEN-LAST:event_jsonKeyDeleteActionPerformed

    private void jsonObjectTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jsonObjectTableKeyPressed
        /**
         * Removes the selected key / value pair when the DELETE key is pressed
         * on it.
         */
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            int targetRow = jsonObjectTable.getSelectedRow();

            if (targetRow >= 0) {
                int selectedRow = 
                        jsonObjectTable.convertRowIndexToModel(targetRow);
                
                String key = (String) 
                        jsonObjectTable.getModel().getValueAt(selectedRow, 0);

                workingJSONObject.keyValues.remove(key);
                buildTableElements(workingJSONObject);
            }
        }
    }//GEN-LAST:event_jsonObjectTableKeyPressed

    private void menuViewRawJSONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewRawJSONActionPerformed
        /**
         * Shows a dialog containing a raw text preview of the current JSON
         * file.
         */
        try {
            JSONRawTextDialog dialog = new JSONRawTextDialog(this,
                    exportJSONTree(jsonRoot));
            dialog.setVisible(true);
        } catch (JSONException e) {
            // Uh. Something wrong happened.
            // We should /prrroooobably/ log this.
        }
    }//GEN-LAST:event_menuViewRawJSONActionPerformed

    private void jsonKeyCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jsonKeyCreateActionPerformed
        /**
         * Add a new key / value pair.
         */
        String key = "";
        Object object = null;

        JSONValueEditDialog.JSONValueDialogResponse returnValue =
                (new JSONValueEditDialog(JSONToolWindow.this, key, object))
                .getReturnValue();

        if (returnValue.dialogResponse == JSONValueEditDialog.ReturnValue.SAVE) {
            String newKey = returnValue.key;
            Object newValue = returnValue.value;

            if (!keyExistsInNode(workingJSONObject, newKey)) {
                putKeyValueAndRebuild(newKey, newValue);
            } else {
                JOptionPane.showMessageDialog(JSONToolWindow.this,
                        "A key with that name already exists.",
                        "Error creating key / value pair.",
                        JOptionPane.ERROR_MESSAGE);
                /**
                 * TODO Confirm replace? Handle things properly if a tree node
                 * is replaced.
                 */
            }
        }
    }//GEN-LAST:event_jsonKeyCreateActionPerformed

    private void jsonTreeRenameNodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jsonTreeRenameNodeActionPerformed
        /**
         * Renames the selected node.
         */
        JSONObjectTreeNode node =
                (JSONObjectTreeNode) jsonTree.getLastSelectedPathComponent();

        String s = (String) JOptionPane.showInputDialog(this,
                "New node name:", node.getName());

        if (s != null && s.length() > 0) {
            if (!keyExistsInNode((JSONObjectTreeNode) node.getParent(), s)) {
                node.setName(s);
            } else {
                JOptionPane.showMessageDialog(JSONToolWindow.this,
                        "Key already exists.",
                        "Error renaming node.",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jsonTreeRenameNodeActionPerformed

    private void jsonTreeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jsonTreeMouseReleased
        /**
         * Show right-click menu for 'node' actions.
         */
        if (evt.getButton() == MouseEvent.BUTTON3) {
            JSONObjectTreeNode node = (JSONObjectTreeNode) jsonTree.getLastSelectedPathComponent();

            // Disable node rename / delete if the selected non-null node is the root.
            jsonTreeRenameNode.setEnabled(
                    node != null ? !node.isObjectRoot : false);
            jsonTreeRemoveNode.setEnabled(
                    node != null ? !node.isObjectRoot : false);

            // Disable node creation if the selected node is null.
            jsonTreeNewChildNode.setEnabled(node != null);

            // Show menu.
            jsonObjectTreeModify.show(jsonTree, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jsonTreeMouseReleased

    private void jsonTreeNewChildNodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jsonTreeNewChildNodeActionPerformed
        /**
         * Create a new child node.
         */
        JSONObjectTreeNode node = (JSONObjectTreeNode) jsonTree.getLastSelectedPathComponent();

        String s = (String) JOptionPane.showInputDialog(this,
                "New node name:", "");

        if (s != null && s.length() > 0) {
            if (!keyExistsInNode(node, s)) {
                JSONObjectTreeNode childNode = new JSONObjectTreeNode(s);
                node.add(childNode);
                ((DefaultTreeModel) jsonTree.getModel()).reload(node);

                // Move to child node.
                jsonTree.setSelectionPath(new TreePath(childNode.getPath()));
            } else {
                JOptionPane.showMessageDialog(JSONToolWindow.this,
                        "Key already exists.",
                        "Error creating child node.",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jsonTreeNewChildNodeActionPerformed

    private void jsonTreeRemoveNodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jsonTreeRemoveNodeActionPerformed
        /**
         * Removes the selected node and its children.
         */
        JSONObjectTreeNode node =
                (JSONObjectTreeNode) jsonTree.getLastSelectedPathComponent();

        // Move up to parent path.
        jsonTree.setSelectionPath(jsonTree.getSelectionPath().getParentPath());

        jsonRoot.remove(node);
        ((DefaultTreeModel) jsonTree.getModel()).reload();

        // Just to be sure, remove all the child nodes.
        node.removeAllChildren();
    }//GEN-LAST:event_jsonTreeRemoveNodeActionPerformed

    private void menuViewSelectionRawJSONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewSelectionRawJSONActionPerformed
        /**
         * Shows a dialog containing a raw text preview of the current JSON
         * file.
         */
        JSONObjectTreeNode selectedNode = (JSONObjectTreeNode)
                jsonTree.getLastSelectedPathComponent();

        if (selectedNode != null) {
            try {
                JSONRawTextDialog dialog = new JSONRawTextDialog(this,
                        exportJSONTree(selectedNode));
                dialog.setVisible(true);
            } catch (JSONException e) {
                // Uh. Something wrong happened.
                // We should /prrroooobably/ log this.
            }
        } else {
            JOptionPane.showMessageDialog(JSONToolWindow.this,
                    "No node selected.",
                    "Error showing raw JSON text.",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_menuViewSelectionRawJSONActionPerformed

    private void jsonKeyDuplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jsonKeyDuplicateActionPerformed
        /**
         * Duplicates the selected key / value pair from the right-click menu.
         */
        int targetRow = jsonObjectTable.getSelectedRow();

        if (targetRow >= 0) {
            int selectedRow = 
                        jsonObjectTable.convertRowIndexToModel(targetRow);
            
            String key = (String) 
                    jsonObjectTable.getModel().getValueAt(selectedRow, 0);
            String duplicateKey = (String) JOptionPane.showInputDialog(this,
                    "Duplicated key name:", key);

            if (duplicateKey != null) {
                if (!keyExistsInNode(workingJSONObject, duplicateKey)) {
                    Object dupe = workingJSONObject.keyValues.get(key);

                    putKeyValueAndRebuild(duplicateKey, dupe);
                } else {
                    JOptionPane.showMessageDialog(JSONToolWindow.this,
                            "Key already exists.",
                            "Error duplicating key.",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jsonKeyDuplicateActionPerformed

    private void menuFileSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileSaveAsActionPerformed
        int fileDialogReturnValue = FILE_DIALOG.showSaveDialog(this);

        if (fileDialogReturnValue == JFileChooser.APPROVE_OPTION) {
            File saveFile = FILE_DIALOG.getSelectedFile();

            saveFile(saveFile);
        }
    }//GEN-LAST:event_menuFileSaveAsActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuItem jsonKeyCreate;
    private javax.swing.JMenuItem jsonKeyDelete;
    private javax.swing.JMenuItem jsonKeyDuplicate;
    private javax.swing.JPopupMenu jsonKeyValueModify;
    private javax.swing.JSplitPane jsonMainPane;
    private javax.swing.JTable jsonObjectTable;
    private javax.swing.JPopupMenu jsonObjectTreeModify;
    private javax.swing.JScrollPane jsonTableScrollPane;
    private javax.swing.JTree jsonTree;
    private javax.swing.JMenuItem jsonTreeNewChildNode;
    private javax.swing.JMenuItem jsonTreeRemoveNode;
    private javax.swing.JMenuItem jsonTreeRenameNode;
    private javax.swing.JScrollPane jsonTreeScrollPane;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuFileOpen;
    private javax.swing.JMenuItem menuFileSave;
    private javax.swing.JMenuItem menuFileSaveAs;
    private javax.swing.JMenu menuView;
    private javax.swing.JMenuItem menuViewRawJSON;
    private javax.swing.JMenuItem menuViewSelectionRawJSON;
    // End of variables declaration//GEN-END:variables
}
