package com.topway.reader.server.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class IndexFiles {
	
	@Inject
	private PropertiesProxy config;
	
	public void buildIndex(String docsPath) {
		String indexPath = config.get("serverDir") + "/index";
		File docDir = new File(docsPath);
		boolean isCreate = true;
		File genFile = new File(indexPath + "/segments.gen");
		if (!docDir.exists() || !docDir.canRead()) {
			System.out.println("Document directory '"
							+ docDir.getAbsolutePath()
							+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}else if(genFile.exists()){
			isCreate = false;
		}
		System.out.println(isCreate);
		try {
			Date start = new Date();
			System.out.println("Indexing to directory '" + indexPath + "'...");
            Analyzer analyzer = new PaodingAnalyzer();
            IndexWriter writer = new IndexWriter(indexPath, analyzer, isCreate, IndexWriter.MaxFieldLength.UNLIMITED); 
			indexDocs(writer, docDir);
			writer.optimize();   
	        writer.close();   

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime()
					+ " total milliseconds");
		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void indexDocs(IndexWriter writer, File file) throws Exception {
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]));
					}
				}
			} else {
				String text = null;
				String path = file.getPath();
				if (path.contains(".")) {
					int index = path.lastIndexOf(".");
					String suffix = path.substring(index + 1);
					if("txt".equals(suffix)) {
						text = readTxt(path);
					}else if("xls".equals(suffix)) {
						text = readXls(file);
					}else if("xlsx".equals(suffix)) {
						text = readXlsx(path);
					}else if("doc".equals(suffix)) {
						text = readDoc(file);
					}else if("docx".equals(suffix)) {
						text = readDocx(path);
					}else if("ppt".equals(suffix)) {
						text = readPpt(file);
					}else if("pptx".equals(suffix)) {
						text = readPptx(path);
					}else if("pdf".equals(suffix)) {
						text = readPdf(file);
					}else {
						text = readTxt(path);
					}
				} else {
					text = readTxt(path);
				}
				Document doc = new Document();

				doc.add(new Field("filename", file.getName(),   
                        Field.Store.YES, Field.Index.ANALYZED,   
                        Field.TermVector.WITH_POSITIONS_OFFSETS));
				
				doc.add(new Field("contents", text,   
                        Field.Store.YES, Field.Index.ANALYZED,   
                        Field.TermVector.WITH_POSITIONS_OFFSETS));
				
				doc.add(new Field("path", file.getAbsolutePath(), 
						Field.Store.YES, Field.Index.ANALYZED));
				
				doc.add(new Field("modified", Long.toString(file.lastModified()), 
						Field.Store.NO, Field.Index.ANALYZED));
				
				System.out.println("adding " + file);
				writer.addDocument(doc);
			}
		}
	}

	public String readTxt(String path) {
		String text = new String();
		BufferedReader br = null;
		try {
			FileReader read = new FileReader(path);
			br = new BufferedReader(read);
			String row;
			while(( row = br.readLine()) != null){
			    text += (row + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return text;
	}

	public String readXls(File xls) {
		FileInputStream fis = null;
		ExcelExtractor extractor = null;
		try {
			fis = new FileInputStream(xls);
			HSSFWorkbook workbook = new HSSFWorkbook(fis);
			extractor = new ExcelExtractor(workbook);

			extractor.setFormulasNotResults(true);
			extractor.setIncludeSheetNames(false);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return extractor.getText();
	}
	
	public String readXlsx(String path) {
		XSSFExcelExtractor ee = null;
		try {
			OPCPackage opcPackage = POIXMLDocument.openPackage(path);
			ee = new XSSFExcelExtractor(opcPackage);
			ee.setFormulasNotResults(true);
			ee.setIncludeSheetNames(false);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlException e) {
			e.printStackTrace();
		} catch (OpenXML4JException e) {
			e.printStackTrace();
		} 
		return ee.getText();
	}

	public String readDoc(File doc) {
		FileInputStream fis = null;
		WordExtractor extractor = null;
		try {
			fis = new FileInputStream(doc);
			extractor = new WordExtractor(fis);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return extractor.getText();
	}
	
	public String readDocx(String path) {
		XWPFWordExtractor we = null;
		try {
			OPCPackage opcPackage = POIXMLDocument.openPackage(path);
			we = new XWPFWordExtractor(opcPackage);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlException e) {
			e.printStackTrace();
		} catch (OpenXML4JException e) {
			e.printStackTrace();
		} 
		return we.getText();
	}

	public String readPpt(File ppt) throws Exception {
		FileInputStream fis = null;
		String text = new String();
		try {
			fis = new FileInputStream(ppt);
			SlideShow ss = new SlideShow(new HSLFSlideShow(fis));
			Slide[] slides = ss.getSlides();

			for (int i = 0; i < slides.length; i++) {
				TextRun[] t = slides[i].getTextRuns();
				for (int j = 0; j < t.length; j++) {
					text += t[j].getText();
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return text;
	}
	
	public String readPptx(String path) {
		XSLFPowerPointExtractor ppe = null;
		try {
			OPCPackage opcPackage = POIXMLDocument.openPackage(path);
			ppe = new XSLFPowerPointExtractor(opcPackage);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlException e) {
			e.printStackTrace();
		} catch (OpenXML4JException e) {
			e.printStackTrace();
		}
		return ppe.getText();
	}
	

	public String readPdf(File pdf) {
		String text = new String();
		FileInputStream is = null;
		PDDocument document = null;
		try {
			is = new FileInputStream(pdf);
			PDFParser parser = new PDFParser(is);
			parser.parse();
			document = parser.getPDDocument();
			PDFTextStripper stripper = new PDFTextStripper();
			text = stripper.getText(document);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				is = null;
			}
			if (document != null) {
				try {
					document.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				document = null;
			}
		}
		return text;
	}
}
