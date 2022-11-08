package com.bjev.apps.service;

import java.util.Map;

public interface DataHandleService {
	public Map<String, Object> procMappingData(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> getWriteBackConfig(Map<String, Object> map) throws Exception;

}
