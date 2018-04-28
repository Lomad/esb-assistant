package com.winning.monitor.agent.collector.machine.core;

import com.winning.monitor.agent.collector.api.core.IDataCollector;
import com.winning.monitor.agent.collector.api.entity.CollectData;
import org.hyperic.sigar.*;

import java.net.InetAddress;

/**
 * Created by nicholasyan on 16/9/5.
 */
public class SystemInfoCollector implements IDataCollector {

    private Sigar sigar = new Sigar();

    @Override
    public CollectData collect() {
        CollectData collectData = new CollectData();
        this.collectCpu(collectData);
        this.collectMem(collectData);
        this.collectOS(collectData);
        return collectData;
    }

    private void collectCpu(CollectData collectData) {
        try {
            CpuInfo[] infos = sigar.getCpuInfoList();
            collectData.put("cpuNum", infos.length); //cpu总数
            CpuInfo info = infos[0];
            collectData.put("cpuVendor", info.getVendor());
            collectData.put("cpuModel", info.getModel());

        } catch (SigarException e) {
            e.printStackTrace();
        }
    }

    private void collectMem(CollectData collectData) {
        try {
            // 物理内存信息
            Mem mem = sigar.getMem();
            // 内存总量
            collectData.put("memTotal", Math.ceil(mem.getTotal() / 1024L / 1024));
            // 系统页面文件交换区信息
            Swap swap = sigar.getSwap();
            collectData.put("swapTotal", Math.ceil(swap.getTotal() / 1024L / 1024));
        } catch (SigarException e) {
            e.printStackTrace();
        }
    }

    private void collectOS(CollectData collectData) {

        String hostname = "";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (Exception exc) {
            try {
                hostname = sigar.getNetInfo().getHostName();
            } catch (SigarException e) {
                hostname = "localhost.unknown";
            } finally {
                sigar.close();
            }
        }
        collectData.put("hostName", hostname);

        // 取当前操作系统的信息
        OperatingSystem OS = OperatingSystem.getInstance();
        String osName = OS.getVendor() + " " + OS.getVendorName() + " " + OS.getVersion() + " " + OS.getArch();
        collectData.put("osName", osName);

        System.out.println(collectData.get("osName"));
    }

}
