package com.hdsx.ao.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureCursor;
import com.esri.arcgis.geodatabase.IFeatureWorkspace;
import com.esri.arcgis.geometry.IGeometry;
import com.hdsx.ao.config.AEWorkspace;
import com.hdsx.ao.util.ReflectUtils;

public class ShpRequest extends AbstractRequest implements Request {

	private Logger log=LoggerFactory.getLogger(XlsRequest.class);
	public ShpRequest(String layer) {
		super(layer);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getList(String path) {
		List<T> list=new ArrayList<T>();
		String directory=path.substring(0, path.lastIndexOf("/"));
		String name=path.substring(path.lastIndexOf("/")+1, path.length());
		IFeatureWorkspace workspace=(IFeatureWorkspace) AEWorkspace.getShapeWorkSpace(directory);
		log.debug("\n"+"------------开始解析Shp------------"+name.trim());
		try 
		{
			IFeatureClass featureClass=workspace.openFeatureClass(name.trim());
			IFeatureCursor cursor=featureClass.search(null, true);
			int total=featureClass.featureCount(null);
			log.debug("\n"+"总记录数:"+total);
			Map<String,String> relationShip=getMapping();
			int count=0;
			do{
				log.debug(
						"\n"+
						"总记录数:"+total+"\n"+
						"以读取条数:"+count+"\n"+
						"最大可用内存，对应-Xmx："+Runtime.getRuntime().maxMemory()+"\n"+
						"当前JVM空闲内存:"+Runtime.getRuntime().freeMemory()+"\n"+
						"当前JVM占用的内存总数:"+Runtime.getRuntime().totalMemory()+"\n");
				IFeature feature=cursor.nextFeature();
				if(feature==null)continue;
				T bean=(T) getTable().getBean();
				for(String field:getTable().getPKFieldsShape())
				{	
					Object value=feature.getValue(cursor.findField(field)) ;
					String attribute=relationShip.get(field);
					ReflectUtils.setValue(attribute, value, bean);
				}
				IGeometry shape= feature.getShapeCopy();
				ReflectUtils.setValue(getGeometry(),shape, bean);
				list.add(bean);
				bean=null;
				feature=null;
				shape=null;
				count++;
			}while(count<total);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("------------结束解析Shp------------");
		return list;
	}

	

	public boolean getType(String path) {
		// TODO Auto-generated method stub
		return path.endsWith(TYPE.SHP);
	}

}
