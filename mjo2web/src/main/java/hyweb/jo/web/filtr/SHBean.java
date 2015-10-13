package hyweb.jo.web.filtr;

import java.net.URLConnection;

// 未來要可轉成 
// InfoUser
// Member
// Activitymember
public class SHBean {

    private String serial; // 憑證序號
    private String ban; // 統一編號
    private String pid; // 身份字號
    private String oid; // 機關代碼 
    private String userID; //使用者代碼 
    private String password; //使用者密碼
    private String userType; //登入者身份
    private String serviceType; //服務種類
    private String isMaster; //憑證是否為正卡
    private String loginType; //登入方式 
    private String loginWay; //登入介面 (IE, 手機) [W-網頁,P-手機]
    private String signData;
    public int error = 0;

    //整合 session
    private String sessionID;
    // for GIP 整合到 GIP
    private Object gipInfo;

    public SHBean() {

    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getBan() {
        return ban;
    }

    public void setBan(String ban) {
        this.ban = ban;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getIsMaster() {
        return isMaster;
    }

    public void setIsMaster(String isMaster) {
        this.isMaster = isMaster;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getLoginWay() {
        return loginWay;
    }

    public void setLoginWay(String loginWay) {
        this.loginWay = loginWay;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public Object getGipInfo() {
        return this.gipInfo;
    }

    public void setGipInfo(Object gipInfo) {
        this.gipInfo = gipInfo;
    }

    public String getSignData() {
        return this.signData;
    }

    public void setSignData(String signData) {
        this.signData = signData;
    }

	// 代登時使用
        /*
     public void setHeaders(PostMethod pm){
     if(serial!=null) pm.setRequestHeader(new Header("serial",serial)) ; // 憑證序號
     if(ban!=null) pm.setRequestHeader(new Header("ban",ban)) ; // 統一編號
     if(pid!=null) pm.setRequestHeader(new Header("pid",pid)) ; // 身份字號
     if(oid!=null) pm.setRequestHeader(new Header("oid",oid)) ; // 機關代碼 
     if(userID!=null) pm.setRequestHeader(new Header("userID",userID)) ; //使用者代碼 
     if(password!=null) pm.setRequestHeader(new Header("password",password)) ; //使用者密碼
     if(userType!=null) pm.setRequestHeader(new Header("userType",userType)) ; //登入者身份
     if(serviceType!=null) pm.setRequestHeader(new Header("serviceType",serviceType)) ; //服務種類
     if(isMaster!=null) pm.setRequestHeader(new Header("isMaster",isMaster)) ; //憑證是否為正卡
     if(loginType!=null) pm.setRequestHeader(new Header("loginType",loginType)) ; //登入方式 
     if(loginWay!=null) pm.setRequestHeader(new Header("loginWay",loginWay)) ; //登入介面 (IE, 手機) [W-網頁,P-手機]
     }
     */
    public void setHeaders(URLConnection conn) {
        if (serial != null) {
            conn.addRequestProperty("serial", serial); // 憑證序號
        }
        if (ban != null) {
            conn.addRequestProperty("ban", ban); // 統一編號
        }
        if (pid != null) {
            conn.addRequestProperty("pid", pid); // 身份字號
        }
        if (oid != null) {
            conn.addRequestProperty("oid", oid); // 機關代碼 
        }
        if (userID != null) {
            conn.addRequestProperty("userID", userID); //使用者代碼 
        }
        if (password != null) {
            conn.addRequestProperty("password", password); //使用者密碼
        }
        if (userType != null) {
            conn.addRequestProperty("userType", userType); //登入者身份
        }
        if (serviceType != null) {
            conn.addRequestProperty("serviceType", serviceType); //服務種類
        }
        if (isMaster != null) {
            conn.addRequestProperty("isMaster", isMaster); //憑證是否為正卡
        }
        if (loginType != null) {
            conn.addRequestProperty("loginType", loginType); //登入方式 
        }
        if (loginWay != null) {
            conn.addRequestProperty("loginWay", loginWay); //登入介面 (IE, 手機) [W-網頁,P-手機]
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (serial != null) {
            sb.append("serial:" + serial + ","); // 憑證序號
        }
        if (ban != null) {
            sb.append("ban:" + ban + ","); // 統一編號
        }
        if (pid != null) {
            sb.append("pid:" + pid + ","); // 身份字號
        }
        if (oid != null) {
            sb.append("oid:" + oid + ","); // 機關代碼 
        }
        if (userID != null) {
            sb.append("userID:" + userID + ","); //使用者代碼 
        }
        if (password != null) {
            sb.append("password:" + password + ","); //使用者密碼
        }
        if (userType != null) {
            sb.append("userType:" + userType + ","); //登入者身份
        }
        if (serviceType != null) {
            sb.append("serviceType:" + serviceType + ","); //服務種類
        }
        if (isMaster != null) {
            sb.append("isMaster:" + isMaster + ","); //憑證是否為正卡
        }
        if (loginType != null) {
            sb.append("loginType:" + loginType + ","); //登入方式 
        }
        if (loginWay != null) {
            sb.append("loginWay:" + loginWay + ","); //登入介面 (IE, 手機) [W-網頁,P-手機]
        }
        sb.append(']');
        return sb.toString();
    }

    public boolean binding(String name, String value) {
        boolean ret = true;
        if ("serial".equals(name)) {
            serial = value;
        } else if ("ban".equals(name)) {
            ban = value;
        } else if ("pid".equals(name)) {
            pid = value; // 身份字號
        } else if ("oid".equals(name)) {
            oid = value; // 機關代碼 
        } else if ("userID".equals(name)) {
            userID = value; //使用者代碼 
        } else if ("password".equals(name)) {
            password = value; //使用者密碼
        } else if ("userType".equals(name)) {
            userType = value; //登入者身份
        } else if ("serviceType".equals(name)) {
            serviceType = value; //服務種類
        } else if ("isMaster".equals(name)) {
            isMaster = value; //憑證是否為正卡
        } else if ("loginType".equals(name)) {
            loginType = value; //登入方式 
        } else if ("loginWay".equals(name)) {
            loginWay = value; //登入介面 (IE, 手機) [W-網頁,P-手機]
        } else {
            ret = false;
        }
        return ret;
    }

}
