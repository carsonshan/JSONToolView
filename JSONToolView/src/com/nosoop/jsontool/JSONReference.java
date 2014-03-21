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

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class JSONReference {
    String name;
    Map<String, Object> keyValues;
    
    JSONReference(String name, JSONObject object) throws JSONException {
        this.name = name;
        keyValues = new HashMap<>();
        
        for (String key : (Set<String>) object.keySet()) {
            if (object.optJSONObject(key) == null) {
                keyValues.put(key, object.get(key));
            }
        }
    }
    
    @Override
    public String toString() {
        return name;
    }
}
