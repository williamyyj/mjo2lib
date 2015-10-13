package hyweb.jo.web.fun;

import hyweb.jo.org.json.JSONArray;
import hyweb.jo.org.json.JSONObject;
import hyweb.jo.JOProcConst;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;

/**
 * @author William
 */
public class JOFormParams extends JOWebParams {

    @Override
    public JSONObject exec(JSONObject web) throws Exception {
        JSONObject params = new JSONObject();
        HttpServletRequest req = (HttpServletRequest) web.opt(JOProcConst.w_req);
        Enumeration<String> names = req.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String[] values = req.getParameterValues(name); // 利用 XSSRequestWrapper 
            if (values.length == 1) {
                String v = esapi_get_parameter(values[0]);
                params.put(name, v);
            } else {
                JSONArray arr = new JSONArray();
                for (String value : values) {
                    String v = esapi_get_parameter(value);
                    arr.put(v);

                }
                params.put(name, arr);
            }
        }
        return params;
    }

}
