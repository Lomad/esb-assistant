package com.winning.esb.model.ext;

import com.winning.esb.model.SimulationFlowModel;
import com.winning.esb.model.SimulationTestLogModel;

/**
 * @author xuehao
 * @date 2017/8/29
 */
public class SimulationFlowExtModel {
    private SimulationFlowModel obj;
    private SimulationTestLogModel log;

    public SimulationFlowExtModel() {
    }

    public SimulationFlowExtModel(SimulationFlowModel obj, SimulationTestLogModel log) {
        this.obj = obj;
        this.log = log;
    }

    public SimulationFlowModel getObj() {
        return obj;
    }

    public void setObj(SimulationFlowModel obj) {
        this.obj = obj;
    }

    public SimulationTestLogModel getLog() {
        return log;
    }

    public void setLog(SimulationTestLogModel log) {
        this.log = log;
    }
}