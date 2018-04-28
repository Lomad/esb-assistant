var contextPath = $("#contextPath").val();

$(document).ready(function () {
    console.log("当前路径：" + contextPath);
    global_Object.initDomEvent();
});

var startUrl = contextPath + "/ajax/start";

var global_Object = {
    initDomEvent: function () {
        /*日期控件初始化*/
        JqCommon.setDateTimePicker(".form_datetime");
        $("#sampleDate").val(JqCommon.getCurrentDateString());

        $("#startLaunch").on("click", function () {
            var sampleDate = $("#sampleDate").val();
            var percent = $("#percent").val();
            var randomGap = $("#gap").val();
            var params = {sampleDate: sampleDate, percent: percent, randomGap: randomGap};

            JqAjax.postByDefaultErrorCatch(startUrl,{params: JSON.stringify(params)},function (result) {

            });
        });
    }
};