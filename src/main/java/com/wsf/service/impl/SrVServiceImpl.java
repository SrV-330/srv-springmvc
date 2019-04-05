package com.wsf.service.impl;
import com.wsf.annotation.MyServcice;
import com.wsf.service.*;


@MyServcice("SrVServiceImpl")
public class SrVServiceImpl implements SrVService {

	public String query(String name, String age) {
		
		return "{ name = "+name+", age = "+age+" }";
	}

}
