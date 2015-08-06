package com.hdsx.ao.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hdsx.ao.util.FileUtil;
import com.hdsx.ao.util.ReflectUtils;

public class ZipRequest extends AbstractRequest implements Request {

	private Logger log=LoggerFactory.getLogger(ZipRequest.class);
	private Request shpRequest;
	
	private Request xlsRequest;
	
	public ZipRequest(String layer) {
		super(layer);
		this.shpRequest=new ShpRequest(layer);
		this.xlsRequest=new XlsRequest(layer);

	}

	public <T> List<T> getList(String path) {
		// TODO Auto-generated method stub
		FileUtil file=new FileUtil();
		file.unZipFile(path);
		List<T> xlss=null;
		List<T> shps=null;
		for(String xls :file.xlsPaths)
		{
			xlss=xlsRequest.getList(xls);
		}
		for(String shp:file.shpPaths)
		{
			shps=shpRequest.getList(shp);
		}
		log.debug("\n"+"读取Excel："+xlss.size()+",shp:"+shps.size());
		return checkList(xlss,shps);
	}

	public <T> List<T> checkList(List<T> xlss,List<T> shps){
		List<T> list=new ArrayList<T>();
		for(T xls :xlss)
		{
			for(T shp:shps)
			{	
				if(isEquals(xls,shp))
				{
					ReflectUtils.setValue(getGeometry(),ReflectUtils.getValue(getGeometry(), shp) , xls);
					shps.remove(shp);
					list.add(xls);break;
				}
			}
		}
		return list;
	}
	
	public <T> boolean isEquals(T xls,T shp){
		
		Map<String,String> relationShip=getMapping();
		Set<String> keys=relationShip.keySet();
		for(String key:keys)
		{
			Object shpValue=ReflectUtils.getValue(relationShip.get(key), shp);
			Object xlsValue=ReflectUtils.getValue(relationShip.get(key), xls);
			if(shpValue==null||xlsValue==null||!shpValue.equals(xlsValue))
			return false;
			log.debug("\n"+"Shape("+key+","+shpValue+"),Excel("+relationShip.get(key)+","+xlsValue+")");
		}
		return true;
	}
	public boolean getType(String path) {
		// TODO Auto-generated method stub
		return path.endsWith(TYPE.ZIP);
	}

}
