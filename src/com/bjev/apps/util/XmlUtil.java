package com.bjev.apps.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

public class XmlUtil {

	public static <T> T getBean(String xml,Class<T> c) {
		XStream xstream = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));
		xstream.processAnnotations(c);
		T obj=c.cast(xstream.fromXML(xml));
		return obj;
	}
	

	
	public static String getXml(Object obj) {
		XStream xstream = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));
		xstream.processAnnotations(obj.getClass());
		String xml = xstream.toXML(obj);
		return xml;
	}
	
	
	public static String turnMeanings(String s) {
		String rst=s;
		if(s!=null) {
			rst=rst.replace("&", "&amp;");
			rst=rst.replace("<", "&lt;");
			rst=rst.replace(">", "&gt;");
			rst=rst.replace("'", "&apos;");
			rst=rst.replace("\"", "&quot;");
		}
		return rst;
	}
	
	public static String converseXml(Map<String, Object> map) {
		StringBuffer content = new StringBuffer("<root>");
		try {
			if(map != null && map.size() > 0) {
				Iterator<Entry<String, Object>> entryIter = map.entrySet().iterator();
				int i = 0;
				while(entryIter.hasNext()) {
					Entry<String, Object> entry = entryIter.next();
					String key = entry.getKey();
					List<Map> vals = (List<Map>)entry.getValue();
					if(vals != null && vals.size()>0) {
						content.append("<").append("table"+i).append(">");
						for(Map valMap : vals) {
							content.append("<row>");
							Iterator<Entry<String, Object>> mapEntryIter = valMap.entrySet().iterator();
							while(mapEntryIter.hasNext()) {
								Entry<String, Object> mapEntry = mapEntryIter.next();
								String subKey = mapEntry.getKey();
								Object subVal = mapEntry.getValue();
								if(subVal != null) {
									content.append("<").append(subKey).append(">");
									content.append(subVal);
									content.append("</").append(subKey).append(">");
								}else {
									content.append("<").append(subKey).append("/>");
								}
							}
							content.append("</row>");
						}
						content.append("</").append("table"+i).append(">");
					}else {
						content.append("<").append("table"+i).append("/>");
					}
					i++;
				}
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		content.append("</root>");
		 System.out.println("-----root:"+content.toString());
		return content.toString();
	}
	
	public static String subStringBetween(String str, String strStart, String strEnd) {         
		/* 找出指定的2个字符在 该字符串里面的 位置 */        
		int strStartIndex = str.indexOf(strStart);        
		int strEndIndex = str.indexOf(strEnd);        
		/* index 为负数 即表示该字符串中 没有该字符 */        
		if (strStartIndex < 0) {            
			return "字符串 :---->" + str + "<---- 中不存在 " + strStart + ", 无法截取目标字符串";        
		}        
		if (strEndIndex < 0) {            
			return "字符串 :---->" + str + "<---- 中不存在 " + strEnd + ", 无法截取目标字符串";        
		}        
		/* 开始截取 */        
		String result = str.substring(strStartIndex, strEndIndex).substring(strStart.length());        
		return result;    
	}
	
	public static List<String> match(String s) {
        List<String> results = new ArrayList<String>();
        Pattern p = Pattern.compile("<item>(.*?)</item>");
        Matcher m = p.matcher(s);
        while (m.find()) {
            results.add("<item>"+m.group(1)+"</item>");
        }
        return results;
    }
	
	public static void main(String args[]) {
//		OMFactory fac = OMAbstractFactory.getOMFactory();
//		OMNamespace omNs = fac.createOMNamespace("urn:gdgc.com.cn:I_ECC:OA", "n0");
//		OMNamespace omNs2 = fac.createOMNamespace("", "");
//		OMElement method = fac.createOMElement("MT_TEST_OA_ECC_PO_CREATE_RESP", omNs);
//		
//		OMElement omElement1 = fac.createOMElement("TYPE", omNs2);
//		omElement1.setText("S");
//		OMElement omElement2 = fac.createOMElement("MESSAGE", omNs2);
//		omElement2.setText("采购订单创建成功");
//		OMElement omElement3 = fac.createOMElement("Details", omNs2);
//		
//		OMElement omElement4 = fac.createOMElement("EBELN", omNs2);
//		omElement4.setText("4500005176");
//		
//		OMElement omElement5 = fac.createOMElement("EBELP", omNs2);
//		omElement5.setText("00010");
//		
//		omElement3.addChild(omElement4);
//		omElement3.addChild(omElement5);
//		
//		method.addChild(omElement1);
//		method.addChild(omElement2);
//		method.addChild(omElement3);
//		System.out.println(method);
//		
//		String rstStr = method.toString();
//		if (rstStr != null && !"".equals(rstStr)) {
//			String rstStrXml = XmlUtil.subStringBetween(rstStr, "<EBELN>", "</EBELN>");
//			System.out.println(rstStrXml);
//		}		
	}
	
	
}
