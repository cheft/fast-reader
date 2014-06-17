package com.topway.reader.client.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Encoding;
import org.nutz.lang.Streams;
import org.nutz.mvc.View;

import com.topway.reader.client.entity.FileContent;

public class DocumentView implements View{
	
    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) {
    	if (obj instanceof FileContent) {
    		try{
	    		FileContent fileContent = (FileContent)obj;
	    		String name =  fileContent.getName();
	    		FileInputStream fis = null;
	    		File file = null;
	            if("swf".equals(fileContent.getSuffix())) {
	            	name = name.substring(0, name.lastIndexOf(".")) + ".swf";
	            	file = new File(fileContent.getFlashFilePath());
	            	resp.setContentType("application/x-shockwave-flash");
	            }else {
	            	file = new File(fileContent.getSrcFilePath());
	            	resp.setContentType("application/msword");
	            }
	    		String filename = URLEncoder.encode(name, Encoding.UTF8);
	            resp.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
	            resp.setHeader("Content-Length", "" + file.length());
            	fis = new FileInputStream(file);
	            Streams.writeAndClose(resp.getOutputStream(), fis);	
    		}catch(UnsupportedEncodingException uee) {
    			uee.printStackTrace();
    		}catch(IOException ioe) {
    			ioe.printStackTrace();
    		}
        }
    }
}
