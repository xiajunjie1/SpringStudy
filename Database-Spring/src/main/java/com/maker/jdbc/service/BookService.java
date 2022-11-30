package com.maker.jdbc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
/**
 * 如果使用了try...catch捕捉了异常
 * 那么@Transactional会失效，需要手动回滚
 * */
@Service
public class BookService {
    private static final Logger LOGGER= LoggerFactory.getLogger(BookService.class);
    @Autowired
    private JdbcTemplate template;
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void remove(Integer bid){

        String sql="delete from book where bid=?";


            int result=template.update(sql,bid);
            LOGGER.info("已删除数据，影响条数：{}",result);
            int count=10/0;//制造异常


    }
}
