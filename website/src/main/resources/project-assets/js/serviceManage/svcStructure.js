/**
 * Created by xuehao on 2017/08/10.
 */

//公用的请求前缀
var ajaxReqPre = "/ajax/serviceManage/svcStructure/";
var ajaxValueListReqPre = "/ajax/serviceManage/valueList/";

//ID
var formContainerID = '#editFormContainer';
var formID = '#editForm';
var valueListFormContainerID = '#valueListFormContainer';
var valueListFormID = '#valueListForm';
var idSvcInTree = '#svcInTree';
var idSvcAckTree = '#svcAckTree';

var idSelectRequired = '#required';
var idSelectIsLoop = '#is_loop';
var idSelectDataType = '#data_type';
var idSelectResultMark = '#result_mark';
var idSelectIsAttr = '#is_attr';

var idSvcForm = '#svcForm';
var idUpfile = '#upfile';
var idUploadFormContainer = '#uploadFormContainer';
var idUploadForm = '#uploadForm';

var idMsgReqSkin = '#msgReqSkin';
var idMsgAckSkin = '#msgAckSkin';

var importFormContainerID = '#importFormContainer';
var importFormID = '#importForm';
var idImportSvcTree = '#importSvcTree';
var idMsgDirection = '#msgDirection';
var importSvcTree;

//全局变量
var isFirstBindingTree = true;
var treeType = 0;
var svcObj = {};

//数据协议中的业务节点占位符
var ProtocalBizMark = '<TEMPLATE_CONTENT>';
//数据协议中的业务节点占位符友好名称
var ProtocalBizMarkFriendly = '【此处放置业务信息】';

$(document).ready(function () {
    global_Object.initDomEvent();
});

var global_Object = {
    treeTypeIn: 1,
    treeTypeAck: 2,
    ajaxGetBaseInfo: contextPath + ajaxReqPre + 'getBaseInfo',
    ajaxExistCode: contextPath + ajaxReqPre + 'existCode',
    ajaxGetJsTree: contextPath + ajaxReqPre + 'getJsTree',
    ajaxGetZTree: contextPath + ajaxReqPre + 'getZTree',
    ajaxImport: contextPath + ajaxReqPre + 'importStruncture',
    ajaxUpload: contextPath + ajaxReqPre + 'upload',
    ajaxExport: contextPath + ajaxReqPre + 'export',
    ajaxSave: contextPath + ajaxReqPre + 'save',
    ajaxDel: contextPath + ajaxReqPre + 'delete',
    ajaxGetDataProtocalTemplate: contextPath + ajaxReqPre + 'getDataProtocalTemplate',
    ajaxUpdateWhenDrop: contextPath + ajaxReqPre + 'updateWhenDrop',
    ajaxGetExtValueList: contextPath + ajaxValueListReqPre + 'getExtBySsid',
    ajaxSaveValueList: contextPath + ajaxValueListReqPre + 'insertAfterDelete',
    initDomEvent: function () {
        // console.log('init');  //测试

        //绑定基础信息
        CommonFunc.ajaxPostForm(global_Object.ajaxGetBaseInfo, null, global_Object.bindBaseInfo, CommonFunc.msgEx);
        //绑定服务信息
        global_Object.getSvcInfo();

        $("#btnAddRootIn").on("click", function () {
            global_Object.addClick(idSvcInTree, 0);
        });
        $("#btnImportIn").on("click", function () {
            treeType = global_Object.treeTypeIn;
            global_Object.showImportModal();
        });
        $("#btnUploadIn").on("click", function () {
            treeType = global_Object.treeTypeIn;
            // $(idUpfile).click();
            global_Object.uploadModal();
        });
        $("#btnDownloadIn").on("click", function () {
            // var msgType = $('[name="msgType"]').val();
            // var datas = {
            //     msgType: msgType,
            //     sid: svcObj.id,
            //     direction: 1,
            //     valueType: 2,
            //     wrapperDataProtocal : true
            // };
            // CommonFunc.ajaxPostForm(global_Object.ajaxExport, datas, global_Object.downloadFiles);
            global_Object.download(1, 2, false);
        });
        $("#btnDownloadIn_1_1").on("click", function () {
            global_Object.download(1, 2, true);
        });
        $("#btnAddRootAck").on("click", function () {
            global_Object.addClick(idSvcAckTree, 0);
        });
        $("#btnImportAck").on("click", function () {
            treeType = global_Object.treeTypeAck;
            global_Object.showImportModal();
        });
        $("#btnUploadAck").on("click", function () {
            treeType = global_Object.treeTypeAck;
            // $(idUpfile).click();
            global_Object.uploadModal();
        });
        $("#btnDownloadAck").on("click", function () {
            // var msgType = $('[name="msgType"]').val();
            // var datas = {
            //     msgType: msgType,
            //     sid: svcObj.id,
            //     direction: 2,
            //     valueType: 2,
            //     wrapperDataProtocal : true
            // };
            // CommonFunc.ajaxPostForm(global_Object.ajaxExport, datas, global_Object.downloadFiles);
            global_Object.download(2, 2, false);
        });
        $("#btnDownloadAck_1_1").on("click", function () {
            global_Object.download(2, 2, true);
        });

        $(idUpfile).change(function (e) {
            // console.log(e.currentTarget.files[0]);  //测试
            var upfile = e.currentTarget.files[0];
            //清空空间的value，以便重复选择相同文件时，也能加载成功
            $(idUpfile).val('');
            global_Object.openFile(upfile);
        });

        $('#btnQuerySvc').on('click', function () {
            CommonFunc.prompt('请输入服务代码或名称查询', global_Object.bindImportSvcTree);
        });
        $('#btnRereshSvc').on('click', function () {
            global_Object.bindImportSvcTree();
        });

    },
    getSvcInfo: function (resp) {
        // console.log(sysID); //测试
        // console.log(obj); //测试

        svcObj = {
            id: CommonFunc.getQueryString('id'),
            code: CommonFunc.getQueryString('code'),
            name: CommonFunc.getQueryString('name'),
            msgType: CommonFunc.getQueryString('msgType')
        };
        //绑定服务信息
        CommonFunc.setForm(idSvcForm, svcObj);

        //绑定数据协议模版内容
        var protocalCode = CommonFunc.getQueryString('dataProtocal');
        var req = {
            protocalCode: protocalCode,
            msgType: svcObj.msgType
        };
        CommonFunc.ajaxPostForm(global_Object.ajaxGetDataProtocalTemplate, req, function (resp) {
            if (!CommonFunc.isEmpty(resp.data) && !CommonFunc.isEmpty(resp.data.protocalReq))
                $('[name="protocalReq"]').val(resp.data.protocalReq);
            if (!CommonFunc.isEmpty(resp.data) && !CommonFunc.isEmpty(resp.data.protocalAck))
                $('[name="protocalAck"]').val(resp.data.protocalAck);
        }, CommonFunc.msgEx);

        //初始化树
        global_Object.initTree(0);
    },
    bindBaseInfo: function (data) {
        // console.log(data);  //测试

        CommonFunc.bindSelect(idSelectRequired, data.map.required);
        CommonFunc.bindSelect(idSelectIsLoop, data.map.is_loop);
        CommonFunc.bindSelect(idSelectDataType, data.map.data_type);
        CommonFunc.bindSelect(idSelectResultMark, data.map.result_mark);
        CommonFunc.bindSelect(idSelectIsAttr, data.map.is_attr);
        CommonFunc.bindSelect(idMsgDirection, data.map.direction);
    },
    /**
     * 初始化树
     * @param type 0-全部，1-输入树，2-应答树
     */
    initTree: function (type) {
        // console.log('init');  //测试

        //绑定服务结构树
        var datas = {
            sid: svcObj.id
        };
        //绑定输入消息树
        if (type == 0 || type == global_Object.treeTypeIn) {
            datas["direction"] = 1;
            CommonFunc.ajaxPostForm(global_Object.ajaxGetZTree, datas, function (data) {
                global_Object.bindZTree(data, idSvcInTree);
            }, CommonFunc.msgEx);
        }
        //绑定应答消息树
        if (type == 0 || type == global_Object.treeTypeAck) {
            datas["direction"] = 2;
            CommonFunc.ajaxPostForm(global_Object.ajaxGetZTree, datas, function (data) {
                global_Object.bindZTree(data, idSvcAckTree);
            }, CommonFunc.msgEx);
        }
    },
    bindZTree: function (data, idSvcTree) {
        // console.log(data);  //测试
        // console.log(initInfo.appId);  //测试

        if (!data.success) {
            CommonFunc.msgEx('获取服务结构失败！' + data.errorMsg);
            return;
        }
        var datas = data.datas;

        var setting = {
            view: {
                showIcon: true,
                selectedMulti: false,
                nameIsHTML: true,
                showLine: false,
                addHoverDom: global_Object.addHoverDom,
                removeHoverDom: global_Object.removeHoverDom,
                addDiyDom : function (treeId, treeNode) {
                    // console.log(treeId);  //测试
                    // console.log(treeNode);  //测试

                    CommonTree.addDiyDom(treeId, treeNode, false);
                }
            },
            edit: {
                enable: true,
                showRenameBtn: false,
                showRemoveBtn: false,
                drag : {
                    isCopy : false
                }
            },
            callback: {
                beforeDrop: global_Object.beforeDrop,
                onDrop: global_Object.onDrop
            }
        };

        //绑定树以及change事件
        $.fn.zTree.init($(idSvcTree), setting, datas);

        //设置容器高度
        CommonFunc.sameWrapperHeight(idSvcInTree, idSvcAckTree);
    },
    //显示新增按钮，并打开新增窗口
    addHoverDom: function (treeId, treeNode) {
        var sObj = $("#" + treeNode.tId + "_span");
        if (treeNode.editNameFlag || $("#editBtn_" + treeNode.tId).length > 0)
            return;
        var addStr = '';
        // if (treeNode.level == 0)
        //     addStr += "<span class='fa fa-plus-circle' id='addRootBtn_" + treeNode.tId + "' title='新增根节点' onfocus='this.blur();'></span>";
        if (treeNode.myData.can_edit == 1 || (treeNode.myData.can_edit == 0 && treeNode.myData.code == 'Body'))
            addStr += "<span class='fa fa-plus' id='addBtn_" + treeNode.tId + "' title='新增子节点' onfocus='this.blur();'></span>";
        addStr += "<span class='fa fa-edit' id='editBtn_" + treeNode.tId + "' title='修改' onfocus='this.blur();'></span>";
        addStr += "<span class='fa fa-list-ul' id='valueListBtn_" + treeNode.tId + "' title='候选值' onfocus='this.blur();'></span>";
        if (treeNode.myData.can_edit == 1)
            addStr += "<span class='fa fa-remove' id='deleteBtn_" + treeNode.tId + "' title='删除' onfocus='this.blur();'></span>";
        sObj.after(addStr);
        // //新增根节点按钮
        // var btn = $("#addRootBtn_" + treeNode.tId);
        // if (btn) btn.bind("click", function () {
        //     global_Object.addClick(treeId, 0);
        //     return false;
        // });
        //新增子节点按钮
        btn = $("#addBtn_" + treeNode.tId);
        if (btn) btn.bind("click", function () {
            global_Object.addClick(treeId, treeNode.myData.id);
            return false;
        });
        //修改按钮
        btn = $("#editBtn_" + treeNode.tId);
        if (btn) btn.bind("click", function () {
            global_Object.editClick(treeId, treeNode);
            return false;
        });
        //候选值按钮
        btn = $("#valueListBtn_" + treeNode.tId);
        if (btn) btn.bind("click", function () {
            global_Object.valueListClick(treeId, treeNode);
            return false;
        });
        //删除按钮
        btn = $("#deleteBtn_" + treeNode.tId);
        if (btn) btn.bind("click", function () {
            global_Object.deleteClick(treeId, treeNode);
            return false;
        });
    },
    //移除新增按钮
    removeHoverDom: function (treeId, treeNode) {
        // $("#addRootBtn_" + treeNode.tId).unbind().remove();
        $("#addBtn_" + treeNode.tId).unbind().remove();
        $("#editBtn_" + treeNode.tId).unbind().remove();
        $("#valueListBtn_" + treeNode.tId).unbind().remove();
        $("#deleteBtn_" + treeNode.tId).unbind().remove();
    },
    //打开新增窗口
    addClick: function (treeId, pid) {
        var direction;
        if (idSvcInTree == CommonFunc.formatIdForJquery(treeId)) {
            direction = 1;
            treeType = global_Object.treeTypeIn;
        } else {
            direction = 2;
            treeType = global_Object.treeTypeAck;
        }
        global_Object.showModal(0);
        var formData = {
            sid: svcObj.id,
            direction: direction,
            pid: pid
        };
        CommonFunc.setForm(formID, formData);
        $(formID).find('[name="code"]').removeAttr("disabled");
    },
    //打开修改窗口
    editClick: function (treeId, treeNode) {
        treeType = (idSvcInTree == '#' + treeId) ? global_Object.treeTypeIn : global_Object.treeTypeAck;
        global_Object.showModal(1);
        CommonFunc.setForm(formID, treeNode.myData);
        if (treeNode.myData.can_edit == 0)
            $(formID).find('[name="code"]').attr("disabled", "disabled");
        else
            $(formID).find('[name="code"]').removeAttr("disabled");
        return false;
    },
    //打开候选值窗口
    valueListClick: function (treeId, treeNode) {
        CommonFunc.clearForm(valueListFormID);
        $(valueListFormContainerID).modal("show");

        var ssid = treeNode.myData.id;
        var resultMark = treeNode.myData.result_mark;
        var req = {ssid: ssid};
        var resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxGetExtValueList, req);
        if (!resp.success) {
            CommonFunc.msgFa(resp.errorMsg);
        } else {
            if (resultMark == 1) {
                $('#lblSuccessTitle').text('成功候选值');
                $('#valueListFailureSkin').show();
            } else {
                $('#lblSuccessTitle').text('候选值');
                $('#valueListFailureSkin').hide();
            }
            var data = resp.data;
            if (CommonFunc.isEmpty(data))
                data = {};
            data.ssid = ssid;
            data.resultMark = treeNode.myData.result_mark;
            CommonFunc.setForm(valueListFormID, data);
        }
        return false;
    },
    //保存事件
    saveClick: function () {
        // console.log('save');    //测试

        var vals = CommonFunc.getForm(formID);
        //如果code文本框出于disabled状态时，form无法获取到值，需单独处理
        if (CommonFunc.isEmpty(vals.code))
            vals.code = $(formID + ' [name="code"]').val();
        CommonFunc.ajaxPostForm(global_Object.ajaxSave, vals, global_Object.afterPost, CommonFunc.msgEx);
    },
    //保存候选值事件
    saveValueListClick: function () {
        // console.log('save');    //测试

        var req = CommonFunc.getForm(valueListFormID);
        var resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxSaveValueList, req);
        if (!resp.success) {
            CommonFunc.msgFa(resp.errorMsg);
        } else {
            CommonFunc.msgSu();
            $(valueListFormContainerID).modal("hide");
        }
    },
    //删除事件
    deleteClick: function (treeId, treeNode) {
        // console.log(treeNode);  //测试
        // console.log(treeNode.children);  //测试

        if (!CommonFunc.isEmpty(treeNode.children) && treeNode.children.length > 0) {
            CommonFunc.msgFa("包含子节点，无法删除！");
            return false;
        }
        if (CommonFunc.confirm("您确定删除吗？")) {
            var reqData = {
                id: treeNode.myData.id
            };
            CommonFunc.ajaxPostForm(global_Object.ajaxDel, reqData, global_Object.afterPost, CommonFunc.msgEx);
        }
        return false;
    },
    //拖拽事件
    beforeDrop: function (treeId, treeNodes, targetNode, moveType, isCopy) {
        // console.log(treeId); //测试

        var result = false;
        var ajaxComplete = false;
        var errInfo = "";

        if (treeNodes && treeNodes.length == 1) {
            var treeNode = treeNodes[0];
            var pid = targetNode.myData.pid;
            if (treeNode.myData.direction != targetNode.myData.direction) {
                errInfo = "禁止该类操作！";
            } else {
                var vals = {
                    id: treeNode.myData.id,
                    pid: pid,
                    code: treeNode.myData.code,
                    direction: treeNode.myData.direction
                };
                var respResult = CommonFunc.ajaxPostFormSync(global_Object.ajaxExistCode, vals);
                errInfo = respResult.errorMsg;
            }
        }

        if (errInfo)
            CommonFunc.msgFa(errInfo);
        else
            result = true;

        return result;
    },
    //拖拽事件
    onDrop: function (event, treeId, treeNodes, targetNode, moveType, isCopy) {
        // console.log(treeId); //测试
        // console.log(treeNodes); //测试
        // console.log(targetNode); //测试
        // console.log(moveType); //测试

        var treeNode = treeNodes[0];
        var pid;
        var parentNode;
        var siblingsNodes;  //用于获取同级节点
        if (('inner' == moveType)) {
            pid = targetNode.myData.id;
            parentNode = targetNode;
            siblingsNodes = null;
        } else {
            pid = targetNode.myData.pid;
            parentNode = targetNode.getParentNode();
            if (targetNode.getParentNode() == null) {
                var treeObj = $.fn.zTree.getZTreeObj(treeId);
                siblingsNodes = treeObj.getNodesByParam("level", 0, null);  //拖到顶层
            } else {
                siblingsNodes = targetNode.getParentNode().children;        //拖到非顶层
            }
        }

        var dataArray = [];
        var preNode = treeNode.getPreNode();
        var orderNumBegin = preNode ? preNode.myData.order_num : 0;

        if (siblingsNodes == null) {
            dataArray.push({
                id: treeNode.myData.id,
                pid: pid,
                order_num: orderNumBegin + 1
            });
        } else {
            for (var i = treeNode.getIndex(); i < siblingsNodes.length; i++) {
                dataArray.push({
                    id: siblingsNodes[i].myData.id,
                    pid: pid,
                    order_num: orderNumBegin + i + 1
                });
            }
        }
        var vals = {
            objs: JSON.stringify(dataArray)
        };
        CommonFunc.ajaxPostForm(global_Object.ajaxUpdateWhenDrop, vals, global_Object.afterPost, CommonFunc.msgEx);
    },
    /**
     * 显示维护窗口
     * @param type  0-新增，1-修改
     */
    showModal: function (type) {
        CommonFunc.clearForm(formID);
        var title;
        if (type == 1) {
            title = "修改节点";
        } else {
            title = "新增节点";
        }
        $(formContainerID).find('h4').html(title);
        $(formContainerID).modal("show");
    },
    afterPost: function (data) {
        // console.log(data);  //测试

        if (data.success) {
            CommonFunc.clearForm(formID);
            $(formContainerID).modal("hide");
            CommonFunc.msgSu();
            global_Object.initTree(treeType);
        } else {
            CommonFunc.msgFa(data.errorMsg);
        }
    },
    //下载
    download: function (direction, valueType, wrapperDataProtocal) {
        // console.log(data);  //测试

        var msgType = $('[name="msgType"]').val();
        var datas = {
            msgType: msgType,
            sid: svcObj.id,
            direction: direction,
            valueType: valueType,
            wrapperDataProtocal : wrapperDataProtocal
        };
        CommonFunc.ajaxPostForm(global_Object.ajaxExport, datas, global_Object.downloadFiles);
    },
    //下载服务结构
    downloadFiles: function (data) {
        // console.log(data);  //测试
        // console.log(contextPath);  //测试

        if (data.success) {
            var a = document.getElementById("downFiles");
            a.href = contextPath + data.data;
            a.download = data.data;
            a.click();
        } else {
            CommonFunc.msgFa(data.errorMsg);
        }
    },
    //上传确认窗口
    uploadModal: function () {
        // console.log(upfile);  //测试

        var contentTemplate = '';
        if (treeType == global_Object.treeTypeIn)
            contentTemplate = $('[name="protocalReq"]').val();
        else
            contentTemplate = $('[name="protocalAck"]').val();
        var vals = {
            content: contentTemplate.replace(ProtocalBizMark, ProtocalBizMarkFriendly)
        };
        CommonFunc.setForm(idUploadForm, vals);
        $(idUploadFormContainer).modal("show");
    },
    //打开文件
    openFile: function (upfile) {
        // console.log(upfile);  //测试

        //判断文件大小
        if (upfile.size > 1048576) {
            CommonFunc.msgFa('文件大小不能超过1M');
            return;
        }
        //读取文件内容
        var reader = new FileReader();
        reader.readAsText(upfile, 'UTF-8');
        reader.onload = function () {
            // console.log('上传');  //测试

            var vals = {
                contentRaw: this.result,
                content: this.result
            };

            // console.log(vals);  //测试

            CommonFunc.setForm(idUploadForm, vals);
        }
    },
    //上传服务结构
    upload: function () {
        // console.log(upfile);  //测试

        var formData = CommonFunc.getForm(idUploadForm);
        var vals = {
            sid: svcObj.id,
            msgType: svcObj.msgType,
            direction: treeType == global_Object.treeTypeIn ? 1 : 2,
            rawContent: formData.content
        };
        if (CommonFunc.isEmpty(vals.rawContent)) {
            CommonFunc.msgFa('参数内容不能为空！');
        } else {
            var resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxUpload, vals);
            if (!resp.success) {
                CommonFunc.msgFa(resp.errorMsg);
            } else {
                //初始化树
                global_Object.initTree(treeType == global_Object.treeTypeIn ? 1 : 2);
                $(idUploadFormContainer).modal("hide");
            }
        }
    },
    //绑定导入消息结构弹窗的服务树
    bindImportSvcTree: function (queryWord) {
        // console.log('init');  //测试

        var params = {
            treeID: idImportSvcTree,
            triggerLevel: 2,
            needSvc: true,
            async: false,
            queryWord: queryWord,
        };
        var treeObj = CommonTree.init(params);
        var resp = treeObj.resp;
        if (!resp.success) {
            CommonFunc.msgFa('获取服务失败！' + resp.errorMsg);
            return false;
        }
        importSvcTree = treeObj.tree;
    },
    /**
     * 显示导入消息结构的窗口
     */
    showImportModal: function () {
        CommonFunc.clearForm(importFormID);
        global_Object.bindImportSvcTree();
        $(importFormContainerID).modal("show");
    },
    //保存导入的消息结构
    importClick: function () {
        var err = '';
        //获取服务ID列表
        var sidFrom;
        var svcName = '';
        var nodes = importSvcTree.getSelectedNodes();
        if (!CommonFunc.isEmpty(nodes) && nodes.length > 0 && nodes[0].level == 2) {
            sidFrom = nodes[0].myData.id;
            svcName = nodes[0].myData.name;
        }
        var directionFrom = $(idMsgDirection).val();
        if (CommonFunc.isEmpty(sidFrom))
            err += '请选择服务！';
        if (CommonFunc.isEmpty(directionFrom))
            err += '请选择消息方向！';
        //判断是否错误
        if (!CommonFunc.isEmpty(err)) {
            CommonFunc.msgFa(err);
        } else {
            var directionFromText = $(idMsgDirection).find("option:selected").text();
            CommonFunc.confirmAsync('导入时将会清空已有的消息结构，您确定导入【' + svcName + '】的' + directionFromText + '结构吗？', function () {
                    global_Object.importPost(sidFrom, directionFrom);
                }
            );
        }
    },
    //提交导入的消息结构
    importPost: function (sidFrom, directionFrom) {
        var vals = {
            sid: svcObj.id,
            direction: treeType,
            sidFrom: sidFrom,
            directionFrom: directionFrom
        };
        var resp = CommonFunc.ajaxPostFormSync(global_Object.ajaxImport, vals);
        global_Object.afterImportPost(resp);
    },
    afterImportPost: function (data) {
        // console.log(data);  //测试

        if (data.success) {
            $(importFormContainerID).modal("hide");
            global_Object.initTree(treeType);
            CommonFunc.msgSu();
        } else {
            CommonFunc.msgFa(data.errorMsg);
        }
    },

};