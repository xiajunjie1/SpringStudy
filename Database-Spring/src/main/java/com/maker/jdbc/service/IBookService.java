package com.maker.jdbc.service;

import com.maker.jdbc.vo.Book;

public interface IBookService {
    public boolean addBook(Book book,boolean hasex)throws Exception;
    public boolean editBook(Book book,boolean hasex)throws Exception;
}
