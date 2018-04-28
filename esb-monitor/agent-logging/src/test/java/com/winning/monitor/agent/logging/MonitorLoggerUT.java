package com.winning.monitor.agent.logging;

import com.winning.monitor.agent.config.utils.Properties;
import com.winning.monitor.agent.logging.transaction.DefaultTransaction;
import com.winning.monitor.agent.logging.transaction.Transaction;
import org.junit.Test;

import java.io.File;

import static com.winning.monitor.agent.logging.MonitorLoggerUT.MANAGER_NUMBER.*;

/**
 * Created by nicholasyan on 16/9/8.
 */
public class MonitorLoggerUT {

    public static final String INTERFACE_MANAGER = "接口负责人";
    public static final String INTERFACE_MANAGER_CALL = "负责人联系方式";

    public enum MANAGER_NUMBER {
        殷奇隆("QQ:403252077"), 高然("QQ:1652117"),
        顾传欢("QQ:785068548"), 郑德俊("QQ:254880723"),
        郑远远("QQ:396006797");

        private String number;

        MANAGER_NUMBER(String number) {
            this.number = number;
        }

        public String getNumber() {
            return number;
        }
    }

    public static void addManagerNumber(Transaction transaction, MANAGER_NUMBER manager) {
        switch (manager) {
            case 殷奇隆:
                transaction.addData(INTERFACE_MANAGER, 殷奇隆);
                transaction.addData(INTERFACE_MANAGER_CALL, 殷奇隆.getNumber());
            case 高然:
                transaction.addData(INTERFACE_MANAGER, 高然);
                transaction.addData(INTERFACE_MANAGER_CALL, 高然.getNumber());
            case 郑德俊:
                transaction.addData(INTERFACE_MANAGER, 郑德俊);
                transaction.addData(INTERFACE_MANAGER_CALL, 郑德俊.getNumber());
            case 顾传欢:
                transaction.addData(INTERFACE_MANAGER, 顾传欢);
                transaction.addData(INTERFACE_MANAGER_CALL, 顾传欢.getNumber());
            case 郑远远:
                transaction.addData(INTERFACE_MANAGER, 郑远远);
                transaction.addData(INTERFACE_MANAGER_CALL, 郑远远.getNumber());
        }
    }

    @Test
    public void testEnum(){
        Transaction transaction = MonitorLogger.beginTransactionType("123");
        addManagerNumber(transaction,殷奇隆);
        transaction.success();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testTransaction() throws InterruptedException {
        Transaction transaction = MonitorLogger.newTransaction("SQL ", "SELECT ");
        Thread.sleep(1000);


        transaction.setStatus(Transaction.SUCCESS);
        transaction.complete();

        System.out.println(transaction.getDurationInMillis());
        System.out.println(transaction.getDurationInMicros());

        transaction = MonitorLogger.newTransaction("SQL", "SELECT");
        Thread.sleep(2000);
        transaction.setStatus(Transaction.SUCCESS);
        transaction.complete();

        System.out.println(transaction.getDurationInMillis());
        System.out.println(transaction.getDurationInMicros());

    }


    @Test
    public void testTransactionRunning() throws InterruptedException {
        transactionThread();
        Thread.sleep(60000);
    }

    @Test
    public void testTransaction2K() throws InterruptedException {
        for (int i = 0; i < 2000; i++) {
            Transaction transaction = MonitorLogger.newTransaction("SQL2", "SELECT 2");
            transaction.setStatus(Transaction.SUCCESS);
            transaction.complete();
        }
        Thread.sleep(300000);
        System.out.println("complete");
    }

    private void transactionThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 2000; i++) {
                    Transaction transaction = MonitorLogger.newTransaction("SQL1", "SELECT 1");
//                    try {
//                        Thread.sleep(1);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    transaction.setStatus(Transaction.SUCCESS);
                    transaction.complete();
                }
                System.out.println("complete");
            }
        });
        thread.start();
    }


    @Test
    public void testNestedTransaction() throws InterruptedException {
        MonitorLogger.checkAndInitialize();
        Transaction parentTransaction = MonitorLogger.newTransaction("PARENT", "HELLO");
        Transaction childTransaction = MonitorLogger.newTransaction("CHILD", "HELLO");
        Thread.sleep(10);
        childTransaction.success();
        parentTransaction.success();
        Thread.sleep(1000);
    }

    @Test
    public void testTransactionData() throws InterruptedException {
        MonitorLogger.checkAndInitialize();

        int i = 2;

        while (i>0) {
            MonitorLogger.setCaller("HIS", "192.16.0.1", "PC");
            DefaultTransaction parentTransaction = (DefaultTransaction)
                    MonitorLogger.beginTransactionType("挂号");
            parentTransaction.addData("data1", "data1");

            Transaction childTransaction = MonitorLogger.
                    beginTransactionName(parentTransaction, "读取数据库");
            childTransaction.success();

            parentTransaction.success();
            Thread.sleep(1000);

            i--;
        }
    }

    @Test
    public void testDomain(){
        MonitorLogger.setCaller("HIS101", "192.16.0.1", "PC");
        DefaultTransaction parentTransaction = (DefaultTransaction)
                MonitorLogger.beginTransactionType("患者注册");
//        parentTransaction.addData("病人姓名", "张三2");
        parentTransaction.addData("病人姓名", "<?xml version=\"1.0\" encoding=\"utf-8\"?><RegisterPatient><ConfigHeader><Facility>42500244X01</Facility><Application>WinningSoft-HIS</Application></ConfigHeader><PatientIDs><PatientID><CardID>1293923</CardID><DomainCode>PATID_O</DomainCode></PatientID><PatientID><CardID>50267584</CardID><DomainCode>BLH_O</DomainCode></PatientID><PatientID><CardID>5003566400990336959265127552</CardID><DomainCode>YBKH</DomainCode></PatientID></PatientIDs><GlobalPID></GlobalPID><SourcePatient><FamilyName>陆雯婧</FamilyName><GivenName></GivenName><MiddleName></MiddleName><MothersMaidenName></MothersMaidenName><DateTimeOfBirth>2007-12-23</DateTimeOfBirth><Sex>F</Sex><MaritalStatus>B</MaritalStatus><Religion></Religion><SSNNumber></SSNNumber><DriversLicenseNumber></DriversLicenseNumber><PatientAccountNumber /><Ethnic>汉族</Ethnic><BirthPlace>上海市</BirthPlace><MultipleBirthIndicator></MultipleBirthIndicator><BirthOrder></BirthOrder><Nationality>    </Nationality><PatientDeathIndicator></PatientDeathIndicator><PatientDeathDateAndTime></PatientDeathDateAndTime><LastUpdateDateTime></LastUpdateDateTime><LastUpdateFacility></LastUpdateFacility></SourcePatient><PatientAddresses><PatientAddress><Country>CN</Country><Province></Province><City></City><StreetAddress>0</StreetAddress><ZipCode></ZipCode><Type>H</Type></PatientAddress></PatientAddresses><PatientPhones><PatientPhone><AddressType>PRN</AddressType><EquipmentType>CP</EquipmentType><CountryCode>086</CountryCode><AreaCode></AreaCode><PhoneNumber>13482376969</PhoneNumber><Extension></Extension><Email></Email></PatientPhone><PatientPhone><AddressType>PRN</AddressType><EquipmentType>CP</EquipmentType><CountryCode>086</CountryCode><AreaCode></AreaCode><PhoneNumber>13482376969</PhoneNumber><Extension></Extension><Email></Email></PatientPhone></PatientPhones><PatientVisit><VisitNumber><CardID>4991266</CardID><DomainCode>GHXH</DomainCode></VisitNumber><PatientClass>O</PatientClass><AssignedPatientLocation><PointOfCare>急诊</PointOfCare><Room></Room><Bed></Bed><Facility>上海市儿童医院</Facility><PatientLocationType>C</PatientLocationType><Building></Building><Floor></Floor><LocationDescription></LocationDescription></AssignedPatientLocation><AdmissionType>R</AdmissionType><AttendingDoctor><FamilyName>张夏南</FamilyName><GivenName></GivenName><MiddleName></MiddleName><CardID>2258</CardID><DomainCode></DomainCode></AttendingDoctor><ReferringDoctor><FamilyName></FamilyName><GivenName></GivenName><MiddleName></MiddleName><CardID></CardID><DomainCode></DomainCode></ReferringDoctor><ConsultingDoctor><FamilyName></FamilyName><GivenName></GivenName><MiddleName></MiddleName><CardID></CardID><DomainCode></DomainCode></ConsultingDoctor><HospitalService></HospitalService><AdmittingDoctor/><ReadmissionIndicator></ReadmissionIndicator><AdmitSource></AdmitSource><FinancialClass></FinancialClass><DischargeDisposition></DischargeDisposition><AdmitDateTime>2016-08-18 17:23:54</AdmitDateTime><DischargeDateTime>2016-08-18 17:23:54</DischargeDateTime></PatientVisit></RegisterPatient>\n");

        parentTransaction.success();
    }

    @Test
    public void getHome() {
        String homePath = Properties.forString().fromEnv().fromSystem().getProperty("MONITOR_HOME", "/data/winning-monitor");
        File file = new File(homePath, "client.json");
        System.out.println(homePath);
    }

}
