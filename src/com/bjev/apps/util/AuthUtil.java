package com.bjev.apps.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import com.bjev.apps.security.MessageEncoder;

public class AuthUtil {
	private static final AuthUtil instance = new AuthUtil();
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	private AuthUtil() {}
	
	public static AuthUtil getInstance() {
		return instance;
	}
	
	public boolean verifyAuthorization(String version, String authorizedCode) {
		if(StringUtils.isBlank(version) || StringUtils.isBlank(authorizedCode)) {
			return false;
		}
		boolean authFlag = false;
		String [] arrs = version.split("_");
		String prefixStr = arrs[0];
		if("WISTY".equals(prefixStr)) {
			String seedKey = this.getSeedKey(arrs);
			MessageEncoder mencoder = MessageEncoder.getInstance();
			String seedEncode = mencoder.shaEncode(seedKey);
			String aesDes = mencoder.aesDecode(authorizedCode.toLowerCase());
			if(seedEncode.equals(aesDes)) {
				authFlag = true;
			}else {
				authFlag = false;
			}
		}else{
			String seedKey = this.getSeedKey(arrs);
			MessageEncoder mencoder = MessageEncoder.getInstance();
			String seedEncode = mencoder.shaEncode(seedKey);
			String dataStr = dateFormat.format(new Date(System.currentTimeMillis()));
			String aesDes = mencoder.aesDecode(authorizedCode.toLowerCase());
			if(aesDes == null) {
				aesDes = "";
			}
			String authDate = aesDes.replace(seedEncode, "");
			if(dataStr.compareTo(authDate) <= 0) {
				authFlag = true;
			}else {
				authFlag = false;
			}
		}
		return authFlag;
	}
	
	private String getSeedKey(String [] arrs) {
		StringBuffer buf = new StringBuffer();
		int lastIndex = arrs.length-1;
		for(int i = 0; i < lastIndex; i++) {
			buf.append(arrs[i]);
			if(i != lastIndex-1) {
				buf.append("_");
			}
		}
		return buf.toString();
	}
	
	public static void main(String args[]) {
		AuthUtil autil = AuthUtil.getInstance();
		String version = "WIS_ACC_DOCUMENT_CREATE_190816001";
		String authorizedCode = "1111";
		System.out.println(authorizedCode.toUpperCase());
		//"WIS_PO_CREATE_190624"
		boolean authFlag = autil.verifyAuthorization(version, authorizedCode);
	}
}
