package one.microstream.domain.lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import one.microstream.storage.DB;


public class LuceneUtils
{
	private static IndexWriter createWriter() throws IOException
	{
		String property = System.getProperty("java.io.tmpdir");
		
		FSDirectory dir = FSDirectory.open(Paths.get(property));
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		IndexWriter writer = new IndexWriter(dir, config);
		return writer;
	}
	
	private static Document createDocument(String isbn, String title)
	{
		Document document = new Document();
		document.add(new TextField("isbn", isbn, Field.Store.YES));
		document.add(new TextField("title", title, Field.Store.YES));
		return document;
	}
	
	public static void createIndex() throws IOException
	{
		IndexWriter writer = createWriter();
		List<Document> documents = new ArrayList<>();
		
		DB.root.getBooks().all().forEach(b ->
		{
			Document doc = createDocument(b.getIsbn(), b.getName());
			documents.add(doc);
		});
		
		// Let's clean everything first
		writer.deleteAll();
		
		writer.addDocuments(documents);
		writer.commit();
		writer.close();
	}
	
	private static IndexSearcher createSearcher() throws IOException
	{
		String property = System.getProperty("java.io.tmpdir");
		
		Directory dir = FSDirectory.open(Paths.get(property));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}
	
	public static List<String> searchByTitle(String title) throws IOException, ParseException
	{
		IndexSearcher searcher = createSearcher();
		QueryParser qp = new QueryParser("title", new StandardAnalyzer());
		Query idQuery = qp.parse(title);
		TopDocs hits = searcher.search(idQuery, 10);
		
		List<String> collect = Stream.of(hits.scoreDocs).map(h ->
		{
			try
			{
				return searcher.doc(h.doc).get("isbn");
			}
			catch(IOException e)
			{
				return "";
			}
		}).collect(Collectors.toList());
		
		return collect;
	}
}
