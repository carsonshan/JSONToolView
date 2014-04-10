/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.jsontool;

import java.awt.EventQueue;
import java.io.File;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
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
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JSONToolWindow window = new JSONToolWindow();

                if (args.length > 0) {
                    /**
                     * TODO Parse file argument more clearly.
                     *
                     * Check that it can actually be a file path and isn't just
                     * string data.
                     */
                    File f = new File(args[0]);

                    window.loadFile(f);
                }

                window.setVisible(true);
            }
        });
    }
}
