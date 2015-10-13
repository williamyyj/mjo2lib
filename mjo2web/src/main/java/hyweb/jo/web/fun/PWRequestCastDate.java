/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyweb.jo.web.fun;

import hyweb.jo.IJOProcedure;
import hyweb.jo.util.TextUtils;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author William
 */
public class PWRequestCastDate implements IJOProcedure<HttpServletRequest, String[]> {

    public void exec(HttpServletRequest request, String[] p) throws Exception {
        if (p != null) {
            for (String n : p) {
                Object v = request.getAttribute(n);
                if (v != null) {
                    request.setAttribute(n, TextUtils.toDate(v));
                }
            }
        }
    }

}
