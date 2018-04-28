

//公用的请求前缀
var ajaxReqPre = "/ajax/baseManage/svcGroup/";
var idTreeDefault = '#groupSvcTree';
var isFirstBindingTree = true;

var treeInitInfo = {
    // '以树的元素ID为key' : {
    //
    // }
};

var GroupTree = {
    // treeInitInfo : {
    //     // '以树的元素ID为key' : {
    //     //
    //     // }
    // },
    ajaxGetTree: contextPath + ajaxReqPre + 'getZTree',
    /**
     * 初始化树
     * @param params
        {
           tree : null,    //树对象
           isCookie: false,
           cookidPrefix: "AppTree",
           defaultSelect: "",      //true - 默认选择（默认）， false - 默认不选择
           defaultID: "",      //设置默认节点的ID
           sysDirection: "",   //系统方向：null - 全部（默认）， 0 - 提供方/消费方， 1 - 提供方， 2 - 消费方
           treeID: "",         //树ID
           triggerLevel: 1,     //click可触发的节点层次，默认第二级（即业务系统）
           triggerNullToOtherLevel: false,     //true - 选择其他层次传回Null对象， false - 不触发（默认）
           callback: null,      //回调函数（回传的参数为选中节点的ID）
            strIdList,          //需要包含的业务系统ID
            strIdNotInList,     //需要排除的业务系统ID
            strSidNotInList,    //需要排除的服务ID
            exceptAppGrantAllSvc,     //是否排除已授权全部服务的业务系统(默认值：false)
            exceptGrantSvc,     //是否排除已授权的服务(默认值：false)
            async,     //true - 异步（默认）， false - 同步
            check,     //true - 显示check， false - 不显示check（默认）
            checkWhenClick,     //点击是否选择复选框：true - 选择（默认）， false - 不选择
            checkedIdList,     //默认选中的节点ID列表，只对叶子节点有效
            username,     //登录用户名
            ajaxUrl,     //请求后台的地址
       }
     *
     */
    init: function (params) {
        // console.log(params);  //测试

        if (CommonFunc.isEmpty(params))
            params = {};
        //绑定基础信息
        var reqData = {
            datas: JSON.stringify({
                username : params.username ? params.username : null,
            })
        };
        var async = CommonFunc.isEmpty(params.async) ? true : params.async;
        var ajaxUrl = CommonFunc.isEmpty(params.ajaxUrl) ? GroupTree.ajaxGetTree : params.ajaxUrl; //请求后台的地址
        if (async) {
            CommonFunc.ajaxPostForm(ajaxUrl, reqData, function (resp) {
                GroupTree.bindAppTree(resp, params);
            }, CommonFunc.msgEx);
        } else {
            var resp = CommonFunc.ajaxPostFormSync(ajaxUrl, reqData);
            return GroupTree.bindAppTree(resp, params);
        }
        return null;
    },
    /**
     * 初始化树（既包含提供方，也包含消费方）
     * @param callback  回调函数（回传的参数为选中节点的ID）
     * @param appId  系统代码
     * @param treeID    树所在的div的ID，如果为空，则ID默认为“appTree”
     */
    initAll: function (callback, appId, treeID) {
        var params = {
            callback: callback,
            appId: appId,
            treeID: treeID
        };
        GroupTree.init(params);
    },

    bindAppTree: function (resp, params) {
        //console.log(resp);  //测试
        // console.log(params);  //测试

        if (!resp.success) {
            CommonFunc.msgFa('获取服务组与服务关系树失败！' + resp.errorMsg);
            return;
        }

        //设置树对象的完整信息
        var treeObject = {
            resp : resp,
            isCookie: params.isCookie,
            cookidPrefix: params.treeID,    //cookie前缀
            defaultSelect : CommonFunc.isEmpty(params.defaultSelect) ? true : params.defaultSelect,
            defaultID: params.groupSvcTree,      //设置默认的ID
            treeID: params.treeID,         //树ID
            triggerLevel: CommonFunc.isEmpty(params.triggerLevel) ? 1 : params.triggerLevel, //click可触发的节点层次，默认第二级（即业务系统）
            triggerNullToOtherLevel : CommonFunc.isEmpty(params.triggerNullToOtherLevel) ? false : params.triggerNullToOtherLevel,
            callback: params.callback,
            async: CommonFunc.isEmpty(params.async) ? true : params.async,
            check: CommonFunc.isEmpty(params.check) ? false : params.check,
            checkWhenClick : CommonFunc.isEmpty(params.checkWhenClick) ? true : params.checkWhenClick,
            checkedIdList: CommonFunc.isEmpty(params.checkedIdList) ? null : JSON.parse(params.checkedIdList), //默认选中的复选框
        };

        //设置树的初始化树形
        var setting = {
            view: {
                showIcon: true,
                selectedMulti: treeObject.check,
                nameIsHTML: true,
                showLine: false
            },
            check: {
                enable: treeObject.check
            },
            callback: {
                onClick: GroupTree.onClick
            }
        };
        //数据
        var datas = resp.datas;
        //设置默认选择的复选框，只对叶子节点有效
        GroupTree.setCheckedNode(datas, treeObject.checkedIdList);
        //树的元素ID
        if (CommonFunc.isEmpty(params.treeID))
            params.treeID = CommonFunc.formatIdForJquery(idTreeDefault);
        else
            params.treeID = CommonFunc.formatIdForJquery(params.treeID);
        //绑定树以及change事件
        $.fn.zTree.init($(params.treeID), setting, datas);
            //console.log(params.treeID);
        //获取树的ID（不包含井号）
        var treeIDWithoutJquery = CommonFunc.formatIdRemoveJquery(params.treeID);

        //保存树的对象
        treeObject.tree = $.fn.zTree.getZTreeObj(treeIDWithoutJquery);   //树对象
        treeInitInfo[treeIDWithoutJquery] = treeObject;

        //如果不是复选框模式，则设置默认选择的节点
        if (!treeObject.check && treeObject.defaultSelect) {
            //console.log(treeObject.tree);
            var nodes = treeObject.tree.getNodes();
            if (nodes && nodes.length > 0)
                GroupTree.selectDefaultNode(treeIDWithoutJquery, nodes[0]);
        }

        return treeObject;
    },
    /**
     * 树的点击事件
     */
    onClick: function (event, treeID, treeNode, clickFlag) {
        // console.log(event); //测试
         //console.log(treeID); //测试
        // console.log(treeNode); //测试
        // console.log(clickFlag); //测试

        var treeIDWithoutJquery = CommonFunc.formatIdRemoveJquery(treeID);
        var treeObj = treeInitInfo[treeIDWithoutJquery];
        //console.log(treeObj);
        var tree = treeObj.tree;
        if (treeObj.check && treeObj.checkWhenClick) {
            tree.checkNode(treeNode, null, true);
        } else {
            if (!CommonFunc.isEmpty(treeObj.callback)
                && (CommonFunc.isEmpty(treeObj.triggerLevel) || treeNode.level == treeObj.triggerLevel)) {
                treeObj.callback(treeNode.id, treeNode.myData, treeObj);
            } else if(treeObj.triggerNullToOtherLevel) {
                treeObj.callback(treeNode.id, null, treeObj);
            }
            // else {
            //     tree.expandNode(treeNode);
            // }
        }
    },
    /**
     * 遍历树，设置树的显示内容
     */
    resetDisplay: function (item) {
        // console.log(item); //测试

        if (CommonFunc.size(item.text) > 22) {
            item.text = CommonFunc.ellipsisString(item.text, 22);
        }
        if (item.children != null && item.children.length > 0) {
            $.each(item.children, function (i, itemChild) {
                GroupTree.resetDisplay(itemChild);
            });
        }
    },
    //选择默认选择的节点
    selectDefaultNode: function (treeID, firstNode) {
        // console.log(firstNode); //测试
        // console.log(treeInitInfo[treeID]); //测试

        var nodes = firstNode.children;
        if (nodes && nodes.length > 0) {
            GroupTree.selectDefaultNode(treeID, nodes[0]);
        } else {
            treeInitInfo[treeID].tree.selectNode(firstNode);
            GroupTree.onClick(null, treeID, firstNode);
        }
    },
    //设置默认选择的复选框，只对叶子节点有效
    setCheckedNode: function (datas, checkedIdList) {
        // console.log(datas); //测试
        // console.log(checkedIdList); //测试

        if (datas && datas.length > 0 && checkedIdList && checkedIdList.length > 0) {
            $.each(datas, function (i, item) {
                if(item.children && item.children.length>0)
                    GroupTree.setCheckedNode(item.children, checkedIdList);
                else {
                    if($.inArray(item.myData.id, checkedIdList)>=0)
                        item.checked = true;
                }
            });
        }
    },
    /**
     * 获取选择服务列表ID列表，如果“triggerLevel”属性为空，则获取全部选择的节点，否则按照“triggerLevel”获取
     */
    getCheckedIdListFromMyData: function (treeID) {
        var idList = [];
        if (CommonFunc.isEmpty(treeID))
            treeID = idTreeDefault;
        var treeIDWithoutJquery = CommonFunc.formatIdRemoveJquery(treeID);
        var treeObj = treeInitInfo[treeIDWithoutJquery];
        if (!CommonFunc.isEmpty(treeObj)) {
            var tree = treeObj.tree;
            var nodes = tree.getCheckedNodes(true);
            if (nodes && nodes.length > 0) {
                var isLevelEmpty = CommonFunc.isEmpty(treeObj.triggerLevel);
                for (var i = 0, len = nodes.length; i < len; i++) {
                    if (isLevelEmpty) {
                        idList.push(nodes[i].myData.id);
                    } else if (nodes[i].level == treeObj.triggerLevel) {
                        idList.push(nodes[i].myData.id);
                    }
                }
            }
        }
        return idList;
    },
    /**
     * 根据ID设置复选框的状态
     */
    checkNodeById: function (treeID, idList) {
        // console.log(idList); //测试

        if (CommonFunc.isEmpty(idList))
            return;
        else
            idList = JSON.parse(idList);
        if (CommonFunc.isEmpty(treeID))
            treeID = idTreeDefault;
        var treeIDWithoutJquery = CommonFunc.formatIdRemoveJquery(treeID);
        var treeObj = treeInitInfo[treeIDWithoutJquery];
        if (!CommonFunc.isEmpty(treeObj)) {
            var tree = treeObj.tree;
            var nodes = tree.transformToArray(tree.getNodes());
            if (nodes && nodes.length > 0) {
                for (var i = 0, len = nodes.length; i < len; i++) {
                    //只对叶子节点选择
                    if (!nodes[i].isParent && $.inArray(nodes[i].myData.id, idList) >= 0) {
                        tree.checkNode(nodes[i], true, true);
                    }
                }
            }
        }
    },
    /**
     * 获取树的对象
     */
    getTreeObject: function (treeID) {
        // console.log(treeID); //测试
        // console.log(treeInitInfo); //测试

        var treeIDWithoutJquery = CommonFunc.formatIdRemoveJquery(treeID);
        var treeObject = treeInitInfo[treeIDWithoutJquery];
        if(treeObject)
            return treeObject.tree;
        return null;
    }
};