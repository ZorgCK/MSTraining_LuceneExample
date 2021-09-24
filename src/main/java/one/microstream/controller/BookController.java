package one.microstream.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import one.microstream.domain.Book;
import one.microstream.domain.lucene.LuceneUtils;
import one.microstream.storage.DB;
import one.microstream.utils.MockupUtils;


@Controller("/books")
public class BookController
{
	@Get("/create")
	public HttpResponse<?> createBooks()
	{
		List<Book> allCreatedBooks = MockupUtils.loadMockupData();
		
		DB.root.getBooks().addAll(allCreatedBooks);
		
		return HttpResponse.ok("Books successfully created!");
	}
	
	@Get
	public List<Book> getBook()
	{
		return DB.root.getBooks().all();
	}
	
	@Get("/createIndex")
	public HttpResponse<?> createLuceneIndex()
	{
		try
		{
			LuceneUtils.createIndex();
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return HttpResponse.ok("Index successfully cleared!");
	}
	
	@Get("/searchLucene")
	public HttpResponse<List<String>> searchWithLucene()
	{
		List<String> searchByTitle = new ArrayList<String>();
		
		try
		{
			searchByTitle = LuceneUtils.searchByTitle("star");
		}
		catch(IOException | ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return HttpResponse.ok(searchByTitle);
	}
}
