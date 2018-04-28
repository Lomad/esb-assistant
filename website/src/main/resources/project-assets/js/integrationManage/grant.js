//公用的请求前缀
var ajaxReqPre = "/ajax/integrationManage/grant/";
var ajaxReqPreService = "/ajax/serviceManage/serviceManagement/";
var ajaxAppReqPre = "/ajax/baseManage/appInfo/";
var ajaxReqPreSvcStructure = "/ajax/serviceManage/svcStructure/";

var tableID = '#listTable';
var formContainerID = '#editFormContainer';
var formID = '#editForm';
var applyFormContainerID = '#applyFormContainer';
var applyFormID = '#applyForm';
var idWrapperId2 = '#wrapperId2';

var idSecretKey = '#secret_key';
var idLicKey = '#lic_key';
var idSvcName = '#svcName';
var idApproveState = '#approve_state';
var idSvcStructureTree = '#svcStructureTree';
var idSvcStructureTreeSkin = '#svcStructureTreeSkin';
var idSvcTree = '#svcTree';
var idAppTree = '#appTree';

var idLoginUserid = '#loginUserid';

var idBtnAdd = '#btnAdd';
var idBtnApply = '#btnApply';
var idBtnApplyCancel = '#btnApplyCancel';

// var idFilterApp = '#filterApp';
// var idFilterSvc = '#filterSvc';

// var idApplyApp = '#applyApp';
var idApplySvcTree = '#applySvcTree';
var idApplySvcReqTree = '#applySvcReqTree';
var idApplySvcAckTree = '#applySvcAckTree';
var applySvcTree;

var treeObj;
var svcStructureTree;
var pageType;   //1 - 服务申请， 其他 - 授权管理

$(document).ready(function () {
    global_Object.initDomEvent();
});

var global_Object = {
    pageTypeApply: 1,  //申请页面
    ajaxGetBaseInfo: contextPath + ajaxReqPre + 'getBaseInfo',
    ajaxQuery: contextPath + ajaxReqPre + 'query',
    ajaxApply: contextPath + ajaxReqPre + 'apply',
    ajaxSave: contextPath + ajaxReqPre + 'save',
    ajaxDelete: contextPath + ajaxReqPre + 'delete',
    ajaxApproveState: contextPath + ajaxReqPre + 'approveState',
    ajaxGetSvcStructureTree: contextPath + ajaxReqPreSvcStructure + 'getZTree',
    initDomEvent: function () {
        //根据页面类型设置按钮
        global_Object.setButtons();

        // //绑定文本框数
        // global_Object.bindTextboxTree();

        //绑定基础数据
        global_Object.bindBaseInfo();
        //绑定列表
        global_Object.bindTableData();

        $(idApproveState).on("change", function () {
            //刷新列表
            global_Object.bindTableData();
        });

        $(idBtnAdd).on("click", function () {
            //初始添加授权弹窗的业务系统文本框树
            var myAttrs = {
                treeID: CommonFunc.formatIdRemoveJquery(idAppTree),
                callback: global_Object.bindSvcTree,
                exceptAppGrantAllSvc: true,
                async: false,
                username: $(idLoginUserid).val(),
                triggerLevel: 1,
                triggerNullToOtherLevel: true
            };
            var respTreeObject = TextboxTree.init(myAttrs);
            if (!CommonFunc.isEmpty(respTreeObject.resp) && respTreeObject.resp.success
                && respTreeObject.resp.datas && respTreeObject.resp.datas.length > 0) {
                TextboxTree.enable(CommonFunc.formatIdRemoveJquery(idAppTree));
                global_Object.showModal();
            } else {
                CommonFunc.msgFa('所有业务系统已申请全部服务的授权！');
            }
        });

        $(idBtnApply).on("click", function () {
            // //初始申请授权弹窗的业务系统文本框树
            // var myAttrs = {
            //     treeID: CommonFunc.formatIdRemoveJquery(idApplyApp),
            //     callback: global_Object.bindApplySvcTree,
            //     exceptAppGrantAllSvc: true,
            //     async: false,
            //     username: $(idLoginUserid).val()
            // };
            // var respTreeObject = TextboxTree.init(myAttrs);
            // if (!CommonFunc.isEmpty(respTreeObject.resp) && respTreeObject.resp.success
            //     && respTreeObject.resp.datas && respTreeObject.resp.datas.length > 0) {
            //     global_Object.showModalApply();
            // } else {
            //     CommonFunc.msgFa('当前用户相关的业务系统已申请全部服务的授权！');
            // }

            global_Object.showModalApply();
            global_Object.bindApplySvcTree();
        });

        $(idBtnApplyCancel).on("click", function () {
            var idList = [];
            $('.checkbox_select').each(function () {
                if (this.checked)
                    idList.push($(this).val());
            });
            if (idList && idList.length > 0) {
                global_Object.delMany(idList);
                $('[name="select_all"]').removeAttr("checked");
            } else {
                CommonFunc.msgFa('请选择待取消的申请！');
            }
        });

        $('#btnQuery').on("click", function () {
            global_Object.bindTableData();
        });

        $('#btnQuerySvc').on('click', function () {
            CommonFunc.prompt('请输入服务代码或名称查询', global_Object.bindApplySvcTree);
        });

        $('#btnRereshSvc').on('click', function () {
            global_Object.bindApplySvcTree();
        });

    },
    //根据页面类型设置按钮
    setButtons: function () {
        //获取页面类型
        pageType = $('#pageType').val();
        //服务申请
        if (pageType == global_Object.pageTypeApply) {
            $(idBtnAdd).addClass('hidden');
            $(idBtnApply).removeClass('hidden');
            $(idBtnApplyCancel).removeClass('hidden');
        }
        //授权管理
        else {
            $(idBtnAdd).removeClass('hidden');
            $(idBtnApply).addClass('hidden');
            $(idBtnApplyCancel).addClass('hidden');
        }
    },

    // //绑定文本框数
    // bindTextboxTree: function () {
    //     // console.log(data);  //测试
    //
    //     //初始化筛选的业务系统文本框树
    //     var myAttrs = {
    //         treeID: CommonFunc.formatIdRemoveJquery(idFilterApp),
    //         callback: global_Object.bindTableData,
    //         triggerNullToOtherLevel: true,
    //         username: $(idLoginUserid).val()
    //     };
    //     TextboxTree.init(myAttrs);
    //     //初始化筛选的服务文本框树
    //     myAttrs = {
    //         treeID: CommonFunc.formatIdRemoveJquery(idFilterSvc),
    //         callback: global_Object.bindTableData,
    //         triggerLevel: 2,
    //         triggerNullToOtherLevel: true,
    //         needSvc: true
    //     };
    //     TextboxTree.init(myAttrs);
    // },

    //绑定基础数据
    bindBaseInfo: function () {
        // console.log(data);  //测试

        var resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxGetBaseInfo);
        if (resp.success) {
            var data = resp.data;
            var myAttrs = {
                required: true,
                requiredText: '全部状态'
            };
            //绑定审核状态的下拉列表
            CommonFunc.bindSelectAdvanced(idApproveState, data.approve_state, myAttrs);
        }
    },
    //绑定弹窗的服务树
    bindSvcTree: function () {
        // console.log('init');  //测试

        //初始化筛选的服务文本框树
        var aid = TextboxTree.getValue(CommonFunc.formatIdRemoveJquery(idAppTree));
        var myAttrs = {
            treeID: CommonFunc.formatIdRemoveJquery(idSvcTree),
            callback: global_Object.bindSvcStructureAckTree,
            triggerLevel: 2,
            strIdNotInList: JSON.stringify([aid]),
            exceptGrantSvc: true,
            svcStatus: 1,
            needSvc: true,
            triggerLevel: 2,
            triggerNullToOtherLevel: true
        };
        TextboxTree.init(myAttrs);
        TextboxTree.enable(CommonFunc.formatIdRemoveJquery(idSvcTree));
        TextboxTree.clear(CommonFunc.formatIdRemoveJquery(idSvcTree));
        $.fn.zTree.destroy(CommonFunc.formatIdRemoveJquery(idSvcStructureTree));
    },
    //绑定申请授权弹窗的服务树
    bindApplySvcTree: function (queryWord) {
        // console.log('init');  //测试

        // var aid = TextboxTree.getValue(CommonFunc.formatIdRemoveJquery(idApplyApp));
        // var appName = TextboxTree.getText(CommonFunc.formatIdRemoveJquery(idApplyApp));
        var params = {
            treeID: idApplySvcTree,
            check: true,
            checkWhenClick: false,
            triggerLevel: 2,
            needSvc: true,
            async: false,
            svcStatus: 1,
            queryWord: queryWord,
            // exceptAppGrantAllSvc: true,
            // exceptGrantSvc: true,
            // strIdNotInList: JSON.stringify([aid]),
            callback: global_Object.applySvcTreeChange
        };
        var treeObj = CommonTree.init(params);
        var resp = treeObj.resp;
        if (!resp.success) {
            CommonFunc.msgFa('获取服务失败！' + resp.errorMsg);
            return false;
        }
        // else if (!resp.datas || resp.datas.length < 1) {
        //     CommonFunc.msgFa(appName + '已经申请了所有服务，无需再申请！');
        //     return false;
        // }
        applySvcTree = treeObj.tree;
    },
    //申请授权弹窗服务树的点击事件
    applySvcTreeChange: function (nodeID, obj) {
        // console.log(nodeID); //测试
        // console.log(obj); //测试

        //初始化消息结构树
        global_Object.initSvcStructureTree(obj.id, 0);
    },
    /**
     * 申请授权弹窗的初始化消息结构树
     * @param type 0-全部，1-输入树，2-应答树
     */
    initSvcStructureTree: function (sid, type) {
        // console.log('init');  //测试

        //绑定服务结构树
        var datas = {
            sid: sid
        };
        //绑定输入消息树
        if (type == 0 || type == global_Object.treeTypeIn) {
            datas["direction"] = 1;
            CommonFunc.ajaxPostForm(global_Object.ajaxGetSvcStructureTree, datas, function (data) {
                global_Object.bindSvcStructureTree(data, idApplySvcReqTree);
            }, CommonFunc.msgEx);
        }
        //绑定应答消息树
        if (type == 0 || type == global_Object.treeTypeAck) {
            datas["direction"] = 2;
            CommonFunc.ajaxPostForm(global_Object.ajaxGetSvcStructureTree, datas, function (data) {
                global_Object.bindSvcStructureTree(data, idApplySvcAckTree);
            }, CommonFunc.msgEx);
        }
    },
    //绑定申请授权弹窗的消息结构
    bindSvcStructureTree: function (data, idTree) {
        // console.log(data);  //测试
        // console.log(initInfo.appId);  //测试

        if (!data.success) {
            CommonFunc.msgEx('获取消息结构失败！' + data.errorMsg);
            return;
        }
        var datas = data.datas;
        var setting = {
            view: {
                showIcon: false,
                selectedMulti: false,
                showLine: false,
                nameIsHTML: true
            }
        };
        //绑定树以及change事件
        $.fn.zTree.init($(idTree), setting, datas);
    },
    //绑定弹窗的服务应答结构树
    bindSvcStructureAckTree: function (type) {
        console.log(type);  //测试

        var formData = CommonFunc.getForm(formID);
        if (CommonFunc.isEmpty(formData.sid))
            formData.sid = TextboxTree.getValue(CommonFunc.formatIdRemoveJquery(idSvcTree));
        var params = {
            sid: formData.sid,
            direction: 2,
            grantID: formData.id
        };
        if (CommonFunc.isEmpty(params.sid)) {
            $.fn.zTree.destroy(CommonFunc.formatIdRemoveJquery(idSvcStructureTree));
            return false;
        }
        var resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxGetSvcStructureTree, params);
        if (!resp.success) {
            CommonFunc.msgFa('获取服务结构失败！' + resp.errorMsg);
            return false;
        }
        var datas = resp.datas;
        if (datas && datas.length < 1) {
            CommonFunc.msgSu('该服务的应答消息结构为空！');
        }
        var setting = {
            view: {
                showIcon: false,
                selectedMulti: true,
                nameIsHTML: true
            },
            check: {
                enable: true
            },
            callback: {
                onClick: global_Object.onTreeClick
            }
        };
        //服务申请时checkbox不可点击
        if (type == 1) {
            global_Object.setChildrenChkDisabled(datas);
        }
        //绑定树以及change事件
        $.fn.zTree.init($(idSvcStructureTree), setting, datas);
        svcStructureTree = $.fn.zTree.getZTreeObj(CommonFunc.formatIdRemoveJquery(idSvcStructureTree));

        if (CommonFunc.isEmpty(formData.id)) {
            svcStructureTree.checkAllNodes(true);
        }

        return true;
    },
    /**
     * 树的点击事件
     */
    onTreeClick: function (event, treeId, treeNode, clickFlag) {
        // console.log(treeId); //测试
        // console.log(treeNode); //测试

        svcStructureTree.checkNode(treeNode, null, true);
    },
    bindTableData: function () {
        // console.log(data);  //测试

        var columns = [
            {title: '申请方', data: 'appName', width: "15%"},
            {title: '申请时间', data: 'apply_time', width: "15%"},
            {title: '服务代码', data: 'svcCode', width: "15%"},
            {title: '服务名称', data: 'svcName'},
            {title: '授权状态', data: 'approveStateName', width: "10%"},
            {title: '是否加密', data: 'secretKeyName', width: "10%"}
        ];
        var columnDefs = [
            $.fn.dataTable.columndefs.cutoff(0),
            $.fn.dataTable.columndefs.cutoff(1),
            $.fn.dataTable.columndefs.cutoff(2),
            $.fn.dataTable.columndefs.cutoff(3),{
            targets: 4,
            render: function (data, type, row, meta) {
                // console.log(data);  //测试
                // console.log(row);  //测试

                var className, faName, displayName;
                if (row.approve_state == 1) {
                    className = 'prject-color-success';
                    faName = 'check-circle-o';
                    displayName = data;
                } else if (row.approve_state == 2) {
                    className = 'prject-color-failure';
                    faName = 'ban';
                    displayName = data;
                } else {
                    className = 'prject-color-normal2';
                    faName = 'circle-o';
                    displayName = data;
                }
                var html = '<span class="' + className + '">'
                    + '<i class="fa fa-' + faName + '"></i> ' + displayName + '</span>';
                return html;
            }
        }];
        //根据是页面类型，决定是否显示操作列
        var myAttrs = {};
        var optColumnIndex;
        if (pageType == global_Object.pageTypeApply) {
            myAttrs.checkbox = true;
            //设置授权状态列索引
            columnDefs[0].targets = columnDefs[0].targets + 1;
            //添加操作列
            columns.push({title: '操作', data: null, className: 'optColumn'});
            columnDefs.push({
                targets: 7,
                width: "75px",
                render: function (data, type, row, meta) {
                    //修改
                    var html = '<span onclick="global_Object.edit(' + meta.row
                        + ', 1)" class="tableBtnImg" title="查看授权码与密钥">'
                        + '<i class="fa fa-key fa-lg cp"></i></span>';
                    return html;
                }
            });
        } else {
            //添加操作列
            columns.push({title: '操作', data: null, className: 'optColumn'});
            columnDefs.push({
                targets: 6,
                width: "75px",
                render: function (data, type, row, meta) {
                    //通过
                    var htmlSuccess = '  <span onclick="global_Object.grantSuccess(' + meta.row
                        + ')" class="tableBtnImg prject-color-success" title="通过">' +
                        '<i class="fa fa-check-circle-o fa-lg cp"></i></span>';
                    //驳回
                    var htmlFailure = '  <span onclick="global_Object.grantFailure(' + meta.row
                        + ')" class="tableBtnImg prject-color-failure" title="驳回">' +
                        '<i class="fa fa-ban fa-lg cp"></i></span>';
                    //收回
                    var htmlBack = '  <span onclick="global_Object.grantBack(' + meta.row
                        + ')" class="tableBtnImg prject-color-normal2" title="返回申请状态">' +
                        '<i class="fa fa-reply fa-lg cp"></i></span>';

                    //修改
                    var html = '<span onclick="global_Object.edit(' + meta.row
                        + ')" class="tableBtnImg"><i class="fa fa-key fa-lg cp"></i></span>';
                    // html += '  <span onclick="global_Object.del(' + meta.row
                    //     + ')" class="tableBtnImg"><i class="fa fa-trash-o fa-lg cp"></i></span>';
                    if (row.approve_state == 0) {
                        html += htmlFailure;
                    } else if (row.approve_state == 1) {
                        html += htmlFailure;
                        // html += htmlBack;
                    } else if (row.approve_state == 2) {
                        html += htmlSuccess;
                        // html += htmlBack;
                    }
                    return html;
                }
            });
        }
        CommonTable.createTableAdvanced(tableID, global_Object.bindTableAjax, columns, columnDefs, myAttrs);
    },
    bindTableAjax: function (data, callback, settings) {
        // var aid = TextboxTree.getValue(CommonFunc.formatIdRemoveJquery(idFilterApp));
        // var sid = TextboxTree.getValue(CommonFunc.formatIdRemoveJquery(idFilterSvc));
        var reqData = {
            datas: JSON.stringify({
                approve_state: $(approve_state).val(),
                // aid: aid,
                // sid: sid,
                queryWord: $('#queryWord').val(),
                startIndex: data.start,
                pageSize: data.length,
                username: $(idLoginUserid).val()
            })
        };
        var resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxQuery, reqData);
        var grantList = [];
        if (resp.success && resp.datas && resp.datas.length > 0) {
            var datas = resp.datas;

            console.log(datas); //测试

            var svcItem;
            var tempObj;
            for (var i = 0, len = datas.length; i < len; i++) {
                svcItem = datas[i];
                grantList[i] = svcItem.obj;
                grantList[i]["appName"] = svcItem.appInfoModel.appName;
                grantList[i]["svcCode"] = svcItem.svcInfoModel.code;
                grantList[i]["svcName"] = svcItem.svcInfoModel.name;
                // tempObj = svcItem.obj.apply_time;
                // if (!CommonFunc.isEmpty(tempObj) && tempObj.length >= 16) {
                //     tempObj = tempObj.substr(0, 16);
                //     tempObj = tempObj.replace(/-0/g, '/');
                //     tempObj = tempObj.replace(/-/g, '/');
                // } else {
                //     tempObj = "";
                // }
                // grantList[i]["apply_time"] = tempObj;
                //授权状态
                grantList[i]["approveStateName"] = svcItem.approveStateName;
                //是否加密
                grantList[i]["secretKeyName"] = svcItem.secretKeyName;
            }
        }
        //绑定数据到列表
        callback({
            recordsTotal: resp.totalSize,//过滤之前的总数据量
            recordsFiltered: resp.totalSize,//过滤之后的总数据量
            data: grantList
        });

        //调整高度
        CommonFunc.maxHeightToFrame(idWrapperId2);
    },
    selectAll: function (me) {
        // console.log('selectAll');   //测试
        // console.log(me);   //测试

        if (me.name == "select_all") {
            if (me.checked) {
                $('.checkbox_select').each(function () {
                    this.checked = true;
                });
            } else {
                $('.checkbox_select').each(function () {
                    this.checked = false;
                });
            }
        } else {
            var selectAllState = $('[name="select_all"]').is(':checked');
            var isSelectAll = true;
            $('.checkbox_select').each(function () {
                if (!this.checked)
                    isSelectAll = false;
            });
            if (isSelectAll && !selectAllState) {
                $('[name="select_all"]').prop("checked", "true");
            }
            else if (!isSelectAll && selectAllState) {
                $('[name="select_all"]').removeAttr("checked");
            }
        }
    },
    //保存授权
    saveClick: function () {
        var err = '';
        var formData = CommonFunc.getForm(formID);
        //设置审批状态
        formData.approve_state = 1;
        //申请方
        formData.aid = TextboxTree.getValue(CommonFunc.formatIdRemoveJquery(idAppTree));
        if (CommonFunc.isEmpty(formData.aid))
            err += '请选择申请方！';
        //服务
        formData.sid = TextboxTree.getValue(CommonFunc.formatIdRemoveJquery(idSvcTree));
        if (CommonFunc.isEmpty(formData.sid))
            err += '请选择服务！';
        //授权码
        if (CommonFunc.isEmpty(formData.lic_key))
            err += '授权码不能为空！';
        else if (formData.lic_key.length != 16)
            err += '授权码长度必须16位！';
        //应答消息结构
        var ssidList = [];
        if (!CommonFunc.isEmpty(svcStructureTree)) {
            var nodes = svcStructureTree.getCheckedNodes(true);
            if (nodes && nodes.length > 0) {
                for (var i = 0, len = nodes.length; i < len; i++) {
                    ssidList.push(nodes[i].myData.id);
                }
            }
        }
        if (ssidList.length < 1)
            err += '请选择服务应答结构！';
        //密钥
        if (!CommonFunc.isEmpty(formData.secret_key) && formData.secret_key.length != 16)
            err += '密钥长度必须16位！';
        //判断是否有错误
        if (!CommonFunc.isEmpty(err)) {
            CommonFunc.msgFa(err);
            return;
        }
        //提供数据库
        var vals = {
            "strModel": JSON.stringify(formData),
            "strSsidList": JSON.stringify(ssidList)
        };
        var resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxSave, vals);
        global_Object.afterPost(resp);
    },
    //保存申请
    saveApplyClick: function () {
        var err = '';
        var formData = CommonFunc.getForm(applyFormID);
        // formData.aid = TextboxTree.getValue(CommonFunc.formatIdRemoveJquery(idApplyApp));
        var resp;
        if (CommonFunc.isEmpty(formData.id)) {
            // //申请方
            // if (CommonFunc.isEmpty(formData.aid))
            //     err += '请选择申请方！';
            //获取服务ID列表
            var sidList = [];
            if (applySvcTree) {
                var nodes = applySvcTree.getCheckedNodes(true);
                if (nodes && nodes.length > 0) {
                    for (var i = 0, len = nodes.length; i < len; i++) {
                        if (nodes[i].level == 2)
                            sidList.push(nodes[i].myData.id);
                    }
                }
                if (sidList.length < 1)
                    err += '请选择服务！';
            }
            //判断是否错误
            if (!CommonFunc.isEmpty(err)) {
                CommonFunc.msgFa(err);
                return;
            }
            var vals = {
                "userid": $(idLoginUserid).val(),
                "strModel": JSON.stringify(formData),
                "strSidList": JSON.stringify(sidList)
            };
            resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxApply, vals);
        }
        global_Object.afterPostApply(resp);
    },
    //生成授权码
    creatLicKey: function () {
        $(idLicKey).val(CommonFunc.generateRandomMixed(16));
    },
    //生成密钥
    creatSecretKey: function () {
        $(idSecretKey).val(CommonFunc.generateRandomMixed(16));
    },
    showModal: function () {
        CommonFunc.clearForm(formID);
        $.fn.zTree.destroy(CommonFunc.formatIdRemoveJquery(idSvcStructureTree));
        $(formContainerID).modal("show");
    },
    afterPost: function (data) {
        // console.log(data);  //测试

        if (data.success) {
            $(formContainerID).modal("hide");
            CommonFunc.clearForm(formID);
            global_Object.bindTableData();  //刷新列表
            CommonFunc.msgSu();
        } else {
            CommonFunc.msgFa(data.errorMsg);
        }
    },
    //显示申请授权的弹窗
    showModalApply: function () {
        CommonFunc.clearForm(applyFormID);
        $.fn.zTree.destroy(CommonFunc.formatIdRemoveJquery(idApplySvcTree));
        $.fn.zTree.destroy(CommonFunc.formatIdRemoveJquery(idApplySvcReqTree));
        $.fn.zTree.destroy(CommonFunc.formatIdRemoveJquery(idApplySvcAckTree));
        $(applyFormContainerID).modal("show");
    },
    afterPostApply: function (data) {
        // console.log(data);  //测试

        if (data.success) {
            $(applyFormContainerID).modal("hide");
            CommonFunc.clearForm(applyFormID);
            global_Object.bindTableData();  //刷新列表
            CommonFunc.msgSu();
        } else {
            CommonFunc.msgFa(data.errorMsg);
        }
    },
    afterRelease: function (data) {
        if (data.success) {
            CommonFunc.msgSuModal("发布成功");
        } else {
            CommonFunc.msgFa(data.errorMsg);
        }

    },
    /**
     * 编辑
     * @param type 1 - 只能查看， 空 - 可编辑
     */
    edit: function (rowIndex, type) {
        global_Object.showModal();
        var rowData = $(tableID).dataTable().fnGetData()[rowIndex];
        if (rowData.approve_state == 0)
            delete rowData.approve_state;
        CommonFunc.setForm(formID, rowData);
        //设置业务系统的文本框树
        TextboxTree.setText(CommonFunc.formatIdRemoveJquery(idAppTree), rowData.appName);
        TextboxTree.setValue(CommonFunc.formatIdRemoveJquery(idAppTree), rowData.aid);
        TextboxTree.disable(CommonFunc.formatIdRemoveJquery(idAppTree));
        //设置服务的文本框树
        TextboxTree.setText(CommonFunc.formatIdRemoveJquery(idSvcTree), rowData.svcName);
        TextboxTree.setValue(CommonFunc.formatIdRemoveJquery(idSvcTree), rowData.sid);
        TextboxTree.disable(CommonFunc.formatIdRemoveJquery(idSvcTree));
        //绑定应答消息结构
        global_Object.bindSvcStructureAckTree(type);

        //设置保存按钮
        if (type == 1) {
            $('#btnSave').hide();
            $('#lic_key').attr("readonly", "readonly");
            $('#secret_key').attr("readonly", "readonly");
            $('#btnLic').attr("disabled", "true");
            $('#btnSecret').attr("disabled", "true");

        } else {
            $('#btnSave').show();
        }
    },
    grantSuccess: function (rowIndex) {
        global_Object.grantCommon('您确定通过吗？', rowIndex, 1);
    },
    grantFailure: function (rowIndex) {
        global_Object.grantCommon('您确定驳回吗？', rowIndex, 2);
    },
    grantBack: function (rowIndex) {
        global_Object.grantCommon('您确定收回授权吗（收回后将恢复到申请状态）？', rowIndex, 0);
    },

    // //查看授权码与密钥
    // viewKey: function (rowIndex) {
    //     var row = $(tableID).dataTable().fnGetData()[rowIndex];
    //     var showInfo = '请妥善保存以下信息！<br/>';
    //     showInfo += '授权码：' + (CommonFunc.isEmpty(row.lic_key) ? '【空】' : row.lic_key) + '<br/>';
    //     showInfo += '密　钥：' + (CommonFunc.isEmpty(row.secret_key) ? '【空】' : row.secret_key);
    //     CommonFunc.msgWa(showInfo);
    // },

    grantCommon: function (msg, rowIndex, approveState) {
        // console.log(msg);   //测试

        if (CommonFunc.confirm(msg)) {
            var row = $(tableID).dataTable().fnGetData()[rowIndex];
            var reqData = {
                id: row.id,
                approve_state: approveState
            };
            CommonFunc.ajaxPostForm(global_Object.ajaxApproveState, reqData, global_Object.afterPost, CommonFunc.msgEx);
        }
    },
    del: function (rowIndex) {
        var row = $(tableID).dataTable().fnGetData()[rowIndex];
        var sidList = [row.id];
        global_Object.delMany(sidList);
    },
    delMany: function (idList) {
        if (CommonFunc.confirm("您确定取消申请吗？")) {
            var reqData = {
                strIdList: JSON.stringify(idList)
            };
            CommonFunc.ajaxPostForm(global_Object.ajaxDelete, reqData, global_Object.afterPost, CommonFunc.msgEx);
        }
    },
    //设置所有节点CheckBox为disabled
    setChildrenChkDisabled:function (datas) {
        for(var i=0; i<datas.length; i++){
            datas[i].chkDisabled = true;
            if(datas[i].children != null){
                global_Object.setChildrenChkDisabled(datas[i].children);
            }
        }

    }
};