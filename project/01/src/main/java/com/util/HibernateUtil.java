package com.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * @Author: mhy
 * @Description:
 * @Date:Create：in 2022/5/3 18:53
 * @Modified By：
 */

/**
 * 工具类
 */
public class HibernateUtil {
    //使用ThreadLocal模式初始化Session
    private static ThreadLocal<Session> threadLocal = new ThreadLocal<>();
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    //静态块初始化Session工厂
    static {
        //初始化注册服务对象
        registry = new StandardServiceRegistryBuilder()
                .configure() // 默认加载hibernate.cfg.xml配置文件，如果配置文件名称被修改，.configure("修改的名称")
                .build();
        //从元信息获取Session工厂
        sessionFactory = new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();
    }

    //获取Session的方法
    public static Session getSession(){
        //从线程中获取Session
        Session session = threadLocal.get();
        //判断Session是否存在
        if(session == null || !session.isOpen()){
            //不存在从工厂获取Session
            //工厂是否存在，不存在重新加载工厂
            if(null == sessionFactory)
                buildSessionFactory();
            //从工厂获取session
            session = (null != sessionFactory) ? sessionFactory.openSession() : null;
            //放入线程
            threadLocal.set(session);
        }
        return session;
    }

    //加载session工厂
    private static void buildSessionFactory(){
        //初始化注册服务对象
        registry = new StandardServiceRegistryBuilder()
                .configure() // 默认加载hibernate.cfg.xml配置文件，如果配置文件名称被修改，.configure("修改的名称")
                .build();
        //从元信息获取Session工厂
        sessionFactory = new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();
    }

    //关闭session
    public static void closeSession(){
        Session session = threadLocal.get();
        threadLocal.set(null);
        if(null != session && session.isOpen())
            session.close();
    }

}