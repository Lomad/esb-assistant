/**
 * Created by xuehao on 2017/08/10.
 */

//公用的请求前缀
var ajaxReqPre = "/ajax/inspection/main/";
var ajaxIndexReqPre = "/ajax/inspection/index/";
// var ajaxDetailReqPre = "/ajax/inspection/detail/";
var ajaxStepReqPre = "/ajax/inspectionStep/";

//ID
var formContainerID = '#editFormContainer';
var formID = '#editForm';
var handleFormContainer = '#handleFormContainer';
var handleForm = '#handleForm';
var idResult = '#result';
var idResultHandle = '#resultHandle';
var idInsList = '#insList';
var idBtnBeginInspection = '#btnBeginInspection';
// var idChartSkin = '#chartSkin';
var idProgressSkin = '#progressSkin';
var idProgressItem = 'progressItem_';
var idProgressItemImg = 'progressItemImg_';
var idProgressItemInfo = 'progressItemInfo_';

//全局变量
var inspectionIndexList;
var inspectFinsihCount = 0;   //巡检完成的条数
var btnBeginInspection_In = '正在巡检...';
var btnBeginInspection_Finish = '巡检完成';
var inspectionObj = {
    result: '',
    check_uid: '',
    check_desp: '',
    time_len: 0,
    btime: '',
    etime: ''
};
var page_StartIndex = 0;
var page_TotalSize = 0;
var page_Size = 10;

$(document).ready(function () {
    global_Object.initDomEvent();
});

var global_Object = {
    ajaxGetBaseInfo: contextPath + ajaxReqPre + 'getBaseInfo',
    ajaxQuery: contextPath + ajaxReqPre + 'query',
    ajaxQueryByID: contextPath + ajaxReqPre + 'queryByID',
    ajaxSave: contextPath + ajaxReqPre + 'save',
    ajaxSaveResult: contextPath + ajaxReqPre + 'saveResult',
    ajaxIndexList: contextPath + ajaxIndexReqPre + 'list',
    ajaxInspectionStep: contextPath + ajaxStepReqPre,
    initDomEvent: function () {
        // console.log('init');  //测试

        //绑定基础信息
        CommonFunc.ajaxPostForm(global_Object.ajaxGetBaseInfo, null, global_Object.bindBaseInfo);

        //初始化巡检指标
        CommonFunc.ajaxPostFormSync(global_Object.ajaxIndexList, null, global_Object.initInsIndex);

        //初始化历史巡检
        global_Object.initHistory();

        //开始巡检
        $(idBtnBeginInspection).on("click", function () {
            global_Object.beginInspection();  //刷新列表
        });
    },
    bindBaseInfo: function (data) {
        // console.log(data);  //测试

        CommonFunc.bindSelect(idResult, data.map.result);
        CommonFunc.bindSelect(idResultHandle, data.map.result);
    },
    //初始化历史巡检【type：1-上页，2-下页】
    initHistory: function (type) {
        // console.log(type);  //测试

        var startIndex = 0;
        if (type)
            startIndex = (type == 1) ? page_StartIndex - page_Size : page_StartIndex + page_Size;
        if (type == 1 && startIndex < 0) {
            CommonFunc.msgSu('已达首页');
            return;
        } else if (type == 2 && startIndex >= page_TotalSize) {
            CommonFunc.msgSu('已达末页');
            return;
        }

        //获取数据
        var reqData = {
            datas: JSON.stringify({
                startIndex: startIndex,
                pageSize: page_Size
            })
        };
        var resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxQuery, reqData);
        if (!resp.success) {
            CommonFunc.msgEx('获取巡检历史失败！' + resp.errorMsg);
            return;
        }
        var html = '';
        var datas = resp.datas;
        if (datas && datas.length > 0) {
            var mainData;
            var axisColor, axisImg, titleClass;
            for (var i = 0, len = datas.length; i < len; i++) {
                mainData = datas[i];
                html += '<li class="layui-timeline-item">';
                //时间线图标
                if (mainData.result && mainData.result == 1) {
                    axisImg = "x1005";
                    axisColor = "009933";
                    titleClass = "timeline-success";
                } else if (mainData.result && mainData.result == 2) {
                    axisImg = "x1007";
                    axisColor = "CC0000";
                    titleClass = "timeline-error";
                } else {
                    axisImg = "xe63f";
                    axisColor = "444444";
                    titleClass = "timeline-normal";
                }
                var btime = new Date(Date.parse(mainData.btime.replace(/-/g, "/")));
                html += '<i class="layui-icon layui-timeline-axis" style="color: #' + axisColor + ';">&#' + axisImg + ';</i>';
                //时间线内容
                html += '<div class="layui-timeline-content layui-text">';
                html += '<h3 class="layui-timeline-title"';
                if(!CommonFunc.isEmpty(mainData.check_desp)) {
                    html += ' title="'+mainData.check_desp+'"';
                }
                html += '>';
                if (mainData.result == 1) {
                    html += "巡检正常";
                } else if (mainData.result == 2) {
                    html += "巡检有错误 "+CommonFunc.substr(mainData.check_desp, 10);
                } else {
                    html += "未知";
                }
                html += '<div class="pull-right">' + (btime.Format(DateUtils.formatFriendly)) + '</div>';
                html += '</h3>';
                html += '<p>';
                html += ' <a onclick="global_Object.queryMainDetails(' + mainData.id + ')">巡检详细</a> ';
                if (!CommonFunc.isEmpty(mainData.result_desp)) {
                    html += ' <span class="pull-right">已处理</span> ';
                }
                html += '</p>';
                html += '</div>';
                html += '</li>';
            }
        }
        $(idInsList).html(html);
        //保存分页信息
        page_StartIndex = startIndex;
        page_TotalSize = resp.totalSize;
        //设置高度
        // global_Object.setWrapperHeight();
        CommonFunc.sameWrapperHeight('divLeftWrapper', 'divRightWrapper');
    },
    //初始化巡检指标
    initInsIndex: function (data) {
        // console.log(data);    //测试

        if (!data.success) {
            CommonFunc.msgEx('获取巡检指标失败！' + data.errorMsg);
            return;
        }
        var html = '';
        var datas = data.datas;
        if (datas && datas.length > 0) {
            var axisColor, titleClass;
            for (var i = 0, len = datas.length; i < len; i++) {
                html += '<div class="progress-item">';
                //标题
                html += '<div class="progress-title">' + datas[i].name + ' ';
                html += '</div>';
                //描述
                html += '<div class="progress-desp">' + datas[i].desp + '</div>';
                //进度条
                html += '<div class="row">';
                html += '   <div class="col-md-11 col-sm-11">';
                html += '       <div class="progress">';
                html += '           <div id="' + idProgressItem + datas[i].id + '" class="progress-bar progress-bar-striped" style="width: 0%"></div>';
                html += '       </div>';
                html += '   </div>';
                html += '   <div class="col-md-1 col-sm-1" style="padding-left: 0; padding-right: 0">';
                html += '       <i id="' + idProgressItemImg + datas[i].id + '" class="hide"></i>';
                html += '       <i id="' + idProgressItemInfo + datas[i].id + '" title="查看错误明细" class="hide"></i>';
                html += '   </div>';
                html += '</div>';
                //结束符
                html += '</div>';
            }
            inspectionIndexList = datas;
        }
        $(idProgressSkin).html(html);
        //设置高度
        // global_Object.setWrapperHeight();
        CommonFunc.sameWrapperHeight('divLeftWrapper', 'divRightWrapper');
    },
    //开始巡检
    beginInspection: function () {
        // console.log(inspectionIndexList);    //测试

        var btnText = $(idBtnBeginInspection).children('span').html();
        if (btnBeginInspection_In == btnText) {
            CommonFunc.msgSu('正在巡检，请稍候...');
            return;
        } else {
            $(idBtnBeginInspection).children('span').html(btnBeginInspection_In);
        }

        //巡检对象
        var btime = new Date();
        //设置初始值
        inspectFinsihCount = 0;
        //开始每个巡检
        if (inspectionIndexList && inspectionIndexList.length > 0) {
            var insID;
            for (var i = 0, len = inspectionIndexList.length; i < len; i++) {
                insID = inspectionIndexList[i].id;
                inspectionIndexList[i].obj = {};    //用于保存每个巡检的结果对象
                inspectionIndexList[i].timer = global_Object.simulateLoading(insID); //显示模拟加载效果
                inspectionIndexList[i].obj.btime = new Date();  //设置开始时间
                inspectionIndexList[i].obj.index_id = inspectionIndexList[i].id;
                CommonFunc.ajaxPostForm(global_Object.ajaxInspectionStep + inspectionIndexList[i].action, null, global_Object.inspectionResult);
            }

            //1秒钟检测一次巡检是否完成
            var timer = setInterval(function () {
                if (inspectFinsihCount == inspectionIndexList.length) {
                    clearInterval(timer);
                    $(idBtnBeginInspection).children('span').html(btnBeginInspection_Finish);

                    //获取单项的巡检结果
                    var finalResult = 1;
                    var check_desp = '', detailError = '';
                    for (var i = 0, len = inspectionIndexList.length; i < len; i++) {
                        if (inspectionIndexList[i].obj.result && inspectionIndexList[i].obj.result == 2) {
                            finalResult = 2;
                        }
                        if (inspectionIndexList[i].obj.result == 2 && !CommonFunc.isEmpty(inspectionIndexList[i].obj.desp)) {
                            if (detailError) {
                                check_desp += "\r\n";
                                detailError += "\r\n";
                            }
                            check_desp += '【' + inspectionIndexList[i].name + '】巡检发现异常！';
                            detailError += '【' + inspectionIndexList[i].name + '】' + inspectionIndexList[i].obj.desp;
                        }
                    }

                    //设置巡检对象值
                    var etime = new Date();
                    inspectionObj.result = finalResult;
                    inspectionObj.check_desp = check_desp;
                    inspectionObj.details = detailError;
                    inspectionObj.time_len = etime.getTime() - btime.getTime();
                    inspectionObj.time_len_SHOW = Math.ceil((etime.getTime() - btime.getTime()) / 1000);
                    inspectionObj.btime = btime.Format(DateUtils.formatBase);
                    inspectionObj.etime = etime.Format(DateUtils.formatBase);

                    // console.log(obj);//测试

                    //打开巡检结果窗口
                    global_Object.showModal();
                    CommonFunc.setForm(formID, inspectionObj);
                }
            }, 1000);
        }
    },
    //模拟加载效果（最多加载到90%）
    simulateLoading: function (insID) {
        // console.log(data);    //测试

        var interval = 300 + Math.random() * 1000;
        var itemID;
        var n = 0, timer = setInterval(function () {
            n = n + Math.random() * 10 | 0;
            if (n > 90) {
                n = 90;
                clearInterval(timer);
            }

            //设置进度条动态效果
            itemID = '#' + idProgressItem + insID;
            $(itemID).addClass('active');
            $(itemID).css('width', n + '%');
            //设置图标动态效果
            itemID = '#' + idProgressItemImg + insID;
            $(itemID).removeClass().addClass('fa fa-spinner fa-pulse');
        }, interval);
        return timer;
    },
    //处理单项的巡检结果
    inspectionResult: function (data) {
        // console.log(data);    //测试

        var action = data.data.action;
        var indexObject = global_Object.getObjectByAction(action);
        var itemID;
        var classProgressItemImg, classProgressItemInfo;

        //设置结果图标的样式
        if (data.success) {
            classProgressItemImg = 'fa fa-check-circle progress-img-success';
        } else {
            classProgressItemImg = 'fa fa-times-circle progress-img-error';
        }
        //设置提示图标的样式
        if (data.errorMsg) {
            classProgressItemInfo = 'fa fa-commenting-o shape-link progress-info-normal';
        } else {
            classProgressItemInfo = 'hide';
        }

        //停止模拟加载效果
        clearInterval(indexObject.timer);
        //设置进度条
        itemID = '#' + idProgressItem + indexObject.id;
        $(itemID).css("width", "100%");
        $(itemID).removeClass('active');
        //设置结果图标
        itemID = '#' + idProgressItemImg + indexObject.id;
        $(itemID).removeClass().addClass(classProgressItemImg);
        //设置提示图标
        itemID = '#' + idProgressItemInfo + indexObject.id;
        $(itemID).removeClass().addClass(classProgressItemInfo);
        if (!CommonFunc.isEmpty(data.errorMsg)) {
            $(itemID).unbind().on("click", function () {
                if (data.success) {
                    CommonFunc.msgSuModal(data.errorMsg);
                } else {
                    CommonFunc.msgFa(data.errorMsg);
                }
            });
        }

        //设置结果标志
        if (data.success) {
            indexObject.obj.result = 1;  //设置结果
        } else {
            indexObject.obj.result = 2;  //设置结果
        }

        //设置巡检时间
        var btime = indexObject.obj.btime;
        var etime = new Date();  //设置结束时间
        indexObject.obj.time_len = etime.getTime() - btime.getTime();
        indexObject.obj.btime = btime.Format(DateUtils.formatBase);
        indexObject.obj.etime = etime.Format(DateUtils.formatBase);
        indexObject.obj.desp = data.errorMsg; //保存错误信息

        inspectFinsihCount++;
    },
    //根据action获取对象
    getObjectByAction: function (action) {
        // console.log(action);    //测试

        if (action && inspectionIndexList && inspectionIndexList.length > 0) {
            for (var i = 0, len = inspectionIndexList.length; i < len; i++) {
                if (inspectionIndexList[i].action == action)
                    return inspectionIndexList[i];
            }
        }
    },
    //根据指标ID获取指标对象
    getObjectByIndexID: function (indexID) {
        // console.log(inspectionIndexList);    //测试

        if (indexID && inspectionIndexList && inspectionIndexList.length > 0) {
            for (var i = 0, len = inspectionIndexList.length; i < len; i++) {
                if (inspectionIndexList[i].id == indexID)
                    return inspectionIndexList[i];
            }
        }
    },
    //显示维护窗口
    showModal: function () {
        CommonFunc.clearForm(formID);
        $(formContainerID).modal("show");
    },
    afterPost: function (data) {
        // console.log(data);  //测试

        if (data.success) {
            CommonFunc.clearForm(formID);
            $(formContainerID).modal("hide");
            CommonFunc.clearForm(handleForm);
            $(handleFormContainer).modal("hide");
            global_Object.initHistory();
            CommonFunc.msgSu();
        } else {
            CommonFunc.msgFa(data.errorMsg);
        }
    },
    //type：0-新增巡检结果，1-更新处理结果
    saveClick: function (type) {
        // console.log(data);  //测试

        //设置巡检结果描述
        var mainObject;
        if (type == 0) {    //新增巡检结果
            mainObject = CommonFunc.getForm(formID);
            //设置明细信息的指标ID
            var inspectionDetails = [];
            if (inspectionIndexList && inspectionIndexList.length > 0) {
                for (var i = 0, len = inspectionIndexList.length; i < len; i++) {
                    inspectionDetails.push(inspectionIndexList[i].obj);
                }
            }
            inspectionObj.result_desp = mainObject.result_desp;
            //拼接巡检结果与明细信息
            var vals = {
                mainObject: JSON.stringify(inspectionObj),
                childrenObject: JSON.stringify(inspectionDetails)
            };

            // console.log(inspectionObj);  //测试
            // console.log(inspectionIndexList);  //测试
            // console.log(inspectionDetails);  //测试

            CommonFunc.ajaxPostForm(global_Object.ajaxSave, vals, global_Object.afterPost);
        } else {    //添加处理结果
            mainObject = CommonFunc.getForm(handleForm);
            var vals = {
                id: mainObject.id,
                result_desp: mainObject.result_desp,
                result_uid: 0,
                result_time: new Date()
            };

            // console.log(mainObject);  //测试
            // console.log(vals);  //测试

            CommonFunc.ajaxPostForm(global_Object.ajaxSaveResult, vals, global_Object.afterPost);
        }
    },
    //舍弃巡检结果
    abandonClick: function () {
        // console.log(data);  //测试

        CommonFunc.confirmAsync("您确定舍弃巡检结果吗？", function () {
            $(formContainerID).modal("hide");
        });
    },
    //获取巡检主信息以及明细
    queryMainDetails: function (id) {
        // console.log(id);  //测试

        var vals = {
            id: id
        };
        var resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxQueryByID, vals);
        //拼接明细信息
        var insDetailList = resp.data.children;
        var detailError = '';
        for (var i = 0, len = insDetailList.length; i < len; i++) {
            if (insDetailList[i].desp) {
                if (detailError)
                    detailError += "\r\n";
                detailError += '【' + global_Object.getObjectByIndexID(insDetailList[i].index_id).name + '】  '
                    + insDetailList[i].desp;
            }
        }
        var mainObject = resp.data.obj;
        mainObject.details = detailError;
        mainObject.time_len_SHOW = Math.ceil(mainObject.time_len / 1000);

        //打开巡检结果窗口
        CommonFunc.clearForm(handleForm);
        $(handleFormContainer).modal("show");
        CommonFunc.setForm(handleForm, mainObject);
    }

};