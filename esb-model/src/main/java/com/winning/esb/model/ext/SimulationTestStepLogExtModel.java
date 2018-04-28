package com.winning.esb.model.ext;

import com.winning.esb.model.SimulationTestStepLogModel;

/**
 * Created by xuehao on 2017/8/21.
 */
public class SimulationTestStepLogExtModel {
    private SimulationTestStepLogModel obj;

    /**
     * 测试次数
     */
    private Integer testCount;

    public SimulationTestStepLogModel getObj() {
        return obj;
    }

    public void setObj(SimulationTestStepLogModel obj) {
        this.obj = obj;
    }

    public Integer getTestCount() {
        return testCount;
    }

    public void setTestCount(Integer testCount) {
        this.testCount = testCount;
    }
}