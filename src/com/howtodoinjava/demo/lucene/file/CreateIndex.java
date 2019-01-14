package com.howtodoinjava.demo.lucene.file;
import org.apache.commons.io.FileUtils;
import org.json.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.*; 

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.util.Date;
import java.util.Scanner; 

public class CreateIndex
{
    public static void main(String[] args)
    {
    	System.out.println("Enter 1 to create main index, 2 to create auxillary index, 3 to Merge Indexes - Perform Merge After building auxiliary index always. \n");
    	
    	Scanner sc = new Scanner(System.in);
    	
    	int inp = sc.nextInt();
    	
    	
    	String docsPath="", indexPath="";
    	
    	if (inp == 1) {
    		docsPath = "all_tweets";
    		indexPath = "main_index";
    	}
    	else if (inp == 2) {
    		docsPath = "new_tweets";
    		indexPath = "auxillary_index";
    	}
    	else if (inp == 3) {
    		System.out.println("merging");
    		mergeIndexes("main_index", "auxillary_index", "merged_index");
    		
    		System.exit(0);
    	}
    	else {
    		System.out.println("Invalid command");
    		System.exit(0);
    	}
 
        //Input Path Variable
        final Path docDir = Paths.get(docsPath);
 
        try
        {            
            Directory dir = FSDirectory.open( Paths.get(indexPath) );
                         
            Analyzer analyzer = new StandardAnalyzer();
             
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(OpenMode.CREATE);
             
            IndexWriter writer = new IndexWriter(dir, iwc);
             
            indexDocs(writer, docDir);
 
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    static void postProcess() {
    	try {
			FileUtils.deleteDirectory(new File("main_index"));
			FileUtils.deleteDirectory(new File("auxillary_index")); 
			
			//create source File object
		    File oldName = new File("merged_index");
		   
		    //create destination File object
		    File newName = new File("main_index");
		    
		    boolean isFileRenamed = oldName.renameTo(newName);
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    static void joinIndexes(String main_index, String auxillary_index, String index_path) {
    	try {
    		Date start = new Date(); 	    	
	    	
	    	Directory dir = FSDirectory.open( Paths.get("merged_index") );
	    	
	    	Directory dir_main = FSDirectory.open( Paths.get(main_index) );
	    	
	    	Directory dir_aux = FSDirectory.open( Paths.get(auxillary_index) );
	        
	        Analyzer analyzer = new StandardAnalyzer();
	        
	        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
	        iwc.setOpenMode(OpenMode.CREATE);
	        IndexWriter writer = new IndexWriter(dir, iwc);
	        
	        Directory[] INDEXES_DIR = {dir_main, dir_aux}; 
	        
	        Directory indexes[] = new Directory[INDEXES_DIR.length];
	        
	        for (int i = 0; i < INDEXES_DIR.length; i++) {
                System.out.println("Adding: " + INDEXES_DIR[i]);
                indexes[i] = INDEXES_DIR[i];
                System.out.println(indexes[i]);
            }
	        
	        System.out.print("Merging added indexes...");
            writer.addIndexes(indexes);
            System.out.println("done");

            writer.close();

         
            writer.close();
            
            postProcess();
//            
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    static int count_of_docs = 0;
    static void numberOfDocumentsInDirectory(Path path) throws IOException
    {
    	//Directory?
    	
        if (Files.isDirectory(path))
        {	
            //Iterate directory
            Files.walkFileTree(path, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    count_of_docs += 1;
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        else
        {
        	
        }
    }
    
    static void moveDocuments(Path path) throws IOException {
    	Files.walkFileTree(path, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                count_of_docs += 1;    
                
                String file_name = file.toString().split("/")[1];
                String destination = "all_tweets/" + file_name;
                System.out.printf("%s %s %s\n", file_name, destination, file.toString());
                
                try {
                    Path temp = Files.move (Paths.get(file.toString()),  Paths.get(destination));
                    if(temp != null) 
                    { 
                        System.out.println("File renamed and moved successfully"); 
                    } 
                    else
                    { 
                        System.out.println("Failed to move the file"); 
                    }
                }
                catch (IOException e) {
            		e.printStackTrace();
            	}
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    
    static void mergeIndexes(String main_index, String auxillary_index, String index_path) {
    	
    	System.out.println("Press 1 for checking index size and merging and 2 for manual merge(Not Recommended)");
    	
    	Scanner sc = new Scanner(System.in);
    	int inp = sc.nextInt();
    	final Path docDir = Paths.get("new_tweets");
    	if (inp == 1) {
    		
    		try {
				numberOfDocumentsInDirectory(docDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
    		
    		System.out.print(count_of_docs);
    		
    		if(count_of_docs >= 100) {
    			try {
    				joinIndexes(main_index, auxillary_index, index_path);
					moveDocuments(docDir);				
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    		else {
    			System.out.println("\nAuxiliary Index not big enough for merge");
    		}
    	}
    	else {
    		joinIndexes(main_index, auxillary_index, index_path);
    		try {
				moveDocuments(docDir);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
    	}    	
    }
    
    static void renamePathInIndex() throws IOException {
    	Directory dir;
    	dir = FSDirectory.open(Paths.get("merged_index"));
    	
    }
     
    static void indexDocs(final IndexWriter writer, Path path) throws IOException
    {
        //Directory?
        if (Files.isDirectory(path))
        {
            //Iterate directory
            Files.walkFileTree(path, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    try
                    {
                        //Index this file
                        indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        else
        {
            //Index this file
            indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
            
            /*
             * increment number of tweets indexed
             * 
             */
        }
    }
 
    static int count = 1;
    static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException
    {
    	
        try (InputStream stream = Files.newInputStream(file))
        {
            //Create lucene Document
            Document doc = new Document();
            
            /*
             * Preprocess Files.readAllBytes, add fields accordingly
             */
            
//          System.out.println(new String(Files.readAllBytes(file)));
//          System.out.printf("%s\n", new String(Files.readAllBytes(file)));
//          System.out.println(jsonObj.get("text")+ "" + (String) jsonObj.get("created_at"));            
            
            JSONObject jsonObj = new JSONObject(new String(Files.readAllBytes(file)));

           
            String[] path = file.toString().split("/");
            int length_of_path = path.length;
            		
            doc.add(new StringField("document_name", path[length_of_path-1], Field.Store.YES));
            doc.add(new LongPoint("modified", lastModified));
            doc.add(new TextField("contents", (String) jsonObj.get("text"), Store.YES));
            doc.add(new TextField("tweet_date", (String) jsonObj.get("created_at"), Field.Store.YES));
            writer.updateDocument(new Term("document_name", path[length_of_path-1]), doc);
            
            System.out.printf("%d.) Document Name- %s\n",count++, path[length_of_path-1]);
            
        }
    }
}