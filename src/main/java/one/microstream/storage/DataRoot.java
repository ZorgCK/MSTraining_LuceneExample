package one.microstream.storage;

import java.util.ArrayList;
import java.util.List;

import one.microstream.domain.Book;
import one.microstream.reference.Lazy;


public class DataRoot
{
	private final Lazy<List<Book>> books = Lazy.Reference(new ArrayList<Book>());
	
	public Lazy<List<Book>> getBooks()
	{
		return books;
	}
}
