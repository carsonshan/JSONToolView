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

    private JSONObjectTreeNode(String name, JSONObject object, boolean isRoot)
            throws JSONException {
        this.name = name;
        this.isObjectRoot = isRoot;

        buildKeyValues(object);
    }

    final void buildKeyValues(JSONObject object) throws JSONException {
        keyValues = new HashMap<>();

        for (String key : (Set<String>) object.keySet()) {
            if (object.optJSONObject(key) == null) {
                keyValues.put(key, object.get(key));
            } else {
                this.add(new JSONObjectTreeNode(key, object.getJSONObject(key),
                        false));
            }
        }
    }

    /**
     * Renames the node if it is not the root node.
     *
     * @param name
     */
    public void rename(String name) {
        if (!isObjectRoot) {
            this.name = name;
        }
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
