package com.howtodoinjava.demo.lucene.file;
import java.nio.file.*; 
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
 
public class QueryOnIndex
{
    //directory contains the lucene indexes
    private static final String MAIN_INDEX = "main_index";
    private static final String AUXILLARY_INDEX = "auxillary_index";
    
    static boolean check = false;
    
    public static void main(String[] args) throws Exception
    {
    	System.out.println("Enter Query\n");
        
        Scanner sc = new Scanner(System.in);
    	String query = sc.nextLine();
            	
    	if (Files.exists(Paths.get(AUXILLARY_INDEX))) {
    		check = true;
    	}
    	else {
    		check = false;
    	}
    	
        
        
        //Search indexed contents using search term
        
        if (check) {
        	IndexSearcher searcher_auxillary_index = createSearcher(false);
        	TopDocs foundDocsNew = searchInContent(query, searcher_auxillary_index);
                 
        	System.out.println("Total Results from Auxillary Index :: " + foundDocsNew.totalHits);
         
        	for (ScoreDoc sd : foundDocsNew.scoreDocs)
        	{
        		Document d = searcher_auxillary_index.doc(sd.doc);
        		System.out.println("Doc Name : "+ d.get("document_name") + " Tweeted on : " + d.get("tweet_date") + ", Score : " + sd.score);
        	}
    	}
        
        IndexSearcher searcher = createSearcher(true);
        TopDocs foundDocs = searchInContent(query, searcher);
        
        System.out.println("Total Results from Merged Main Index :: " + foundDocs.totalHits);
         
        for (ScoreDoc sd : foundDocs.scoreDocs)
        {
            Document d = searcher.doc(sd.doc);
            System.out.println("Doc Name : "+ d.get("document_name") + " Tweeted on : " + d.get("tweet_date")  + ", Score : " + sd.score);
        }       
    }
     
    private static TopDocs searchInContent(String textToFind, IndexSearcher searcher) throws Exception
    {
        //Create search query
        QueryParser qp = new QueryParser("contents", new StandardAnalyzer());
        Query query = qp.parse(textToFind);
         
        //search the index
        TopDocs hits = searcher.search(query, 10);
        return hits;
    }
 
    private static IndexSearcher createSearcher(boolean main_index) throws IOException
    {
    	Directory dir;
    	if (main_index) {
            dir = FSDirectory.open(Paths.get(MAIN_INDEX));	
    	}
    	else {
    		dir = FSDirectory.open(Paths.get(AUXILLARY_INDEX));
    	}
         
        //It is an interface for accessing a point-in-time view of a lucene index
        IndexReader reader = DirectoryReader.open(dir);
         
        //Index searcher
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }

}