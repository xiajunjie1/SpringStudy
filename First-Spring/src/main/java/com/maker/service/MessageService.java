package com.maker.service;

import com.maker.di.config.MessageConfig;
import com.maker.di.type.MessageSendStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息发送类接口
 * */
public class MessageService implements AutoCloseable{
    private static final Logger LOGGER= LoggerFactory.getLogger(MessageService.class);
    private MessageConfig messageConfig;
    public boolean connect(){
        LOGGER.info("连接到 {}:{}",messageConfig.getHostname(),messageConfig.getPort());
        return messageConfig.isEnable();
    }

    public MessageSendStatus send(String msg){
        if(connect()){
            LOGGER.info("【消息发送】{}",msg);
            try{
                this.close();
            }catch (Exception e){
                LOGGER.error("关闭连接出现异常{}",e);
            }
            return MessageSendStatus.SUCCESS;
        }
        LOGGER.error("无法与服务器创建连接");
        return MessageSendStatus.FAILURE;
    }
    @Override
    public void close() throws Exception {
        LOGGER.info("关闭与服务器的连接");
    }

    public void setMessageConfig(MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
    }
}
