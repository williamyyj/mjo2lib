package hyweb.jo.web.fun;

import hyweb.jo.IJOFunction;
import hyweb.jo.log.JOLogger;
import hyweb.jo.org.json.JSONObject;
import hyweb.jo.util.JOFunctional;
import hyweb.jo.JOProcConst;
import hyweb.jo.web.JOWebObject;
import javax.servlet.RequestDispatcher;

/**
 *
 * @author William
 */
public class FJOForward implements IJOFunction<Object, JOWebObject> {

    public Object exec(JOWebObject web) throws Exception {
        JSONObject proc = web.proc();
        String status = web.optString(JOProcConst.w_status);
        JSONObject forward = proc.optJSONObject(status);
        String url = null;
        String msg = null;
        String view = null;
        String query = null;
        String fld = null;
        if (forward != null) {
            url = forward.optString("url");
            msg = forward.optString("msg");
            fld = forward.optString("mfld");
            view = forward.optString("view", null);
            query = forward.optString("query");
            web.request().setAttribute("err_" + fld, msg);
        } else {
            url = proc.optString("url");
        }
        if ("dispatcher".equals(view)) {
            JOFunctional.exec2("web.dispatcher", web, url);
            return null;
        } else if ("alert".equals(view)) {
            JOFunctional.exec2("web.alert", web, url, msg, query);
            return null;
        } else if ("redirect".equals(view)) {
            JOFunctional.exec2("web.redirect", web, url, query);
            return null;
        }
        JOFunctional.exec2("web.forward", web, url);
        return null;
    }

    private void proc_dispatcher(JOWebObject web, String url) throws Exception {
        JOLogger.debug("===== chk proc_dispatcher : --->  " + url);
        RequestDispatcher dispatcher = web.application().getRequestDispatcher(url);
        dispatcher.forward(web.request(), web.response());
    }

}
