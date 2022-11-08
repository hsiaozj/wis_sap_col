package com.bjev.apps.util;

import org.apache.commons.lang3.StringUtils;

public class CommonUtils {
	
	public static String fillPreZeros(String str, int length) {
		if(StringUtils.isBlank(str)) {
			return str;
		}
		for(int i = 0; i < length; i++) {
			str = "0" + str;
		}
		return str;
	}
	
	public static String cleanPreZeros(String str) {
		if(StringUtils.isBlank(str)) {
			return str;
		}
		return Integer.valueOf(str)+"";
	}
}
