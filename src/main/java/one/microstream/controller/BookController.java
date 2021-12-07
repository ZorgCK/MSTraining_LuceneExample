package one.microstream.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.lucene.queryparser.classic.ParseException;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import one.microstream.domain.Author;
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
		
		try
		{
			LuceneUtils.createIndex();
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return HttpResponse.ok("Books successfully created!");
	}
	
	@Get
	public List<Book> getBook()
	{
		return DB.root.getBooks().all();
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
	
	@Get("/insert")
	public HttpResponse<?> insert()
	{
		Optional<Author> optionalAuthor = DB.root.getBooks().all().stream().map(b -> b.getAuthor()).filter(
			a -> a.getLastname().equalsIgnoreCase("Jaggi")).findAny();
		
		Book book = new Book("123456789", "A great star", LocalDate.now(), new BigDecimal(10.30), optionalAuthor.get());
		
		DB.root.getBooks().add(book);
		
		LuceneUtils.updateIndex(Arrays.asList(book));
		
		return HttpResponse.ok("Books successfully inserted!");
	}
}
