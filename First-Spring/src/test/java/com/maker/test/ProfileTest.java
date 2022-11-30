package com.maker.test;

import com.maker.di.config.MessageConfig_Profile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

@ContextConfiguration(locations = {"classpath:spring/application.xml"})
@ExtendWith(SpringExtension.class)
@ActiveProfiles("dev")
public class ProfileTest {
    private static final Logger LOGGER= LoggerFactory.getLogger(ProfileTest.class);
    @Autowired
    private MessageConfig_Profile messageConfigProfile;
    @Autowired
    private ApplicationContext context;
    @Test
    public void testProfile(){
        LOGGER.info("当前环境获取到的对象为：{}",messageConfigProfile);
        LOGGER.info("当前的环境为：{}", Arrays.toString(context.getEnvironment().getActiveProfiles()));
    }

}
