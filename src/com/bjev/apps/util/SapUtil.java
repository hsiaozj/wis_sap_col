package com.bjev.apps.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.bjev.apps.po.SapSource;
import com.bjev.apps.sap.SAPConn;

public class SapUtil {
	
	private static SapUtil instance = new SapUtil();
	private JCoDestination destination;

	private SapUtil() {}
	
	public static SapUtil getInstance() {
		return instance;
	}
	
	
	public JSONObject doSapMethod(SapSource sapSource,JSONObject jsonInput) throws JCoException {
		JCoFunction function = null;
		destination = getSapSource(sapSource);
		function = destination.getRepository().getFunction(jsonInput.getString("function"));
		JCoParameterList input = function.getTableParameterList();
		JSONArray tableParams = jsonInput.getJSONArray("tableParams");
		boolean isReturnResult = true;
		boolean getTableResult = true;
		String isReturn =  jsonInput.getString("isReturn");
		if(StringUtils.isNotBlank(isReturn)) {
			isReturnResult = Boolean.parseBoolean(isReturn);
		}
		
		String getTableRst =  jsonInput.getString("getTableResult");
		if(StringUtils.isNotBlank(getTableRst)) {
			getTableResult = Boolean.parseBoolean(getTableRst);
		}

		fillTableParam(input, tableParams);
		
		JSONArray structParams = jsonInput.getJSONArray("structParams");
		if(structParams!=null) {
			input = function.getImportParameterList();
			Iterator<Object> structParamsIterator = structParams.iterator();
			while (structParamsIterator.hasNext()) {// 遍历表参数
				JSONObject structParam = (JSONObject) structParamsIterator.next();
				//System.out.println(structParam.getString("structName"));
				JCoStructure sapStruct = input.getStructure(structParam.getString("structName"));
				JSONObject jsonStruct = (JSONObject) structParam.get("jsonStruct");
				Iterator<Entry<String, Object>> structRowIterator = jsonStruct.entrySet().iterator();
				while (structRowIterator.hasNext()) {// 遍历属性
					Entry<String, Object> structEntry = structRowIterator.next();
					sapStruct.setValue(structEntry.getKey(), structEntry.getValue());
				}
			}
		}
		
		JSONArray varParams = jsonInput.getJSONArray("varParams");
		if(varParams != null) {
			input = function.getImportParameterList();
			Iterator<Object> varParamsIterator = varParams.iterator();
			while (varParamsIterator.hasNext()) {// 遍历表参数
				JSONObject varParam = (JSONObject) varParamsIterator.next();
				Iterator<Entry<String, Object>>  varRowIterator = varParam.entrySet().iterator();
				while (varRowIterator.hasNext()) {// 遍历属性
					Entry<String, Object> varEntry = varRowIterator.next();
					input.setValue(varEntry.getKey(), varEntry.getValue());
				}
			}
		}
		function.execute(destination);
		
		JSONObject rstobj = null;
		if(isReturnResult) {
			if(function.getExportParameterList()!=null) {
				rstobj= toJsonObj2(function.getExportParameterList().iterator());
			}
			if(getTableResult) {
				if(rstobj != null && function.getTableParameterList() != null) {
					JSONObject rstobj2 = toJsonObj2(function.getTableParameterList().iterator());
					if(rstobj2 != null) {
						Iterator<Entry<String, Object>> iterator = rstobj2.entrySet().iterator();
						while(iterator.hasNext()) {
							Entry<String, Object> entry = iterator.next();
							String key = entry.getKey();
							Object val = entry.getValue();
							if(key.equals("ET_RETURN") && rstobj.containsKey("ET_RETURN")) {
								rstobj.put("ET_RETURN2", val);
							}else {
								rstobj.put(key, val);
							}
						}
					}
				}else if(rstobj == null && function.getTableParameterList() != null) {
					rstobj= toJsonObj2(function.getTableParameterList().iterator());
				}
			}
		}
		return rstobj;
	}


	public JSONObject doSapMethod(JCoDestination dest, JSONObject jsonInput) throws JCoException {
		JCoFunction function = null;
		function = dest.getRepository().getFunction(jsonInput.getString("function"));
		JCoParameterList input = function.getTableParameterList();
		JSONArray tableParams = jsonInput.getJSONArray("tableParams");
		boolean isReturnResult = true;
		boolean getTableResult = true;
		String isReturn =  jsonInput.getString("isReturn");
		if(StringUtils.isNotBlank(isReturn)) {
			isReturnResult = Boolean.parseBoolean(isReturn);
		}
		
		String getTableRst =  jsonInput.getString("getTableResult");
		if(StringUtils.isNotBlank(getTableRst)) {
			getTableResult = Boolean.parseBoolean(getTableRst);
		}

		fillTableParam(input, tableParams);
		
		JSONArray structParams = jsonInput.getJSONArray("structParams");
		if(structParams!=null) {
			input = function.getImportParameterList();
			Iterator<Object> structParamsIterator = structParams.iterator();
			while (structParamsIterator.hasNext()) {// 遍历表参数
				JSONObject structParam = (JSONObject) structParamsIterator.next();
				//System.out.println(structParam.getString("structName"));
				JCoStructure sapStruct = input.getStructure(structParam.getString("structName"));
				JSONObject jsonStruct = (JSONObject) structParam.get("jsonStruct");
				Iterator<Entry<String, Object>> structRowIterator = jsonStruct.entrySet().iterator();
				while (structRowIterator.hasNext()) {// 遍历属性
					Entry<String, Object> structEntry = structRowIterator.next();
					sapStruct.setValue(structEntry.getKey(), structEntry.getValue());
				}
			}
		}
		
		JSONArray varParams = jsonInput.getJSONArray("varParams");
		if(varParams != null) {
			input = function.getImportParameterList();
			Iterator<Object> varParamsIterator = varParams.iterator();
			while (varParamsIterator.hasNext()) {// 遍历表参数
				JSONObject varParam = (JSONObject) varParamsIterator.next();
				Iterator<Entry<String, Object>>  varRowIterator = varParam.entrySet().iterator();
				while (varRowIterator.hasNext()) {// 遍历属性
					Entry<String, Object> varEntry = varRowIterator.next();
					input.setValue(varEntry.getKey(), varEntry.getValue());
				}
			}
		}
		function.execute(dest);
		
		JSONObject rstobj = null;
		if(isReturnResult) {
			if(function.getExportParameterList()!=null) {
				rstobj= toJsonObj2(function.getExportParameterList().iterator());
			}
			
			if(getTableResult) {
				if(rstobj != null && function.getTableParameterList() != null) {
					JSONObject rstobj2 = toJsonObj2(function.getTableParameterList().iterator());
					if(rstobj2 != null) {
						Iterator<Entry<String, Object>> iterator = rstobj2.entrySet().iterator();
						while(iterator.hasNext()) {
							Entry<String, Object> entry = iterator.next();
							String key = entry.getKey();
							Object val = entry.getValue();
							if(key.equals("ET_RETURN") && rstobj.containsKey("ET_RETURN")) {
								rstobj.put("ET_RETURN2", val);
							}else {
								rstobj.put(key, val);
							}
						}
					}
				}else if(rstobj == null && function.getTableParameterList() != null) {
					rstobj= toJsonObj2(function.getTableParameterList().iterator());
				}
			}
		}
		return rstobj;
	}
	
	public JSONObject toJsonObj2(Iterator<JCoField> exportIterator) {
		JSONObject rst = new JSONObject();
		while (exportIterator.hasNext()) {
			JCoField exportParam = exportIterator.next();
			if (exportParam.isTable()) {
				JCoTable tb = exportParam.getTable();
				JSONArray tableRst = new JSONArray();
				
				Iterator<JCoField> tbIterator = tb.iterator();
				tableRst.add(toJsonObj2(tbIterator));
				
				while(tb.nextRow()){
					Iterator<JCoField> tbIterator1 = tb.iterator();
					tableRst.add(toJsonObj2(tbIterator1));
				}
				rst.put(exportParam.getName(), tableRst);
			} else if (exportParam.isStructure()) {
				rst.put(exportParam.getName(), toJsonObj2(exportParam.getStructure().iterator()));
			} else {
				if (!exportParam.isInitialized()) {
					continue;
				}
				rst.put(exportParam.getName(), exportParam.getValue());
			}
		}
		return rst;
	}

	private void fillTableParam(JCoParameterList input, JSONArray tableParams) {
		if (tableParams != null) {
			Iterator<Object> tableParamsIterator = tableParams.iterator();
			while (tableParamsIterator.hasNext()) {// 遍历表参数
				JSONObject tableParam = (JSONObject) tableParamsIterator.next();
				JCoTable sapTable = input.getTable(tableParam.getString("tableName"));
				JSONArray tableRows = tableParam.getJSONArray("tableRows");
				Iterator<Object> tableRowsIterator = tableRows.iterator();
				while (tableRowsIterator.hasNext()) {// 遍历数据行
					sapTable.appendRow();
					JSONObject tableRow = (JSONObject) tableRowsIterator.next();
					Iterator<Entry<String, Object>> tableRowIterator = tableRow.entrySet().iterator();
					while (tableRowIterator.hasNext()) {// 遍历属性
						Entry<String, Object> tableRowEntry = tableRowIterator.next();
						sapTable.setValue(tableRowEntry.getKey(), tableRowEntry.getValue());
					}
				}
			}
		}
	}

	public JCoDestination getSapSource(SapSource sapSource) {
//		SapSource sapSource = new SapSource();
//		sapSource.setJcoAshost(jcoAshost);//"192.168.0.127"
//		sapSource.setJcoClient(jcoClient);//300,400
//		sapSource.setJcoLang(jcoLang);//"ZH"
//		sapSource.setJcoUser(jcoUser);//"oarfc"
//		sapSource.setJcoPasswd(jcoPasswd);//"123456"
//		sapSource.setJcoPeakLimit(jcoPeakLimit);//10
//		sapSource.setJcoSysNr(jcoSysNr);//"00"
//		sapSource.setJcoPoolCapacity(jcoPoolCapacity);//3
		if(destination == null) {
			destination = SAPConn.connect2(sapSource);
		}
		return destination;
	}
	
	/**
	 * 转换请求SAP数据
	 * @param functionName
	 * @param paramMap
	 * @return
	 */
	public JSONObject transformSapData(String functionName, Map<String, Object> paramMap) {
		JSONObject jsonSapObj = new JSONObject();
		//输入字段
		JSONArray varParams = new JSONArray();
		//输入结构体
		JSONArray structParams = new JSONArray();
		//输入表
		JSONArray tableParams = new JSONArray();
		try {
			if(paramMap != null && paramMap.size() > 0) {
				Iterator<Entry<String, Object>> iter = paramMap.entrySet().iterator();
				while(iter.hasNext()) {
					Entry<String, Object> entry = iter.next();
					String key = entry.getKey();
					Object val = entry.getValue();
					if(key.equals("sapSource")) {
						continue;
					}
					if(val instanceof java.util.Map) {
						Map<String,Object> strucParamMap = (Map<String,Object>)val;
						if(strucParamMap != null && strucParamMap.size() > 0) {
							JSONObject strucParam = new JSONObject();
							JSONObject jsonStruct = new JSONObject();
							Iterator<Entry<String, Object>> strucIter = strucParamMap.entrySet().iterator();
							while(strucIter.hasNext()) {
								Entry<String, Object> strucEntry = strucIter.next();
								String structKey = strucEntry.getKey();
								Object structVal = strucEntry.getValue();
								jsonStruct.put(structKey, structVal == null ? "" : structVal);
							}
							strucParam.put("jsonStruct", jsonStruct);
							strucParam.put("structName", key);
							structParams.add(strucParam);
						}
					}else if(val instanceof java.util.List) {
						List<Map> tableParamList = (List<Map>)val;
						if(tableParamList != null && tableParamList.size() > 0) {
							JSONObject tableParam = new JSONObject();
							JSONArray tableRows = new JSONArray();
							tableParam.put("tableName", key);
							List<JSONObject> tableList = procTableData(tableParamList);
							tableRows.addAll(tableList);
							tableParam.put("tableRows", tableRows);
							tableParams.add(tableParam);
						}
					}else {
						JSONObject varParam = new JSONObject();
						varParam.put(key, val == null ? "" : val);
						varParams.add(varParam);
					}
				}
			}
			//填充输入字段
			if(varParams.size() > 0) {
				jsonSapObj.put("varParams", varParams);
			}
			//填充输入结构体
			if(structParams.size() > 0) {
				jsonSapObj.put("structParams", structParams);
			}
		
			//填充输入表
			if(tableParams.size() > 0) {
				jsonSapObj.put("tableParams", tableParams);
			}
			jsonSapObj.put("function", functionName);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return jsonSapObj;
	}
	
	/**
	 * 组装表数据
	 * @param tableList
	 * @return
	 */
	private List<JSONObject> procTableData(List<Map> tableList){
		List<JSONObject> results = new ArrayList();
		if(tableList != null) {
			 for(Map dataMap : tableList) {
				if(dataMap != null && dataMap.size() > 0) {
					JSONObject tableRow = new JSONObject();
					Iterator<Entry<String, Object>> its = dataMap.entrySet().iterator();
					while(its.hasNext()) {
						Entry<String, Object> entry  = its.next();
						String key = entry.getKey();
						Object val = entry.getValue();
						tableRow.put(key, val == null ? "" : val);
					}
					results.add(tableRow);
				}
			 }
		}
		return results;
	}
}
