package com.topway.reader.client.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.sender.FilePostSender;
import org.nutz.http.sender.GetSender;
import org.nutz.http.sender.PostSender;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.upload.FieldMeta;
import org.nutz.mvc.upload.TempFile;
import org.nutz.service.NameEntityService;

import com.topway.reader.client.entity.FileContent;

@IocBean
public class FileContentService extends NameEntityService<FileContent>{
	
	private static final Log log = Logs.getLog(FileContentService.class);
	
	@Inject
	private Dao dao;
	
	@Inject
	private PropertiesProxy config;

	// 一些属性设值，比如创建时间等
	public String upload(FileContent fileContent, TempFile tf) {
		String id = R.UU16();
		fileContent.setId(id);
		File f = tf.getFile();                       
	    FieldMeta meta = tf.getMeta();
	    String fileName = fileContent.getName();
		try {
			fileName = java.net.URLDecoder.decode(fileName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		fileContent.setName(fileName);
		String fileExt = meta.getFileExtension();
	    fileContent.setSuffix(fileExt.substring(fileExt.lastIndexOf(".") + 1, fileExt.length()));
	    fileContent.setLength(f.length());
	    Date d = new Date();
	    fileContent.setCreateDate(d);
	    String srcFilePath = config.get("clientDir") + "/document/" + id  + fileExt;
	    File file = new File(srcFilePath);
	    Files.copy(f, file);
	    fileContent.setSrcFilePath(srcFilePath);
	    fileContent.setFlashFilePath(config.get("clientDir") + "/flash/" + id  + ".swf");
	    /*
	    try {
			fileContent.setContent(Streams.readBytes(new FileInputStream(f)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	    dao().insert(fileContent);
		return id;
	}
	
	public boolean commonInvoke(String id, String url) {
		url = config.get("serverUrl") + url;
		log.debug("requestUrl:" + url);
		FileContent fileContent = this.fetch(id);
		boolean result = false;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("file", new File(fileContent.getSrcFilePath()));
		Request request = Request.create(url, METHOD.POST, params);
		FilePostSender sender = new FilePostSender(request);
		Response response = sender.send();
		result = new Boolean(response.getContent()).booleanValue();
		return result;
	}
	
	public List<FileContent> textRetrieval(String keyword, int page) {
		String url = config.get("serverUrl") + "/server/retrieval";
		int pageSize = new Integer(config.get("pageSize"));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("keyword", keyword);
		params.put("page", page);
		params.put("pageSize", pageSize);
		Request request = Request.create(url, METHOD.POST, params);
		PostSender sender = new PostSender(request);
		Response response = sender.send();
		String json = response.getContent();
		List<String> list = Json.fromJsonAsList(String.class, json);
		if(list == null || list.size() == 0) {
			return null;
		}
		String sqlStr = new String("select id, length, name, suffix, flashFilePath from file_content where id in (");
		for(int i = 0; i < list.size(); i++) {
			String id = list.get(i).substring(0, list.get(i).lastIndexOf("."));
			sqlStr += ("'" + id + "',");
		}
		sqlStr = sqlStr.substring(0, sqlStr.length() - 1);
		sqlStr += ")";
 		
		Sql sql = Sqls.queryEntity(sqlStr);
		
		sql.setEntity(dao().getEntity(FileContent.class));
		dao().execute(sql);
		return sql.getList(FileContent.class);
	}
	
	public boolean receive(String id, File file) {
		String swfFile = config.get("clientDir") + "/flash/" + id + ".swf";
		FileContent fileContent = this.fetch(id);
		fileContent.setConvertCount(fileContent.getConvertCount() + 1);
		this.dao().updateIgnoreNull(fileContent);
		return Files.copy(file, new File(swfFile));
	}
	
	public boolean remove(String id) {
		FileContent fileContent = this.fetch(id);
		String suffix = fileContent.getSuffix();
		String clientDir = config.get("clientDir");
		String swfPath = clientDir + "/flash/" + id + ".swf";
		File swfFile = new File(swfPath);
		if(swfFile.exists()) {
			Files.deleteFile(swfFile);
		}
		String docPath = clientDir + "/document/" + id + "." + suffix;
		File docFile = new File(docPath);
		if(docFile.exists()) {
			Files.deleteFile(docFile);
		}
		this.dao().delete(fileContent);
		
		String url = config.get("serverUrl") + "/server/remove/" + id;
		Map<String, Object> params = new HashMap<String, Object>();
		Request request = Request.create(url, METHOD.GET, params);
		GetSender sender = new GetSender(request);
		Response response = sender.send();
		String str = response.getContent();
		Boolean result = new Boolean(str);
		return result;
	}
}
