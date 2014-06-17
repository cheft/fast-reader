var ioc = {
	config : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			paths : ["properties"]
		}
	},
	utils : {
	    type : 'com.topway.reader.server.util.MyUtil',
	    fields : {
	        sc : {app:'$servlet'}
	    }
	},
	tmpFilePool : {
	    type : 'org.nutz.filepool.NutFilePool',
	    args : [{java:'$utils.getPath("/WEB-INF/tmp")'}, 1000]   
	},
	uploadFileContext : {
	    type : 'org.nutz.mvc.upload.UploadingContext',
	    singleton : false,
	    args : [ { refer : 'tmpFilePool' } ],
	    fields : {
	        ignoreNull : true,
	        maxFileSize : 20971520, //20M
	        nameFilter : '^(.+[.])(doc|docx|xls|xlsx|ppt|pptx|pdf|txt|rtf)$'
	    } 
	},
	myUpload : {
	    type : 'org.nutz.mvc.upload.UploadAdaptor',
	    singleton : false,
	    args : [ { refer : 'uploadFileContext' } ] 
	}
	
};
