package com.winning.esb.service.middleware;

import java.util.List;

public interface IMiddlewareService {
    List<String> checkEndpoint(String url);

    List<String>  checkRoute(String url);

    Double checkCpu(String url);

    Double checkMemory(String url);

    Double checkDisk(String url);

    Integer checkErrorList(String url);

    /**
     * 同步授权到ESB
     * @param url ESB的地址 ，如11,127.0.0.1,8080,admin,password
     */
    void releaseGrant(String url);

    void releaseApiUrl(String url);

    void releaseToken(String url);

    /**
     * 同步测试token到ESB
     * @param url
     */
    void releaseToken_Test(String url);

    /**
     * 同步服务地址到ESB
     * @param url
     */
    String  releaseUrl(String url);

    /**
     * 同步服务密钥到ESB
     * @param url
     */
    String  releaseSecret(String url);

    /**
     * 同步服务与系统对应表到ESB
     * @param url
     */
    String  releaseSvcApp(String url);
}
