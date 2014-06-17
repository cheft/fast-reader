package com.topway.reader.server.convert.jacob;

import java.io.File;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Variant;

public class Converter
{

	public static void convert(String inFilePath, String outFilePath) {
		if (!new File(inFilePath).exists()) {
			return;
		}
        try
        {
            ComThread.InitSTA();
            // Create Server object
            ActiveXComponent p2f = new ActiveXComponent("Print2Flash3.Server");
            // Setup interface and protection options
            ActiveXComponent defProfile = new ActiveXComponent(p2f.getProperty("DefaultProfile").toDispatch());
//                defProfile.setProperty("InterfaceOptions", P2FConst.INTLOGO | P2FConst.INTZOOMSLIDER | P2FConst.INTPREVPAGE | P2FConst.INTGOTOPAGE | P2FConst.INTNEXTPAGE);
//                defProfile.setProperty("ProtectionOptions", P2FConst.PROTDISPRINT | P2FConst.PROTENAPI);
            defProfile.setProperty("InterfaceOptions", P2FConst.INTDRAG // 拖曳页面手型
                | P2FConst.INTZOOMSLIDER // 页面缩放
                | P2FConst.INTPREVPAGE // 上一页
                | P2FConst.INTGOTOPAGE // 页数跳转
                | P2FConst.INTNEXTPAGE // 下一页
                | P2FConst.INTSELTEXT // 文本选择
                | P2FConst.INTFITWIDTH // 适当页宽
                | P2FConst.INTFITPAGE // 适当页面
                | P2FConst.INTSEARCHBOX // 全文搜索框
                | P2FConst.INTSEARCHBUT // 搜索按钮
                | P2FConst.INTFULLSCREEN // 全屏按钮
                );
              defProfile.setProperty("ProtectionOptions", P2FConst.PROTDISPRINT | P2FConst.PROTENAPI);
            // Convert document
            // p2f.invoke("ConvertFile", new String(args[0]));
			Variant in = new Variant();
			in.putString(inFilePath);
			Variant out = new Variant();
			out.putString(outFilePath);
            p2f.invoke("ConvertFile", in, out);
            System.out.println("Conversion completed successfully");
        }
        catch (Exception e)
        {
            System.out.println("An error occurred at conversion: " + e.toString());
        }
        finally
        {
            ComThread.Release();
        }
        
    }
}
