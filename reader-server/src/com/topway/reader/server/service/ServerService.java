package com.topway.reader.server.service;

import java.io.File;
import java.util.List;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.upload.TempFile;

import com.topway.reader.server.lucene.IndexFiles;
import com.topway.reader.server.lucene.SearchFiles;

@IocBean
public class ServerService {
	
	private static final Log log = Logs.getLog(ServerService.class);
	
	@Inject
	private PropertiesProxy config;
	
	@Inject
	private IndexFiles indexFiles;
	
	@Inject
	private SearchFiles searchFiles;
	
	public boolean convert(String id, TempFile tf) {
		File f = tf.getFile();                       
		String name = f.getName();
		String fileExt = name.substring(name.lastIndexOf("."), name.length());
	    File qfile = new File(config.get("serverDir") + "/queue/" + id + fileExt);
	    log.debug(qfile.getAbsoluteFile());
	    return Files.copy(f, qfile);
	}

	public boolean index(String id, TempFile tf) {
		File f = tf.getFile();                       
		String name = f.getName(); 
		String fileExt = name.substring(name.lastIndexOf("."),  name.length());
		String filePath = config.get("serverDir") + "/temp/" + id + fileExt;
	    File qfile = new File(filePath);
	    log.debug(qfile.getAbsoluteFile());
	    Files.copy(f, qfile);
	    indexFiles.buildIndex(filePath);
	    return Files.deleteFile(qfile);
	}
	
	public List<String> retrieval(String keyword, int page, int pageSize) {
		List<String> list = searchFiles.searchByPage(keyword, page, pageSize);
		return list;
	}

	public boolean remove(String id) {
		String swfPath = config.get("serverDir") + "/flash/" + id + ".swf";
	    log.debug(swfPath);
		File swfFile = new File(swfPath);
		if(swfFile.exists()) {
			return Files.deleteFile(swfFile);
		}
		return true;
	}
	
}
