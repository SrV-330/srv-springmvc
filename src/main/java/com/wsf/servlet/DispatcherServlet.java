package com.wsf.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wsf.annotation.MyController;
import com.wsf.annotation.MyRequestMapping;
import com.wsf.annotation.MyServcice;

public class DispatcherServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> classNames=new ArrayList<String>();
	private Map<String,Object> beans=new HashMap<String, Object>();
	public DispatcherServlet() {
		
	}
	public void init(ServletConfig conf){
		doScanPackage("com.wsf");
		for(String className:classNames){
			System.out.println("ClassName:"+className);
		}
		doInstance();
		for(Map.Entry<String, Object> entry:beans.entrySet()){
			System.out.println("Key:"+entry.getKey()+",Value:"+entry.getValue());
		}
	}
	private void doInstance() {
		if(classNames.size()<=0) return;
		for(String className:classNames){
			String cn=className.replace(".class", "");
			try {
				Class<?> clazz=Class.forName(cn);
				if(clazz.isAnnotationPresent(MyController.class)){
					Object obj=clazz.newInstance();
					MyRequestMapping rm=clazz.getAnnotation(MyRequestMapping.class);
					String key=rm.value();
					beans.put(key, obj);
				}else if(clazz.isAnnotationPresent(MyServcice.class)){
					Object obj=clazz.newInstance();
					MyServcice service=clazz.getAnnotation(MyServcice.class);
					String key=service.value();
					beans.put(key, obj);
				}
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	private void doScanPackage(String basePackage) {
		
		URL url=getClass().getResource("./"+basePackage.replaceAll("\\.", "/"));
		String filePath=url.getFile();
		File file=new File(filePath);
		String[] filePaths=file.list();
		for(String path:filePaths){
			File f=new File(filePath+path);
			if(f.isDirectory()){
				doScanPackage(basePackage+"."+path);
			}else{
				classNames.add(basePackage+"."+f.getName());
			}
		}
		
		
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		super.doGet(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		super.doPost(req, resp);
	}
	

}
