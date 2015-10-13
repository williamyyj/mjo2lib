/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyweb.jo.web.fun;

import hyweb.jo.IJOProcedure;
import hyweb.jo.org.json.JSONObject;
import hyweb.jo.util.TextUtils;

/**
 *
 * @author William
 */
public class PJOCastDate  implements IJOProcedure<JSONObject, String[]>{

    public void exec(JSONObject jo, String[] p) throws Exception {
          if (p != null) {
            for (String n : p) {
                Object v = jo.opt(n);
                if (v != null) {
                    jo.put(n, TextUtils.toDate(v));
                }
            }
        }
    }
    
}
