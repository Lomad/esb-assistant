/**
 * 使用方法
 1.引入样式和脚本
     <link rel="stylesheet" type="text/css" href="css/jquery.step.css" />
     <script src="http://www.jq22.com/jquery/jquery-1.10.2.js"></script>
     <script src="js/jquery.step.min.js"></script>
 2.初始化插件
     var $step = $("#step");
     $step.step({
       index: 0,
       time: 500,
       title: ["填写申请表", "上传资料", "待确认", "已确认", "预约完成"]
    });
 3.方法
     $step.getIndex()；// 获取当前的index
     $step.prevStep();// 上一步
     $step.nextStep();// 下一步
     $step.toStep(index);// 跳到指定步骤
 */
!function (i) {
    /**
     * @param e 参数格式如下：
     * {
            index: 0,
            time: 500,
            title: ["通信测试", "模拟流程测试", "测试完成"]
        }
     */
    i.fn.step = function (e) {
        var t = this, n = {
            index: 0,
            time: 400,
            title: []
        }, s = (e = i.extend({}, n, e)).title, d = s.length, u = e.time, p = (t.width() - 1) / d;
        t.index = e.index;
        var a = function () {
            var e = "";
            s.length > 0 && (e += '<div class="ui-step-wrap"><div class="ui-step-bg"></div><div class="ui-step-progress"></div><ul class="ui-step">', i.each(s, function (i, t) {
                e += '<li class="ui-step-item"><div class="ui-step-item-title">' + t + '</div>' +
                    '<div class="ui-step-item-num"><span>' + (i + 1) + "</span></div></li>"
            }), e += "</ul></div>"), t.append(e), t.find(".ui-step").children(".ui-step-item").width(p), t.toStep(t.index)
        };
        return t.toStep = function (e) {
            var n = t.find(".ui-step").children(".ui-step-item");
            t.index = e, t.find(".ui-step-progress").animate({width: p * (e + 1)}, u, function () {
                i.each(n, function (t) {
                    t > e ? i(this).removeClass("active") : i(this).addClass("active")
                })
            })
        }, t.getIndex = function () {
            return t.index
        }, t.nextStep = function () {
            t.index > d - 2 || (t.index++, t.toStep(t.index))
        }, t.prevStep = function () {
            t.index < 1 || (t.index--, t.toStep(t.index))
        }, a(), this
    }
}(jQuery);