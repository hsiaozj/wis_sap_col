package com.bjev.apps.sap;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.bjev.apps.po.SapSource;
import com.seeyon.v3x.dee.adapter.sap.jco.plugin.DeeSapJco;

public class SAPConn {
    private static final String ABAP_AS = "ABAP_AS_WITH_POOL";
    private static final Logger log = LoggerFactory.getLogger(SAPConn.class);

    /**
     * 创建SAP接口属性文件。
     * @param name    ABAP管道名称
     * @param suffix    属性文件后缀
     * @param properties    属性文件内容
     */
    private static void createDataFile(String name, String suffix, Properties properties){
        File cfg = new File(name+"."+suffix);
        if(cfg.exists()){
            cfg.deleteOnExit();
        }
        try{
            FileOutputStream fos = new FileOutputStream(cfg, false);
            properties.store(fos, "for tests only !");
            fos.close();
        }catch (Exception e){
        	log.info("Create Data file fault, error msg: " + e.toString());
            throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);
        }
    }

    /**
     * 获取SAP连接
     * @return  SAP连接对象
     */
    public static JCoDestination connect(SapSource sapSource){
        JCoDestination destination =null;
        try {
        	  Properties connectProperties = new Properties();
              connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, sapSource.getJcoAshost());               //服务器
              connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  sapSource.getJcoSysNr());                //系统编号
              connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, sapSource.getJcoClient());               //SAP集团
              connectProperties.setProperty(DestinationDataProvider.JCO_USER,   sapSource.getJcoUser());                 //SAP用户名
              connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, sapSource.getJcoPasswd());               //密码
              connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   sapSource.getJcoLang());                 //登录语言
              connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, sapSource.getJcoPoolCapacity().toString()); //最大连接数
              connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, sapSource.getJcoPeakLimit().toString());       //最大连接线程
              
              createDataFile(ABAP_AS, "jcoDestination", connectProperties);
            destination = JCoDestinationManager.getDestination(ABAP_AS);
            destination.ping();
        } catch (JCoException e) {
        	System.out.println("Connect SAP fault, error msg: " + e.toString());
        }
        return destination;
    }
    
    public static JCoDestination connect2(SapSource sapSource){
        JCoDestination destination = null;
        try {
    		DeeSapJco jco = DeeSapJco.getInstance(sapSource.getJcoAshost(), sapSource.getJcoSysNr(), 
    				sapSource.getJcoClient(), sapSource.getJcoUser(), sapSource.getJcoPasswd());
    		destination = JCoDestinationManager.getDestination(jco.getServerUrl());
            destination.ping();
        } catch (Exception e) {
        	log.info("Connect SAP fault, error msg: " + e.toString());
        	System.out.println("Connect SAP fault, error msg: " + e.toString());
        }
        return destination;
    }
    
}
