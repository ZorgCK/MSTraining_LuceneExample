package one.microstream.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import one.microstream.persistence.types.Persister;
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
		List<Book> list = this.isbnToBooks.computeIfAbsent(
			(String)String.valueOf(book.getIsbn().charAt(0)),
			k -> Lazy.Reference(new ArrayList<>())).get();
		list.add(book);
		
		List<Book> list2 = this.authorToBooks.computeIfAbsent(
			(one.microstream.domain.Author)book.getAuthor(),
			k -> Lazy.Reference(new ArrayList<>())).get();
		list2.add(book);
		
		persister.storeAll(list, list2, authorToBooks, isbnToBooks);
	}
	
	private void addAll(final Collection<? extends Book> books, final Persister persister)
	{
		Set<Object> storeTargets = new HashSet<>();
		
		books.forEach(b ->
		{
			List<Book> list = this.isbnToBooks.computeIfAbsent(
				(String)String.valueOf(b.getIsbn().charAt(0)),
				k -> Lazy.Reference(new ArrayList<>())).get();
			list.add(b);
			
			List<Book> list2 = this.authorToBooks.computeIfAbsent(
				(one.microstream.domain.Author)b.getAuthor(),
				k -> Lazy.Reference(new ArrayList<>())).get();
			list2.add(b);
			
			storeTargets.addAll(Arrays.asList(list, list2));
		});
		
		storeTargets.addAll(Arrays.asList(authorToBooks, isbnToBooks));
		
		persister.storeAll(storeTargets);
	}
	
}
