package com.nosoop.inputdialog;

import java.awt.Frame;
import javax.swing.JDialog;

/**
 * Creates a modal JDialog with the explicit ability to return a value of a
 * specified type. Subclasses should specify the type in the class definition.
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public abstract class ModalInputDialog<T> extends JDialog {

    /**
     * Creates a modal input dialog with a Frame as a parent.
     *
     * @param parent A Frame to attach this dialog to.
     */
    public ModalInputDialog(Frame parent) {
        super(parent, true);
    }

    /**
     * Gets a value from the dialog once it is closed.
     *
     * @return The return value from the dialog of a specified type.
     */
    public abstract T getReturnValue();
}
