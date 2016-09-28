package hyweb.jo.os;

/**
 *
 * @author william
 */
public class MonitorInfoBean {  
      
    /** */  
    /** 可使用內存. */  
    private long totalMemory;  
  
    /** */  
    /** 剩餘內存. */  
    private long freeMemory;  
  
    /** */  
    /** 最大可使用內存. */  
    private long maxMemory;  
  
    /** */  
    /** 操作系統. */  
    private String osName;  
  
    /** */  
    /** 總物理內存. */  
    private long totalMemorySize;  
  
    /** */  
    /** 剩餘的物理內存. */  
    private long freePhysicalMemorySize;  
  
    /** */  
    /** 已使用的物理內存. */  
    private long usedMemory;  
  
    /** */  
    /** 線程總數. */  
    private int totalThread;  
  
    /** */  
    /** cpu使用率. */  
    private double cpuRatio;  
  
    public long getFreeMemory() {  
        return freeMemory;  
    }  
  
    public void setFreeMemory(long freeMemory) {  
        this.freeMemory = freeMemory;  
    }  
  
    public long getFreePhysicalMemorySize() {  
        return freePhysicalMemorySize;  
    }  
  
    public void setFreePhysicalMemorySize(long freePhysicalMemorySize) {  
        this.freePhysicalMemorySize = freePhysicalMemorySize;  
    }  
  
    public long getMaxMemory() {  
        return maxMemory;  
    }  
  
    public void setMaxMemory(long maxMemory) {  
        this.maxMemory = maxMemory;  
    }  
  
    public String getOsName() {  
        return osName;  
    }  
  
    public void setOsName(String osName) {  
        this.osName = osName;  
    }  
  
    public long getTotalMemory() {  
        return totalMemory;  
    }  
  
    public void setTotalMemory(long totalMemory) {  
        this.totalMemory = totalMemory;  
    }  
  
    public long getTotalMemorySize() {  
        return totalMemorySize;  
    }  
  
    public void setTotalMemorySize(long totalMemorySize) {  
        this.totalMemorySize = totalMemorySize;  
    }  
  
    public int getTotalThread() {  
        return totalThread;  
    }  
  
    public void setTotalThread(int totalThread) {  
        this.totalThread = totalThread;  
    }  
  
    public long getUsedMemory() {  
        return usedMemory;  
    }  
  
    public void setUsedMemory(long usedMemory) {  
        this.usedMemory = usedMemory;  
    }  
  
    public double getCpuRatio() {  
        return cpuRatio;  
    }  
  
    public void setCpuRatio(double cpuRatio) {  
        this.cpuRatio = cpuRatio;  
    }  
}  
