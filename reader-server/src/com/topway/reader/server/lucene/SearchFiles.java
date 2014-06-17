package com.topway.reader.server.lucene;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class SearchFiles {
	
	@Inject
	private PropertiesProxy config;


	public static void main2(String[] args) throws IOException, ParseException {
		Date start = new Date();
	    SearchFiles searchFiles = new SearchFiles();
	    searchFiles.search("我是中国人");
	    
	    Date end = new Date();
		System.out.println(end.getTime() - start.getTime() + " total milliseconds");
	}
	
	
	public List<String> searchByPage(String keyword, int page, int pageSize) {
        List<String> list = new ArrayList<String>();
		try {
			//请求字段
	        //String keyword = "document";
	                
	        // 1，把要搜索的文本解析为 Query
	        String[] fields = { "filename", "contents" };
			Analyzer analyzer = new PaodingAnalyzer();
			// Analyzer analyzer = new SimpleAnalyzer();
	        QueryParser queryParser = new MultiFieldQueryParser(fields, analyzer);
	        Query query = queryParser.parse(keyword);
	
	        // 2，进行查询，从索引库中查找
			String indexPath = config.get("serverDir") + "/index";

	        IndexSearcher indexSearcher = new IndexSearcher(indexPath);
	        Filter filter = null;
			Sort sort = new Sort(new SortField[]{new SortField("modified", SortField.LONG, true)});
			//int pageSize = new Integer(config.get("pageSize"));
			int hits = pageSize * page;
			int count = (page - 1) * pageSize - 1;
	        TopDocs topDocs = indexSearcher.search(query, filter, hits, sort);
	        System.out.println("总共有【" + topDocs.totalHits + "】条匹配结果");
	        int i = 0;
	        // 3，打印结果
	        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
	            // 文档内部编号
	            int index = scoreDoc.doc; 
	            // 根据编号取出相应的文档
	            Document doc = indexSearcher.doc(index);
	            System.out.println("filename = " + doc.get("filename"));
	            // System.out.println("contents = " + doc.get("contents"));
	            System.out.println("path = " + doc.get("path"));
	            if(i > count) {
	            	list.add(doc.get("filename"));
	            }
	            i++;
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}catch(ParseException pe) {
			pe.printStackTrace();
		}
		return list;
    }
	
	
	public List<String> search(String keyword) {
		List<String> list = null;
		String index = config.get("serverDir") + "/index";
	    String queries = null;
	    int repeat = 0;
	    int page = 3;
	    boolean raw = false;
	    int hitsPerPage = 5;
	    String[] field = { "filename", "contents" };
	    SearchFiles searchFiles = new SearchFiles();
	    try {
			list = searchFiles.doSearch(index, queries, keyword, field, hitsPerPage, page, raw, repeat);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    return list;
	}

	public List<String> doSearch(String index, String queries, String queryString, String[] field, int hitsPerPage, int page, boolean raw, int repeat) throws IOException, ParseException {
		FSDirectory	directory = FSDirectory.getDirectory(index);
		IndexReader	reader = IndexReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new PaodingAnalyzer();

		BufferedReader in = null;
		if (queries != null) {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					queries), "UTF-8"));
		} else {
			in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
		}
		BooleanClause.Occur[] flags = new BooleanClause.Occur[] {
				BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD };
		Query query = MultiFieldQueryParser.parse(queryString, field, flags, analyzer);
		Sort sort = new Sort(new SortField[]{new SortField("modified", SortField.LONG, true)});

		System.out.println("Searching for");

		if (repeat > 0) { // repeat & time as benchmark
			Date start = new Date();
			for (int i = 0; i < repeat; i++) {
				searcher.search(query, null, 100, sort);
			}
			Date end = new Date();
			System.out.println("Time: " + (end.getTime() - start.getTime())
					+ "ms");
		}

		List<String> list = doPagingSearch(in, searcher, query, hitsPerPage, page, raw,
				queries == null && queryString == null);

		reader.close();
		return list;
	}

	public List<String> doPagingSearch(BufferedReader in,
			IndexSearcher searcher, Query query, int hitsPerPage, int page, boolean raw,
			boolean interactive) throws IOException {
		List<String> list = new ArrayList<String>();
		Sort sort = new Sort(new SortField[]{new SortField("modified", SortField.LONG, true)});

		TopDocs results = searcher.search(query, null, 5 * hitsPerPage, sort);
		ScoreDoc[] hits = results.scoreDocs;

		int numTotalHits = results.totalHits;
		System.out.println(numTotalHits + " total matching documents");

		int start = 0;
		int end = Math.min(numTotalHits, hitsPerPage);

		while (true) {
			if (end > hits.length) {
				System.out.println("Only results 1 - " + hits.length + " of "
								+ numTotalHits
								+ " total matching documents collected.");
				System.out.println("Collect more (y/n) ?");
				String line = in.readLine();
				if (line.length() == 0 || line.charAt(0) == 'n') {
					break;
				}
				hits = searcher.search(query, null, numTotalHits, sort).scoreDocs;
			}

			end = Math.min(hits.length, start + hitsPerPage);

			for (int i = start; i < end; i++) {
				if (raw) {
					System.out.println("doc=" + hits[i].doc + " score="
							+ hits[i].score);
					continue;
				}

				Document doc = searcher.doc(hits[i].doc);
				String filename = doc.get("filename");
				
				if (filename != null) {
					System.out.println((i + 1) + ". " + filename);
					list.add(filename);
				} else {
					System.out.println((i + 1) + ". "
							+ "No Name for this document");
				}

			}

			if (!interactive || end == 0) {
				break;
			}

			if (numTotalHits >= end) {
				boolean quit = false;
				while (true) {
					System.out.print("Press ");
					if (start - hitsPerPage >= 0) {
						System.out.print("(p)revious page, ");
					}
					if (start + hitsPerPage < numTotalHits) {
						System.out.print("(n)ext page, ");
					}
					System.out
							.println("(q)uit or enter number to jump to a page.");

					String line = in.readLine();
					if (line.length() == 0 || line.charAt(0) == 'q') {
						quit = true;
						break;
					}
					if (line.charAt(0) == 'p') {
						start = Math.max(0, start - hitsPerPage);
						break;
					} else if (line.charAt(0) == 'n') {
						if (start + hitsPerPage < numTotalHits) {
							start += hitsPerPage;
						}
						break;
					} else {
						// int page = Integer.parseInt(line);
						if ((page - 1) * hitsPerPage < numTotalHits) {
							start = (page - 1) * hitsPerPage;
							break;
						} else {
							System.out.println("No such page");
						}
					}
				}
				if (quit) {
					break;
				}
				end = Math.min(numTotalHits, start + hitsPerPage);
			}
		}
		return list;
	}

}
