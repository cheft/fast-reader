package com.topway.reader.server.util;

import javax.servlet.ServletContext;

public class MyUtil {
	
	private ServletContext sc;

    public String getPath(String path) {
        return sc.getRealPath(path);
    }
}
