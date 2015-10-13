/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyweb.jo.web.fun;

import hyweb.jo.IJOProcedure;
import hyweb.jo.org.json.JSONArray;
import hyweb.jo.org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author William
 *
 * JSONObject setting request
 */
public class PJO2WRequest implements IJOProcedure<JSONObject, HttpServletRequest> {

    public void exec(JSONObject jo, HttpServletRequest request) throws Exception {
        if (jo != null) {
            JSONArray names = jo.names();
            for (int i = 0; i < names.length(); i++) {
                String name = names.optString(i);
                request.setAttribute(name, jo.opt(name));
            }
        }
    }

}
