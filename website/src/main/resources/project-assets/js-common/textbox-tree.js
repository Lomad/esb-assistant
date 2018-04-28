//xuehao 2017-07-27 ：封装文本框自带弹出业务系统树的公共js操作

var TextboxTree = {
    initInfo:{},
    /**
     * 初始化树
     * @param myAttrs 参数列表
     */
    init: function (myAttrs) {
        var treeID = myAttrs.treeID;
        var params = {
            callback: TextboxTree.treeChange,
            treeID: treeID,
            defaultSelect: false,
            check : myAttrs.check,
            triggerLevel : myAttrs.triggerLevel,
            triggerNullToOtherLevel: myAttrs.triggerNullToOtherLevel,
            strIdNotInList: myAttrs.strIdNotInList,
            exceptAppGrantAllSvc: myAttrs.exceptAppGrantAllSvc,
            exceptGrantApp: myAttrs.exceptGrantApp,
            needSvc : myAttrs.needSvc,
            svcStatus : myAttrs.svcStatus,
            async : myAttrs.async,
            username : myAttrs.username
        };
        var respTreeObject = CommonTree.init(params);

        //保存初始化信息
        TextboxTree.initInfo[treeID] = {
            treeID : treeID,
            callback : myAttrs.callback
        };

        //绑定文本框的点击事件
        var allControl = TextboxTree.getAllControl(treeID);
        allControl.inputTextbox.on("click", function () {
            TextboxTree.showTree(treeID);
        });
        allControl.buttonAppend.on("click", function () {
            TextboxTree.showTree(treeID);
        });

        return respTreeObject;
    },
    getAllControl: function (treeID) {
        // console.log(treeID); //测试

        var tree = $(CommonFunc.formatIdForJquery(treeID));
        var rootDiv = tree.parent().parent();
        var inputTextbox = rootDiv.children('input[type="text"]');
        var buttonAppend = rootDiv.find('button');
        var inputHidden = rootDiv.children('input[type="hidden"]');
        var treeSkin = rootDiv.children('.popTreeSkin');
        return {
            idInputTextbox: treeID + '-textbox',
            idInputHidden: treeID + '-hidden',
            idTreeSkin: treeID + '-skin',
            inputTextbox: inputTextbox,
            buttonAppend: buttonAppend,
            inputHidden: inputHidden,
            treeSkin: treeSkin
        };
    },
    treeChange: function (nodeID, obj, treeObj) {
        // console.log(nodeID); //测试
        // console.log(obj); //测试
        // console.log(treeObj); //测试

        var treeID = treeObj.treeID;
        var allControl = TextboxTree.getAllControl(treeID);
        if (CommonFunc.isEmpty(obj)) {
            allControl.inputTextbox.val('');
            allControl.inputHidden.val('');
        } else {
            if(obj.appName)
                allControl.inputTextbox.val(obj.appName);
            if(obj.name)
                allControl.inputTextbox.val(obj.name);
            allControl.inputHidden.val(obj.id);
        }

        //回调函数
        var callback = TextboxTree.initInfo[CommonFunc.formatIdRemoveJquery(treeID)].callback;
        if(callback) {
            callback();
        }
        //隐藏弹出窗
        TextboxTree.hideTree(treeID);
    },
    //显示树
    showTree: function (treeID) {
        // console.log(treeID);  //测试
        // console.log(TextboxTree.initInfo); //测试

        if(CommonTree.initInfo[treeID].resp.datas.length<1) {
            CommonFunc.msgWa('暂无可用服务，可能未发布或未创建！');
        } else {
            var allControl = TextboxTree.getAllControl(treeID);
            var inputTextbox = allControl.inputTextbox;
            $(allControl.treeSkin).css({
                width: inputTextbox.outerWidth() + "px"
            }).slideDown("fast");
            $("body").bind("mousedown", function() {
                TextboxTree.onBodyDownHideTree(treeID);
            });
        }
    },
    //隐藏树
    hideTree: function (treeID) {
        var allControl = TextboxTree.getAllControl(treeID);
        var idTreeSkinJquery = CommonFunc.formatIdForJquery(allControl.idTreeSkin);
        $(idTreeSkinJquery).fadeOut("fast");
        $("body").unbind("mousedown", function () {
            TextboxTree.onBodyDownHideTree(treeID);
        });
    },
    //点击树以外的任何位置，隐藏弹窗
    onBodyDownHideTree: function (treeID) {
        var allControl = TextboxTree.getAllControl(treeID);
        var idTreeSkinJquery = CommonFunc.formatIdForJquery(allControl.idTreeSkin);
        if (!(event.target.id == treeID || event.target.id == allControl.idTreeSkin
            || $(event.target).parents(idTreeSkinJquery).length > 0)) {
            TextboxTree.hideTree(treeID);
        }
    },
    getText: function (treeID) {
        var allControl = TextboxTree.getAllControl(treeID);
        return allControl.inputTextbox.val();
    },
    getValue: function (treeID) {
        var allControl = TextboxTree.getAllControl(treeID);
        return allControl.inputHidden.val();
    },
    setText: function (treeID, text) {
        var allControl = TextboxTree.getAllControl(treeID);
        allControl.inputTextbox.val(text);
    },
    setValue: function (treeID, value) {
        var allControl = TextboxTree.getAllControl(treeID);
        allControl.inputHidden.val(value);
    },
    clear: function (treeID) {
        var allControl = TextboxTree.getAllControl(treeID);
        allControl.inputTextbox.val('');
        allControl.inputHidden.val('');
        $.fn.zTree.destroy(CommonFunc.formatIdRemoveJquery(treeID));
    },
    enable: function (treeID) {
        var allControl = TextboxTree.getAllControl(treeID);
        allControl.inputTextbox.removeAttr("disabled");
        allControl.buttonAppend.removeAttr("disabled");
    },
    disable: function (treeID) {
        var allControl = TextboxTree.getAllControl(treeID);
        allControl.inputTextbox.attr("disabled", "disabled");
        allControl.buttonAppend.attr("disabled", "disabled");
    }
}