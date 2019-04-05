package com.wsf.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wsf.annotation.MyAutowired;
import com.wsf.annotation.MyController;
import com.wsf.annotation.MyRequestMapping;
import com.wsf.annotation.MyRequestParam;
import com.wsf.service.SrVService;

@MyRequestMapping("/srv")
@MyController
public class SrVController {
	
	@MyAutowired("MyServiceImpl")
	private SrVService service;
	@MyRequestMapping("/qeury")
	public void query(HttpServletRequest req,HttpServletResponse resp,
			@MyRequestParam("name") String name,@MyRequestParam("age") String age){
		
		PrintWriter pw;
		try {
			pw = resp.getWriter();
			pw.println(service.query(name, age));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
