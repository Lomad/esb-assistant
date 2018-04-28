JqCommon = {
    //日期选择控件初始化 datetimepicker
    setDateTimePicker: function (elementType) {
        $(elementType).datetimepicker({
            language: 'zh-CN',
            format: "yyyy-mm-dd",
            autoclose: true,
            startView: 2,//首先显示的视图
            minView: 2,//最精确的时间选择视图，2为月视图
            weekStart: 1,
            todayBtn: true,//可以直接选择当前时间
            todayHighlight: true,
            endDate: new Date(),
            pickerPosition: "bottom-left"
        });
    },

    getCurrentDateString: function () {
        var date = new Date();
        date = date.valueOf();
        var today = new Date(date);
        var seperator1 = "-";
        var year = today.getFullYear();
        var month = today.getMonth() + 1;

        var day = today.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (day >= 0 && day <= 9) {
            day = "0" + day;
        }
        return year + seperator1 + month + seperator1 + day;
    },

    generateDomFromString: function (text) {
        try //Internet Explorer
        {
            var xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
            xmlDoc.async = "false";
            xmlDoc.loadXML(text);
            return (xmlDoc);
        }
        catch (e) {
            try //Firefox, Mozilla, Opera, etc.
            {
                var parser = new DOMParser();
                xmlDoc = parser.parseFromString(text, "text/xml");
                return (xmlDoc);
            }
            catch (e) {
                alert(e.message)
            }
        }
        return (null);
    }
};

JqAjax = {
    /**
     * ajax post method with default error catch by layer alert
     * @param url
     * @param params
     * @param callback success
     */
    postByDefaultErrorCatch: function (url, params, callback) {
        $.post(url, params, callback).error(function (xhr) {
            var ele = JqCommon.generateDomFromString(xhr.responseText);
            var errorText = ele.getElementsByTagName("h1")[0].innerHTML;
            layer.alert(errorText, {
                icon: 5,
                title: '异常'
            });
        });
    }
};