/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyweb.jo.web.fun;

import hyweb.jo.IJOBiFunction;
import hyweb.jo.IJOProcedure;
import hyweb.jo.org.json.JSONObject;
import hyweb.jo.util.FJO2Map;
import hyweb.jo.util.JOTools;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * @author William
 */
public class FEJO2Request implements IJOBiFunction<JSONObject, HttpServletRequest, String[]> {

    IJOProcedure<JSONObject, HttpServletRequest> p1 = new PJO2WRequest();
    IJOProcedure<HttpServletRequest, String[]> p2 = new PWRequestCastDate();

    public JSONObject exec(HttpServletRequest request, String... p) throws Exception {
        String ejo = request.getParameter("ejo");
        if (ejo != null) {
            JSONObject jo = JOTools.decode_jo(ejo);
            Map<String, Object> m = FJO2Map.toMap(jo);
            p1.exec(jo, request);
            p2.exec(request, p);
            request.setAttribute("ejo", m);
            return jo;
        } else {
            return null;
        }
    }

}
