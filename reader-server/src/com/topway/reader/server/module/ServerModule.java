package com.topway.reader.server.module;

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

import com.topway.reader.server.service.ServerService;

@IocBean
@At("/server")
public class ServerModule {
	
	private static final Log log = Logs.getLog(ServerModule.class);

	@Inject
	private ServerService serverService;
	
	@POST
	@At("/convert")
	@Ok("raw:html")	
	@AdaptBy(type = UploadAdaptor.class, args = {"ioc:myUpload"})
	public boolean convertFile(@Param("id") String id, @Param("file") TempFile tf) {
		log.debug("server convertFile request invoke, " + id);
		return serverService.convert(id, tf);
	}
	
	@POST
	@At("/index")
	@Ok("raw:html")	
	@AdaptBy(type = UploadAdaptor.class, args = {"ioc:myUpload"})
	public boolean createIndex(@Param("id") String id, @Param("file") TempFile tf) {
		log.debug("server createIndex request invoke, " + id);
		return serverService.index(id, tf);
	}
	
	@POST
	@At("/retrieval")
	@Ok("json")	
	public List<String> textRetrieval(@Param("keyword") String keyword, @Param("page") int page, @Param("pageSize") int pageSize) {
		log.debug("server textRetrieval request invoke, " + keyword);
		return serverService.retrieval(keyword, page, pageSize);
	}
	
	@GET
	@At("/remove/?")
	@Ok("raw:html")	
	public boolean removeFile(String id) {
		log.debug("server removeFile request invoke, " + id);
		return serverService.remove(id);
	}
	
	
}
