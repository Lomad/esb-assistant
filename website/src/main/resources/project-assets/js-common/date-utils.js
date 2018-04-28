//xuehao 2017-08-24 ：封装日期时间的公共的js操作

Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

var DateUtils = {
    formatBase: 'yyyy-MM-dd hh:mm:ss',
    formatDateBase: 'yyyy-MM-dd',
    formatTimeBase: 'hh:mm:ss',
    formatTimeHM: 'hh:mm',
    formatFriendly: 'MM-dd hh:mm',
    formatSSS: 'yyyy-MM-dd hh:mm:ss.SSS',
    formatHour: 'yyyy-MM-dd hh',
    formatMinute: 'yyyy-MM-dd hh:mm',
    format: function (fmt, date) {
        var o = {
            "M+": date.getMonth() + 1, //月份
            "d+": date.getDate(), //日
            "h+": date.getHours(), //小时
            "m+": date.getMinutes(), //分
            "s+": date.getSeconds(), //秒
            "q+": Math.floor((date.getMonth() + 3) / 3), //季度
            "S": date.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    },
    /**
     * 加上指定的秒数（如果为负数，则减去指定的秒数）
     * @returns 返回日期
     */
    addSeconds: function (seconds, baseDatetime) {
        if (!baseDatetime) {
            baseDatetime = new Date();
        }
        var baseTimestamp = baseDatetime.getTime();
        var result = new Date(baseTimestamp + seconds * 1000);
        return result;
    },
    /**
     * 加上指定的分钟数（如果为负数，则减去指定的分钟数）
     * @param minutes   分钟数
     * @param baseDatetime  基准时间，如果为空，则默认为当前时间
     */
    addMinutes: function (minutes, baseDatetime) {
        if (!baseDatetime) {
            baseDatetime = new Date();
        }
        var baseTimestamp = baseDatetime.getTime();
        var result = new Date(baseTimestamp + minutes * 60 * 1000);
        return result;
    },
    /**
     * 加上指定的小时数（如果为负数，则减去指定的分钟数）
     * @param hours   小时数
     * @param baseDatetime  基准时间，如果为空，则默认为当前时间
     */
    addHours: function (hours, baseDatetime) {
        if (!baseDatetime) {
            baseDatetime = new Date();
        }
        var baseTimestamp = baseDatetime.getTime();
        var result = new Date(baseTimestamp + hours * 60 * 60 * 1000);
        return result;
    },
    /**
     * 加上指定的天数（如果为负数，则减去指定的分钟数）
     * @param days   天数
     * @param baseDatetime  基准时间，如果为空，则默认为当前时间
     */
    addDays: function (days, baseDatetime) {
        if (!baseDatetime) {
            baseDatetime = new Date();
        }
        var baseTimestamp = baseDatetime.getTime();
        var result = new Date(baseTimestamp + days * 24 * 60 * 60 * 1000);
        return result;
    },
    /**
     * 获取当天或基准时间的零点整的时间戳
     * @param baseDatetime  基准时间，如果为空，则默认为当前时间
     */
    getTodayZeroOclock: function (baseDatetime) {
        if (!baseDatetime) {
            baseDatetime = new Date();
        }
        var result = new Date(DateUtils.format(DateUtils.formatDateBase, baseDatetime) + " 00:00:00.000");
        return result;
    }

}