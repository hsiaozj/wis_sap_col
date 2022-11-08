package com.bjev.apps.auth;

import java.util.Map;

public interface AuthService {
	public Map<String, Object> validAuthInfo(String version) throws Exception;
}
