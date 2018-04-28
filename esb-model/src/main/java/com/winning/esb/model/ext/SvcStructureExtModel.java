package com.winning.esb.model.ext;

import com.winning.esb.model.SvcStructureModel;
import com.winning.esb.model.ValueListModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/9.
 */
public class SvcStructureExtModel {
    private SvcStructureModel obj;
    private List<SvcStructureExtModel> children;
    private List<ValueListModel> valueList;

    /**
     * 生成虚拟的顶级节点
     */
    public static SvcStructureExtModel createVirtualRoot(List<SvcStructureExtModel> list) {
        SvcStructureExtModel svcStructureExtModel;
        if (list != null && list.size() > 0) {
            if (list.size() > 1) {
                svcStructureExtModel = new SvcStructureExtModel();
                svcStructureExtModel.setObj(null);
                svcStructureExtModel.setChildren(list);
            } else {
                svcStructureExtModel = list.get(0);
            }
        } else {
            svcStructureExtModel = null;
        }
        return svcStructureExtModel;
    }

    public SvcStructureModel getObj() {
        return obj;
    }

    public void setObj(SvcStructureModel obj) {
        this.obj = obj;
    }

    public List<SvcStructureExtModel> getChildren() {
        return children;
    }

    public void setChildren(List<SvcStructureExtModel> children) {
        this.children = children;
    }

    public List<ValueListModel> getValueList() {
        return valueList;
    }

    public void setValueList(List<ValueListModel> valueList) {
        this.valueList = valueList;
    }

    /**
     * 将children列表转为map（key - 节点代码， value - 结构扩展对象）
     */
    public Map<String, SvcStructureExtModel> childrenListToMap() {
        Map<String, SvcStructureExtModel> map;
        if (children != null && children.size() > 0) {
            map = new HashMap<>();
            for (SvcStructureExtModel model : children) {
                map.put(model.getObj().getCode(), model);
            }
        } else {
            map = null;
        }
        return map;
    }
}