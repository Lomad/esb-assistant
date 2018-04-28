//xuehao 2017-07-25 ：封装一些公共的js操作

var CommonFunc = {
    //用于生成随机字符串
    chars: ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'],
    //成功提示
    isEmpty: function (data) {
        if (data == null || data == undefined)
            return true;
        //由于js中数字0与空相同，所以要对于0应该特殊处理
        if (CommonFunc.trimStr(data.toString()) == "0")
            return false;
        if (CommonFunc.trimStr(data) == '' || JSON.stringify(data) == '{}' || JSON.stringify(data) == '[{}]')
            return true;
        else
            return false;
    },
    trimStr: function (str) {
        return str.toString().replace(/(^\s*)|(\s*$)/g, "");
    },
    /**
     * 获取Jquery格式的ID，即判断ID前是否有井号，如果没有，则加上井号
     */
    formatIdForJquery: function (id) {
        if (!CommonFunc.isEmpty(id) && id.substr(0, 1) != '#')
            id = '#' + id;
        return id;
    },
    /**
     * 删除ID中井号
     */
    formatIdRemoveJquery: function (id) {
        if (!CommonFunc.isEmpty(id) && id.substr(0, 1) == '#' && id.length > 1)
            id = id.substr(1);
        return id;
    },
    /**
     * 将array2的元素插入到array1中，如果已经存在，则跳过
     */
    arrayInsert: function (array1, array2) {
        if (array2 != null && array2.length > 0) {
            for (var a in array2) {
                if ($.inArray(array2[a], array1) < 0) {
                    array1.push(array2[a]);
                }
            }
        }
        return array1;
    },
    /**
     * 强制合并json，将config合并到ojbect
     * @param object
     * @param config
     * @param defaults  默认值
     */
    mergeObject: function (object, config, defaults) {
        if (defaults && typeof defaults === 'object') {
            CommonFunc.mergeObject(object, defaults);
        }
        if (object && config && typeof config === 'object') {
            for (var i in config) {
                object[i] = config[i];
            }
        }
        return object;
    },
    /**
     * 合并json，将config合并到ojbect，如果object中的对应属性为“undefined”，则使用config中的属性值替换，否则，保留object中的属性值
     * @param object
     * @param config
     */
    mergeObjectIf: function (object, config) {
        var property;
        if (object) {
            for (property in config) {
                if (object[property] === undefined) {
                    object[property] = config[property];
                }
            }
        }
        return object;
    },
    //Ajax请求
    ajaxPostForm: function (url, data, successFunc, errorFunc) {
        // var $=layui.jquery;

        if (!successFunc)
            successFunc = CommonFunc.ajaxSuccessResult;
        if (!errorFunc)
            errorFunc = CommonFunc.msgEx;

        //ajax调用方式1
        $.ajax({
            url: url,
            type: "post",
            contentType: "application/x-www-form-urlencoded",
            data: data,
            success: function (resp) {
                CommonFunc.successCallback(resp, successFunc);
            },
            error: errorFunc
        })

        //ajax调用方式2【限Post方式】
        // $.post(url, data, func);
    },
    //Ajax请求同步
    ajaxPostFormSync: function (url, data, successFunc, errorFunc) {
        //ajax调用方式1
        var config = {
            url: url,
            type: "post",
            contentType: "application/x-www-form-urlencoded",
            data: data,
            async: false
        };
        if (successFunc)
            config.success = successFunc;
        if (errorFunc)
            config.error = errorFunc;
        return $.ajax(config).responseJSON;
    },
    //成功提示
    ajaxPostJson: function (url, data, successFunc, errorFunc) {
        // var $=layui.jquery;

        if (!successFunc)
            successFunc = CommonFunc.ajaxSuccessResult;
        if (!errorFunc)
            errorFunc = CommonFunc.msgEx;

        //ajax调用方式1
        $.ajax({
            url: url,
            type: "post",
            contentType: "application/json",
            data: JSON.stringify(data),
            success: function (resp) {
                // console.log(resp);  //测试
                // console.log(successFunc);  //测试

                CommonFunc.successCallback(resp, successFunc);
            },
            error: errorFunc
        });
    },
    //请求成功后的回调函数
    successCallback: function (resp, callback) {
        // console.log(resp); //测试

        if(!CommonFunc.isEmpty(resp) && !CommonFunc.isEmpty(resp.success)) {

            if(resp.success) {
                callback(resp);
            } else {
                CommonFunc.msgFa(resp.errorMsg);
            }
        } else {
            callback(resp);
        }
    },
    //请求成功后的回调函数
    ajaxSuccessResult: function (data) {
        if (CommonFunc.isEmpty(data))
            CommonFunc.msgSu();
        else if (data.success)
            CommonFunc.msgSu(data.errorMsg);
        else
            CommonFunc.msgFa(data.errorMsg);
    },
    //确认框
    confirm: function (msg) {
        return confirm(msg);
    },
    //异步确认框
    //confirm(callback)
    //confirm(callback,configs)
    //confirm(msg,callback)
    //confirm(msg,callback,configs)
    confirmAsync: function () {
        var msg, callback, configs;
        if (arguments.length == 0) {
            throw new Error('参数错误');
        } else {
            var first = arguments[0];
            if (typeof (first) == 'function') {
                callback = first;
                if (arguments.length > 1) {
                    configs = arguments[1];
                }
            } else if (typeof (first) == 'string') {
                msg = first;
                if (arguments.length > 1) {
                    callback = arguments[1];
                    if (!typeof (callback) == 'function') {
                        throw new Error('参数错误');
                    } else {
                        if (arguments.length > 2) {
                            configs = arguments[2];
                        }
                    }
                }
            }
        }

        var defaults = {
            icon: 3
        };
        defaults = CommonFunc.mergeObject(defaults, configs);
        if (!msg || msg.length == 0) {
            msg = "请确认您的操作！";
        }
        layer.confirm(msg, defaults, function (index) {
            layer.close(index);
            if (!CommonFunc.isEmpty(callback))
                callback();
        }, function (index) {
            layer.close(index);
        });
    },
    /**
     * 成功提示
     * @param time  显示的时间长度（单位：毫秒），默认1000
     */
    msgSu: function (msg, time) {
        if (CommonFunc.isEmpty(msg)) {
            msg = "操作成功";
        }
        if (CommonFunc.isEmpty(time)) {
            time = 1000;
        }

        layer.msg(msg, {
            time: time
        });
    },
    //成功提示
    msgSuSimple: function () {
        CommonFunc.msgSu(null);
    },
    //成功提示后，回调对应的函数
    msgSuCallBack: function (msg, successFunc) {
        if (typeof (msg) == undefined || msg == null || msg == "")
            msg = "操作成功";
        layer.msg(msg, successFunc);
    },
    //成功弹窗
    msgSuModal: function (msg, myAttrs) {
        if (CommonFunc.isEmpty(myAttrs)) {
            myAttrs = {};
        }
        var title = CommonFunc.isEmpty(myAttrs.title) ? '提示' : myAttrs.title;
        var btn = CommonFunc.isEmpty(myAttrs.btn) ? '确定' : myAttrs.btn;
        if (myAttrs.noBtn == true) {
            btn = '';
        }
        layer.alert(msg, {
            icon: 1,
            title: title,
            btn: btn,
            shadeClose: true
        });
    },
    //成功弹窗
    msgSuTips: function (msg, targetId, myAttrs) {
        if (CommonFunc.isEmpty(myAttrs)) {
            myAttrs = {};
        }
        var time = CommonFunc.isEmpty(myAttrs.time) ? 3000 : myAttrs.time;
        var tipWrapperId;
        if (time == 0) {
            time = 5000;
            tipWrapperId = CommonFunc.generateRandomMixed(10);
            msg = '<div id="' + tipWrapperId + '">' + msg
                + '<i class="fa fa-times-circle fa-lg" style="position: absolute; top : 10px; right: 10px"></i></div>';
        }
        var tipIndex = layer.tips(msg, CommonFunc.formatIdForJquery(targetId), {
            tips: [2, '#777777'],
            time: time
        });
        if (!CommonFunc.isEmpty(tipWrapperId)) {
            $(CommonFunc.formatIdForJquery(tipWrapperId)).on("mouseleave", function () {
                layer.close(tipIndex);
            });
            $(CommonFunc.formatIdForJquery(tipWrapperId) + ' .fa-times-circle').on("click", function () {
                layer.close(tipIndex);
            });
        }
    },
    //失败提示
    msgFa: function (msg) {
        if (typeof (msg) == undefined || msg == null || msg == "")
            msg = "操作失败";
        layer.alert(msg, {
            icon: 2,
            title: '错误'
        });
    },
    //失败提示
    msgFaSimple: function () {
        CommonFunc.msgFa(null);
    },
    //警告提示
    msgWa: function (msg) {
        layer.alert(msg, {
            icon: 0,
            title: '警告'
        });
    },
    //异常提示
    msgEx: function (msg, callback) {
        // console.log(msg);   //测试
        // console.log(callback);   //测试

        msg = (typeof (msg) != 'string') ? "操作异常：请重试！" : msg;
        layer.alert(msg, {
            icon: 5,
            title: '异常',
            yes: function (index, layero) {
                if (!CommonFunc.isEmpty(callback) && typeof (callback) == 'function') {
                    callback();
                }
                layer.close(index); //如果设定了yes回调，需进行手工关闭
            }
        });
    },
    //等待弹窗
    msgLoading: function (msg, maxWaitSeconds, callback) {
        //生成倒计时容器的ID，后面加上随机数，可最大努力规避因为倒计时关闭失败导致的计时错误
        var remainSecondsID = 'remainSeconds_' + CommonFunc.generateRandomMixed(10);

        var layerLoadingIntervalID = null;
        var content = "<div style='text-align: center; line-height: 30px; padding: 5px;'>" +
            "<div>" + msg + "</div>" +
            "<div>" +
            "<i class='fa fa-spinner fa-pulse margin-right-5'></i>剩余" +
            "<span id='" + remainSecondsID + "' style='font-weight: 600'>" + maxWaitSeconds + "</span>秒</div>" +
            "</div>";
        var layerLoadingIndex = layer.open({
            type: 1,
            title: false,
            shade: [0.3, '#393D49'],
            closeBtn: 0,
            // skin: 'layui-layer-rim', //加上边框
            // area: '300px', //宽高
            content: content,
            success: function (layero, index) {
                //设置倒计时
                layerLoadingIntervalID = window.setInterval(function () {
                    $('#' + remainSecondsID).html(--maxWaitSeconds);
                    if (maxWaitSeconds < 1) {
                        if (!CommonFunc.isEmpty(callback))
                            callback();
                        window.clearInterval(layerLoadingIntervalID);
                        layer.close(index);
                    }
                }, 1000);
            }
        });
        return {
            layerLoadingIndex: layerLoadingIndex,
            layerLoadingIntervalID: layerLoadingIntervalID
        };
    },
    //关闭等待弹窗
    msgLoadingClose: function (index) {
        if (!CommonFunc.isEmpty(index))
            layer.close(index);
        else
            layer.closeAll();
    },
    /**
     * 弹窗显示信息
     * @param title 标题
     * @param content   内容
     * @param isFull    是否全屏
     */
    msgShowInfo: function (title, content, isFull) {
        if (!CommonFunc.isEmpty(content)) {
            if(CommonFunc.isEmpty(title)) {
                title = "信息";
            }
            var index = layer.open({
                type: 1,
                title:title,
                skin: 'layui-layer-rim', //加上边框
                area: '1000px', //宽高
                shadeClose : true,
                maxmin: true,
                content: '<div><textarea style="width: 100%;border: 0;height: 545px;">'
                    + content + '</textarea></div>'
            });
            if(isFull == true) {
                layer.full(index);
            }
        }
    },
    /**
     * 弹出简单输入框
     * @param title 弹层标题
     * @param callback  回调函数
     * @param formType  //输入框类型，支持0（文本）默认1（密码）2（多行文本）
     */
    prompt: function (title, callback, formType) {
        if (CommonFunc.isEmpty(formType))
            formType = 0;
        layer.prompt({title: title, formType: formType}, function (value, index) {
            layer.close(index);
            callback(value);
        });
    },
    /**
     * 清空Form
     */
    clearForm: function (formID) {
        formID = CommonFunc.formatIdForJquery(formID);
        var form = $(formID)[0];
        form.reset();
        $(formID + " input").attr("value", "");
    },
    /**
     * 初始化form，传入一个json对象，根据元素name为form赋值
     * 依赖：jQuery v1.5 or later
     * 说明：1、此方法能赋值一般所有表单，但考虑到checkbox的赋值难度，以及表单中很少用checkbox，这里不对checkbox赋值
     *     2、此插件现在只接收json赋值，不考虑到其他的来源数据
     *     3、对于特殊的textarea，比如CKEditor,kindeditor...，他们的赋值有提供不同的自带方法，这里不做统一，如果项目中有用到，不能正确赋值，请单独赋值
     */
    setForm: function (formID, jsonValue) {
        formID = CommonFunc.formatIdForJquery(formID);
        var form = $(formID);
        //如果传入的json字符串，将转为json对象
        if ($.type(jsonValue) === "string") {
            jsonValue = $.parseJSON(jsonValue);
        }
        //如果传入的json对象为空，则不做任何操作
        if (!$.isEmptyObject(jsonValue)) {
            var hasFocus = false;
            $.each(jsonValue, function (key, value) {
                var formField = form.find("[name='" + key + "']");
                if ($.type(formField[0]) === "undefined") {
//					console.log("没找到指定name["+key+"]的表单");
                } else {
                    var fieldTagName = formField[0].tagName.toLowerCase();
                    if (fieldTagName == "input") {
                        if (formField.attr("type") == "radio") {
                            $("input:radio[name='" + key + "'][value='" + value + "']")
                                .attr("checked", "checked");
                        } else {
                            formField.val(value);
                        }
                        //设置焦点
                        if (formField.attr("type") == "text" && !hasFocus) {
                            formField.focus();
                            hasFocus = true;
                        }
                    } else if (fieldTagName == "select") {
                        formField.val(value);
                    } else if (fieldTagName == "textarea") {
                        formField.val(value);
                    } else if (fieldTagName == "label") {
                        formField.text(value);
                    } else {
                        formField.val(value);
                    }
                }
            });
        }
        return form; //返回对象，提供链式操作
    },
    /**
     * 根据元素name获取form值，返回一个就是对象
     */
    getForm: function (formID) {
        formID = CommonFunc.formatIdForJquery(formID);
        var form = $(formID);
        var o = {};
        var a = form.serializeArray();
        $.each(a, function () {
            if (o[this.name] !== undefined) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o; //返回对象，提供链式操作
    },
    /**
     * 将简单对象转为map
     * @param data  待绑定的数据(item1 - 键， item2 - 值)
     */
    getSimpleMap: function (data) {
        var result = {};
        for (var i = 0; i < data.length; i++) {
            result[data[i].item1] = data[i].item2;
        }
        return result;
    },
    /**
     * 获取select下拉列表的所有值与名称，以json格式返回
     */
    getSelectOptions: function (selID) {
        var result = {};
        selID = CommonFunc.formatIdForJquery(selID);
        $(selID + ' option').each(function () {
            var txt = $(this).text();	//获取单个text
            var val = $(this).val();	//获取单个value
            result[val] = txt;
        });
        return result;
    },
    /**
     * 绑定Select下拉列表
     * @param selID 下拉列表的ID
     * @param data  待绑定的数据(item1 - 键， item2 - 值)
     * @param defaultValue  默认值，如果为null，则默认不选中任何项
     */
    bindSelect: function (selID, data, defaultValue) {
        var myAttrs = {
            defaultValue: defaultValue
        };
        CommonFunc.bindSelectAdvanced(selID, data, myAttrs);
    },
    /**
     * 绑定Select下拉列表
     * @param selID 下拉列表的ID
     * @param data  待绑定的数据(item1 - 键， item2 - 值)
     * @param myAttrs  自定义属性如下
     *    select2    是否使用select2插件
     *    defaultValue  默认值（优先级高于索引）
     *    defaultIndex  默认值的索引
     *    required  是否需要请选择
     *    requiredText  请选择的自定义名称
     */
    bindSelectAdvanced: function (selID, data, myAttrs) {
        // console.log(selID); //测试
        // console.log(data); //测试
        // console.log(myAttrs); //测试

        selID = CommonFunc.formatIdForJquery(selID);
        var options = '';
        var optionsSelect2 = [];
        if (CommonFunc.isEmpty(myAttrs))
            myAttrs = {};
        //是否使用select2插件
        var select2 = myAttrs.select2;
        //获取默认值
        var defaultValue = myAttrs.defaultValue;
        var defaultIndex = myAttrs.defaultIndex;
        //拼接下拉选项
        for (var i = 0; i < data.length; i++) {
            if (defaultValue == data[i].item1)
                defaultIndex = i;
            if (CommonFunc.isEmpty(data[i].item1) && CommonFunc.isEmpty(data[i].item2)) {
                options += '<option value="' + data[i] + '">' + data[i] + '</option>';
            } else if (select2) {
                optionsSelect2.push({id: data[i].item1, text: data[i].item2})
            } else {
                options += '<option value="' + data[i].item1 + '">' + data[i].item2 + '</option>';
            }
        }
        //判断是否添加“请选择”
        myAttrs.requiredText = CommonFunc.isEmpty(myAttrs.requiredText) ? '请选择' : myAttrs.requiredText;
        if (myAttrs.required) {
            defaultIndex = 0;
            options = '<option value="">' + myAttrs.requiredText + '</option>' + options;
        }
        //绑定下拉列表
        $(selID).html(options);
        //设置默认选项
        if (select2) {
            $(selID).select2();
            $(selID).select2({
                data: optionsSelect2,
                placeholder: myAttrs.requiredText
            });
        } else {
            if (CommonFunc.isEmpty(defaultIndex)) {
                $(selID).get(0).selectedIndex = -1;
            } else {
                $(selID).get(0).selectedIndex = defaultIndex;
            }
        }
    },
    /**
     * 添加数据到下拉列表
     */
    addToSelect: function (selID, value, text) {
        // console.log(selID); //测试

        selID = CommonFunc.formatIdForJquery(selID);
        var option = '<option value="' + value + '">' + text + '</option>';
        $(selID).append(option);
    },
    /**
     * 设置下拉列表的选中项
     */
    setSelected: function (selID, value) {
        selID = CommonFunc.formatIdForJquery(selID);
        $(selID).find("option[value='" + value + "']").attr("selected", true);
    },
    /**
     * 根据索引号设置下拉列表的选中项
     * @param selectIndex   索引号，如果为空，则选择第一项
     */
    setSelectedByIndex: function (selID, selectIndex) {
        if (CommonFunc.isEmpty()) {
            selectIndex = 0;
        }
        selID = CommonFunc.formatIdForJquery(selID);
        $(selID).get(0).selectedIndex = selectIndex;
    },
    /**
     * 从下拉列表删除数据
     */
    deleteFromSelectByIndex: function (selID, index) {
        selID = CommonFunc.formatIdForJquery(selID);
        $(selID + " option[index='" + index + "']").remove();
    },
    /**
     * 从下拉列表删除数据
     */
    deleteFromSelectByValue: function (selID, value) {
        selID = CommonFunc.formatIdForJquery(selID);
        $(selID + " option[value='" + value + "']").remove();
    },
    /**
     * 从下拉列表删除数据
     */
    deleteFromSelectByText: function (selID, text) {
        selID = CommonFunc.formatIdForJquery(selID);
        $(selID + " option[text='" + text + "']").remove();
    },
    /**
     * 设置cookie
     */
    setCookie: function (c_name, value, expiredays) {
        var exdate = new Date()
        exdate.setDate(exdate.getDate() + expiredays)
        document.cookie = c_name + "=" + escape(value) +
            ((expiredays == null) ? "" : ";expires=" + exdate.toGMTString())
    },
    /**
     * 获取cookie
     */
    getCookie: function (c_name) {
        if (document.cookie.length > 0) {
            var c_start = document.cookie.indexOf(c_name + "=")
            if (c_start != -1) {
                c_start = c_start + c_name.length + 1
                c_end = document.cookie.indexOf(";", c_start)
                if (c_end == -1) c_end = document.cookie.length
                return unescape(document.cookie.substring(c_start, c_end))
            }
        }
        return ""
    },
    /**
     * 获取字符长度，一个汉字相当于两个英文字符
     */
    size: function (inStr) {
        if (CommonFunc.isEmpty(inStr)) {
            return 0;
        } else {
            var re = /[\u4E00-\u9FA5]/g;  //测试中文字符的正则
            var len = inStr.length;
            var lenChinese;
            if (re.test(inStr))
                lenChinese = inStr.match(re).length;
            else
                lenChinese = 0;
            return len + lenChinese;
        }
    },
    /**
     * 判断是否是汉字
     */
    isChinese: function (inStr) {
        if (CommonFunc.isEmpty(inStr)) {
            return false;
        } else {
            var re = /[\u4E00-\u9FA5]/g;  //测试中文字符的正则
            var len = inStr.length;
            var lenChinese;
            if (re.test(inStr))
                lenChinese = inStr.match(re).length;
            else
                lenChinese = 0;
            return len == lenChinese;
        }
    },
    /**
     * 如果字符超过规定长度，设置省略号
     */
    ellipsisString: function (inStr, len) {
        if (CommonFunc.isEmpty(inStr) || len < 1 || CommonFunc.size(inStr) < len) {
            return inStr;
        } else {
            var result = '';
            for (var i = 0; i < inStr.length; i++) {
                if (len < 1)
                    break;

                result += inStr.charAt(i);
                if (CommonFunc.isChinese(inStr.charAt(i)))
                    len -= 2;
                else
                    len--;
            }

            return result + '..';
        }
    },
    //生成随机字符（字母与数字混合）
    generateRandomMixed: function (len) {
        var res = "";
        for (var i = 0; i < len; i++) {
            var id = Math.floor(Math.random() * 36);
            res += CommonFunc.chars[id];
        }
        return res;
    },
    /**
     * 获取框架容器对象
     */
    getFrameObj: function () {
        return $('div.page-content');
    },
    /**
     * 获取框架容器的高度
     */
    getFrameHeight: function () {
        var $PageContent = CommonFunc.getFrameObj();
        return $PageContent.outerHeight(true);
    },
    /**
     * 将元素高度设为容器的最大高度；
     * 如果wrapperId1和wrapperId2不为空，wrapperId3为空，则是左右结构；
     * 如果三个参数都不为空，则是左右结构，其中右侧是上下结构，wrapperId2在上，wrapperId3在下；
     */
    maxHeightToFrame: function (wrapperId1, wrapperId2, wrapperId3) {
        var idTemp;
        if (typeof(wrapperId1) == 'string') {
            wrapperId1 = CommonFunc.formatIdForJquery(wrapperId1);
        } else {
            idTemp = $(wrapperId1).attr('id');
            if (!CommonFunc.isEmpty(idTemp)) {
                wrapperId1 = CommonFunc.formatIdForJquery(idTemp);
            }
        }
        if (typeof(wrapperId2) == 'string') {
            wrapperId2 = CommonFunc.formatIdForJquery(wrapperId2);
        } else {
            idTemp = $(wrapperId2).attr('id');
            if (!CommonFunc.isEmpty(idTemp)) {
                wrapperId2 = CommonFunc.formatIdForJquery(idTemp);
            }
        }
        if (typeof(wrapperId3) == 'string') {
            wrapperId3 = CommonFunc.formatIdForJquery(wrapperId3);
        } else {
            idTemp = $(wrapperId3).attr('id');
            if (!CommonFunc.isEmpty(idTemp)) {
                wrapperId3 = CommonFunc.formatIdForJquery(idTemp);
            }
        }
        $wrapperId1 = $(wrapperId1);
        $wrapperId2 = $(wrapperId2);
        $wrapperId3 = $(wrapperId3);

        var frameHeightOffset = 67;  //框架的高度偏移量
        // var $PageContent = $('div.page-content');
        // var heightPageContent = $PageContent.outerHeight(true);
        var $PageContent = CommonFunc.getFrameObj();
        var heightPageContent = CommonFunc.getFrameHeight();
        heightPageContent = heightPageContent - frameHeightOffset;
        var treeWrapper = '#treeWrapper';
        //删除已有的高度限制，如果是树的容器，则不用设置高度
        if (treeWrapper != wrapperId1) {
            $wrapperId1.css('height', '');
        }
        //调整左右联测的高度
        var leftPart = $wrapperId1.outerHeight(true);
        var baseHeight;
        //如果id2不为空，id3为空
        if (!CommonFunc.isEmpty(wrapperId2) && CommonFunc.isEmpty(wrapperId3)) {
            $wrapperId2.css('height', '');
            var rightPart = $wrapperId2.outerHeight(true);
            baseHeight = (rightPart > heightPageContent) ? rightPart : heightPageContent;
        } else if (!CommonFunc.isEmpty(wrapperId2) && !CommonFunc.isEmpty(wrapperId3)) {
            $wrapperId2.css('height', '');
            $wrapperId3.css('height', '');
            var rightPart = $wrapperId2.outerHeight(true) + $wrapperId3.outerHeight(true);
            baseHeight = (rightPart > heightPageContent) ? rightPart : heightPageContent;
        } else {
            baseHeight = heightPageContent;
        }
        //如果是树的容器，则不用设置高度
        if (leftPart > baseHeight) {
            if (wrapperId1 != treeWrapper) {
                baseHeight = leftPart;
            } else {
                //下面两句的目的是使框架容器的高度恢复原样
                $wrapperId1.css('height', '300px');
                if (!CommonFunc.isEmpty(wrapperId2) && CommonFunc.isEmpty(wrapperId3)) {
                    $wrapperId2.css('height', '300px');
                } else if (!CommonFunc.isEmpty(wrapperId2) && !CommonFunc.isEmpty(wrapperId3)) {
                    $wrapperId2.css('height', '200px');
                    $wrapperId2.css('height', '100px');
                }

                heightPageContent = $PageContent.outerHeight(true);
                heightPageContent = heightPageContent - frameHeightOffset;
                baseHeight = heightPageContent;
            }
        }
        $wrapperId1.css('height', baseHeight + 'px');

        // console.log(baseHeight);    //测试

        if (!CommonFunc.isEmpty(wrapperId2) && CommonFunc.isEmpty(wrapperId3)) {

            // console.log(wrapperId2);    //测试

            $wrapperId2.css('height', baseHeight + 'px');
        } else if (!CommonFunc.isEmpty(wrapperId2) && !CommonFunc.isEmpty(wrapperId3)) {
            var heightRightUp = $wrapperId2.outerHeight(true);
            $wrapperId3.css('height', (baseHeight - heightRightUp - 15) + 'px');
        }
    },
    //将两个容器的高度设为相同
    sameWrapperHeight: function (wrapperId1, wrapperId2) {
        wrapperId1 = CommonFunc.formatIdForJquery(wrapperId1);
        wrapperId2 = CommonFunc.formatIdForJquery(wrapperId2);
        //删除已有的高度限制
        $(wrapperId1).css('height', '');
        $(wrapperId2).css('height', '');
        //调整左右联测的高度
        var leftPart = $(wrapperId1).outerHeight(true);
        var rightPart = $(wrapperId2).outerHeight(true);
        if (rightPart > leftPart) {
            $(wrapperId1).height(rightPart);
        } else {
            $(wrapperId2).height(leftPart);
        }
    },
    //获取网址参数
    getQueryString: function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return decodeURI(r[2]);
        return null;
    },
    /**
     * 拼接URL参数
     * @param obj   URL参数（Json对象）
     * @param baseUrl   如果baseUrl不为空，则拼接完整的网址返回，并使用encodeURI转义
     */
    joinUrlParams: function (obj, baseUrl) {
        // console.log(obj);   //测试

        var urlParams = '';
        if (!CommonFunc.isEmpty(obj)) {
            var i = 0;
            for (var a in obj) {
                if (!CommonFunc.isEmpty(urlParams)) {
                    urlParams += "&";
                }
                urlParams = urlParams + a + "=" + obj[a];
                i++;
            }

            //如果对象是空的，则直接返回空或baseUrl
            if (i < 1) {
                return CommonFunc.isEmpty(baseUrl) ? "" : baseUrl;
            }

            urlParams = "?" + urlParams;
            if (!CommonFunc.isEmpty(baseUrl)) {
                urlParams = encodeURI(baseUrl + urlParams);
            }
        }
        return urlParams;
    },

    /**
     * 设置滚动条
     * @param element   组件ID或对象
     */
    setScrollBarByElement: function (element) {
        var myAttrs = {};
        myAttrs.idWrapper = element;
        CommonFunc.setScrollBar(myAttrs);
    },
    /**
     * 设置竖直滚动条滚动条
     * @param element   组件ID或对象
     */
    setScrollBarYByElement: function (element) {
        var myAttrs = {};
        myAttrs.idWrapper = element;
        myAttrs.axis = "y"
        CommonFunc.setScrollBar(myAttrs);
    },
    //设置滚动条
    setScrollBarWithWrapper: function (myAttrs) {
        CommonFunc.setScrollBar(myAttrs);

        //设置容器
        var idWrapperId1 = '#wrapperId1';
        var idTreeWrapper = '#treeWrapper';
        if (CommonFunc.isEmpty(myAttrs))
            myAttrs = {};
        myAttrs.idWrapper = idWrapperId1;
        CommonFunc.setScrollBar(myAttrs);
        myAttrs.idWrapper = idTreeWrapper;
        CommonFunc.setScrollBar(myAttrs);
    },
    //设置滚动条
    setScrollBar: function (myAttrs) {
        if (!CommonFunc.isEmpty(myAttrs)) {
            if(CommonFunc.isEmpty(myAttrs.axis)) {
                myAttrs.axis = "yx"
            }
            var idWrapper = myAttrs.idWrapper;
            var obj = $(idWrapper);
            if (obj.length > 0) {
                $(idWrapper).mCustomScrollbar({
                    autoHideScrollbar: true,	//自动隐藏滚动条
                    axis: myAttrs.axis, //水平与竖直都出现滚动条
                    theme: "minimal-dark",
                });
            }
        }

    },
    //获取当前URL
    getURL: function (myAttrs) {
        var url = window.location.href;
        //删除URL最后的井号
        if (url.substr(url.length - 1, 1) == '#')
            url = url.substr(0, url.length - 1);
        if (!CommonFunc.isEmpty(myAttrs)) {
            if (!myAttrs.needParam) {
                var stop = url.indexOf("?");
                if (stop > 0)
                    url = url.substring(0, stop);
                else
                    url = url.substring(0);
            }
        }
        return url;
    },
    /**
     * 计算百分比
     * 四舍五入
     * 小数点后2位
     * 4.78=4.78%
     * @param num   被除数
     * @param total 除数
     */
    calPercent: function (num, total) {
        if (total > 0) {
            return (Math.round(num / total * 10000) / 100.00);// 小数点后两位百分比
        } else {
            return '~';
        }
    },
    /**
     * 在输入的字符或数字前补零
     * @param num 目标对象
     * @param len 目标长度
     */
    addPreZero: function (num, len) {
        var t = (num + '').length;
        if (len > t) {
            var s = '';
            for (var i = 0; i < len - t; i++) {
                s += '0';
            }
            return s + num;
        } else {
            return num;
        }
    },
    /**
     * 截取字符串，如果超过长度，后面加上省略号“..”
     */
    substr: function (str, len) {
        var t = (str + '').length;
        if (len > 0 && len < t) {
            return str.substr(0, len) + '..';
        } else {
            return str;
        }
    },
    /**
     * 匹配字符串出现的次数
     * @param str   待检测的源字符串
     * @param target    检测的目标字符串
     */
    matchCount: function (str, target) {
        var matchTarget=eval("/"+target+"/ig");
        var matchResult = str.match(matchTarget);
        return CommonFunc.isEmpty(matchResult) ? 0 : matchResult.length;
    },
}