package com.topway.reader.client.view;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

public class DocumentViewMaker implements ViewMaker{
	
	public View make(Ioc ioc, String type, String value){
		if("document".equalsIgnoreCase(type)){
			return new DocumentView();
		}
		return null;
	}

}