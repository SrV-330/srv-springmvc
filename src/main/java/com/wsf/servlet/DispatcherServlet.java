package com.wsf.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wsf.annotation.MyAutowired;
import com.wsf.annotation.MyController;
import com.wsf.annotation.MyRequestMapping;
import com.wsf.annotation.MyRequestParam;
import com.wsf.annotation.MyServcice;
import com.wsf.controller.SrVController;

public class DispatcherServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> classNames=new ArrayList<String>();
	private Map<String,Object> beans=new HashMap<String, Object>();
	private Map<String,Method> mapping=new HashMap<String, Method>();
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
		doIoc();
		buildMapping();
		for(Map.Entry<String, Method> entry:mapping.entrySet()){
			System.out.println("Key:"+entry.getKey()+",Value:"+entry.getValue());
		}
	}
	private void buildMapping() {
		
		if(beans.size()<=0) return;
		for(Map.Entry<String, Object> entry:beans.entrySet()){
			Object obj=entry.getValue();
			Class<?> clazz=obj.getClass();
			if(clazz.isAnnotationPresent(MyController.class)){
				MyRequestMapping rm=clazz.getAnnotation(MyRequestMapping.class);
				String classPath=rm.value();
				Method[] methods=clazz.getMethods();
				for(Method method:methods){
					if(method.isAnnotationPresent(MyRequestMapping.class)){
						MyRequestMapping rm1=method.getAnnotation(MyRequestMapping.class);
						String methodPath=rm1.value();
						
						mapping.put(classPath+methodPath,method);
					}
				}
			}
		}
		
		
		
	}
	private void doIoc() {
		if(beans.size()<=0) return;
		
		for(Map.Entry<String, Object> entry:beans.entrySet()){
			Object obj=entry.getValue();
			Class<?> clazz=obj.getClass();
			if(clazz.isAnnotationPresent(MyController.class)){
				Field[] fields=clazz.getDeclaredFields();
				for(Field field:fields){
					if(field.isAnnotationPresent(MyAutowired.class)){
						
						MyAutowired ma=field.getAnnotation(MyAutowired.class);
						String value=ma.value();
						field.setAccessible(true);
						try {
							field.set(obj, beans.get(value));
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
					}
				}
			}
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
		
		URL url=getClass().getResource("/"+basePackage.replaceAll("\\.", "/"));
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
		
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String uri=req.getRequestURI();
		System.out.println("URI:"+uri);
		String context=req.getContextPath();
		System.out.println("Context:"+context);
		String path=uri.replace(context, "");
		System.out.println("Path:"+path);
		Method method=(Method)mapping.get(path);
		System.out.println("Method:"+method);
		SrVController controller=(SrVController)beans.get("/"+path.split("/")[1]);
		Object[] args=getMethodParams(req, resp, method);
		for(Object o:args){
			System.out.println(o);
		}
		try {
			method.invoke(controller, args);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private Object[] getMethodParams(HttpServletRequest req, HttpServletResponse resp,Method method){
		Class<?>[] paramTypes= method.getParameterTypes();
		Object[] args=new Object[paramTypes.length];
		int args_i=0;
		int index=0;
		for(Class<?> paramType:paramTypes){
			if(ServletRequest.class.isAssignableFrom(paramType)){
				args[args_i++]=req;
			}
			if(ServletResponse.class.isAssignableFrom(paramType)){
				args[args_i++]=resp;
			}
			Annotation[] paramAnns =method.getParameterAnnotations()[index];
			if(paramAnns.length>0){
				for(Annotation paramAnn:paramAnns){
					if(MyRequestParam.class.isAssignableFrom(paramAnn.getClass())){
						MyRequestParam param=(MyRequestParam)paramAnn;
						args[args_i++]=req.getParameter(param.value());
					}
				}
				
			}
			index++;
			
			
		}
		
		return args;
	}
	

}
