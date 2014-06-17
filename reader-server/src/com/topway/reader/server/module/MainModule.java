package com.topway.reader.server.module;

import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

@At("/nutz")
@Modules(scanPackage = true)
@IocBy(type = ComboIocProvider.class, 
	args = {"*js","ioc","*annotation",
	"com.topway.reader.server.lucene",
	"com.topway.reader.server.service",
	"com.topway.reader.server.module", 
})
public interface MainModule {

}
