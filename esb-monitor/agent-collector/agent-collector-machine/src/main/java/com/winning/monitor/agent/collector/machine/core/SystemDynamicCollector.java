package com.winning.monitor.agent.collector.machine.core;

import com.winning.monitor.agent.collector.api.core.IDataCollector;
import com.winning.monitor.agent.collector.api.entity.CollectData;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * Created by nicholasyan on 16/8/26.
 */
public class SystemDynamicCollector implements IDataCollector {

    private Sigar sigar = new Sigar();

    @Override
    public CollectData collect() {
        CollectData collectData = new CollectData();

        try {
            CpuInfo[] infos = sigar.getCpuInfoList();
            CpuPerc[] cpuList = null;
            cpuList = sigar.getCpuPercList();
            double totalUsed = 0, userUsed = 0;
            // 不管是单块CPU还是多CPU都适用
            for (int i = 0; i < infos.length; i++) {
                //CpuInfo info = infos[i];
                CpuPerc cpuPerc = cpuList[i];
//                System.out.println("第" + (i + 1) + "块CPU信息");
//                System.out.println("CPU的总量MHz:    " + info.getMhz());// CPU的总量MHz
//                System.out.println("CPU生产商:    " + info.getVendor());// 获得CPU的卖主，如：Intel
//                System.out.println("CPU类别:    " + info.getModel());// 获得CPU的类别，如：Celeron
//                System.out.println("CPU缓存数量:    " + info.getCacheSize());// 缓冲存储器数量
//                System.out.println("CPU用户使用率:    " + CpuPerc.format(cpuPerc.getUser()));// 用户使用率
//                System.out.println("CPU系统使用率:    " + CpuPerc.format(cpuPerc.getSys()));//  系统使用率
//                System.out.println("CPU当前等待率:    " + CpuPerc.format(cpuPerc.getWait()));// 当前等待率
//                System.out.println("CPU当前错误率:    " + CpuPerc.format(cpuPerc.getNice()));//
//                System.out.println("CPU当前空闲率:    " + CpuPerc.format(cpuPerc.getIdle()));// 当前空闲率
//                System.out.println("CPU总的使用率:    " + CpuPerc.format(cpuPerc.getCombined()));// 总的使用率
                totalUsed = totalUsed + cpuPerc.getCombined();
                userUsed = userUsed + cpuPerc.getUser();
            }
            collectData.put("cpuTotalUsed", Math.ceil(totalUsed * 100 / infos.length));
            collectData.put("cpuUserUserd", Math.ceil(userUsed * 100 / infos.length));


            System.out.println("CPU总的使用率:    " + collectData.get("cpuUserUserd"));// 总的使用率
        } catch (SigarException e) {
            e.printStackTrace();
        }

        return collectData;
    }
}