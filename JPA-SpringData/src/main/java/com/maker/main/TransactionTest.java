package com.maker.main;

import com.maker.SpringDataJPAStart;
import com.maker.dao.ICompanyDao;
import com.maker.po.Company;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionTest {
    private static final Logger LOGGER= LoggerFactory.getLogger(TransactionTest.class);
    @Autowired
    private ICompanyDao companyDao;
    @Transactional(rollbackFor = Exception.class)//使用事务，此类必须被Spring管理，否则事务失效
    public  void updateTest(){


        Company param=new Company();
        param.setCapital(78901.1);
        param.setCid(1L);
        param.setNum(88);

        Company company=new Company();
        company.setCid(2L);
        //制造异常，测试事务是否生效
        company.setCapital(null);
        company.setNum(null);
        int result= companyDao.editBase(param);
        companyDao.editBase(company);
        LOGGER.info("影响的行数：{}",result);
    }
}
