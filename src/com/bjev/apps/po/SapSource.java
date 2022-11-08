package com.bjev.apps.po;


public class SapSource{

	private String jcoAshost; // 服务器
	private String jcoSysNr; // 系统编号
	private String jcoClient; // SAP集团
	private String jcoUser; // SAP用户名
	private String jcoPasswd; // 密码
	private String jcoLang; // 登录语言
	private Integer jcoPoolCapacity; // 最大连接数
	private Integer jcoPeakLimit; // 最大连接线程

	public String getJcoAshost() {
		return jcoAshost;
	}

	public void setJcoAshost(String jcoAshost) {
		this.jcoAshost = jcoAshost;
	}

	public String getJcoSysNr() {
		return jcoSysNr;
	}

	public void setJcoSysNr(String jcoSysNr) {
		this.jcoSysNr = jcoSysNr;
	}

	public String getJcoClient() {
		return jcoClient;
	}

	public void setJcoClient(String jcoClient) {
		this.jcoClient = jcoClient;
	}

	public String getJcoUser() {
		return jcoUser;
	}

	public void setJcoUser(String jcoUser) {
		this.jcoUser = jcoUser;
	}

	public String getJcoPasswd() {
		return jcoPasswd;
	}

	public void setJcoPasswd(String jcoPasswd) {
		this.jcoPasswd = jcoPasswd;
	}

	public String getJcoLang() {
		return jcoLang;
	}

	public void setJcoLang(String jcoLang) {
		this.jcoLang = jcoLang;
	}

	public Integer getJcoPoolCapacity() {
		return jcoPoolCapacity;
	}

	public void setJcoPoolCapacity(Integer jcoPoolCapacity) {
		this.jcoPoolCapacity = jcoPoolCapacity;
	}

	public Integer getJcoPeakLimit() {
		return jcoPeakLimit;
	}

	public void setJcoPeakLimit(Integer jcoPeakLimit) {
		this.jcoPeakLimit = jcoPeakLimit;
	}

}
