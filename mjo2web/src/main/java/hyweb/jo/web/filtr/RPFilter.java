package hyweb.jo.web.filtr;

import hyweb.jo.log.JOLogger;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.ResourceBundle;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/*
 * RPFilter 程式 : 代理後台程式
 * @author William Yin 
 * 
 */
public class RPFilter implements Filter {

    private final static ResourceBundle rb = ResourceBundle.getBundle("proxy");
    //private static java.util.concurrent.Executor tp;
    private static int bufferSize = 4096;
    private String exclude;
    private int connectTimeout = 3000;
    private int readTimeout = 3000;
    protected ServletContext _context;

    protected HashSet skipHdrs = new HashSet();

    {
        skipHdrs.add("proxy-connection");
        skipHdrs.add("connection");
        skipHdrs.add("keep-alive");
        skipHdrs.add("transfer-encoding");
        skipHdrs.add("te");
        skipHdrs.add("trailer");
        skipHdrs.add("proxy-authorization");
        skipHdrs.add("proxy-authenticate");
        skipHdrs.add("upgrade");
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig cfg) throws ServletException {
        JOLogger.debug("===== init rp proxy ");
        exclude = rb.getString("exclude");
        exclude = (exclude != null) ? exclude : "";
        _context = cfg.getServletContext();
        readTimeout = (rb.getString("readTimeout") == null) ? readTimeout : Integer.parseInt(rb.getString("readTimeout"));
        JOLogger.debug("===== readTimeout : " + readTimeout);
        connectTimeout = (rb.getString("connectTimeout") == null) ? connectTimeout : Integer.parseInt(rb.getString("connectTimeout"));
        JOLogger.debug("===== connectTimeout : " + connectTimeout);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {

    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        long ts = System.nanoTime();
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession();
        String sid = session.getId();
        String serverName = null;
        String target = null;
        int serverPort = 0;
        String schema = null;
        String check_uri = request.getRequestURI();
        String postfix = (check_uri.indexOf('.') >= 0) ? check_uri.substring(check_uri.indexOf('.') + 1) : null;
        if (postfix != null && exclude.indexOf(postfix) >= 0) {
            chain.doFilter(request, response);
        } else if ("CONNECT".equalsIgnoreCase(request.getMethod())) {
            handleConnect(request, response);
        } else {
            String uri = request.getRequestURI();
            String remoteAddr = request.getRemoteAddr();
            schema = request.getScheme();
            serverName = request.getServerName();
            serverPort = request.getServerPort();
            if (request.getQueryString() != null) {
                uri += "?" + request.getQueryString();
            }
            URL url = this.proxyHttpURL(request);
            target = this.getTarget(request);
            JOLogger.debug("===== rp start : " + url);

            URLConnection connection = url.openConnection();
            connection.setAllowUserInteraction(false);

            // Set method
            HttpURLConnection http = null;
            if (connection instanceof HttpURLConnection) {
                http = (HttpURLConnection) connection;
                http.setRequestMethod(request.getMethod());
                http.setInstanceFollowRedirects(false);

                http.setConnectTimeout(connectTimeout);
                http.setReadTimeout(readTimeout);

            }

            //check connection header
            String connectionHdr = request.getHeader("Connection");
            if (connectionHdr != null) {
                connectionHdr = connectionHdr.toLowerCase();
                if (connectionHdr.equals("keep-alive") || connectionHdr.equals("close")) {
                    connectionHdr = null;
                }
            }

            // copy headers
            // boolean xForwardedFor=false;
            boolean hasContent = false;

            //處理關貿客制化header
            SHBean sh = (SHBean) session.getAttribute("@sh");
            if (sh == null) {
                sh = new SHBean();
                session.setAttribute("@sh", sh);
            }

            Enumeration enm = request.getHeaderNames();
            while (enm.hasMoreElements()) {
                // TODO could be better than this!
                String hdr = (String) enm.nextElement();
                String lhdr = hdr.toLowerCase();
                if (skipHdrs.contains(lhdr)) {
                    continue;
                }
                //if (connectionHdr!=null && connectionHdr.indexOf(lhdr)>=0) continue;
                if ("content-type".equals(lhdr)) {
                    hasContent = true;
                }

                Enumeration vals = request.getHeaders(hdr);
                while (vals.hasMoreElements()) {
                    String val = (String) vals.nextElement();

                    if (sh.binding(hdr, val)) {
                        //} else if(hdr.startsWith("If")){ // remvoe 304 
                    } else if ("cookie".equalsIgnoreCase(hdr)) {
                        JOLogger.debug("===== proxyTo header : " + hdr + ":" + val);
                        val = val.replace("JSESSIONID", "SERVER_SID");
                        val = val.replace("PROXY_" + getKey(request), "JSESSIONID");
                        JOLogger.debug("===== proxyTo after : " + hdr + ":" + val);
                        connection.addRequestProperty(hdr, val);
                    } else if ("Referer".equalsIgnoreCase(hdr)) {

                        JOLogger.debug("===== proxyTo header : " + hdr + ":" + val);
                        URL loc = new URL(val);
                        URL newLoc = null;
                        if (loc.getQuery() == null) {
                            newLoc = new URL(url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + url.getPath());
                        } else {
                            newLoc = new URL(url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + url.getPath() + "?" + loc.getQuery());
                        }
                        JOLogger.debug("===== proxyTo after  : " + hdr + ":" + newLoc.toString());
                        connection.addRequestProperty(hdr, newLoc.toString());

                    } else if ("host".equalsIgnoreCase(hdr)) {

                        JOLogger.debug("===== proxyTo header : " + hdr + ":" + val);
                        // 修正為了給AP判斷來源server
                        connection.addRequestProperty("rphost", val);
                        JOLogger.debug("===== proxyTo new header : rphost:" + val);
                        val = url.getHost() + ":" + url.getPort();
                        JOLogger.debug("===== proxyTo after  : " + hdr + ":" + val);
                        connection.addRequestProperty(hdr, val);

                    } else if (val != null) {
                        JOLogger.debug("===== proxyTo header : " + hdr + ":" + val);
                        connection.addRequestProperty(hdr, val);
                        //xForwardedFor|="X-Forwarded-For".equalsIgnoreCase(hdr);
                    }

                }
            }
            sh.setHeaders(connection);

           // Proxy headers
            // connection.setRequestProperty("Via","1.1 (jetty)");
            // if (!xForwardedFor)
            // {
            //     connection.addRequestProperty("X-Forwarded-For",request.getRemoteAddr());
            //     connection.addRequestProperty("X-Forwarded-Proto",request.getScheme());
            //     connection.addRequestProperty("X-Forwarded-Host",request.getServerName());
            //     connection.addRequestProperty("X-Forwarded-Server",request.getLocalName());
            // }
            // a little bit of cache control
            String cache_control = request.getHeader("Cache-Control");
            if (cache_control != null
                    && (cache_control.indexOf("no-cache") >= 0
                    || cache_control.indexOf("no-store") >= 0)) {
                connection.setUseCaches(false);
            }

            // customize Connection
            JOLogger.debug("=====backend request: " + url + " time:" + (System.nanoTime() - ts) / 1E9);
            try {
                connection.setDoInput(true);

                // do input thang!getTarget
                InputStream in = request.getInputStream();

                if (hasContent) {
                    connection.setDoOutput(true);
                    connection.getOutputStream();
                    OutputStream p_out = connection.getOutputStream();
                    copy(in, p_out);
                    p_out.flush();
                    p_out.close();
                }
                in.close();

                // Connect                
                connection.connect();
            } catch (Exception e) {
                JOLogger.debug("rp", e);
            }

            JOLogger.debug("===== backend response: " + url + " time:" + (System.nanoTime() - ts) / 1E9);

            InputStream proxy_in = null;

            // handler status codes etc.
            int code = http.getResponseCode();
            JOLogger.debug("===== getResponseCode: " + url + " time:" + (System.nanoTime() - ts) / 1E9);

            if (code == 404 || code == 500) {
                proxy_in = http.getErrorStream();
            } else {
                try {
                    proxy_in = connection.getInputStream();
                } catch (Exception e) {
                    JOLogger.debug("rp stream", e);
                    proxy_in = http.getErrorStream();
                }
            }
            response.setStatus(code);

            // clear response defaults.
            response.setHeader("Date", null);
            response.setHeader("Server", null);
            // set response headers

            int h = 0;
            String hdr = connection.getHeaderFieldKey(h);
            String val = connection.getHeaderField(h);
            while (hdr != null || val != null) {
                JOLogger.debug("===== url response " + hdr + ":" + val);
                String lhdr = hdr != null ? hdr.toLowerCase() : null;
                if ("Location".equalsIgnoreCase(hdr)) {
                    if (val.startsWith(target)) {
                        URL loc = new URL(val);
                        URL newLoc = null;
                        if (loc.getQuery() == null) {
                            newLoc = new URL(schema + "://" + serverName + ":" + serverPort + loc.getPath());
                        } else {
                            newLoc = new URL(schema + "://" + serverName + ":" + serverPort + loc.getPath() + "?" + loc.getQuery());
                        }
                        JOLogger.debug("===== Location after : " + hdr + ":" + newLoc.toString());
                        response.addHeader(hdr, newLoc.toString());
                    } else {
                        response.addHeader(hdr, val);
                    }
                } else if ("Set-Cookie".equalsIgnoreCase(hdr)) {
                    val = val.replace("JSESSIONID", "PROXY_" + getKey(request));
                    JOLogger.debug("===== replase : " + hdr + ":" + val);
                    response.addHeader(hdr, val);
                } else if (hdr != null && val != null && !skipHdrs.contains(lhdr)) {
                    response.addHeader(hdr, val);
                }

                h++;
                hdr = connection.getHeaderFieldKey(h);
                val = connection.getHeaderField(h);
            }

            JOLogger.debug("===== getContentLength :" + connection.getContentLength());
            JOLogger.debug("===== rp response start :" + (System.nanoTime() - ts) / 1E9);
            ServletOutputStream sos = response.getOutputStream();
            //if (proxy_in!=null){
            if (proxy_in != null && code != 304 && code != 302) {
                copy(proxy_in, sos);
                proxy_in.close();
            } else {
                //proxy_in.close();
            }
            sos.flush();
            sos.close();
            http.disconnect();
            JOLogger.debug("===== rp end : target " + url + " time:" + (System.nanoTime() - ts) / 1E9);
            JOLogger.debug("============================================================================");

        }
    }

    /*
     *	@param	request  HttpServletRequest
     *	@return 代理的 URL
     */
    protected URL proxyHttpURL(HttpServletRequest request) throws MalformedURLException {
        URL url = null;
        String context = _context.getContextPath();
        System.out.println("===== context : " + context);
        String uri = null;
        if (request.getQueryString() != null) {
            uri = request.getRequestURI() + "?" + request.getQueryString();
        } else {
            uri = request.getRequestURI();
        }

        String[] uri_items = uri.split("/");
        int idx = (context.length() == 0) ? 1 : 2;
        if (uri_items.length >= idx) {
	    		// [0]/[1]ap 
            // [0]/[1]context/[2]ap
            String key = "/" + uri_items[idx];
            String target = rb.getString(key);
            if (target != null && target.length() > 0) {
                String prefix = (context.length() == 0) ? "/" + uri_items[1] : "/" + uri_items[1] + "/" + uri_items[2];
                url = new URL(target + uri.substring(prefix.length()));
            }
        }
        JOLogger.debug("===== proxy url : " + url);
        return url;
    }

    /*
     *	@param	request  HttpServletRequest
     *	@return 代理的 URL字串
     */
    protected String getTarget(HttpServletRequest request) throws MalformedURLException {
        String context = _context.getContextPath();
        String uri = request.getRequestURI();

        String[] uri_items = uri.split("/");
        int idx = (context.length() == 0) ? 1 : 2;
        if (uri_items.length >= idx) {
	    		// [0]/[1]ap 
            // [0]/[1]context/[2]ap
            String key = "/" + uri_items[idx];
            return rb.getString(key);
        }
        return null;
    }

    /*
     *	@param	request  HttpServletRequest
     *	@return 取得proxy.properties 對應的key
     */
    protected String getKey(HttpServletRequest request) throws MalformedURLException {
        String context = _context.getContextPath();
        String uri = request.getRequestURI();

        String[] uri_items = uri.split("/");
        int idx = (context.length() == 0) ? 1 : 2;
        if (uri_items.length >= idx) {
	    		// [0]/[1]ap 
            // [0]/[1]context/[2]ap
            return uri_items[idx];
        }
        return null;
    }

    /*
     * 處理Connect protocol
     * @param	request  HttpServletRequest
     * @param HttpServletResponse response
     */
    public void handleConnect(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        JOLogger.debug("===== process connect start =====");
        String uri = request.getRequestURI();

        String port = "";
        String host = "";

        int c = uri.indexOf(':');
        if (c >= 0) {
            port = uri.substring(c + 1);
            host = uri.substring(0, c);
            if (host.indexOf('/') > 0) {
                host = host.substring(host.indexOf('/') + 1);
            }
        }

        InetSocketAddress inetAddress = new InetSocketAddress(host, Integer.parseInt(port));

        InputStream in = request.getInputStream();
        OutputStream out = response.getOutputStream();

        Socket socket = new Socket(inetAddress.getAddress(), inetAddress.getPort());

        response.setStatus(200);
        response.setHeader("Connection", "close");
        response.flushBuffer();

        //tp().execute(new IO.Job(socket.getInputStream(),out));
        copy(socket.getInputStream(), out);
        copy(in, socket.getOutputStream());
        JOLogger.debug("===== process connect end =====");
    }

//		public Executor tp(){
//			if(tp==null){
//				tp = Executors.newFixedThreadPool(16);
//			}
//			return tp;
//		}
    /*
     * 串流複制
     * @param in input stream
     * @param out output stream 
     * 
     */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte buffer[] = new byte[bufferSize];
        int len = bufferSize;
        while ((len = in.read(buffer, 0, bufferSize)) != -1) {
            out.write(buffer, 0, len);
        }
    }

}
