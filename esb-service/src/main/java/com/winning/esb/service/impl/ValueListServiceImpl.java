package com.winning.esb.service.impl;

import com.winning.esb.dao.IValueListDao;
import com.winning.esb.model.SvcStructureModel;
import com.winning.esb.model.ValueListModel;
import com.winning.esb.model.enums.SvcStructureEnum;
import com.winning.esb.model.enums.ValueListEnum;
import com.winning.esb.model.ext.ValueListExtModel;
import com.winning.esb.service.ISvcStructureService;
import com.winning.esb.service.IValueListService;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.RegexUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ValueListServiceImpl implements IValueListService {
    @Autowired
    private IValueListDao dao;
    @Autowired
    private ISvcStructureService svcStructureService;

    private final String NEW_LINE = "\r\n";

    @Override
    public String insertAfterDelete(Integer ssid, Integer resultMark, String strValueList, String strValueListFailure) {
        List<ValueListModel> list = new ArrayList<>();
        ValueListModel obj;
        List<String> valueList = ListUtils.transferToList(strValueList.split(NEW_LINE));
        List<String> valueListFailure = ListUtils.transferToList(strValueListFailure.split(NEW_LINE));
        Integer type;
        if (resultMark == null || SvcStructureEnum.ResultMarkEnum.No.getCode() == resultMark.intValue()) {
            type = ValueListEnum.TypeEnum.Normal.getCode();
        } else {
            type = ValueListEnum.TypeEnum.Success.getCode();
        }
        //候选值列表
        if (!StringUtils.isEmpty(strValueList) && !ListUtils.isEmpty(valueList)) {
            for (String value : valueList) {
                list.add(new ValueListModel(ssid, type, value));
            }
        }
        //失败的候选值列表
        if (!StringUtils.isEmpty(strValueListFailure) && !ListUtils.isEmpty(valueListFailure)) {
            type = ValueListEnum.TypeEnum.Failure.getCode();
            for (String value : valueListFailure) {
                list.add(new ValueListModel(ssid, type, value));
            }
        }
        return insertAfterDelete(ssid, list);
    }

    @Override
    public String insertAfterDelete(Integer ssid, List<ValueListModel> list) {
        return insert(ssid, list, true);
    }

    @Override
    public String insert(Integer ssid, List<ValueListModel> list, boolean deleteBeforeInsert) {
        SvcStructureModel svcStructureModel = svcStructureService.queryById(ssid);
        String errInfo = "";
        if (svcStructureModel != null) {
            Integer data_type = svcStructureModel.getData_type();
            String regex = SvcStructureEnum.DataTypeEnum.getRegex(data_type);
            for (ValueListModel obj : list) {
                String value = obj.getValue();
                if (!RegexUtils.match(regex, value)) {
                    errInfo += value + "不符合数据类型！ ";
                }
            }
        }
        if (StringUtils.isEmpty(errInfo)) {
            dao.insert(ssid, list, deleteBeforeInsert);
        }
        return errInfo;
    }

    @Override
    public String delete(Integer ssid) {
        dao.delete(ssid);
        return null;
    }

    @Override
    public List<ValueListModel> queryResultNodeValuesBySid(Integer sid) {
        SvcStructureModel svcStructureModel = svcStructureService.getResultNode(sid);
        return queryBySsid(svcStructureModel.getId());
    }

    @Override
    public List<ValueListModel> queryResultNodeValuesSuccessBySid(Integer sid) {
        SvcStructureModel svcStructureModel = svcStructureService.getResultNode(sid);
        return queryBySsid(svcStructureModel.getId(), ValueListEnum.TypeEnum.Success.getCode());
    }

    @Override
    public List<String> listValueByModel(List<ValueListModel> list) {
        List<String> values;
        if (!ListUtils.isEmpty(list)) {
            values = new ArrayList<>();
            for (ValueListModel model : list) {
                values.add(model.getValue());
            }
        } else {
            values = null;
        }
        return values;
    }

    @Override
    public List<ValueListModel> queryBySid(Integer sid, Integer direction) {
        return dao.queryBySid(sid, direction);
    }

    @Override
    public List<ValueListModel> queryBySsid(Integer ssid) {
        return queryBySsid(ssid, null);
    }

    @Override
    public List<ValueListModel> queryBySsid(Integer ssid, Integer type) {
        return dao.queryBySsid(ssid, type);
    }

    @Override
    public ValueListExtModel getExtBySsid(Integer ssid) {
        ValueListExtModel result;
        List<ValueListModel> list = queryBySsid(ssid);
        if (!ListUtils.isEmpty(list)) {
            result = new ValueListExtModel();
            List<String> valueList = new ArrayList<>();
            List<String> valueListFailure = new ArrayList<>();
            for (ValueListModel model : list) {
                if (ValueListEnum.TypeEnum.Failure.getCode() == model.getType().intValue()) {
                    valueListFailure.add(model.getValue());
                } else {
                    valueList.add(model.getValue());
                }
            }
            result.setValueList(valueList);
            result.setValueListFailure(valueListFailure);
            result.setStrValueList(StringUtils.join(valueList, NEW_LINE));
            result.setStrValueListFailure(StringUtils.join(valueListFailure, NEW_LINE));
        } else {
            result = null;
        }
        return result;
    }
}