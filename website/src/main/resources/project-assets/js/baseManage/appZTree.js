/**
 * Created by xuehao on 2017/07/28.
 */

var CommonTree = {
    idTreeDefault: '#appTree',
    initInfo: {
        // '以树的元素ID为key' : {
        //
        // }
    },
    // ajaxGetTreeShort: ajaxPubReqPre + '/ajax_pub/baseManage/appInfo/getZTree',
    ajaxGetTree: contextPath + '/ajax/baseManage/appInfo/getZTree',
    /**
     * 初始化树
     * @param params
        {
           tree : null,    //树对象
           isCookie: true,
           cookidPrefix : treeID,
           defaultSelect: "",      //true - 默认选择（默认）， false - 默认不选择
           defaultAppId: "",      //设置默认节点的系统ID
           sysDirection: "",   //系统方向：null - 全部（默认）， 0 - 提供方/消费方， 1 - 提供方， 2 - 消费方
           treeID: "",         //树ID
           triggerLevel : 0,     //click可触发的节点层次（默认1，即触发业务系统）
           triggerNullToOtherLevel: false,     //true - 选择其他层次传回Null对象(现已改为禁选)， false - 不触发（默认）
           callback: null,      //回调函数（回传的参数为选中节点的ID）
            needESB,     //true - 返回ESB， false - 不返回ESB（默认）
            needStop,     //true - 显示停用系统， false - 不显示停用系统（默认）
            needSvc,     //true - 显示服务， false - 不显示服务（默认）
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
            queryWord,     //用于搜索叶子节点
            ajaxUrl,     //请求后台的地址
            svcStatus,     //服务状态
            isTestUnit,     //是否对单元测试筛选（true - 是， false或null - 否（默认））
            testUnitFlag,     //单元测试标志（空值 - 获取全部（默认）， 1 - 获取已测试， 2 - 获取未测试）
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
                sysDirection: params.sysDirection ? params.sysDirection : null,
                needESB: params.needESB ? params.needESB : true,
                needStop: params.needStop ? params.needStop : false,
                needSvc: params.needSvc ? params.needSvc : false,
                strIdList: params.strIdList,
                strIdNotInList: params.strIdNotInList,
                strSidNotInList: params.strSidNotInList,
                exceptAppGrantAllSvc: params.exceptAppGrantAllSvc ? params.exceptAppGrantAllSvc : false,
                exceptGrantSvc: params.exceptGrantSvc ? params.exceptGrantSvc : false,
                username: params.username ? params.username : null,
                queryWord: params.queryWord ? params.queryWord : null,
                svcStatus: params.svcStatus ? params.svcStatus : null,
                isTestUnit: params.isTestUnit ? params.isTestUnit : false,
                testUnitFlag: params.testUnitFlag ? params.testUnitFlag : null,
            })
        };
        var async = CommonFunc.isEmpty(params.async) ? true : params.async;
        var ajaxUrl = CommonFunc.isEmpty(params.ajaxUrl) ? CommonTree.ajaxGetTree : params.ajaxUrl; //请求后台的地址
        if (async) {
            CommonFunc.ajaxPostForm(ajaxUrl, reqData, function (resp) {
                CommonTree.bindAppTree(resp, params);
            });
        } else {
            var resp = CommonFunc.ajaxPostFormSync(ajaxUrl, reqData);
            return CommonTree.bindAppTree(resp, params);
        }
        return null;
    },

    // /**
    //  * 初始化树（既包含提供方，也包含消费方）
    //  * @param callback  回调函数（回传的参数为选中节点的ID）
    //  * @param appId  系统代码
    //  * @param treeID    树所在的div的ID，如果为空，则ID默认为“appTree”
    //  */
    // initAll: function (callback, appId, treeID) {
    //     var params = {
    //         callback: callback,
    //         appId: appId,
    //         treeID: treeID
    //     };
    //     CommonTree.init(params);
    // },
    // /**
    //  * 初始化提供方树
    //  * @param callback  回调函数（回传的参数为选中节点的ID）
    //  * @param appId  系统代码
    //  * @param treeID    树所在的div的ID，如果为空，则ID默认为“appTree”
    //  */
    // initProvider: function (callback, appId, treeID) {
    //     var params = {
    //         sysDirection: 1,
    //         callback: callback,
    //         appId: appId,
    //         treeID: treeID
    //     };
    //     CommonTree.init(params);
    // },
    // /**
    //  * 初始化消费方树
    //  * @param callback  回调函数（回传的参数为选中节点的ID）
    //  * @param appId  系统代码
    //  * @param treeID    树所在的div的ID，如果为空，则ID默认为“appTree”
    //  */
    // initConsumer: function (callback, appId, treeID) {
    //     var params = {
    //         sysDirection: 2,
    //         callback: callback,
    //         appId: appId,
    //         treeID: treeID
    //     };
    //     CommonTree.init(params);
    // },

    bindAppTree: function (resp, params) {
        // console.log(resp);  //测试
        // console.log(params);  //测试

        if (!resp.success) {
            CommonFunc.msgFa('获取机构与业务系统关系树失败！' + resp.errorMsg);
            return;
        }

        //树的元素ID
        if (CommonFunc.isEmpty(params.treeID))
            params.treeID = CommonFunc.formatIdForJquery(CommonTree.idTreeDefault);
        else
            params.treeID = CommonFunc.formatIdForJquery(params.treeID);
        //设置树对象的完整信息
        var treeObject = {
            resp: resp,
            isCookie: CommonFunc.isEmpty(params.isCookie) ? true : params.isCookie,
            cookidPrefix: params.treeID,
            defaultSelect: CommonFunc.isEmpty(params.defaultSelect) ? true : params.defaultSelect,
            defaultAppId: params.defaultAppId,      //设置默认的系统ID
            treeID: params.treeID,         //树ID
            triggerLevel: CommonFunc.isEmpty(params.triggerLevel) ? 1 : params.triggerLevel, //click可触发的节点层次，默认触发业务系统
            triggerNullToOtherLevel: CommonFunc.isEmpty(params.triggerNullToOtherLevel) ? false : params.triggerNullToOtherLevel,
            callback: params.callback,
            async: CommonFunc.isEmpty(params.async) ? true : params.async,
            check: CommonFunc.isEmpty(params.check) ? false : params.check,
            checkWhenClick: CommonFunc.isEmpty(params.checkWhenClick) ? true : params.checkWhenClick,
            checkedIdList: CommonFunc.isEmpty(params.checkedIdList) ? null : JSON.parse(params.checkedIdList), //默认选中的复选框
        };

        //设置树的初始化树形
        var setting = {
            view: {
                showIcon: true,
                selectedMulti: treeObject.check,
                nameIsHTML: true,
                showLine: false,
                addDiyDom : function (treeId, treeNode) {
                    // console.log(treeId);  //测试
                    // console.log(treeNode);  //测试

                    CommonTree.addDiyDom(treeId, treeNode, treeObject.check);
                }
            },
            check: {
                enable: treeObject.check
            },
            callback: {
                onClick: CommonTree.onClick,
                beforeClick: CommonTree.treeBeforeClick
            }
        };
        //数据
        var datas = resp.datas;
        //设置默认选择的复选框，只对叶子节点有效
        CommonTree.setCheckedNode(datas, treeObject.checkedIdList);
        //绑定树以及change事件
        $.fn.zTree.init($(params.treeID), setting, datas);

        //获取树的ID（不包含井号）
        var treeIDWithoutJquery = CommonFunc.formatIdRemoveJquery(params.treeID);

        //保存树的对象
        treeObject.tree = $.fn.zTree.getZTreeObj(treeIDWithoutJquery);   //树对象
        CommonTree.initInfo[treeIDWithoutJquery] = treeObject;

        //如果不是复选框模式，则设置默认选择的节点
        var nodes = treeObject.tree.getNodes()
        if (!treeObject.check && treeObject.defaultSelect && nodes && nodes.length > 0) {
            CommonTree.selectNode(treeIDWithoutJquery, treeObject);
        }

        return treeObject;
    },
    treeBeforeClick: function (treeId, treeNode) {
        // console.log(treeId);    //测试
        // console.log(treeNode);    //测试
        // console.log(CommonTree.initInfo[treeId]);    //测试

        var treeObj = CommonTree.initInfo[treeId];
        if (treeObj.triggerNullToOtherLevel == false ||
            (treeObj.triggerNullToOtherLevel == true && treeObj.triggerLevel == treeNode.level)) {
            return true;
        }
        return false;
    },
    /**
     * 树的点击事件
     */
    onClick: function (event, treeID, treeNode, clickFlag) {
        // console.log(event); //测试
        // console.log(treeID); //测试
        // console.log(treeNode); //测试
        // console.log(clickFlag); //测试

        var treeIDWithoutJquery = CommonFunc.formatIdRemoveJquery(treeID);
        var treeObj = CommonTree.initInfo[treeIDWithoutJquery];
        var tree = treeObj.tree;
        if (treeObj.check && treeObj.checkWhenClick) {
            tree.checkNode(treeNode, null, true);
        } else {
            //保存cookie
            if (treeObj.isCookie) {
                CommonFunc.setCookie(treeObj.cookidPrefix + '_' + CommonFunc.getURL(),
                    treeNode.level + '_' + treeNode.myData.id, 1); //设置cookie
            }
            //触发回调
            if (!CommonFunc.isEmpty(treeObj.callback)) {
                // if (CommonFunc.isEmpty(treeObj.triggerLevel) || treeNode.level == treeObj.triggerLevel
                //     || treeObj.triggerLevel == 0) {
                //     treeObj.callback(treeNode.id, treeNode.myData, treeObj, treeNode);
                // } else if (treeObj.triggerNullToOtherLevel) {
                //     treeObj.callback(treeNode.id, null, treeObj, treeNode);
                // }

                if (treeObj.triggerNullToOtherLevel == false ||
                    (treeObj.triggerNullToOtherLevel == true && treeObj.triggerLevel == treeNode.level)) {
                    treeObj.callback(treeNode.id, treeNode.myData, treeObj, treeNode);
                } else {
                    treeObj.callback(treeNode.id, null, treeObj, treeNode);
                }
            }
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
                CommonTree.resetDisplay(itemChild);
            });
        }
    },
    //选择第一个叶子节点作为默认选择的节点
    selectFirstNode: function (treeID, firstNode) {
        // console.log(CommonTree.initInfo[treeID]); //测试

        var nodes = (firstNode) ? firstNode.children : CommonTree.initInfo[treeID].tree.getNodes();
        if (nodes && nodes.length > 0) {
            retNode = CommonTree.selectFirstNode(treeID, nodes[0]);
        } else {
            CommonTree.initInfo[treeID].tree.selectNode(firstNode);
            CommonTree.onClick(null, treeID, firstNode);
        }
    },
    //选择默认节点
    selectNode: function (treeID, treeObject) {
        // console.log(level); //测试
        // console.log(myDataID); //测试

        //获取根节点列表
        var nodes = treeObject.tree.getNodes();

        //设置默认的AppId节点
        var defaultAppId = treeObject.defaultAppId;
        if (!CommonFunc.isEmpty(defaultAppId)) {
            //将树形节点转换为列表
            var nodesArray = treeObject.tree.transformToArray(nodes);
            if (nodesArray && nodesArray.length > 0) {
                var node;
                for (var i = 0, len = nodesArray.length; i < len; i++) {
                    node = nodesArray[i];
                    if (node.level.toString() == "1" && node.myData.appId.toString() == defaultAppId) {
                        CommonTree.initInfo[treeID].tree.selectNode(node);
                        CommonTree.onClick(null, treeID, node);
                        return;
                    }
                }
            }
        }

        //获取cookie
        var cookieValue = CommonFunc.getCookie(treeObject.cookidPrefix + '_' + CommonFunc.getURL());
        //根据Cookie获取待选择的节点，如果cookie为空，则获取第一个节点
        if (treeObject.isCookie && !CommonFunc.isEmpty(cookieValue)) {
            var cookieValueArray = cookieValue.split('_');
            if (cookieValueArray && cookieValueArray.length == 2) {
                var level = cookieValueArray[0];
                var myDataID = cookieValueArray[1];
                //将树形节点转换为列表
                var nodesArray = treeObject.tree.transformToArray(nodes);
                if (nodesArray && nodesArray.length > 0) {
                    var node, cookieFind = false;
                    for (var i = 0, len = nodesArray.length; i < len; i++) {
                        node = nodesArray[i];
                        if (node.level.toString() == level && node.myData.id.toString() == myDataID) {
                            cookieFind = true;
                            CommonTree.initInfo[treeID].tree.selectNode(node);
                            CommonTree.onClick(null, treeID, node);
                        }
                    }
                    //如果未找到cookie节点，则选择第一个节点
                    if (!cookieFind) {
                        CommonTree.selectFirstNode(treeID, nodes[0]);
                    }
                }
            }
        } else {
            CommonTree.selectFirstNode(treeID, nodes[0]);
        }
    },
    //设置默认选择的复选框，只对叶子节点有效
    setCheckedNode: function (datas, checkedIdList) {
        // console.log(datas); //测试
        // console.log(checkedIdList); //测试

        if (datas && datas.length > 0 && checkedIdList && checkedIdList.length > 0) {
            $.each(datas, function (i, item) {
                if (item.children && item.children.length > 0)
                    CommonTree.setCheckedNode(item.children, checkedIdList);
                else {
                    if ($.inArray(item.myData.id, checkedIdList) >= 0)
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
            treeID = CommonTree.idTreeDefault;
        var treeIDWithoutJquery = CommonFunc.formatIdRemoveJquery(treeID);
        var treeObj = CommonTree.initInfo[treeIDWithoutJquery];
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
            treeID = CommonTree.idTreeDefault;
        var treeIDWithoutJquery = CommonFunc.formatIdRemoveJquery(treeID);
        var treeObj = CommonTree.initInfo[treeIDWithoutJquery];
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
        // console.log(CommonTree.initInfo); //测试

        var treeIDWithoutJquery = CommonFunc.formatIdRemoveJquery(treeID);
        var treeObject = CommonTree.initInfo[treeIDWithoutJquery];
        if (treeObject)
            return treeObject.tree;
        return null;
    },
    /**
     * 销毁树对象
     * @param treeID    如果为空，则销毁页面所有树，否则，销毁指定ID的树
     */
    destroy: function (treeID) {
        // console.log(treeID); //测试

        if (CommonFunc.isEmpty(treeID)) {
            $.fn.zTree.destroy();
        } else {
            $.fn.zTree.destroy(CommonFunc.formatIdRemoveJquery(treeID));
        }
    },
    /**
     * 名称超出长度后，显示省略号
     * @param showCheck     true - 显示复选框， false - 不显示复选框
     * @param maxLen     显示的最大字符数，超过使用省略号，如果为空，默认30
     */
    addDiyDom: function (treeId, treeNode, showCheck, maxLen) {
        // console.log(treeId);  //测试
        // console.log(treeNode);  //测试
        // console.log(showCheck);  //测试

        var switchObj = $("#" + treeNode.tId + "_switch"),
            icoObj = $("#" + treeNode.tId + "_ico");
        switchObj.remove();
        icoObj.parent().before(switchObj);

        //处理复选框
        var checkObj;
        if(showCheck == true) {
            checkObj = $("#" + treeNode.tId + "_check");
            checkObj.remove();
            icoObj.parent().before(checkObj);
        }

        var spantxt = $("#" + treeNode.tId + "_span").html();
        if(showCheck == true) {
            $("#" + treeNode.tId + "_span").css({"fontSize":13});
            $("#" + treeNode.tId + "_span").attr("data-toggle","tooltip");
            $("#" + treeNode.tId + "_span").attr("data-placement","top");
        }
        if(CommonFunc.isEmpty(maxLen) || maxLen < 1) {
            maxLen = 30;
        }
        if (spantxt.length > maxLen) {
            spantxt = spantxt.substring(0, maxLen) + "...";
            $("#" + treeNode.tId + "_span").html(spantxt);
        }
    }
};