package com.winning.esb.model;

import com.winning.esb.model.enums.SvcStructureEnum;

import java.util.Date;

/**
 * Created by xuehao on 2017/8/9.
 */
public class SvcStructureModel implements Cloneable {
    private Integer id;
    private Integer sid;
    private Integer direction;
    private Integer pid = 0;
    private String code;
    private String name;
    private Integer order_num;
    private Integer is_attr = SvcStructureEnum.IsAttrEnum.No.getCode();
    private Integer can_edit = SvcStructureEnum.CanEditEnum.Yes.getCode();
    private Integer required = SvcStructureEnum.RequiredEnum.No.getCode();
    private Integer is_loop = SvcStructureEnum.IsLoopEnum.No.getCode();
    private Integer result_mark = SvcStructureEnum.ResultMarkEnum.No.getCode();
    private Integer data_type = SvcStructureEnum.DataTypeEnum.Strings.getCode();
    private String value_default;
    private String desp;
    private Date ctime;
    private Date mtime;

    @Override
    public SvcStructureModel clone() {
        try {
            return (SvcStructureModel) super.clone();
        } catch (Exception ex) {
            return null;
        }
    }

    public SvcStructureModel() {
        this.setPid(0);
        this.setData_type(0);
        this.setOrder_num(0);
        this.setCtime(new Date());
        this.setMtime(new Date());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getData_type() {
        return data_type;
    }

    public void setData_type(Integer data_type) {
        this.data_type = data_type;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getMtime() {
        return mtime;
    }

    public void setMtime(Date mtime) {
        this.mtime = mtime;
    }

    public Integer getIs_attr() {
        return is_attr;
    }

    public void setIs_attr(Integer is_attr) {
        this.is_attr = is_attr;
    }

    public Integer getCan_edit() {
        return can_edit;
    }

    public void setCan_edit(Integer can_edit) {
        this.can_edit = can_edit;
    }

    public Integer getRequired() {
        return required;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }

    public Integer getIs_loop() {
        return is_loop;
    }

    public void setIs_loop(Integer is_loop) {
        this.is_loop = is_loop;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public Integer getOrder_num() {
        return order_num;
    }

    public void setOrder_num(Integer order_num) {
        this.order_num = order_num;
    }

    public Integer getResult_mark() {
        return result_mark;
    }

    public void setResult_mark(Integer result_mark) {
        this.result_mark = result_mark;
    }

    public String getValue_default() {
        return value_default;
    }

    public void setValue_default(String value_default) {
        this.value_default = value_default;
    }
}
