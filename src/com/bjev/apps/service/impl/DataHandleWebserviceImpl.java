package com.bjev.apps.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.ctpenumnew.manager.EnumManager;
import com.seeyon.ctp.common.po.ctpenumnew.CtpEnumItem;
import com.seeyon.ctp.form.modules.engin.base.formData.FormDataDAO;
import com.bjev.apps.service.DataHandleService;
import com.bjev.apps.util.IConstant;
//import net.sf.json.JSONObject;
//import net.sf.json.util.JSONUtils;

public class DataHandleWebserviceImpl implements DataHandleService{
	private static Log log = LogFactory.getLog(DataHandleWebserviceImpl.class);
	    
	private FormDataDAO formDataDAO = (FormDataDAO)AppContext.getBean("formDataDAO");	
	
	private EnumManager enumManagerNew = (EnumManager)AppContext.getBean("enumManagerNew");
	
	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> procMappingData(Map<String, Object> map) throws Exception {
		Map<String, Object> procDataResult = new HashMap<String, Object>();
		Map valMap = null;
		List<Map> allConfigDataList = null;
		Map<String, Object> treeMap = new HashMap();
		String xml = "";
		try {
			String configCategroy = (String)map.get("configCategroy");
			Map<String,List<Map>> documentDataMap = (Map<String,List<Map>>)map.get("documentData");
			if(documentDataMap == null || documentDataMap.size() == 0) {
				return procDataResult;
			}
			Map<String, Object> dataMap = new HashMap();
			dataMap.put(IConstant.wsConfigCategroyCol, configCategroy);
			List<Map> list = this.getValueByMap(null, dataMap, IConstant.wsFormmainTable, "and");
			
	    	if(list != null && list.size() > 0) {
	    		valMap = list.get(0);
	    		Long paramId = 0l;
	    		Object id = valMap.get("id");
				if(id instanceof BigDecimal) {
					paramId = ((BigDecimal)id).longValue();
				}else if(id instanceof Long) {
					paramId = (Long)id;
				}
	    		
	    		procDataResult.put("wsUrl", valMap.get(IConstant.wsUrl));//webservice地址
	    		procDataResult.put("wsFunction", valMap.get(IConstant.wsFunction));//方法名
	    		procDataResult.put("wsNameSpace", valMap.get(IConstant.wsNameSpace));//命名空间列名
	    		procDataResult.put("wsUserName", valMap.get(IConstant.wsUserName));//用户名列名
	    		procDataResult.put("wsPassWord", valMap.get(IConstant.wsPassWord));//密码列名
	    		
	    		//获取所有配置参数
	    		dataMap = new HashMap();
	    		dataMap.put("formmain_id", paramId);
	    		allConfigDataList = this.getValueByMap(null, dataMap, IConstant.wsFormsonTable, "and");
	    		
	    		//封装树形结构
	    		if(allConfigDataList != null) {
	    			List<String> virtualNodeList = new ArrayList();
	    			treeMap.put("ROOT", putTreeData("ROOT", null, allConfigDataList, documentDataMap, paramId, virtualNodeList));
//	    			String json = JSONObject.fromObject(treeMap).toString();
	    			xml = this.getXmlContent(treeMap);
	    			//清除虚拟节点
	    			xml = cleanVirtualNode(xml, virtualNodeList);
	    		}
	    	}
	    	procDataResult.put("xml", xml);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return procDataResult;
	}
	
	/**
	 * 组装树形结构
	 * @param nodeName
	 * @param treeDataList
	 * @param documentDataMap
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Object putTreeData(String nodeName, String region, List<Map> treeDataList, Map<String,List<Map>> documentDataMap, Long formmainId, List<String> virtualNodeList) {
		Object valData = null;
        for (Map treeData : treeDataList) {
			String wsTargetParamCategory = (String)treeData.get(IConstant.wsTargetParamCategoryCol) == null ? "" : ((String)treeData.get(IConstant.wsTargetParamCategoryCol)).replaceAll("\\s*", "");//目标参数类别列
			String wsTargetParentNode = (String)treeData.get(IConstant.wsTargetParentNodeCol) == null ? "" : ((String)treeData.get(IConstant.wsTargetParentNodeCol)).replaceAll("\\s*", "");//field0015 目标父节点列
			String wsTargetNode = (String)treeData.get(IConstant.wsTargetNodeCol) == null ? "" : ((String)treeData.get(IConstant.wsTargetNodeCol)).replaceAll("\\s*", "");//field0017 目标节点列
			String wsSourceParam = (String)treeData.get(IConstant.wsSourceParamCol) == null ? "" : ((String)treeData.get(IConstant.wsSourceParamCol)).replaceAll("\\s*", "");//field0020 源参数列
			String wsSourceComp = (String)treeData.get(IConstant.wsSourceCompCol) == null ? "" : ((String)treeData.get(IConstant.wsSourceCompCol)).replaceAll("\\s*", "");//field0022源组件列
        	String wsRegionTable = (String)treeData.get(IConstant.wsRegionTableCol) == null ? "" : ((String)treeData.get(IConstant.wsRegionTableCol)).replaceAll("\\s*", "");//field0028表结构边界
        	String wsEnumTag = (String)treeData.get(IConstant.wsEnumTagCol) == null ? "" : ((String)treeData.get(IConstant.wsEnumTagCol)).replaceAll("\\s*", "");//field0029是否枚举标记列名
        	String wsVirtualNode = (String)treeData.get(IConstant.wsVirtualNodeCol) == null ? "" : ((String)treeData.get(IConstant.wsVirtualNodeCol)).replaceAll("\\s*", "");//field0037虚拟节点列名
        	
        	//收集虚拟节点
        	if(StringUtils.isNotBlank(wsVirtualNode) && "X".equals(wsVirtualNode.toUpperCase())) {
        		if(!virtualNodeList.contains(wsTargetNode)) {
        			virtualNodeList.add(wsTargetNode);
        		}
        	}
        	
        	//这是一个子节点
            if(wsTargetParentNode.equals(nodeName)){
            	//区分同一个表结构，有相同的表结构边界
            	boolean isSameRegion = true;
            	if(StringUtils.isNotBlank(region) && StringUtils.isNotBlank(wsRegionTable) && !wsRegionTable.equals(region)) {
            		isSameRegion = false;
            	}
            	if(!isSameRegion) {
            		continue;
            	}
            	
            	//封装好的实际表数据
            	Map<String, Object> tableDataMap = null;
            	
            	if("输入字段".equals(wsTargetParamCategory)) {
            		if(valData == null) {
            			valData = new HashMap();
            		}
            	}else if("输入表".equals(wsTargetParamCategory)) {
            			valData = new ArrayList();
            			
            			Map<String,Object> dataMap = new HashMap();
        	    		dataMap.put("formmain_id", formmainId);
        	    		dataMap.put(IConstant.wsTargetParentNodeCol, wsTargetNode);//field0015
        	    		if(StringUtils.isNotBlank(wsRegionTable)) {
        	    			dataMap.put(IConstant.wsRegionTableCol, wsRegionTable);//field0028
        	    		}
        	    		try {
            	    		List<Map> sameTableColList = this.getValueByMap(null, dataMap, IConstant.wsFormsonTable, "and");
            	    		tableDataMap = this.putInputTableData(sameTableColList, documentDataMap);
        	    		}catch(Exception ex) {
        	    			ex.printStackTrace();
        	    		}
            	}
            	 //递归获取子节点下的子节点
            	if(valData instanceof java.util.Map) {
            		if(StringUtils.isNotBlank(wsSourceParam) && wsSourceParam.contains("formmain")) {
    	    			List<Map> tableData = documentDataMap.get(wsSourceParam);
    	    			String val = "";
    	    			if(tableData != null && tableData.size() > 0) {
    	    				Map data =  tableData.get(0);
        					val = (String)data.get(wsSourceComp);
        					val = (val == null ? "" : val.replaceAll("\\s*", ""));
        					if(StringUtils.isNotBlank(wsEnumTag) && "X".equals(wsEnumTag.toUpperCase())) {
        						if(StringUtils.isNotBlank(val)) {
        							CtpEnumItem enumItem = enumManagerNew.getEnumItem(Long.valueOf(val));
        							if(enumItem != null) {
        								 val = enumItem.getShowvalue();
        								 val = (val == null ? "" : val);
        							}
        						}
        					}
    	    			}
    	    			((Map)valData).put(wsTargetNode, val);
            		}else {
            			((Map)valData).put(wsTargetNode, putTreeData(wsTargetNode, wsRegionTable, treeDataList, documentDataMap, formmainId, virtualNodeList));
            		}
            	}else if(valData instanceof java.util.List) {
            		boolean buildBlankNode = false;
            		Map<String, Object> temp = null;
            		//当表结构有数据时，组装表的子节点数据
            		if(tableDataMap != null && tableDataMap.size() > 0) {
            			List<Map> tableDataList = (List<Map>)tableDataMap.get(wsTargetNode);
            			if(tableDataList != null && tableDataList.size() > 0) {
            				List<Map> resultData = new ArrayList();
            				for(Map data : tableDataList) {
            					temp = new HashMap();
            					temp.put(wsTargetNode, data);
            					resultData.add(temp);
            				}
            				return resultData;
            			}else {
            				buildBlankNode = true;
            			}
            		}else {
            			buildBlankNode = true;
            		}
            		//当表结构没有数据时，只组装表结构
            		if(buildBlankNode) {
            			temp = new HashMap();
                		temp.put(wsTargetNode, putTreeData(wsTargetNode, wsRegionTable, treeDataList, documentDataMap, formmainId, virtualNodeList));
                		((List<Map>)valData).add(temp);
            		}
            	}
            }
        }
        return valData;
	}
	
	/**
	 * 组装输入表数据
	 * @param configMappingList
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Map<String,Object> putInputTableData(List<Map> configMappingList, Map<String,List<Map>> documentDataMap){
		Map<String, Object> resultMap = new HashMap();
		
		Map<String, Object> sameTableMappingColsMap = new HashMap();//key:输入表名, value:List<String[]> (String[0]:目标组件列名, String[1]:源组件列名)
		Map<String, Object> markMappingMap = new HashMap();
		Map<String, Object> enumTagMappingMap = new HashMap();
		if(configMappingList != null) {
			for(Map valueMap : configMappingList) {
				String wsTargetParentNode = (String)valueMap.get(IConstant.wsTargetParentNodeCol) == null ? "" : ((String)valueMap.get(IConstant.wsTargetParentNodeCol)).replaceAll("\\s*", "");//目标父节点列名
				String wsTargetNode = (String)valueMap.get(IConstant.wsTargetNodeCol) == null ? "" : ((String)valueMap.get(IConstant.wsTargetNodeCol)).replaceAll("\\s*", "");//目标节点列名
				String wsSourceParam = (String)valueMap.get(IConstant.wsSourceParamCol) == null ? "" : ((String)valueMap.get(IConstant.wsSourceParamCol)).replaceAll("\\s*", "");//源参数列名
				String wsSourceComp = (String)valueMap.get(IConstant.wsSourceCompCol) == null ? "" : ((String)valueMap.get(IConstant.wsSourceCompCol)).replaceAll("\\s*", "");//源组件列名
				String wsSpecialTag = (String)valueMap.get(IConstant.wsSpecialTagCol) == null ? "" : ((String)valueMap.get(IConstant.wsSpecialTagCol)).replaceAll("\\s*", "");//特殊标记列名
	        	String wsEnumTag = (String)valueMap.get(IConstant.wsEnumTagCol) == null ? "" : ((String)valueMap.get(IConstant.wsEnumTagCol)).replaceAll("\\s*", "");//field0029是否枚举标记列名

				List<String[]> colsList = (List<String[]>)sameTableMappingColsMap.get(wsSourceParam+"#"+wsTargetParentNode);
				if(colsList == null) {
					colsList = new ArrayList();
				}
				String [] obj = new String[] {wsTargetNode,wsSourceComp};
				colsList.add(obj);
				sameTableMappingColsMap.put(wsSourceParam+"#"+wsTargetParentNode, colsList);
				if(StringUtils.isNotBlank(wsSpecialTag) && "X".equals(wsSpecialTag.toUpperCase())) {
					markMappingMap.put(wsTargetParentNode+"_"+wsTargetNode, "X");
				}
				if(StringUtils.isNotBlank(wsEnumTag) && "X".equals(wsEnumTag.toUpperCase())) {
					enumTagMappingMap.put(wsTargetParentNode+"_"+wsTargetNode, "X");
				}
			}
		}

		if(sameTableMappingColsMap.size() > 0) {
			Iterator<Entry<String, Object>> iter = sameTableMappingColsMap.entrySet().iterator();
			while(iter.hasNext()) {
				Entry<String, Object> entry = iter.next();
				String key = entry.getKey();
				String [] arrs = key.split("#");
				String tableName = arrs[0];
				String wsTargetParentNode = arrs[1];
				List<String[]> valCols = (List<String[]>)entry.getValue();
				List<String> wsTargetNodeList = new ArrayList();//目标组件列集合
				List<String> wsSourceCompColList = new ArrayList();//源组件列集合
				List<Map> tableData = documentDataMap.get(tableName);

				List<Map> tableValsList = new ArrayList();
				if(tableData != null && tableData.size() > 0) {
					for(String[] strs : valCols) {
						wsTargetNodeList.add(strs[0]);
						wsSourceCompColList.add(strs[1]);
					}
    				for(Map data : tableData) {
    					Map<String,Object> tempMap = new HashMap();
    					boolean isValid = false;
	    				for(int i = 0; i < wsSourceCompColList.size(); i++) {
	    					String sourceCol = wsSourceCompColList.get(i);
	    					String wsTargetNode = wsTargetNodeList.get(i);
	    					String val = (String)data.get(sourceCol);
	    					val = (val == null ? "" : val.replaceAll("\\s*", ""));
	    					
	    					if(enumTagMappingMap.containsKey(wsTargetParentNode+"_"+wsTargetNode)) {//是否做枚举值转换
	    						if(StringUtils.isNotBlank(val)) {
        							CtpEnumItem enumItem = enumManagerNew.getEnumItem(Long.valueOf(val));
        							if(enumItem != null) {
        								 val = enumItem.getShowvalue();
        								 val = (val == null ? "" : val);
        							}
        						}
	    					}

	    					if(markMappingMap.containsKey(wsTargetParentNode+"_"+wsTargetNode)) {//过滤掉标记字段值为空的无效记录
	    						if(StringUtils.isBlank(val)) {
	    							isValid = true;
	    							break;
	    						}
	    					}
	    					tempMap.put(wsTargetNode, val);
	    				}
	    				if(!isValid) {
		    				if(tempMap.size() > 0) {
		    					tableValsList.add(tempMap);
		    				}
	    				}
    				}
				}
				resultMap.put(wsTargetParentNode, tableValsList);
			}
		}
		return resultMap;
	}
	
	/**
	 * 获取xml
	 * @param treeMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getXmlContent(Map<String, Object> treeMap) {
		StringBuffer xml = new StringBuffer();
		if(treeMap != null && treeMap.size() > 0) {
			Iterator<Entry<String, Object>> iters = treeMap.entrySet().iterator();
			while(iters.hasNext()) {
				Entry<String, Object> entry = iters.next();
				String key = entry.getKey();
				Object valObj = entry.getValue();
				
				if(valObj instanceof java.util.Map) {
					Map<String, Object> tempMap = (Map<String,Object>)valObj;
					xml.append("<").append(key).append(">").append(this.getXmlContent(tempMap)).append("</").append(key).append(">");
				}else if(valObj instanceof java.util.List) {
					List<Map> tempList = (List<Map>)valObj;
					xml.append("<").append(key).append(">");
					for(Map dataMap : tempList) {
						xml.append(this.getXmlContent(dataMap));
					}
					xml.append("</").append(key).append(">");
				}else {
					if(valObj != null) {
						xml.append("<").append(key).append(">").append(valObj).append("</").append(key).append(">");
					}else {
						xml.append("<").append(key).append(">").append("</").append(key).append(">");
					}
				}
			}
		}
		return xml.toString();
	}
	
	private String cleanVirtualNode(String xml, List<String> virtualNodeList) throws Exception {
		String result = xml;
		if(StringUtils.isNotBlank(xml) && virtualNodeList.size() > 0) {
			StringBuffer buff = new StringBuffer("(");
			int size = virtualNodeList.size();
			for(int i= 0; i < size; i++) {
				String virtualNode = virtualNodeList.get(i);
				buff.append("<").append(virtualNode).append(">").append("|").append("</").append(virtualNode).append(">");
				if(i < size-1) {
					buff.append("|");
				}
			}
			buff.append(")");
			result = xml.replaceAll(buff.toString(), "");
		}
		return result;
	}
	
	
	public List<Map> getValueByMap(String[] returnFields, Map<String, Object> hm, String tableName, String fieldsLogic)
			throws Exception {
		return formDataDAO.getValueByMap(returnFields, hm, tableName, fieldsLogic);
	}

	@Override
	public Map<String, Object> getWriteBackConfig(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
