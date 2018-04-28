package com.winning.monitor.data.api.largerScreen;

import com.winning.monitor.data.api.base.RunningStatusUnPTVO;
import com.winning.monitor.data.api.largerScreen.entity.ServiceCount;

import java.util.List;
import java.util.Map;

/**
 * @Author Lemod
 * @Version 2018/2/1
 */
public interface IScreenMonitorService {

    /**
     * 获取当天调用量Top5的服务列表
     * status取值：
     * <ul>
     * <li>Total</li>   根据总量倒叙，获取服务列表
     * <li>Error</li>   根据异常量倒叙，获取异常服务列表
     * </ul>
     * <p1>当异常服务量为零时，返回空集合</p1>
     * <p2>当异常服务量大于零，且小于五时，删除异常服务量为0的</p2>
     *
     * @param status 状态
     * @return list
     */
    List<ServiceCount> getServiceCountList(String status);

    /**
     * 获取当天所有活跃的系统
     * <p>
     * {@link RunningStatusUnPTVO} 是系统及统计信息映射对象
     * <p>
     * 对象包含的信息，如下：
     * <ul>
     * <li>server</li> 系统id
     * <li>client</li> 消费方id
     * <li>failCount</li> 调用失败数
     * </ul>
     * 当失败数大于零时，大屏界面需要将服务涉及
     * 到的提供和消费方，全部标红
     *
     * @return 系统统计信息
     */
    List<RunningStatusUnPTVO> getSystemList();

    List<ServiceCount> getServiceList(Map<String, Object> params);

}
