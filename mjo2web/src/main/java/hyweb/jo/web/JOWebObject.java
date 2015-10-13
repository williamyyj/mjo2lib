package hyweb.jo.web;

import hyweb.jo.JOProcObject;
import hyweb.jo.JOProcConst;
import hyweb.jo.JOConst;
import hyweb.jo.org.json.JSONObject;
import hyweb.jo.util.JOCache;
import hyweb.jo.web.fun.JOWebParams;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

/**
 * @author William
 */
public class JOWebObject extends JOProcObject {

    private String pid;

    public JOWebObject(PageContext pg) throws Exception {
        this(null, pg);
    }

    public JOWebObject(String pid, PageContext pg) throws Exception {
        super("");
        put(JOProcConst.w_pg, pg);
        __init(pid, pg.getServletContext(), pg.getRequest(), pg.getResponse());
    }

    public JOWebObject(ServletContext app, ServletRequest request, ServletResponse response) throws Exception {
        super("");
        __init(null, app, request, response);
    }

    private void __init(String pid, ServletContext app, ServletRequest request, ServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        if (pid == null) {
            pid = (String) app.getAttribute(JOProcConst.w_pid);
        }
        this.pid = (pid != null) ? pid : "";
        put(JOConst.root, app.getRealPath("WEB-INF/"));
        put(JOConst.base, app.getRealPath("WEB-INF/" + pid));
        put(JOProcConst.w_app, app);
        put(JOProcConst.w_req, request);
        put(JOProcConst.w_resp, response);
        String upload = System.getProperty("catalina.base",app.getRealPath("public"))+pid;
        put(JOProcConst.w_upload,upload);
        
        HttpServletRequest req = (HttpServletRequest) request;
        put(JOProcConst.w_session, req.getSession(true));
        String cp = req.getContextPath();
        request.setAttribute("$cp", cp);
        String path = pid + "/proc" + req.getRequestURI().replace(cp, "");
        System.out.println("===== path : " + path);
        path = path.replace(".do", ".json");
        File proc = new File(app.getRealPath("WEB-INF"), path);
        if (proc.exists()) {
            put(JOProcConst.w_proc, JOCache.load(proc));
        } else {
            put(JOProcConst.w_proc, new JSONObject());
        }
        put(JOProcConst.wp, new JOWebParams().exec(this));
       // JOFunctional.exec("web.ejo2params", this);
    }

    public Object session(String key) {
        return ((HttpSession) opt(JOProcConst.w_session)).getAttribute(key);
    }

    public void session(String key, Object value) {
        ((HttpSession) opt(JOProcConst.w_session)).setAttribute(key, value);
    }

    public HttpServletRequest request() {
        return (HttpServletRequest) opt(JOProcConst.w_req);
    }

    public ServletContext application() {
        return (ServletContext) opt(JOProcConst.w_app);
    }

    public JSONObject proc() {
        JSONObject params = this.optJSONObject(JOProcConst.wp);
        JSONObject proc = this.optJSONObject(JOProcConst.w_proc);
        if (params != null && params.has("act")) {
            JSONObject act = proc.optJSONObject(params.optString("act"));
            return (act != null) ? act : proc;
        } else {
            params.put("act", "default");
            return proc.optJSONObject("default");
        }
    }

    public HttpServletResponse response() {
        return (HttpServletResponse) this.opt(JOProcConst.w_resp);
    }

    public void forward(String url) throws ServletException, IOException {
        request().getRequestDispatcher(url).forward(request(), response());
    }

    @Override
    public Object get(int fld, String name, Object dv) {
        Object o = null;
        switch (fld) {
            case p_self:
                o = opt(name);
                return (o != null) ? o : dv;
            case p_params:
                o = params().opt(name);
                return (o != null) ? o : dv;
            case p_request:
                o = request().getAttribute(name);
                return (o != null) ? o : dv;
            case p_session:
                o = this.session(name);
                return (o != null) ? o : dv;
            case p_app:
                o = this.application().getAttribute(name);
                return (o != null) ? o : dv;
        }
        return dv;
    }

    @Override
    public Object set(int fld, String name, Object value) {
        switch (fld) {
            case p_self :
                put(name,value);
                return null;
            case p_params:
                params().put(name, value);
                return null;
            case p_request:
                request().setAttribute(name, value);
                return null;
            case p_session:
                session(name, value);
                return null;
            case p_app:
                application().setAttribute(name, value);
                return null;
        }
        return null;
    }

}
