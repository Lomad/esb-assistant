package com.winning.esb.service.taskmark;

/**
 * 同步到ESB的开关，如果对应开关值为true，则将信息同步到ESB，否则无需同步
 * @author xuehao
 * @date 2017/9/15
 */
public class SyncToEsbMark {
    /**
     * 是否同步服务地址
     */
    private volatile static boolean syncUrl = true;
    /**
     * 是否同步授权
     */
    private volatile static boolean syncGrant = true;

    /**
     * 是否同步密钥
     */
    private volatile static boolean syncSecret = true;

    /**
     * 是否同步Token
     */
    private volatile static boolean syncToken = true;

    /**
     * 是否同步测试Token
     */
    private volatile static boolean syncToken_Test = true;

    /**
     * 是否同步ApiUrl
     */
    private volatile static boolean syncApiUrl = true;

    /**
     * 是否同步服务系统对应表
     */
    private volatile static boolean syncSvcApp = true;

    public static boolean getSyncGrant() {
        return syncGrant;
    }

    public static void setSyncGrant(boolean syncGrant) {
        SyncToEsbMark.syncGrant = syncGrant;
    }

    public static boolean getSyncUrl() {
        return syncUrl;
    }

    public static void setSyncUrl(boolean syncUrl) {
        SyncToEsbMark.syncUrl = syncUrl;
    }

    public static boolean getSyncSecret() {
        return syncSecret;
    }

    public static void setSyncSecret(boolean syncSecret) {
        SyncToEsbMark.syncSecret = syncSecret;
    }

    public static boolean getSyncToken() {
        return syncToken;
    }

    public static void setSyncToken(boolean syncToken) {
        SyncToEsbMark.syncToken = syncToken;
    }

    public static boolean getSyncSvcApp() {
        return syncSvcApp;
    }

    public static void setSyncSvcApp(boolean syncSvcApp) {
        SyncToEsbMark.syncSvcApp = syncSvcApp;
    }

    public static boolean getSyncToken_Test() {
        return syncToken_Test;
    }

    public static void setSyncToken_Test(boolean syncToken_Test) {
        SyncToEsbMark.syncToken_Test = syncToken_Test;
    }

    public static boolean getSyncApiUrl() {
        return syncApiUrl;
    }

    public static void setSyncApiUrl(boolean syncApiUrl) {
        SyncToEsbMark.syncApiUrl = syncApiUrl;
    }
}