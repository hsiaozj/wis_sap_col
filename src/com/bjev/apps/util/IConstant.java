package com.bjev.apps.util;

import com.bjev.apps.util.SystemConfigHelper;

public interface IConstant {

	public static String wsFormmainTable = SystemConfigHelper.getString("ws.formmain.table");// ws配置映射表主表名
	public static String wsFormsonTable = SystemConfigHelper.getString("ws.formson.table");// ws配置映射表子表名

	public static String wsUrl = SystemConfigHelper.getString("ws.url");// webservice地址
	public static String wsFunction = SystemConfigHelper.getString("ws.funciton");// 方法名
	public static String wsNameSpace = SystemConfigHelper.getString("ws.nameSpace");// 命名空间列名
	public static String wsUserName = SystemConfigHelper.getString("ws.userName");// 用户名列名
	public static String wsPassWord = SystemConfigHelper.getString("ws.passWord");// 密码列名

	public static String wsConfigCategroyCol = SystemConfigHelper.getString("ws.config.category.col");// ws配置分类列名
	public static String wsTargetParamCategoryCol = SystemConfigHelper.getString("ws.target.param.category.col");// 目标参数类别列名
	public static String wsTargetParentNodeCol = SystemConfigHelper.getString("ws.target.parent.node.col");// 目标父节点列名
	public static String wsTargetNodeCol = SystemConfigHelper.getString("ws.target.node.col");// 目标节点列名
	public static String wsSourceParamCol = SystemConfigHelper.getString("ws.source.param.col");// 源参数列名
	public static String wsSourceCompCol = SystemConfigHelper.getString("ws.source.comp.col");// 源组件列名
	public static String wsSpecialTagCol = SystemConfigHelper.getString("ws.special.tag.col");// 特殊标记列名
	public static String wsRegionTableCol = SystemConfigHelper.getString("ws.region.table.col");// 表结构边界列名
	public static String wsEnumTagCol = SystemConfigHelper.getString("ws.enum.tag.col");// 是否枚举标记列名
	public static String wsJsonJonin = SystemConfigHelper.getString("ws.join.col");// 是否枚举标记列名
	public static String wsVirtualNodeCol = SystemConfigHelper.getString("ws.virtual.node.col");//虚拟节点列名
}
