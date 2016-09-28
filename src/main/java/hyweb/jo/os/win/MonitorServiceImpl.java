package hyweb.jo.os.win;

import hyweb.jo.os.MonitorInfoBean;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import hyweb.jo.os.IMonitorService;

public class MonitorServiceImpl implements IMonitorService {

    public static final int CPUTIME = 5000;
    private static final int PERCENT = 100;
    private static final int FAULTLENGTH = 10;
    private static String PROC_CMD = System.getenv("windir")
      + "\\system32\\wbem\\wmic.exe process get Caption,CommandLine,"
      + "KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
    private long[] initCpuInfo = null;

    public MonitorServiceImpl() {
        try {
            initCpuInfo = readCpu(Runtime.getRuntime().exec(PROC_CMD));
        } catch (Exception e) {
            e.printStackTrace();
            initCpuInfo = null;
        }
    }

    public MonitorInfoBean getMonitorInfoBean() throws Exception {
        int kb = 1024;

        // 可使用內存  
        long totalMemory = Runtime.getRuntime().totalMemory() / kb;
        // 剩餘內存  
        long freeMemory = Runtime.getRuntime().freeMemory() / kb;
        // 最大可使用內存  
        long maxMemory = Runtime.getRuntime().maxMemory() / kb;

        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory
          .getOperatingSystemMXBean();

        // 操作系統  
        String osName = System.getProperty("os.name");
        // 總物理內存  
        long totalMemorySize = osmxb.getTotalPhysicalMemorySize() / kb;
        // 剩餘的物理內存  
        long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize() / kb;
        // 已使用的物理內存  
        long usedMemory = (osmxb.getTotalPhysicalMemorySize() - osmxb
          .getFreePhysicalMemorySize())
          / kb;

        // 獲得線程總數  
        ThreadGroup parentThread;
        for (parentThread = Thread.currentThread().getThreadGroup(); parentThread
          .getParent() != null; parentThread = parentThread.getParent())  
            ;
        int totalThread = parentThread.activeCount();

        double cpuRatio = 0;
        if (osName.toLowerCase().startsWith("windows")) {
            cpuRatio = this.getCpuRatioForWindows();
        }

        // 返回對象  
        MonitorInfoBean infoBean = new MonitorInfoBean();
        infoBean.setFreeMemory(freeMemory);
        infoBean.setFreePhysicalMemorySize(freePhysicalMemorySize);
        infoBean.setMaxMemory(maxMemory);
        infoBean.setOsName(osName);
        infoBean.setTotalMemory(totalMemory);
        infoBean.setTotalMemorySize(totalMemorySize);
        infoBean.setTotalThread(totalThread);
        infoBean.setUsedMemory(usedMemory);
        infoBean.setCpuRatio(cpuRatio);
        return infoBean;
    }

    private double getCpuRatioForWindows() {
        try {
            if (initCpuInfo == null) {
                return 0.0;
            }
            // 取得進程信息  
            //long[] c0 = readCpu(Runtime.getRuntime().exec(PROC_CMD));  
            //Thread.sleep(CPUTIME);  
            long[] c1 = readCpu(Runtime.getRuntime().exec(PROC_CMD));
            if (c1 != null) {
                long idletime = c1[0] - initCpuInfo[0];
                long busytime = c1[1] - initCpuInfo[1];
                return Double.valueOf(
                  PERCENT * (busytime) / (busytime + idletime))
                  .doubleValue();
            } else {
                return 0.0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
        }
    }

    private long[] readCpu(final Process proc) {
        long[] retn = new long[2];
        try {
            proc.getOutputStream().close();
            InputStreamReader ir = new InputStreamReader(proc.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line = input.readLine();
            if (line == null || line.length() < FAULTLENGTH) {
                return null;
            }
            int capidx = line.indexOf("Caption");
            int cmdidx = line.indexOf("CommandLine");
            int rocidx = line.indexOf("ReadOperationCount");
            int umtidx = line.indexOf("UserModeTime");
            int kmtidx = line.indexOf("KernelModeTime");
            int wocidx = line.indexOf("WriteOperationCount");
            long idletime = 0;
            long kneltime = 0;
            long usertime = 0;
            while ((line = input.readLine()) != null) {
                if (line.length() < wocidx) {
                    continue;
                }
                // 字段出現順序：Caption,CommandLine,KernelModeTime,ReadOperationCount,  
                // ThreadCount,UserModeTime,WriteOperation  
                String caption = Bytes.substring(line, capidx, cmdidx - 1).trim();
                String cmd = Bytes.substring(line, cmdidx, kmtidx - 1).trim();
                if (cmd.indexOf("wmic.exe") >= 0) {
                    continue;
                }
                // log.info("line="+line);  
                if (caption.equals("System Idle Process")
                  || caption.equals("System")) {
                    idletime += Long.valueOf(
                      Bytes.substring(line, kmtidx, rocidx - 1).trim())
                      .longValue();
                    idletime += Long.valueOf(
                      Bytes.substring(line, umtidx, wocidx - 1).trim())
                      .longValue();
                    continue;
                }

                kneltime += Long.valueOf(
                  Bytes.substring(line, kmtidx, rocidx - 1).trim())
                  .longValue();
                usertime += Long.valueOf(
                  Bytes.substring(line, umtidx, wocidx - 1).trim())
                  .longValue();
            }
            retn[0] = idletime;
            retn[1] = kneltime + usertime;
            return retn;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                proc.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
