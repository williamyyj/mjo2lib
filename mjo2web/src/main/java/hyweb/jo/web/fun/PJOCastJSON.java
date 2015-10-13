/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyweb.jo.web.fun;

import hyweb.jo.IJOProcedure;
import hyweb.jo.org.json.JSONObject;
import hyweb.jo.util.JOTools;

/**
 *
 * @author William
 */
public class PJOCastJSON implements IJOProcedure<JSONObject, String> {

    public void exec(JSONObject row, String name) throws Exception {
        if (row != null && row.has(name)) {
            Object o = row.opt(name);
            if (o instanceof JSONObject) {
                row.put(name, o);
            } else if (o != null) {
                String text = o.toString();
                try {
                    JSONObject jo = JOTools.loadString(text);
                    row.put(name, jo);
                } catch (Exception e) {
                   row.remove(name);
                }
            }
        }
    }

}
