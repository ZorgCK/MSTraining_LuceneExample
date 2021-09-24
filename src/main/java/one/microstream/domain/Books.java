package one.microstream.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import one.microstream.persistence.types.Persister;
import one.microstream.persistence.types.Storer;
import one.microstream.reference.Lazy;
import one.microstream.storage.DB;


public class Books
{
	private final Map<String, Lazy<List<Book>>>	isbnToBooks		= new HashMap<>();
	private final Map<Author, Lazy<List<Book>>>	authorToBooks	= new HashMap<>();
	
	public void add(final Book book)
	{
		this.add(book, DB.storageManager);
	}
	
	public void addAll(final Collection<? extends Book> books)
	{
		this.addAll(books, DB.storageManager);
	}
	
	public List<Book> all()
	{
		return isbnToBooks.values().stream().flatMap(b -> b.get().stream()).collect(Collectors.toList());
	}
	
	private void add(final Book book, final Persister persister)
	{
		List<Book> list = isbnToBooks.get(book.getIsbn().charAt(0)).get();
		list.add(book);
		
		List<Book> list2 = authorToBooks.get(book.getAuthor()).get();
		list2.add(book);
		
		persister.storeAll(list, list2);
	}
	
	private void addAll(final Collection<? extends Book> books, final Persister persister)
	{
		Storer storer = persister.createEagerStorer();
		
		books.forEach(b ->
		{
			List<Book> list = isbnToBooks.get(b.getIsbn().charAt(0)).get();
			list.add(b);
			
			List<Book> list2 = authorToBooks.get(b.getAuthor()).get();
			list2.add(b);
			
			storer.storeAll(list, list2);
		});
		
		storer.commit();
	}
	
}
