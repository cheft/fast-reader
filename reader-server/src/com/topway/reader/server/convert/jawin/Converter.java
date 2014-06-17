package com.topway.reader.server.convert.jawin;

import java.io.File;

import org.jawin.COMException;
import org.jawin.DispatchPtr;
import org.jawin.FuncPtr;
import org.jawin.ReturnFlags;
import org.jawin.win32.Ole32;
import org.nutz.log.Log;
import org.nutz.log.Logs;


/**
 * @author ChenHaifeng
 */
public final class Converter {
	
	private static final Log log = Logs.getLog(Converter.class);

	
	public static void main0(String[] args) throws java.io.IOException {
		if (args.length > 0) {
			try {
				Ole32.CoInitialize();
				// Create Server object
				DispatchPtr p2f = new DispatchPtr("Print2Flash3.Server"); 
				// Setup interface and protection options
				DispatchPtr defProfile = (DispatchPtr)p2f.get("DefaultProfile");
				defProfile.put("InterfaceOptions", PTFConst.INTLOGO | PTFConst.INTZOOMSLIDER | PTFConst.INTPREVPAGE | PTFConst.INTGOTOPAGE | PTFConst.INTNEXTPAGE);
				defProfile.put("ProtectionOptions", PTFConst.PROTDISPRINT | PTFConst.PROTENAPI);
				// Convert document
				p2f.invoke("ConvertFile", new String(args[0]));
				System.out.println("Conversion completed successfully");
			} catch (Exception e){
				System.out.println("An error occurred at conversion: "+e.toString());
			} finally {
				try {
					Ole32.CoUninitialize();
				} catch (Exception e) {
					
				}
			}
		}else{ 
			System.out.println("Please provide a document file name as a parameter");
		}
		System.out.println("Press Enter to exit...");
		System.in.read();
	}
	

	public static void main(String[] args) {
		try {

			FuncPtr msgBox = new FuncPtr("USER32.DLL", "MessageBoxW");

			msgBox.invoke_I(0, "Hello From a DLL", "From Jawin", 0,
					ReturnFlags.CHECK_NONE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main3(String[] args) {
		String inFilePath = "D:/fastreader/server/queue/942db7beaa5f41fdb4cb7b1553036430.doc";
		String outFilePath = "D:/3.swf";
		convert(inFilePath, outFilePath);
	}

	public static void convert(String inFilePath, String outFilePath) {
		if (!new File(inFilePath).exists()) {
			return;
		}
		try {
			// 初始化COM组件
			Ole32.CoInitialize();
			// 创建打印服务
			DispatchPtr p2f = new DispatchPtr("Print2Flash3.Server");
			// 设置打印选项
			DispatchPtr defProfile = (DispatchPtr) p2f.get("DefaultProfile");
			defProfile.put("InterfaceOptions", 0);
			defProfile.put("InterfaceOptions",
			// P2FConst.INTLOGO //显示Print2Flsh的LOGO标志
					PTFConst.INTDRAG // 拖曳页面手型
					| PTFConst.INTZOOMSLIDER // 页面缩放
					| PTFConst.INTPREVPAGE // 上一页
					| PTFConst.INTGOTOPAGE // 页数跳转
					| PTFConst.INTNEXTPAGE // 下一页
					| PTFConst.INTSELTEXT // 文本选择
					| PTFConst.INTFITWIDTH // 适当页宽
					| PTFConst.INTFITPAGE // 适当页面
					| PTFConst.INTSEARCHBOX // 全文搜索框
					| PTFConst.INTSEARCHBUT // 搜索按钮
					| PTFConst.INTFULLSCREEN // 全屏按钮
			);

			// defProfile.put("ProtectionOptions", PTFConst.PROTDISPRINT |
			// PTFConst.PROTENAPI);
			// 设置文件转换得类型
			DispatchPtr bpOptions = (DispatchPtr) p2f
					.get("DefaultBatchProcessingOptions");
			bpOptions.put("BeforePrintingTimeout", 0);
			bpOptions.put("KeepAutomationAppRef", PTFConst.TSF_YES);

			String suffix = inFilePath.substring(
					inFilePath.lastIndexOf(".") + 1).toLowerCase();

			if (FileSuffix.DOC.equals(suffix) || FileSuffix.DOCX.equals(suffix)) {
				bpOptions.put("UseAutomation", PTFConst.MSWORD);
			} else if (FileSuffix.XLS.equals(suffix)
					|| FileSuffix.XLSX.equals(suffix)) {
				bpOptions.put("UseAutomation", PTFConst.MSEXCEL);
			} else if (FileSuffix.PPT.equals(suffix)
					|| FileSuffix.PPTX.equals(suffix)) {
				bpOptions.put("UseAutomation", PTFConst.MSPOWERPOINT);
			} else if (FileSuffix.PDF.equals(suffix)) {
				bpOptions.put("UseAutomation", PTFConst.ACROBAT);
			}
			p2f.invoke("ConvertFile", inFilePath, outFilePath);
		} catch (COMException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				Ole32.CoUninitialize();
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}

	}
}
