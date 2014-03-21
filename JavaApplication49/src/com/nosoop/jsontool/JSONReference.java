/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.jsontool;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class JSONReference {
    String name;
    
    JSONReference(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
