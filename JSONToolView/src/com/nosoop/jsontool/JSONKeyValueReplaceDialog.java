/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.jsontool;

import com.nosoop.inputdialog.ModalInputDialog;
import java.util.regex.Pattern;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class JSONKeyValueReplaceDialog
        extends ModalInputDialog<JSONKeyValueReplaceDialog.ReturnValue> {
    /**
     * Struct containing operation data.
     */
    public class ReturnValue {
        Operation operation;
        String replacementSearch;
        String replacementString;
        
        boolean isRegex;
    }
    
    public enum Operation {
        KEY_REPLACE, CANCEL;
    }
    
    ReturnValue returnValue;

    /**
     * Creates new form JSONKeyValueReplaceDialog
     */
    public JSONKeyValueReplaceDialog(java.awt.Frame parent) {
        super(parent);
        
        returnValue = new ReturnValue();
        returnValue.operation = Operation.CANCEL;
        
        initComponents();
    }

    @Override
    public ReturnValue getReturnValue() {
        return returnValue;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        tabReplaceKey = new javax.swing.JPanel();
        fieldKeyName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        fieldKeyReplace = new javax.swing.JTextField();
        buttonCancel = new javax.swing.JButton();
        buttonReplaceKey = new javax.swing.JButton();
        checkRegexSearch = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Key name:");

        jLabel2.setText("Replace with:");

        buttonCancel.setText("Cancel");
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonReplaceKey.setText("Replace");
        buttonReplaceKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonReplaceKeyActionPerformed(evt);
            }
        });

        checkRegexSearch.setText("Regular expression search ($1, $2, $3, ...)");

        javax.swing.GroupLayout tabReplaceKeyLayout = new javax.swing.GroupLayout(tabReplaceKey);
        tabReplaceKey.setLayout(tabReplaceKeyLayout);
        tabReplaceKeyLayout.setHorizontalGroup(
            tabReplaceKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabReplaceKeyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabReplaceKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fieldKeyName)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabReplaceKeyLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                        .addGap(183, 183, 183))
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabReplaceKeyLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonReplaceKey)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonCancel))
                    .addComponent(fieldKeyReplace)
                    .addComponent(checkRegexSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabReplaceKeyLayout.setVerticalGroup(
            tabReplaceKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabReplaceKeyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldKeyName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldKeyReplace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkRegexSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addGroup(tabReplaceKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCancel)
                    .addComponent(buttonReplaceKey))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Key", tabReplaceKey);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonReplaceKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonReplaceKeyActionPerformed
        returnValue.operation = Operation.KEY_REPLACE;
        returnValue.replacementSearch = fieldKeyName.getText();
        returnValue.replacementString = fieldKeyReplace.getText();
        returnValue.isRegex = checkRegexSearch.isSelected();
        
        this.setVisible(false);
    }//GEN-LAST:event_buttonReplaceKeyActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonReplaceKey;
    private javax.swing.JCheckBox checkRegexSearch;
    private javax.swing.JTextField fieldKeyName;
    private javax.swing.JTextField fieldKeyReplace;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel tabReplaceKey;
    // End of variables declaration//GEN-END:variables
}
