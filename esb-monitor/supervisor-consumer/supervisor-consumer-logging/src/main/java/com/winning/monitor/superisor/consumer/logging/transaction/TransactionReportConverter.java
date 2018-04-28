package com.winning.monitor.superisor.consumer.logging.transaction;

import com.winning.monitor.data.api.transaction.TransactionStatisticsComputer;
import com.winning.monitor.data.api.transaction.vo.*;
import com.winning.monitor.data.api.vo.Range;
import com.winning.monitor.data.api.vo.Range2;
import com.winning.monitor.superisor.consumer.logging.transaction.entity.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nicholasyan on 16/9/12.
 */
public class TransactionReportConverter {

    public static TransactionStatisticsComputer computer = new TransactionStatisticsComputer();

    public static DateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static TransactionReportVO toTransactionReportVO(TransactionReport transactionReport) {
        TransactionReportVO transactionReportVO = new TransactionReportVO();
        transactionReportVO.setId(transactionReport.getId());
        transactionReportVO.setDomain(transactionReport.getDomain());
        transactionReportVO.setGroup(transactionReport.getGroup());
        Set<String> ips = new LinkedHashSet<>(transactionReport.getIps());
        transactionReportVO.setIps(ips);
        transactionReportVO.setStartTime(simpleDateTimeFormat.format(transactionReport.getStartTime()));
        transactionReportVO.setEndTime(simpleDateTimeFormat.format(transactionReport.getEndTime()));

        List<TransactionMachineVO> machineList = new ArrayList<>();
        for (Machine machine : transactionReport.getMachines().values()) {
            TransactionMachineVO machineDTO = toTransactionMachineVO(machine);
            machineList.add(machineDTO);
        }
        transactionReportVO.setMachines(machineList);
        return transactionReportVO;
    }

    public static TransactionMachineVO toTransactionMachineVO(Machine machine) {
        TransactionMachineVO transactionMachineVO = new TransactionMachineVO();
        transactionMachineVO.setIp(machine.getIp());
        List<TransactionClientVO> clientVOs = new ArrayList<>();

        for (Client client : machine.getClients().values()) {
            clientVOs.add(toTransactionClientVO(client));
        }
        transactionMachineVO.setTransactionClients(clientVOs);
        return transactionMachineVO;
    }

    public static TransactionClientVO toTransactionClientVO(Client client) {
        TransactionClientVO transactionClientVO = new TransactionClientVO();
        transactionClientVO.setId(client.getId());
        transactionClientVO.setIp(client.getIp());
        transactionClientVO.setDomain(client.getDomain());
        transactionClientVO.setType(client.getType());

        List<TransactionTypeVO> typeDTOList = new ArrayList<>();

        for (TransactionType transactionType : client.getTypes().values()) {
            typeDTOList.add(toTransactionTypeVO(transactionType));
        }
        transactionClientVO.setTransactionTypes(typeDTOList);
        return transactionClientVO;
    }


    public static TransactionTypeVO toTransactionTypeVO(TransactionType transactionType) {
        TransactionTypeVO transactionTypeVO = new TransactionTypeVO();
        transactionTypeVO.setId(transactionType.getId());
        transactionTypeVO.setRouter(transactionType.getRouter());
        transactionTypeVO.setName(transactionType.getId());
        transactionTypeVO.setTotalCount(transactionType.getTotalCount());
        transactionTypeVO.setFailCount(transactionType.getFailCount());
        transactionTypeVO.setMin(transactionType.getMin());
        transactionTypeVO.setMax(transactionType.getMax());
        transactionTypeVO.setSum(transactionType.getSum());
        transactionTypeVO.setSum2(transactionType.getSum2());
        transactionTypeVO.setRange2s(transactionType.getRange2s());
        transactionTypeVO.setAllDurations(transactionType.getAllDurations());
        List<TransactionNameVO> nameDTOList = new ArrayList<>();
        for (TransactionName transactionName : transactionType.getNames().values()) {
            nameDTOList.add(toTransactionNameVO(transactionName));
        }
        transactionTypeVO.setTransactionNames(nameDTOList);

        computer.calcTransactionType(transactionTypeVO);
        for (Range2 range : transactionTypeVO.getRange2s().values()) {
            computer.calcRange2(range);
        }

        return transactionTypeVO;
    }

    public static TransactionNameVO toTransactionNameVO(TransactionName transactionName) {
        TransactionNameVO transactionNameVO = new TransactionNameVO();
        transactionNameVO.setId(transactionName.getId());
        transactionNameVO.setName(transactionName.getId());
        transactionNameVO.setTotalCount(transactionName.getTotalCount());
        transactionNameVO.setFailCount(transactionName.getFailCount());
        transactionNameVO.setMin(transactionName.getMin());
        transactionNameVO.setMax(transactionName.getMax());
        transactionNameVO.setSum(transactionName.getSum());
        transactionNameVO.setSum2(transactionName.getSum2());
        transactionNameVO.setRanges(transactionName.getRanges());
        transactionNameVO.setDurations(transactionName.getDurations());
        transactionNameVO.setAllDurations(transactionName.getAllDurations());

        computer.calcTransactionName(transactionNameVO);

        for (Range range : transactionNameVO.getRanges().values()) {
            computer.calcRange(range);
        }

        return transactionNameVO;
    }


}
