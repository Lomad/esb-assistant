package com.winning.esb.simulator.controller;

import com.winning.esb.simulator.service.api.ISimulatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static com.winning.esb.simulator.utils.GlobalConstant.GSON;

/**
 * @Author Lemod
 * @Version 2018/4/3
 */
@Controller
@RequestMapping(value = {"", "/ajax"})
public class SimulatorController {

    @Autowired
    private ISimulatorService simulatorService;

    @RequestMapping(value = {""})
    public ModelAndView simulator() {
        return new ModelAndView("simulator");
    }

    @RequestMapping(value = "/start")
    @ResponseBody
    public void startSimulate(String params) {
        Map in = GSON.fromJson(params, Map.class);
        simulatorService.startSimulator(in);
    }

}
