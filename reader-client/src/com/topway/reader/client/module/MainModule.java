package com.topway.reader.client.module;

import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Views;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

import com.topway.reader.client.view.DocumentViewMaker;

@At("/nutz")
@Modules(scanPackage = true)
@IocBy(type = ComboIocProvider.class, 
	args = {"*js","ioc","*annotation",
	"com.topway.reader.client.service",
	"com.topway.reader.client.module"})
@Views({DocumentViewMaker.class})   
public interface MainModule {

}
