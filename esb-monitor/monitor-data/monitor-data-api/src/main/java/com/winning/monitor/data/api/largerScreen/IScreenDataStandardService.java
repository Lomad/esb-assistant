package com.winning.monitor.data.api.largerScreen;

import java.util.List;
import java.util.Map;

public interface IScreenDataStandardService {

    /* OverView Of MDM+EMPI+WDK */
    Map countByOverView(String sys,String params);

    Map getUnRegistryCount();

    /* start:MDM存储过程 */
    List queryMonthCountBeforeMDM(String time);

    List queryPerOfCommunication(String time);

    List queryUpdateCount(String time);
    /* end:MDM存储过程 */

    /* start:EMPI存储过程 */
    List queryAddressInfo(String code);
    /* end:EMPI存储过程 */

    /* start:共享文档存储过程 */
    List queryTimePeriodStatistic(String key);
    /* end:共享文档存储过程 */

    List queryAreaDataByEMPI(int type);
    List queryPatRegdataByEMPI(Integer type);

    List queryCategoryDatasetByWDK(Integer type);

    List queryRegdataByWDK(Integer type);
}
