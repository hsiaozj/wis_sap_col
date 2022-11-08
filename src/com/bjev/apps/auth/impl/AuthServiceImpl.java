package com.bjev.apps.auth.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.form.modules.engin.base.formData.FormDataDAO;
import com.bjev.apps.auth.AuthService;
import com.bjev.apps.util.AuthUtil;

public class AuthServiceImpl implements AuthService {
	private static Log log = LogFactory.getLog(AuthServiceImpl.class);
	private FormDataDAO formDataDAO = (FormDataDAO) AppContext.getBean("formDataDAO");
	
	@Override
	public Map<String, Object> validAuthInfo(String verion) throws Exception {
		Map<String, Object> resultMap = new HashMap();
		boolean authFlag = false;
		String msg = "该业务未授权，请授权后使用!";

		try {
			Map<String, Object> paramMap = new HashMap();
			paramMap.put("VERSION", verion);
			String authorizedCode = "";
			List<Map> list = this.getValueByMap(null, paramMap, "WIS_AUTHORIZED_CONFIG", "and");
	    	if(list != null && list.size() > 0) {
	    		Map<String,Object> valMap = list.get(0);
	        	if(valMap != null && valMap.size() > 0) {
	        		authorizedCode = (String)valMap.get("authorized_code");
	        	}
	    	}
	    	if(StringUtils.isBlank(authorizedCode)) {
	    		resultMap.put("authFlag", authFlag);
	    		resultMap.put("msg", msg);
	    		return resultMap;
	    	}
	    	
	    	log.info("verionNo:" + verion + "---authorizedCode:" + authorizedCode);

			authFlag = AuthUtil.getInstance().verifyAuthorization(verion, authorizedCode);
			if (!authFlag) {
				msg = "该业务授权失效，请重新授权后使用!";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		resultMap.put("authFlag", authFlag);
		resultMap.put("msg", msg);
		return resultMap;
	}
	
	public List<Map> getValueByMap(String[] returnFields, Map<String, Object> hm, String tableName, String fieldsLogic)
			throws Exception {
		return formDataDAO.getValueByMap(returnFields, hm, tableName, fieldsLogic);
	}

}
