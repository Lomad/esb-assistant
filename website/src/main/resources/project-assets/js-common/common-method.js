

JqCommon = {
    AllSelect: function (id) {//全选
        $(id).parents("table").on("change", id, function () {
            var isChecked = $(this).prop("checked");
            $(id).parents("table").find("input[type='checkbox']").prop("checked", isChecked);
        });
        $(id).parents("table").on("change", "input[type='checkbox']:gt(0)", function () {
            var isChecked = true;
            $.each($(id).parents("table").find("input[type='checkbox']:gt(0)"), function (i, item) {
                if (!$(this).prop("checked")) {
                    isChecked = false;
                }
            });
            $(id).prop("checked", isChecked);
        });
    },
    ChangeDateFormat: function (cellval) {//时间格式转换
        if (cellval != "" && cellval != null) {
            var date = new Date(parseInt(cellval.replace("/Date(", "").replace(")/", ""), 10));
            var month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
            var currentDate = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
            return date.getFullYear() + "/" + month + "/" + currentDate;
        }
        else {
            return "";
        }
    },
    ChangeDateTimeFormat: function (cellval) {//时间格式转换
        if (cellval != "" && cellval != null) {
            var date = new Date(parseInt(cellval.replace("/Date(", "").replace(")/", ""), 10));
            var month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
            var currentDate = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
            var hour = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
            var minute = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
            var second = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();

            return date.getFullYear() + "/" + month + "/" + currentDate + " " + hour + ":" + minute + ":" + second;
        }
        else {
            return "";
        }
    },
    UpLoadType: function (filepath) {
        var extStart = filepath.lastIndexOf(".");
        var ext = filepath.substring(extStart, filepath.length).toLocaleLowerCase();
        return ext;
    },
    ConvertInvestmentTerm: function (InvestmentTerm, ExtendedTerm, ExitTerm) {
        var html = ""
        if (InvestmentTerm != 0) {
            if (InvestmentTerm % 30 == 0) {
                html += InvestmentTerm / 30 + "个月";
            }
            else {
                html += InvestmentTerm + "天";
            }
        }
        if (ExtendedTerm != 0) {
            if (ExtendedTerm % 30 == 0) {
                if (html == "") {
                    html += ExtendedTerm / 30 + "个月";
                }
                else {
                    html += "+" + ExtendedTerm / 30 + "个月";
                }
            }
            else {
                if (html == "") {
                    html += ExtendedTerm + "天";
                }
                else {
                    html += "+" + ExtendedTerm + "天";
                }
            }
        }
        if (ExitTerm != 0) {
            if (ExitTerm % 30 == 0) {
                if (html == "") {
                    html += ExitTerm / 30 + "个月";
                }
                else {
                    html += "+" + ExitTerm / 30 + "个月";
                }
            }
            else {
                if (html == "") {
                    html += ExitTerm + "天";
                }
                else {
                    html += "+" + ExitTerm + "天";
                }
            }
        }
        return html;
    },
    ConvertAmount: function (StartAmount, EndAmount) {
        var html = "";
        if (StartAmount != -1 && EndAmount != -1) {
            html = StartAmount + "≤X＜" + EndAmount;
        }
        else if (StartAmount != -1 && EndAmount == -1) {
            html = "X≥" + StartAmount;
        }
        else if (StartAmount == -1 && EndAmount != -1) {
            html = "X＜" + EndAmount;
        }
        return html;
    },
    openPostWindow: function (url, datas) {
        var tempForm = document.createElement("form");
        tempForm.id = "tempForm1";
        tempForm.method = "post";
        tempForm.action = url;
        //tempForm.target="_blank"; //打开新页面
        var hideInput1 = document.createElement("input");
        hideInput1.type = "hidden";
        hideInput1.name = "datas"; //后台要接受这个参数来取值
        hideInput1.value = JSON.stringify(datas); //后台实际取到的值

        tempForm.appendChild(hideInput1);
        //tempForm.appendChild(hideInput2);
        if (document.all) {
            tempForm.attachEvent("onsubmit", function () {
            });        //IE
        } else {
            var subObj = tempForm.addEventListener("submit", function () {
            }, false);    //firefox
        }
        document.body.appendChild(tempForm);
        if (document.all) {
            tempForm.fireEvent("onsubmit");
        } else {
            tempForm.dispatchEvent(new Event("submit"));
        }
        tempForm.submit();
        document.body.removeChild(tempForm);
    },

    /*生成服务调用流程图*/
    getServiceFlowShow:function (obj) {
        var last = obj.length;
        var html = [];

        $.each(obj, function (i, v) {
            var index = v.index+1;
            var para_v = JSON.stringify(v).replace(/\"/g,"'");
            var startTime, endTime, duration;
            if (v.startTime && v.endTime) {
                startTime = DateFormat.formatToDate(v.startTime);
                endTime = DateFormat.formatToDate(v.endTime);
                duration = endTime.getTime() - startTime.getTime();
            }
            if (i == 0){
                var sponsor = v.messageTree.caller;
                var para_sps = JSON.stringify(sponsor).replace(/\"/g,"'");
                var sps_time = '';
                if (sponsor.startTime){
                    sps_time = sponsor.startTime;
                }
                var start = '<div class="steps-wrap"><div class="steps-round cp">1</div><div class="steps-bar"></div><i class="fa  step-icon  fa-caret-right fa-2x pull-right"></i><label class="steps-label">'+sponsor.name+'请求<br/>'+sps_time+'</label></div>';
                var answer = '<div class="steps-wrap"><div class="steps-round cp">'+index+'</div><div class="steps-bar"><small style="margin-left: 20px">耗时'+duration+'ms</small></div><i class="fa  step-icon  fa-caret-right fa-2x pull-right"></i><label class="steps-label">'+v.domain+'接收</label></div>';
                html.push(start,answer);
            }else {
                var dom = '<div class="steps-wrap"><div class="steps-round cp">'+index+'</div><div class="steps-bar"><small style="margin-left: 20px">耗时'+duration+'ms</small></div><i class="fa  step-icon  fa-caret-right fa-2x pull-right"></i><label class="steps-label">'+v.domain+'接收</label></div>';
                html.push(dom);
            }
        });

        // console.log(html);  //测试

        $.each(obj.reverse(),function (i,v) {
            var para_v = JSON.stringify(v).replace(/\"/g,"'");
            var v_time = '';
            if (v.endTime){
                v_time = v.endTime;
            }
            var type = "结束时间";
            var begin = html.length;
            var invert;
            if (i == last-1){
                var sponsor = v.messageTree.caller;
                var para_sps = JSON.stringify(sponsor).replace(/\"/g,"'");
                var sps_time = '';
                if (sponsor.endTime){
                    sps_time = sponsor.endTime;
                }
                if (i == 0){
                    begin ++;
                }
                invert = '<div class="steps-wrap"><div class="steps-round cp">'+(begin+i)+'</div><div class="steps-bar"></div><i class="fa  step-icon  fa-caret-right fa-2x pull-right"></i><label class="steps-label">'+v.domain+'应答</label></div>';
                var endDom = '<div class="steps-wrap"><div class="steps-round cp">'+(begin+i+1)+'</div><label class="steps-label" style="margin-top: 24px">'+sponsor.name+'接收<br/>'+sps_time+'</label></div>';
                html.push(invert,endDom);
            }else {
                invert = '<div class="steps-wrap"><div class="steps-round cp">'+(begin+i+1)+'</div><div class="steps-bar"></div><i class="fa  step-icon  fa-caret-right fa-2x pull-right"></i><label class="steps-label">'+v.domain+'应答</label></div>';
                html.push(invert);
            }
        });

        // console.log(html);  //测试

        return html;
    },

    /*生成详情链接的调用次数统计图*/
    getCartogram:function (unit,data) {
        var json = [];
        var name = [];
        for (var key in data.durations) {
            name.push(key);
            json.push(data.durations[key]);
        }

        var option = {
            color: ['#3398DB'],
            tooltip: {
                trigger: 'axis',
                axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                    type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: [
                {
                    type: 'category',
                    data: name,
                    name: unit,
                    nameLocation: 'end',
                    nameTextStyle: {
                        color: 'black',
                        fontSize: 14,
                        fontWeight: 'bolder'
                    }
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    name: '次',
                    nameLocation: 'end',
                    nameTextStyle: {
                        color: 'black',
                        fontSize: 14,
                        fontWeight: 'bolder'
                    }
                }
            ],
            series: [
                {
                    name: '直接访问',
                    type: 'bar',
                    //barWidth: '60',
                    data: json
                }
            ]
        };
        return option;
    },

    /*表格按条件筛选功能*/
    setTableData:function (old,limit) {
        var tableData = [];
        $.each(old, function (i, v) {
            if (v.transactionTypeName.indexOf($.trim(limit)) > -1) {
                tableData.push(v);
            }
        });
        return tableData;
    },

    /*实时提供方和消费方时间生成函数*/
    getSimpleDateString: function (time) {
        function getNowFormatDate() {
            var date = new Date();
            var seperator1 = "-";
            var year = date.getFullYear();
            var month = date.getMonth() + 1;
            var strDate = date.getDate();
            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }
            if (strDate >= 0 && strDate <= 9) {
                strDate = "0" + strDate;
            }
            var currentdate = year + seperator1 + month + seperator1 + strDate;
            return currentdate;
        }
        if (time.length > 0) {
            return getNowFormatDate() + " " + time.split('-')[0] + ":00";
        } else {
            return "";
        }
    },

    /*当前时间为基准，gap为时差，获取时间字符串*/
    getCurrentDateString:function(minUnit){
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
        var hour, minute;
        if (minUnit === "hour") {
            hour = today.getHours();
            if (hour >= 0 && hour <= 9) {
                hour = "0" + hour;
            }
        }
        if (minUnit === "minute") {
            hour = today.getHours();
            minute = today.getMinutes();
            if (hour >= 0 && hour <= 9) {
                hour = "0" + hour;
            }
            if (minute >= 0 && minute <= 9) {
                minute = "0" + minute;
            }
        }
        var currentdate = year + seperator1 + month + seperator1 + day;
        if (minute){
            return currentdate + " "+hour+":"+minute;
        }else {
            return currentdate + " "+hour;
        }
    },

    /*历史监控时间生成函数-昨天*/
    getYesterdayFormatDate:function(){
        var date = new Date();
        date = date.valueOf() - 1000*60*60*24;
        var yesterday = new Date(date);
        var seperator1 = "-";
        var year = yesterday.getFullYear();
        var month = yesterday.getMonth() + 1;

        var strDate = yesterday.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        var currentdate = year + seperator1 + month + seperator1 + strDate;
        return currentdate;
    },

    getTodayFormatDate:function(){
        var date = new Date();
        date = date.valueOf();
        var yesterday = new Date(date);
        var seperator1 = "-";
        var year = yesterday.getFullYear();
        var month = yesterday.getMonth() + 1;

        var strDate = yesterday.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        var currentdate = year + seperator1 + month + seperator1 + strDate;
        return currentdate;
    },

    /*历史监控时间生成函数*/
    getNewDay:function(dateTemp) {
        var dateTemp = dateTemp.split("/");
        var nDate = new Date(dateTemp[1] + '/' + dateTemp[2] + '/' + dateTemp[0]); //转换为MM-DD-YYYY格式
        var week = nDate.getDay();

        var monday;var millSeconds;
        if (week == 0){
            monday = Math.abs(nDate) - (6 * 24 * 60 * 60 * 1000);
            millSeconds = Math.abs(nDate);
        }else{
            monday = Math.abs(nDate) - ((week-1) * 24 * 60 * 60 * 1000);
            millSeconds = monday + (6 * 24 * 60 * 60 * 1000);
        }
        var now = new Date();
        if (millSeconds > Math.abs(now)){
            millSeconds = Math.abs(now);
        }
        function getdate(mills){
            var rDate = new Date(mills);
            var year = rDate.getFullYear();
            var month = rDate.getMonth() + 1;
            if (month < 10) month = "0" + month;
            var date = rDate.getDate();
            if (date < 10) date = "0" + date;
            var newdate = year + "/" + month + "/" + date;
            return newdate;
        }
        return (getdate(monday)+"-"+getdate(millSeconds));
    },

    /*生成实时、历史提供方关系图*/
    generateProviderCommunicationChart:function (result,providerName,chosen,formal,clientNames) {
        var data = [], links = [], categories = [{name: '提供方'}, {name: '消费方'}];
        var provider = {
            id:'provider',
            name:providerName,
            x:500,
            y:0,
            symbol: 'image://'+contextPath+'/project-assets/img/monitor/source.png',
            symbolSize: 40,
            label: {
                normal: {
                    show: true,
                    position: 'bottom',
                    textStyle: {
                        color: '#444'
                    }
                }
            },
            tooltip: {
                show: false
            }
        };
        data.push(provider);

        var num = result.length;
        var count = Math.floor(num / 2);

        for (var i = 0; i < num; i++) {
            var o = result[i], each, x;
            clientNames.push(o.domain);

            if (i < count) {
                x = provider.x - 4 * (count - i);
            } else {
                if (num % 2 == 0) {
                    x = provider.x + 4 * (i + 1 - count);
                } else {
                    x = provider.x + 4 * (i - count);
                }
            }
            var symbol= formal;
            // if (i == 0){
            //     symbol = chosen;
            // }else {
            //     symbol = formal;
            // }

            each = {
                id:'consumer'+'-'+o.domain,
                name: o.type,
                x: x,
                y: 5,
                symbol: symbol,
                symbolSize: 40,
                label: {
                    normal: {
                        show: true,
                        position: 'bottom',
                        textStyle: {
                            color: '#444'
                        }
                    }
                },
                tooltip: {
                    show: false
                }
            };
            var numberColor = '#FBAD47', len = (o.totalCount + '').length;
            var numberSize = 10 + (len - 1) * 7;
            var number = {
                id:o.domain + '-' + o.type + '-' + o.totalCount,
                name:o.totalCount,
                symbol: 'roundRect',
                symbolSize: [numberSize, 20],
                x: each.x + (len-1)*0.2+1.1 ,
                y: each.y - 0.8,
                itemStyle: {
                    normal: {
                        color: numberColor
                    },
                    emphasis: {
                        color: numberColor
                    }
                },
                label: {
                    normal: {
                        show: true,
                        position: 'inside',
                        textStyle: {
                            color: '#fff',
                            fontSize: 8
                        }
                    }
                },
                tooltip: {
                    show: false
                }
            };
            data.push(each);
            data.push(number);

            var link,lineStyle;
            if (each.x < provider.x) {
                lineStyle = {
                    normal: {curveness: -0.2}
                }
            } else if (each.x > provider.x){
                lineStyle = {
                    normal: {curveness: 0.2}
                }
            }else {
                lineStyle = {
                    normal: {curveness: 0}
                }
            }
            link = {
                source: provider.id,
                target: each.id,
                lineStyle: lineStyle
            };
            links.push(link);
        }

        var option = {
            title: {
                text: '提供方监控运行情况',
                show:false
            },
            legend: {
                top: 10,
                left: 15,
                data: [
                    {
                        name: '提供方',
                        icon: 'image://'+contextPath+'/project-assets/img/monitor/source.png'
                    },
                    {
                        name: '消费方',
                        icon: formal
                    }
                ]
            },
            //right:10,
            bottom:3,
            tooltip: {},
            animationDurationUpdate: 1500,
            animationEasingUpdate: 'quinticInOut',
            series : [
                {
                    type: 'graph',
                    layout: 'none',
                    hoverAnimation: false,
                    symbolClip: false,
                    roam: true,
                    label: {
                        normal: {
                            show: true
                        }
                    },
                    edgeSymbol: ['circle', 'arrow'],
                    edgeSymbolSize: [0, 10],
                    edgeLabel: {
                        normal: {
                            textStyle: {
                                fontSize: 20
                            }
                        }
                    },
                    data: data,
                    links: links,
                    lineStyle: {
                        normal: {
                            width: 2,
                            curveness: 0
                        }
                    },
                    categories: categories
                }
            ]
        };
        return option;
    },

    /*生成实时、历史消费方关系图*/
    generateConsumerCommunicationChart:function (result,providerName,chosen,formal,clientNames) {
        var data = [], links = [], categories = [{name: '提供方'}, {name: '消费方'}];
        var provider = {
            id:'provider',
            name:providerName,
            x:500,
            y:0,
            symbol: 'image://'+contextPath+'/project-assets/img/monitor/c_target.png',
            symbolSize: 40,
            label: {
                normal: {
                    show: true,
                    position: 'bottom',
                    textStyle: {
                        color: '#444'
                    }
                }
            },
            tooltip: {
                show: false
            }
        };
        data.push(provider);

        var num = result.length, count = Math.floor(num / 2);
        var isEven = (num % 2 === 0);

        for (var i = 0; i < num; i++) {
            var o = result[i], each, x;
            clientNames.push(o.domain);

            if (isEven) {
                if (i < count) {
                    x = provider.x - 5 * (count - i);
                } else {
                    x = provider.x + 5 * (i + 1 - count);
                }
            } else {
                if (i < count) {
                    x = provider.x - 5 * (count - i);
                } else if (i === count) {
                    x = provider.x;
                } else {
                    x = provider.x + 5 * (i - count);
                }
            }

            var symbol;
            if (i == 0){
                symbol = chosen;
            }else {
                symbol = formal;
            }

            each = {
                id:'consumer'+'-'+o.domain,
                name: o.type,
                x: x,
                y: 5,
                symbol: symbol,
                symbolSize: 40,
                label: {
                    normal: {
                        show: true,
                        position: 'bottom',
                        textStyle: {
                            color: '#444'
                        }
                    }
                },
                tooltip: {
                    show: false
                }
            };
            var numberColor = '#FBAD47', len = (o.totalCount + '').length;
            var numberSize = 10 + (len - 1) * 7;
            var number = {
                id:o.type + o.totalCount,
                name:o.totalCount,
                symbol: 'roundRect',
                symbolSize: [numberSize, 20],
                x: each.x + (len-1)*0.2+1.1 ,
                y: each.y - 0.8,
                itemStyle: {
                    normal: {
                        color: numberColor
                    },
                    emphasis: {
                        color: numberColor
                    }
                },
                label: {
                    normal: {
                        show: true,
                        position: 'inside',
                        textStyle: {
                            color: '#fff',
                            fontSize: 8
                        }
                    }
                },
                tooltip: {
                    show: false
                }
            };
            data.push(each);
            data.push(number);
            var link,lineStyle;
            if (each.x < provider.x) {
                lineStyle = {
                    normal: {curveness: 0.2}
                }
            } else if (each.x > provider.x){
                lineStyle = {
                    normal: {curveness: -0.2}
                }
            }else {
                lineStyle = {
                    normal: {curveness: 0}
                }
            }
            link = {
                source: each.id,
                target: provider.id,
                lineStyle: lineStyle
            };
            links.push(link);
        }

        var option = {
            title: {
                text: '提供方监控运行情况',
                show:false
            },
            legend: {
                top:10,
                left:10,
                data: [
                    {
                        name: '提供方',
                        icon: formal
                    },
                    {
                        name: '消费方',
                        icon: 'image://'+contextPath+'/project-assets/img/monitor/c_target.png'
                    }
                ]
            },
            //right:10,
            bottom:3,
            tooltip: {},
            animationDurationUpdate: 1500,
            animationEasingUpdate: 'quinticInOut',
            series : [
                {
                    type: 'graph',
                    layout: 'none',
                    hoverAnimation: false,
                    symbolClip: false,
                    roam: true,
                    label: {
                        normal: {
                            show: true
                        }
                    },
                    edgeSymbol: ['circle', 'arrow'],
                    edgeSymbolSize: [0, 10],
                    edgeLabel: {
                        normal: {
                            textStyle: {
                                fontSize: 20
                            }
                        }
                    },
                    data: data,
                    links: links,
                    lineStyle: {
                        normal: {
                            width: 2,
                            curveness: 0
                        }
                    },
                    categories: categories
                }
            ]
        };
        return option;
    },

    parseXMLString:function (xml) {

        //去掉多余的空格
        xml = '\n' + xml.replace(/(<\w+)(\s.*?>)/g,function($0, name, props)
            {
                return name + ' ' + props.replace(/\s+(\w+=)/g," $1");
            }).replace(/>\s*?</g,">\n<");

        //把注释编码
        xml = xml.replace(/\n/g,'\r').replace(/<!--(.+?)-->/g,function($0, text) {
            var ret = '<!--' + escape(text) + '-->';
            return ret;
        }).replace(/\r/g,'\n');

        //调整格式
        var rgx = /\n(<(([^\?]).+?)(?:\s|\s*?>|\s*?(\/)>)(?:.*?(?:(?:(\/)>)|(?:<(\/)\2>)))?)/mg;
        var nodeStack = [];
        var output = xml.replace(rgx,function($0,all,name,isBegin,isCloseFull1,isCloseFull2 ,isFull1,isFull2){
            var isClosed = (isCloseFull1 == '/') || (isCloseFull2 == '/' ) || (isFull1 == '/') || (isFull2 == '/');
            var prefix = '';
            if(isBegin == '!')
            {
                prefix = getPrefix(nodeStack.length);
            }
            else
            {
                if(isBegin != '/')
                {
                    prefix = getPrefix(nodeStack.length);
                    if(!isClosed)
                    {
                        nodeStack.push(name);
                    }
                }
                else
                {
                    nodeStack.pop();
                    prefix = getPrefix(nodeStack.length);
                }

            }
            var ret =  '\n' + prefix + all;
            return ret;
        });

        var outputText = output.substring(1);

        //把注释还原并解码，调格式
        outputText = outputText.replace(/\n/g,'\r').replace(/(\s*)<!--(.+?)-->/g,function($0, prefix,  text)
        {
            if(prefix.charAt(0) == '\r')
                prefix = prefix.substring(1);
            text = unescape(text).replace(/\r/g,'\n');
            var ret = '\n' + prefix + '<!--' + text.replace(/^\s*/mg, prefix ) + '-->';
            return ret;
        });

        outputText= outputText.replace(/\s+$/g,'').replace(/\r/g,'\r\n');
        return outputText;

        function getPrefix(prefixIndex){
            var span = '    ';
            var output = [];
            for(var i = 0 ; i < prefixIndex; ++i)
            {
                output.push(span);
            }

            return output.join('');
        }

    },

    formatXML:function (str) {
        function getXMLParser() {
            try {
                return new ActiveXObject("Msxml2.DOMDocument");
            }catch (e){
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
        }
        //去除输入框中xmll两端的空格。
        str = str.replace(/^\s+|\s+$/g,"");
        var source = new ActiveXObject("Msxml2.DOMDocument");
        //装载数据
        source.async = false;
        source.loadXML(str);
        // 装载样式单
        var stylesheet = new ActiveXObject("Msxml2.DOMDocument");
        stylesheet.async = false;
        stylesheet.resolveExternals = false;
        stylesheet.load('//'+contextPath+'/templates/monitor/format.xsl');

        // 创建结果对象
        var result = new ActiveXObject("Msxml2.DOMDocument");
        result.async = false;

        // 把解析结果放到结果对象中方法1
        source.transformNodeToObject(stylesheet, result);
        //alert(result.xml);
        if(result.xml==''||result.xml==null){
            alert('xml报文格式错误，请检查');
            return false;
        }
        var finalStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +result.xml;
        return finalStr;
    },

    splitStringToArray: function (primary) {
        if (primary.indexOf("-") >= 0) {
            var arr = primary.split("-");
            return arr[1];
        } else {
            return primary;
        }
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
    },

    /*滚动条*/
    setScrollBar: function (element) {
        if (element.length > 0) {
            element.mCustomScrollbar({
                autoHideScrollbar: true,	//自动隐藏滚动条
                axis: "yx", //水平与竖直都出现滚动条
                theme: "minimal-dark"
            });
        }
    },

    //获取先前页的首末时间戳
    getTSInPrevious: function (array) {
        var size = array.length;
        if (size > 0) {
            var maxTime = array[0].timestamp;
            //var maxUUID = array[0].messageId;
            var minTime = array[size - 1].timestamp;
            //var minUUID = array[9].messageId;

            /* 统计和最小、最大时间戳相同的明细条数 */
            var minSameCount = -1, maxSameCount = -1;
            $.each(array, function (index, value) {
                var timestamp = value.timestamp;
                if (timestamp === minTime) {
                    minSameCount++;
                }
                if (timestamp === maxTime) {
                    maxSameCount++;
                }
            });
            return ({
                maxTime: maxTime, maxSameCount: maxSameCount,
                minTime: minTime, minSameCount: minSameCount
            });
        }
    },

    //设置日期选择控件 datepicker
    setDatePicker: function (element) {
        element.datepicker({
            language: "zh-CN",
            autoclose: true,//选中之后自动隐藏日期选择框
            format: "yyyy-mm-dd",//日期格式
            weekStart: 1,
            showWeekNumbers: true,
            endDate: "-1d",
            orientation: "bottom left"//控件显示位置
        });
    },

    //日期选择控件初始化 datetimepicker
    setDateTimePicker: function (elementType) {
        $(elementType).datetimepicker({
            language: 'zh-CN',
            format: "yyyy-mm-dd hh:ii",
            autoclose: true,
            startView: 1,//弹出界面为小时选择
            weekStart: 1,
            minuteStep: 1,
            todayBtn: true,//可以直接选择当前时间
            todayHighlight: true,
            pickerPosition: "bottom-left"
        });
    },

    checkStringHasText:function (string) {
        if (string && string.length > 0){
            return true;
        }else {
            return false;
        }
    },

    //转换时间"2008-04-02 10:08:44" String To Date
    convertStringToDate: function (strTime) {
        var str = strTime.toString();
        str = str.replace("/-/g", "/");
        return new Date(str);
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