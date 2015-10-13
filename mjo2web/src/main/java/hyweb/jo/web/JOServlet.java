package hyweb.jo.web;

import hyweb.jo.JOProcConst;
import hyweb.jo.IJOFunction;
import hyweb.jo.log.JOLogger;
import hyweb.jo.org.json.JSONObject;
import hyweb.jo.web.fun.FJOForward;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author William
 */
public class JOServlet extends HttpServlet {

    private FJOForward forward = new FJOForward();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        JOLogger.info("===== start init Servlet ");
        String pid = config.getInitParameter("pid");
        this.getServletContext().setAttribute(JOProcConst.w_pid, pid);
        JOLogger.info("===== pid : " + pid);
        JOLogger.info("===== End init Servlet");

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JOWebObject web = null;
        JOLogger.debug("===== start proc" + hyweb.jo.db.DB.info());
        try {
            web = new JOWebObject(getServletContext(), request, response);
            web.put(JOProcConst.w_status, "success");
            System.out.println("====== check proc :"+web.proc());
            proc("before", web);
            proc("process", web);
            proc("after", web);

            forward.exec(web);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (web != null) {
                web.release();
            }
        }
        JOLogger.debug("===== end proc" + hyweb.jo.db.DB.info());
    
    }

    private void proc(String fid, JOWebObject web) throws Exception {
        JSONObject proc = web.proc();
        String status = web.optString(JOProcConst.w_status);
        if ("success".equals(status) && !(proc.opt(fid)==null)) {
            JOLogger.debug("===== proc " + fid + " : " + proc.optString(fid));
            newInstance(proc.optString(fid)).exec(web);
        }
    }

    private IJOFunction<Object, JOWebObject> newInstance(String classId) throws Exception {
        return (IJOFunction<Object, JOWebObject>) Class.forName(classId).newInstance();
    }

}
