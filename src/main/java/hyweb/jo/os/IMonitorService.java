/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyweb.jo.os;

/**
 *
 * @author william
 */

  
public interface IMonitorService {  
    /** *//** 
     * 獲得當前監控對像. 
     * @return 返回監測對象 
     * @throws Exception 
     */  
    public MonitorInfoBean getMonitorInfoBean() throws Exception;  
} 
