package com.topway.reader.server.convert;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.sender.FilePostSender;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.lang.Files;

import com.topway.reader.server.convert.jacob.Converter;

public class ConvertThread extends Thread {

	private String serverDir = "";
	
	private String clientUrl = "";
	
	private int queueTime = 5;
	
	public ConvertThread() {
		Ioc ioc = new NutIoc(new JsonLoader("ioc/ioc.js"));
		PropertiesProxy config = ioc.get(PropertiesProxy.class, "config");
		serverDir = config.get("serverDir");
		clientUrl = config.get("clientUrl");
		queueTime = new Integer(config.get("queueTime")).intValue();
		ioc.depose(); 
	}
	
	
	@Override
	public void run() {
		while(true) {
			convert();
			try {
				Thread.sleep(queueTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public boolean invokeReceive(String id, File f) {
		Ioc ioc = new NutIoc(new JsonLoader("ioc/ioc.js"));
		PropertiesProxy config = ioc.get(PropertiesProxy.class, "config");
		System.out.println(config);
		
		String url = clientUrl + "/client/receive";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("file", f);
		Request request = Request.create(url, METHOD.POST, params);
		FilePostSender sender = new FilePostSender(request);
		Response response = sender.send();
		boolean result = new Boolean(response.getContent()).booleanValue();
		return result;
	}
	
	public void convert() {
		File queueDir = new File(serverDir + "/queue");
		File[] files = queueDir.listFiles();
		if(files.length > 0) {
			for(int i = 0; i < files.length; i++) {
				File f = files[0];
				String filePath = f.getAbsolutePath();
				String id = f.getName().substring(0, f.getName().lastIndexOf("."));
				String flashPath = serverDir + "/flash/" + id + ".swf";
				Converter.convert(filePath, flashPath);
				Files.deleteFile(f);
				boolean result = invokeReceive(id, new File(flashPath));
				System.out.println("------转换传输结果: " + result + "------");
			}
		}
	}
}
