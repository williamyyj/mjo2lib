package hyweb.jo.web.filtr;

import hyweb.jo.log.JOLogger;
import hyweb.jo.org.json.JSONArray;
import hyweb.jo.org.json.JSONObject;
import hyweb.jo.util.JOCache;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class EncodingFilter implements Filter {

    private String base;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        JSONObject cfg = JOCache.load(new File(base, "/WEB-INF/res/app.json")).optJSONObject("filter.encoding");
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        request.setCharacterEncoding(cfg.optString("enc", "UTF-8"));
        String serverName = req.getServerName();
        String uri = req.getRequestURI();
  
        boolean isSSL = cfg.optBoolean("isSSL", false);
        if (isSSL) {
            boolean is_exclude = in_exclude(cfg.optJSONArray("exclude"), uri);
            if (is_exclude) {
                System.out.println("===== exclude uri : " + uri);
                JOLogger.info("===== exclude uri : " + uri);
            } else if("http".equalsIgnoreCase(req.getScheme())) {
                uri = (req.getQueryString() != null) ? uri + "?" + req.getQueryString() : uri;
                String url_string = "https://" + serverName + ":" + cfg.optInt("sslPort", 443) + uri;
                JOLogger.info("===== ssl redirect " + url_string);
                System.out.println("===== ssl redirect " + url_string);
                resp.sendRedirect(url_string);
                return;
            }
        }


        if (cfg.optBoolean("isShowParams")) {
            Enumeration e = request.getParameterNames();
            while (e.hasMoreElements()) {
                String name = (String) e.nextElement();
                JOLogger.info("=====req[" + name + "]=" + request.getParameter(name));
            }
        }
        long t = System.currentTimeMillis();
        System.out.println("===== begin uri ( "+t+"):" + uri);
        filterChain.doFilter(request, response);
        System.out.println("===== after uri ( "+t+"):" + uri);

    }

    public void init(FilterConfig filterConfig) throws ServletException {
        JOLogger.info("===== init encoding filter");
        base = filterConfig.getServletContext().getRealPath("");
        JOLogger.info("===== base : " + base);
    }

    public boolean in_exclude(JSONArray e_list, String uri) {
        if (e_list != null) {
            for (int i = 0; i < e_list.length(); i++) {
                String item = e_list.optString(i);
                if (uri.indexOf(item) >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void destroy() {
    }
}
