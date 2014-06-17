package com.topway.reader.client.module;

import java.io.File;
import java.util.List;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import com.topway.reader.client.entity.FileContent;
import com.topway.reader.client.service.FileContentService;

@IocBean
@At("/client")
public class ClientModule {
	
	private static final Log log = Logs.getLog(ClientModule.class);

	@Inject
	private FileContentService fileContentService;
	
	@POST
	@At("/")
	@Ok("raw:html")	
	@AdaptBy(type = UploadAdaptor.class, args = {"ioc:myUpload"})
	public String saveFile(@Param("..") FileContent fileContent, @Param("file") TempFile tf) {
		log.debug("saveFile request invoke...");
		return fileContentService.upload(fileContent, tf);
	}
	
	@GET
	@At("/convert/?")
	@Ok("raw:html")	
	public boolean convertFile(String id) {
		log.debug("convertFile request invoke, " + id);
		return fileContentService.commonInvoke(id, "/server/convert");
	}
	
	@GET
	@At("/index/?")
	@Ok("raw:html")	
	public boolean createIndex(String id) {
		log.debug("createIndex request invoke, " + id);
		return fileContentService.commonInvoke(id, "/server/index");
	}
 
	@POST
	@At("/retrieval")
	@Ok("json")
	public List<FileContent> textRetrieval(@Param("keyword") String keyword, @Param("page") int page) {
		log.debug("textRetrieval request invoke, " + keyword);
		return fileContentService.textRetrieval(keyword, page);
	}
	
	@GET
	@At("/?")
	@Ok("document")
	public FileContent getFilesById(String id, @Param("suffix") String suffix) {
		log.debug("getFilesById request invoke, " + id);
		FileContent fileContent = fileContentService.fetch(id);
		fileContent.setSuffix(suffix);
		return fileContent;
	}
	
	@POST
	@At("/receive")
	@Ok("raw:html")	
	@AdaptBy(type = UploadAdaptor.class, args = {"ioc:myUpload"})
	public boolean receiveFile(@Param("id") String id, @Param("file") File file) {
		log.debug("receiveFile request invoke, " + id);
		return fileContentService.receive(id, file);
	}
	
	@GET
	@At("/remove/?")
	@Ok("raw:html")	
	public boolean removeFile(String id) {
		log.debug("removeFile request invoke, " + id);
		return fileContentService.remove(id);
	}
	
}
