# Tweets-Search-Engine
<pre>
 CreateIndex.java   (Developer)  
  3 modes
  - Create Main Index
  - Create Auxillary Index
  - Merge Indexes
    - Based on size
    - Merge Anyway
 
 QueryOnIndex.java  (User)
  User gives query.
 
 
</pre>

## How To Run The Code

<pre>
  - Make Sure there are no hidden files in the directory "all_tweets" and "new_tweets". Run rm -rf {file_name}.
  - Run crawler main.py using command "python3 main.py".
  - Setup Apache Lucene in Eclipse
  - Add the following jars in the build path of the project
     - lucene-queryparser-7.4.0.jar
     - lucene-analyzers-common-7.4.0.jar
     - lucene-core-7.4.0.jar
     - commons-io-2.6.jar
     - json-20180813.jar
  - Make sure you have "all_tweets", "new_tweets" and "main_index" directories
      in your project directory.
  - If you don't have "main_index", run CreateIndex.java in mode 1.
  - Create Auxillary index by running CreateIndex.java in mode 2.
  - Merge Indexes using Mode 3. Here you have option to merge using size
      constraint i.e merge if documents in auxillary index exceed a certain 
      theshold(100) or merge anyway.
  - Run QueryOnIndex.java and enter query.
  - Create auxiliary index and merge, and again query to see results.
  - Always create auxiliary index again before merging.
</pre>
