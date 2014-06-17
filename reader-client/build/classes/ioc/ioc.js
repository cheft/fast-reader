var ioc = {
	config : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			paths : ["properties"]
		}
	},
	dataSource : {
		type : "org.apache.commons.dbcp.BasicDataSource",
		events : {
			depose : 'close'
		},
		fields : {
			driverClassName : {java :"$config.get('driverClassName')"},
			url : {java :"$config.get('url')"},
			username : {java :"$config.get('username')"},
			password : {java :"$config.get('password')"}
		}
	},
	dao : {
		type : "org.nutz.dao.impl.NutDao",
		args : [{refer:"dataSource"}]
	},
	utils : {
	    type : 'com.topway.reader.client.util.MyUtil',
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
	        nameFilter : '^(.+[.])(doc|docx|xls|xlsx|ppt|pptx|pdf|txt|rtf|swf)$'
	    } 
	},
	myUpload : {
	    type : 'org.nutz.mvc.upload.UploadAdaptor',
	    singleton : false,
	    args : [ { refer : 'uploadFileContext' } ] 
	}
};
