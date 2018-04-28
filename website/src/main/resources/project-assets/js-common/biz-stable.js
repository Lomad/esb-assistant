//xuehao 2018-01-16 ：封装业务常用信息

var BizStable = {
    /**
     * 消息方向
     */
    MsgDirection : {
        /**
         * 请求消息
         */
        req : 1,
        /**
         * 应答消息
         */
        ack : 2
    },
    /**
     * 监控角色
     */
    MonitorRole : {
        /**
         * 提供方
         */
        Provider : 1,
        /**
         * 消费方
         */
        Consumer : 2
    },
    /**
     * 监控的日期时间类型
     */
    MonitorDateType : {
        /**
         * 用于错误队列页面
         */
        ERROR_QUEUE : '-11',
        /**
         * 当前小时
         */
        CUR_HOUR : '11',
        /**
         * 当天
         */
        TODAY : '21',
        /**
         * 指定小时
         */
        HOUR : '31'
    },
    /**
     * 监控的日期时间类型
     */
    MonitorStatus : {
        /**
         * 成功
         */
        SUCCESS : '0',
        /**
         * 失败
         */
        FAILURE : '-1'
    },
    /**
     * 监控中用户数据的字段名称
     */
    MonitorDatalistKey : {
        requestEsb : {
            key : "requestEsb",
            name : "请求消息"
        },
        responseEsb : {
            key : "responseEsb",
            name : "应答消息"
        },
        requestProvider : {
            key : "requestProvider",
            name : "路由内部请求消息"
        },
        responseProvider : {
            key : "responseProvider",
            name : "路由内部收到消息"
        },
        mainId : {
            key : "mainId",
            name : "关键ID/姓名"
        },
        router : {
            key : "router",
            name : "路由名称"
        },
        sourceTime : {
            key : "sourceTime",
            name : "路由内部调用时间"
        },
        MessageID : {
            key : "MessageID",
            name : "消息唯一ID"
        },
        ParentMessageID : {
            key : "ParentMessageID",
            name : "父级消息ID"
        },
        errorMsg : {
            key : "errorMsg",
            name : "错误信息"
        }
    },
    /**
     * 通用网址
     */
    CommonURL : {
        dataFormat : contextPath + "/ajax/utils/dataFormat/format",
        serverDetailedRealtime : contextPath + "/view/paas/serverrealtime__serverdetailedrealtime",
        clientDetailedRealtime : contextPath + "/view/paas/clientrealtime__clientdetailedrealtime"
    }
}