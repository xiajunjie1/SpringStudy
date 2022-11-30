package com.maker.jdbc.service.impl;

import com.maker.jdbc.service.IBookService;
import com.maker.jdbc.vo.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements IBookService {
    private static final Logger LOGGER= LoggerFactory.getLogger(BookServiceImpl.class);
    @Autowired
    private JdbcTemplate template;
    @Override
    public boolean addBook(Book book,boolean hasex) throws Exception {
        String sql="insert into book (title,author,price)Values(?,?,?)";
        boolean result= template.update(sql,book.getTitle(),book.getAuthor(),book.getPrice())>0;
        LOGGER.info("【数据新增】结果为：{}",result);
        if(hasex){
            int i=10/0;//制造异常
        }
        return result;
    }

    @Override
    public boolean editBook(Book book,boolean hasex) throws Exception {
        String sql="update book set title=?,author=?,price=? where bid=?";
        boolean result= template.update(sql,book.getTitle(),book.getAuthor(),book.getPrice(),book.getBid())>0;
        LOGGER.info("【数据修改】结果为：{}",result);
        if(hasex){
            int i=10/0;//制造异常
        }
        return result;
    }
}
