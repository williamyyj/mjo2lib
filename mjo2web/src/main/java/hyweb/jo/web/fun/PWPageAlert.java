package hyweb.jo.web.fun;

import hyweb.jo.IJOProcedure;
import hyweb.jo.web.JOWebObject;
import javax.servlet.jsp.PageContext;

/**
 *
 * @author William
 */
public class PWPageAlert implements IJOProcedure<PageContext, JOWebObject> {

    public void exec(PageContext src, JOWebObject target) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void exec(PageContext pg, String msg, String url) throws Exception {
       // String url_string = url + "?ejo=" + JOTools.encode(web.params().toString());
        pg.getOut().println("<script type='text/javascript'>");
        pg.getOut().println("alert('" + msg + "');");
        pg.getOut().println("window.location='" + url + "';");
        pg.getOut().println("</script>");
    }
}
