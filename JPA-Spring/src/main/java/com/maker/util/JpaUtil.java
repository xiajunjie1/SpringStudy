package com.maker.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {
    private static final String PERSISTENCE_UNIT="JayjJPA";//定义持久单元名称
    private static EntityManagerFactory entityManagerFactory;
    private static ThreadLocal<EntityManager> entityManager=new ThreadLocal<>();//保存EntityManager对象，保证了线程安全
    private JpaUtil(){}//让用户无法直接实例化

    static {
        //静态代码块，首次执行的时候初始化EntityManagerFactory，只执行一次
        rebuildFactory();
    }

    public static void rebuildFactory(){//调用此方法，即创建一个新的EntityManagerFactory实例对象
        entityManagerFactory= Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);

    }
    public static EntityManagerFactory getEntityManagerFactory(){
        if(entityManagerFactory==null){
            rebuildFactory();
        }
        return entityManagerFactory;
    }
    /**
     * 获取EntityManager对象
     * */
    public static EntityManager getEntityManager(){
        EntityManager manager=entityManager.get();
        if(manager==null){//EntityManager为空，则创建一个EntityManager
            if(entityManagerFactory==null){//工厂为空，则创建一个工厂
                rebuildFactory();
            }
            manager=entityManagerFactory.createEntityManager();
            entityManager.set(manager);//将EntityManager保存到ThreadLocal中

        }
        return manager;
    }

    public static void close(){
        EntityManager manager=entityManager.get();
        if(manager!=null){
            manager.close();
        }
        entityManager.remove();//从ThreadLocal中移除
    }
}
