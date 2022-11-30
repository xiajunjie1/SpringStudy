package com.maker.producer.service;

import com.maker.common.Dept;

public interface IMessageService {
    public void send(String msg) throws Exception;
    public void sendDept(Dept dept) throws Exception;
}
