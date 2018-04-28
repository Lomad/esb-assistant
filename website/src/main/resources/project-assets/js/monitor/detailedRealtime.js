
$(document).ready(function () {
    global_Object.initDomEvent();
});
var global_Object = {
    //页面对象ID
    idWrapperId2 : '#wrapperId2',
    //初始化
    initDomEvent: function () {
        // console.log(CommonFunc.getQueryString("serverAppId"));//测试

        //绑定业务系统树
        DetailedTimes.idWrapperId2 = global_Object.idWrapperId2;
        DetailedTimes.initDomEvent();
    }
}