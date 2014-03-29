/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.jsontool;

import bundled.jsontool.org.json.JSONException;
import bundled.jsontool.org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A tree node containing a JSONObject's non-JSONObject key/value pairs.
 * (JSONObject values are stored as the node's children.)
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class JSONObjectTreeNode extends DefaultMutableTreeNode {
    boolean isObjectRoot;
    private String name;
    Map<String, Object> keyValues;

    JSONObjectTreeNode(String name, JSONObject object) throws JSONException {
        this(name, object, true);
    }

    /**
     * Generates a new JSONObjectTreeNode from a JSONObject.
     * 
     * @param name Name of the node (name of the JSONObject).
     * @param object The JSONObject data for the node.
     * @param isRoot Whether or not the instance is the tree root. Probably not.
     * @throws JSONException 
     */
    private JSONObjectTreeNode(String name, JSONObject object, boolean isRoot)
            throws JSONException {
        this.name = name;
        this.isObjectRoot = isRoot;

        buildKeyValues(object);
    }

    /**
     * Duplicates a node.
     *
     * @param nodeToBeDuplicated The JSONObjectTreeNode instance to be
     * duplicated.
     */
    JSONObjectTreeNode(JSONObjectTreeNode nodeToBeDuplicated) {
        this.isObjectRoot = nodeToBeDuplicated.isObjectRoot;
        this.name = nodeToBeDuplicated.name;
        this.keyValues = nodeToBeDuplicated.keyValues;
    }

    /**
     * Creates a new, empty, non-root node. This node should be attached to a
     * parent node.
     *
     * @param name The name for the node.
     */
    JSONObjectTreeNode(String name) {
        this.name = name;
        this.isObjectRoot = false;
        this.keyValues = new HashMap<>();
    }

    /**
     * Recursively builds a tree of JSONObjectTreeNodes using the specified
     * JSONObject.
     *
     * @param jsonData The JSONObject with values to add.
     * @throws JSONException when something done screwed itself up.
     */
    final void buildKeyValues(JSONObject object) throws JSONException {
        keyValues = new HashMap<>();

        for (String key : (Set<String>) object.keySet()) {
            if (object.optJSONObject(key) == null) {
                keyValues.put(key, object.get(key));
            } else {
                this.add(new JSONObjectTreeNode(key, object.getJSONObject(key),
                        false));
            }
            // else if jsonarray?
        }
    }

    /**
     * Renames the node if it is not the root node.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the node.
     *
     * @return This node's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns a String representation of the node -- its 'key' if not the root.
     *
     * @return
     */
    @Override
    public String toString() {
        return name;
    }
}
