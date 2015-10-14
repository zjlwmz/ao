package com.hdsx.ao.workspace;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 空间数据源连接注册中心
 *   
 * @author jingzh
 * 
 * @createDate 2015-10-14
 * 
 * @email jingzh@hdsxtech.com
 * 
 * @version 1.0
 */
public class WorkspacesRegister implements IRegister {

	private Map<String, EngineCPWorkspace> multiWorkspaces = new ConcurrentHashMap<String, EngineCPWorkspace>();

	public Map<String, EngineCPWorkspace> getMultiWorkspaces() {
		return multiWorkspaces;
	}

	public void setMultiWorkspaces(Map<String, EngineCPWorkspace> multiWorkspaces) {
		this.multiWorkspaces = multiWorkspaces;
	}

	@Override
	public void regist(String sourceName, EngineCPWorkspace workspace) {
		// TODO Auto-generated method stub
		multiWorkspaces.put(sourceName, workspace);
	}

	@Override
	public EngineCPWorkspace get(String sourceName) {
		// TODO Auto-generated method stub
		return multiWorkspaces.get(sourceName);
	}

	
}
