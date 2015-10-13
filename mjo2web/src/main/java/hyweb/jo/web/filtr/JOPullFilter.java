package hyweb.jo.web.filtr;

import hyweb.jo.data.JOHttp;
import hyweb.jo.log.JOLogger;
import hyweb.jo.org.apache.log4j.Logger;
import hyweb.jo.org.json.JSONObject;
import hyweb.jo.util.JOCache;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author william
 */
public class JOPullFilter implements Filter {

    private static Logger log = JOLogger.getLogger(JOPullFilter.class);
    private File base;
    private ServletContext context;
    private String context_path;

    public void init(FilterConfig filterConfig) throws ServletException {
        context = filterConfig.getServletContext();
        context_path = context.getContextPath();
        base = new File(context.getRealPath(""));
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            JSONObject app = JOCache.load(new File(base, "WEB-INF/res/app.json"));
            JSONObject cfg = app.optJSONObject("filter.pull");
            String url = cfg.optString("src.host") + "/" + cfg.optString("src.context");
            String path = req.getRequestURI().substring(context_path.length());
            url += path;
            log.debug("check url : " + url);
            File fs = new File(base, cfg.optString("path.cache") + path);
            if (valid_data(url, fs, req, resp)) {
                String t_url = "/" + cfg.optString("path.cache") + path;
                req.getRequestDispatcher(t_url).forward(request, response);
            } else {
                chain.doFilter(request, response);
            }

        } catch (Exception ex) {
            throw new ServletException(ex);
        }

    }

    public boolean valid_data(String path, File fs, HttpServletRequest req, HttpServletResponse resp) {
        try {
            // will using header check 
            if (fs.exists() && fs.length() > 0) {
                return true;
            }
            mkdir(fs);
            JSONObject jq = new JSONObject();
            jq.put("$url", path);
            byte[] data = JOHttp.bytes(jq, "UTF-8");
            FileOutputStream fos = new FileOutputStream(fs);
            try {
                fos.write(data);
                fos.flush();
            } finally {
                fos.close();
            }
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    public void show_request(HttpServletRequest req) {
        System.out.println("===== req " + req.getMethod());
        Enumeration<String> e = req.getHeaderNames();
        System.out.println(System.currentTimeMillis());
        while (e.hasMoreElements()) {
            String name = e.nextElement();
            System.out.println("===== req " + name + req.getHeader(name));
        }
    }

    public boolean mkdir(File f) {
        File p = f.getParentFile();
        if (!p.exists()) {
            return p.mkdirs();
        }
        return true;
    }

    public void destroy() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

}
