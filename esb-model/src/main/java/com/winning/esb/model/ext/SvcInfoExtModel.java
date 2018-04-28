package com.winning.esb.model.ext;

import com.winning.esb.model.SvcInfoModel;

/**
 * Created by xuehao on 2017/8/9.
 */
public class SvcInfoExtModel {
    private SvcInfoModel svcInfo;
    private String inContent;
    private String outContent;
    /**
     * 服务状态
     */
    private Integer status;
    /**
     * 第一级目录的索引
     */
    private String paragraphIndexLevel1;
    /**
     * 第二级目录的索引
     */
    private String paragraphIndexLevel2;

    public SvcInfoModel getSvcInfo() {
        return svcInfo;
    }

    public void setSvcInfo(SvcInfoModel svcInfo) {
        this.svcInfo = svcInfo;
    }

    public String getInContent() {
        return inContent;
    }

    public void setInContent(String inContent) {
        this.inContent = inContent;
    }

    public String getOutContent() {
        return outContent;
    }

    public void setOutContent(String outContent) {
        this.outContent = outContent;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getParagraphIndexLevel1() {
        return paragraphIndexLevel1;
    }

    public void setParagraphIndexLevel1(String paragraphIndexLevel1) {
        this.paragraphIndexLevel1 = paragraphIndexLevel1;
    }

    public String getParagraphIndexLevel2() {
        return paragraphIndexLevel2;
    }

    public void setParagraphIndexLevel2(String paragraphIndexLevel2) {
        this.paragraphIndexLevel2 = paragraphIndexLevel2;
    }
}