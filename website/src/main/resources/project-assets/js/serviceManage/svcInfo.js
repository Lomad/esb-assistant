//公用的请求前缀
var ajaxReqPre = "/ajax/serviceManage/svcInfo/";

var tableID = '#listTable';
var formContainerID = '#editFormContainer';
var copyFormContainerID = '#importSvcFormContainer';
var copyFormID = '#importSvcForm';
var formID = '#editForm';
var idAid = '#aid';
var groupId = '#groupId';
var urlId = '#urlId';
var urlAgentId = '#urlAgentId';
var msgType = '#msgType';
var otherMark = '#otherMark';
var queryStatus = '#queryStatus';
var idSelectDataProtocal = '#dataProtocal';
var idBtnToggleUrlInput = '#btnToggleUrlInput';
var idTargetAidTree = '#targetAidTree';
var idSvcCodeNotContainSysCode = '#svcCodeNotContainSysCode';
var idAppId = '#appId';

var idTreeWrapper = '#treeWrapper';
var idWrapperId2 = '#wrapperId2';

$(document).ready(function () {
    global_Object.initDomEvent();
});

var global_Object = {
    treeOrgID: null,
    treeAID: null,
    aidAppId: null,
    ajaxGetBaseInfo: contextPath + ajaxReqPre + 'getBaseInfo',
    ajaxQuery: contextPath + ajaxReqPre + 'query',
    ajaxSave: contextPath + ajaxReqPre + 'save',
    ajaxDel: contextPath + ajaxReqPre + 'delete',
    ajaxPublish: contextPath + ajaxReqPre + 'publish',
    ajaxRollback: contextPath + ajaxReqPre + 'rollback',
    ajaxCopy: contextPath + ajaxReqPre + 'copy',
    ajaxAidListFromSidList: contextPath + ajaxReqPre + 'aidListFromSidList',
    initDomEvent: function () {
        //绑定基础信息
        CommonFunc.ajaxPostForm(global_Object.ajaxGetBaseInfo, null, global_Object.bindBaseInfo);

        //绑定主页面的业务系统树
        var params = {
            needESB: true,
            callback: global_Object.treeChange
        };
        CommonTree.init(params);

        $("#btnAdd").on("click", function () {
            global_Object.showModal();
            if (!CommonFunc.isEmpty(global_Object.treeAID)) {
                CommonFunc.setSelected(idAid, global_Object.treeAID);
            }
            global_Object.fillSvcCodeShow();
        });

        $("#btnQuery").on("click", function () {
            global_Object.bindTableData();  //刷新列表
        });
        $('#queryWord').on('keypress', function (event) {
            if (event.keyCode == "13") {
                global_Object.bindTableData();  //刷新列表
            }
        });

        $('#btnDelete').on("click", function () {
            // var idList = [];
            // $('.checkbox_select').each(function () {
            //     if (this.checked)
            //         idList.push($(this).val());
            // });

            var rows = CommonTable.getSelectedRows(tableID);
            //判断是否包含已发布服务，如果包含，则不能删除
            var existPublished = false;
            if(rows && rows.length>0) {
                for(var i=0, len=rows.length; i<len; i++) {
                    if(rows[i].status == 1) {
                        existPublished = true;
                        break;
                    }
                }

                if(existPublished) {
                    CommonFunc.msgFa('存在已发布服务，禁止删除！');
                } else {
                    var idList = [];
                    for(var i=0, len=rows.length; i<len; i++) {
                        idList.push(rows[i].id);
                    }
                    global_Object.delMany(idList);
                    $('[name="select_all"]').removeAttr("checked");
                }
            } else {
                CommonFunc.msgFa('请选择待删除的信息！');
            }
        });

        $('#btnCopy').on("click", function () {
            var sidList = CommonTable.getSelectedData(tableID);
            if (sidList && sidList.length > 0) {
                var vals = {
                    strIdList: JSON.stringify(sidList)
                };
                CommonFunc.ajaxPostForm(global_Object.ajaxAidListFromSidList, vals, global_Object.showCopyModel);
            } else {
                CommonFunc.msgFa('请选择待复制的信息！');
            }
        });

        $('#btnPublish').on("click", function () {
            var idList = CommonTable.getSelectedData(tableID);
            if (idList && idList.length > 0) {
                global_Object.publish(idList);
            } else {
                CommonFunc.msgFa('请选择待发布的信息！');
            }
        });

        $('#btnRollback').on("click", function () {
            var idList = CommonTable.getSelectedData(tableID);
            if (idList && idList.length > 0) {
                global_Object.rollback(idList);
            } else {
                CommonFunc.msgFa('请选择待下线的信息！');
            }
        });

        $(queryStatus).on("change", function () {
            global_Object.bindTableData();  //刷新列表
        });

        $(idAid).on("change", function () {
            global_Object.fillSvcCodeShow();
        });

        $(idBtnToggleUrlInput).on("click", function () {
            global_Object.toggleUrlInput(this);
        });

        //调整高度
        CommonFunc.maxHeightToFrame(idTreeWrapper, idWrapperId2);

        //设置滚动条
        CommonFunc.setScrollBarWithWrapper();
    },
    treeChange: function (sysID, obj) {
        // console.log(sysID); //测试
        // console.log(obj); //测试

        //将选择的业务系统ID保存到全局变量中
        global_Object.treeOrgID = null;
        global_Object.treeAID = null;
        if (!CommonFunc.isEmpty(obj)) {
            if (!CommonFunc.isEmpty(obj.appId)) {
                global_Object.treeAID = CommonFunc.isEmpty(obj) ? null : obj.id;
            } else {
                global_Object.treeOrgID = CommonFunc.isEmpty(obj) ? null : obj.id;
            }
        }
        //刷新列表
        global_Object.bindTableData();
    },
    bindBaseInfo: function (data) {
        console.log(data);  //测试
        console.log(data.map.groupId);  //测试

        var err = '';
        if (!CommonFunc.isEmpty(data) && !CommonFunc.isEmpty(data.map)) {
            if (CommonFunc.isEmpty(data.map.aid))
                err += '提供方为空！';
            // if (CommonFunc.isEmpty(data.map.urlId))
            //     err += '源地址为空！';
            if (CommonFunc.isEmpty(data.map.urlAgentId))
                err += 'ESB代理地址为空！';
            // if (CommonFunc.isEmpty(data.map.groupId))
            //     err += '分组为空！';
            if (!CommonFunc.isEmpty(err)) {
                CommonFunc.msgFa(err);
            } else {
                CommonFunc.bindSelect(idAid, data.map.aid);
                if (!CommonFunc.isEmpty(data.map.urlId)) {
                    CommonFunc.bindSelect(urlId, data.map.urlId);
                }
                CommonFunc.bindSelect(urlAgentId, data.map.urlAgentId);
                CommonFunc.bindSelect(msgType, data.map.msgType);
                // CommonFunc.bindSelect(groupId, data.map.groupId);
                CommonFunc.bindSelect(idSelectDataProtocal, data.map.dataProtocal);
                CommonFunc.bindSelect(otherMark, data.map.otherMark);
                CommonFunc.bindSelectAdvanced(queryStatus, data.map.status, {required: true, requiredText: '全部状态'});

                // $("#sel_menu2").select2({
                //     language: "zh-CN",
                //     width : '100%'
                // });

                global_Object.aidAppId = data.map.aidAppId;

                global_Object.bindTableData(data.map);  //加载列表
            }
        } else {
            CommonFunc.msgFa('获取基础数据失败！');
        }
    },
    bindTableData: function (map) {
        var mapAppName = CommonFunc.isEmpty(map) ? CommonFunc.getSelectOptions(idAid) : CommonFunc.getSimpleMap(map.aid);
        var mapMsgType = CommonFunc.isEmpty(map) ? CommonFunc.getSelectOptions(msgType) : CommonFunc.getSimpleMap(map.msgType);
        var columns = [
            {
                title: '提供方', data: 'aid', width: "16%",
                render: function (data, type, row, meta) {
                    var showVal = mapAppName[data];
                    return CommonFunc.isEmpty(showVal) ? data : showVal;
                }
            },
            {title: '服务代码', data: 'code', width: "14%"},
            {title: '服务名称', data: 'name'},
            {title: '版本号', data: 'version', width: "10%"},
            // {title: '分组', data: 'groupName', width: "10%"},
            {
                title: '参数类型', data: 'msgType', width: "8%",
                render: function (data, type, row, meta) {
                    var showVal = mapMsgType[data];
                    return CommonFunc.isEmpty(showVal) ? data : showVal;
                }
            },
            {
                title: '状态', data: 'statusName', width: "8%",
                render: function (data, type, row, meta) {
                    // console.log(row);  //测试

                    var html;
                    if (row.status == 1) {
                        html = '<span class="prject-color-success"><i class="fa fa-check-circle"></i> 已发布</span>';
                    } else if (row.status == 0) {
                        html = '<span class="prject-color-normal2"><i class="fa fa-circle-o"></i> 未发布</span>';
                    } else if (row.status == 2) {
                        html = '<span class="prject-color-normal3"><i class="fa fa-times-circle"></i> 已下线</span>';
                    } else {
                        html = '';
                    }
                    return html;
                }
            },
            {title: '操作', data: null, className: 'optColumn'}
        ];
        var columnDefs = [
            $.fn.dataTable.columndefs.cutoff(1),
            $.fn.dataTable.columndefs.cutoff(2),
            $.fn.dataTable.columndefs.cutoff(3), {
            targets: 6
        }, {
            targets: 7,
            width: "75px",
            render: function (data, type, row, meta) {
                var html = '  <span onclick="global_Object.edit(' + meta.row
                    + ')" class="tableBtnImg" title="编辑"><i class="fa fa-pencil-square-o fa-lg cp"></i></span>';
                html += '  <span onclick="global_Object.gotoStructure(null, ' + meta.row
                    + ')" class="tableBtnImg" title="服务参数配置"><i class="fa fa-exchange fa-lg cp"></i></span>';
                // if (row.status == 1) {
                //     html += '  <span onclick="" class="tableBtnImg prject-color-normal3" title="下架"><i class="fa fa-times-circle fa-lg cp"></i></span>';
                // } else {
                //     html += '  <span onclick="" class="tableBtnImg prject-color-success" title="发布"><i class="fa fa-check-circle fa-lg cp"></i></span>';
                // }
                return html;
            }
        }];
        var myAttrs = {
            checkbox: true
        };
        CommonTable.createTableAdvanced(tableID, function (data, callback, settings) {
            var reqData = {
                datas: JSON.stringify({
                    orgId: global_Object.treeOrgID,
                    aid: global_Object.treeAID,
                    queryWord: $("#queryWord").val(),
                    status: $(queryStatus).val(),
                    needUrlStatus: true,
                    startIndex: data.start,
                    pageSize: data.length
                })
            };
            //ajax请求数据
            CommonFunc.ajaxPostForm(global_Object.ajaxQuery, reqData, function (respData) {
                global_Object.bindTableAjax(respData, callback);
            }, CommonFunc.msgEx);

        }, columns, columnDefs, myAttrs);
    },
    bindTableAjax: function (respData, callback) {
        // console.log(respData);  //测试

        var datas = respData.datas;
        var svcInfo = [];
        if (!CommonFunc.isEmpty(datas) && datas.length > 0) {
            $.each(datas, function (index, value) {
                svcInfo.push(value.svcInfo);
            });
        }

        callback({
            recordsTotal: respData.totalSize,//过滤之前的总数据量
            recordsFiltered: respData.totalSize,//过滤之后的总数据量
            data: svcInfo
        });

        //调整高度
        CommonFunc.maxHeightToFrame(idTreeWrapper, idWrapperId2);
    },
    showModal: function () {
        CommonFunc.clearForm(formID);
        $(formContainerID).modal("show");
    },
    saveClick: function () {
        var vals = CommonFunc.getForm(formID);
        //设置服务代码
        var appId = $(idAppId).text();
        var svcCodeNotContainSysCode = $(idSvcCodeNotContainSysCode).val();
        vals.code = appId + svcCodeNotContainSysCode;
        //设置源地址
        var currentInputName = $(idBtnToggleUrlInput).attr('data-url-input');
        if (currentInputName == 'url') {
            vals['urlId'] = null;
        } else {
            vals['url'] = null;
        }
        var resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxSave, vals);
        global_Object.afterSave(resp, vals);
    },
    afterSave: function (resp, row) {
        // console.log(resp);  //测试
        // console.log(row);  //测试

        if (resp.success) {
            var id = $(formID).find('[name="id"]').val();
            $(formContainerID).modal("hide");
            CommonFunc.clearForm(formID);
            global_Object.bindTableData();  //刷新列表
            if (CommonFunc.isEmpty(id) && !CommonFunc.isEmpty(row) && !CommonFunc.isEmpty(row.code)) {
                row.id = resp.data;
                CommonFunc.confirmAsync('保存成功，立即设置服务的请求与应答消息？', function () {
                    global_Object.gotoStructure(row);
                });
            } else {
                CommonFunc.msgSu();
            }
        } else {
            CommonFunc.msgFa(resp.errorMsg);
        }
    },
    edit: function (rowIndex) {
        var rowData = $(tableID).dataTable().fnGetData()[rowIndex];
        if(rowData.status == 1) {
            CommonFunc.msgFa('当前服务已发布，禁止修改。');
        } else {
            global_Object.showModal();
            CommonFunc.setForm(formID, rowData);
            global_Object.fillSvcCodeShow();
            //设置源地址的输入方式
            if ((CommonFunc.isEmpty(rowData.url) && CommonFunc.isEmpty(rowData.urlId))
                || !CommonFunc.isEmpty(rowData.urlId)) {
                $(idBtnToggleUrlInput).attr('data-url-input', 'url');
            } else {
                $(idBtnToggleUrlInput).attr('data-url-input', 'urlId');
            }
            global_Object.toggleUrlInput($(idBtnToggleUrlInput));
        }
    },
    //跳转到结构页面
    gotoStructure: function (row, rowIndex) {
        if (CommonFunc.isEmpty(row))
            row = $(tableID).dataTable().fnGetData()[rowIndex];

        var url = contextPath + "/view/serviceManage/svcInfo__structure?id=" + row.id
            + "&code=" + row.code + "&name=" + row.name + "&msgType=" + row.msgType
            + "&dataProtocal=" + row.dataProtocal;
        window.location.href = encodeURI(url);
    },
    //删除
    delMany: function (idList) {
        if (CommonFunc.confirm("您确定删除吗？")) {
            var reqData = {
                strIdList: JSON.stringify(idList)
            };
            CommonFunc.ajaxPostForm(global_Object.ajaxDel, reqData, global_Object.afterSave, CommonFunc.msgEx);
        }
    },
    //发布服务
    publish: function (idList) {
        if (CommonFunc.confirm("您确定发布吗？")) {
            var reqData = {
                strIdList: JSON.stringify(idList)
            };
            CommonFunc.ajaxPostForm(global_Object.ajaxPublish, reqData, global_Object.afterSave, CommonFunc.msgEx);
        }
    },
    //回收（下架）服务
    rollback: function (idList) {
        if (CommonFunc.confirm("您确定下线吗？")) {
            var reqData = {
                strIdList: JSON.stringify(idList)
            };
            CommonFunc.ajaxPostForm(global_Object.ajaxRollback, reqData, global_Object.afterSave, CommonFunc.msgEx);
        }
    },
    //切换URL输入方式
    toggleUrlInput: function (me) {
        // console.log(me);    //测试

        var currentInputName = $(me).attr('data-url-input');
        if (currentInputName == 'url') {
            $('[name="url"]').addClass('hidden');
            $('[name="urlId"]').removeClass('hidden');
            $(me).attr('data-url-input', 'urlId');
        } else {
            $('[name="urlId"]').addClass('hidden');
            $('[name="url"]').removeClass('hidden');
            $(me).attr('data-url-input', 'url');
        }

    },

    //复制服务
    showCopyModel: function (resp) {
        if (resp.success) {
            $(copyFormContainerID).modal("show");
            //绑定复制功能弹窗的业务系统树
            var params = {
                treeID: idTargetAidTree,
                needESB: true,
                check: true,
                strIdNotInList: JSON.stringify(resp.data)
            };
            CommonTree.init(params);
        } else {
            CommonFunc.msgFa(resp.errorMsg);
        }
    },
    //复制服务
    saveCopyClick: function () {
        var sidList = CommonTable.getSelectedData(tableID);
        if (sidList && sidList.length > 0) {
            var targetAidList = CommonTree.getCheckedIdListFromMyData(idTargetAidTree);
            if (sidList != null) {
                var vals = {
                    targetAidList: JSON.stringify(targetAidList),
                    strIdList: JSON.stringify(sidList)
                };
                CommonFunc.ajaxPostForm(global_Object.ajaxCopy, vals, global_Object.afterCopy);
            }
        } else {
            CommonFunc.msgFa('请选择待复制的服务！');
        }
    },
    afterCopy: function (resp) {
        if (resp.success) {
            $(copyFormContainerID).modal("hide");
            CommonFunc.msgSu("复制成功");
        } else {
            CommonFunc.msgFa(resp.errorMsg);
        }
    },

    //填充显示的服务代码
    fillSvcCodeShow: function () {
        //获取系统代码
        var appId = global_Object.getAppIdByAid($(idAid).val());

        // console.log(appId); //测试

        if (!CommonFunc.isEmpty(appId)) {
            //将系统代码显示到服务代码前缀位置
            $(idAppId).text(appId);
            $(idAppId).css("background-color","#efefef");
            //获取服务代码
            var svcCode = $('input[name="code"]').val();
            if (!CommonFunc.isEmpty(svcCode)) {
                var svcCodeNotContainSysCode = $(idSvcCodeNotContainSysCode).val();
                if(CommonFunc.isEmpty(svcCodeNotContainSysCode)) {
                    //截取服务代码前缀的系统代码
                    var svcCodePrefixAppId = svcCode.substr(0, appId.length);
                    //删除服务代码前缀中的系统代码，得到真正的服务代码并显示
                    if (svcCodePrefixAppId == appId) {
                        $(idSvcCodeNotContainSysCode).val(svcCode.replace(appId, ""));
                    } else {
                        $(idSvcCodeNotContainSysCode).val(svcCode);

                        //xuehao、huanglong讨论决定：由于该情况下，服务代码的前缀与系统代码不一致，因此，无需遵循该规则；
                        // 隐藏系统代码前缀的空间，并清空；
                        $(idAppId).text("");
                        $(idAppId).css("background-color","#ffffff");
                    }
                }
            }
        } else {
            CommonFunc.msgFa("无法获取业务系统代码！");
        }
    },
    //根据系统ID查询系统代码（AppId）
    getAppIdByAid: function (aid) {
        if (!CommonFunc.isEmpty(aid) && !CommonFunc.isEmpty(global_Object.aidAppId)) {
            var data = global_Object.aidAppId;
            for (var i = 0; i < data.length; i++) {
                if (data[i].item1 == aid) {
                    return data[i].item2;
                }
            }
        }
        return null;
    }

}