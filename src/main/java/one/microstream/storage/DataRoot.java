package one.microstream.storage;

import one.microstream.domain.Books;


public class DataRoot
{
	private final Books books = new Books();
	
	public Books getBooks()
	{
		return books;
	}
}
