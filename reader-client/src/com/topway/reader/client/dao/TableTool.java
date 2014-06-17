package com.topway.reader.client.dao;

import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.json.JsonLoader;

import com.topway.reader.client.entity.FileContent;

public class TableTool {

	public static void main(String[] args) {
		Ioc ioc = new NutIoc(new JsonLoader("ioc\\ioc.js"));
		Dao dao = ioc.get(Dao.class); 
		
		// dao.create(Folder.class, true);
		dao.create(FileContent.class, true);
		/*	
		Folder folder = new Folder();
		folder.setId(R.UU16());
		folder.setName("test1");
		
		Folder folder2 = new Folder();
		folder2.setId(R.UU16());
		folder2.setName("test11");
		
		folder2.setParent(folder);
		
		Attachment attachment = new Attachment();
		attachment.setId(R.UU16());
		attachment.setName("attach11");
		attachment.setFolder(folder2);*/
		
		// dao.insertWith(folder2, null);

		/*
		for(int i = 0; i < 1000; i++) {
			Attachment attach = new Attachment();
			attach.setName("测试Nutz.doc");
			attach.setLength(1024L);
			attach.setType("doc");
			dao.insert(attach);
			
			attach = new Attachment();
			attach.setName("测试2.xls");
			attach.setLength(1024L);
			attach.setType("xls");
			dao.insert(attach);
		}
		*/
		ioc.depose(); 
	}
	
}
