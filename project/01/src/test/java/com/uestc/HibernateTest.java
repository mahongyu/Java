package com.uestc;

import com.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.junit.Test;

import java.util.EnumSet;

/**
 * @Author: mhy
 * @Description:
 * @Date:Create：in 2022/5/2 14:57
 * @Modified By：
 */
public class HibernateTest {

    @Test
    public void testInit(){
        StandardServiceRegistry registry = null;
        SessionFactory sessionFactory = null;
        Session session = null;
        Transaction tx = null;
        try{
            //5.X版本的写法
            //初始化注册服务对象
            registry = new StandardServiceRegistryBuilder()
                    .configure() // 默认加载hibernate.cfg.xml配置文件，如果配置文件名称被修改，.configure("修改的名称")
                    .build();
            //从元信息获取Session工厂
            sessionFactory = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
            //从工厂创建Session连接
            session = sessionFactory.openSession();
            //获取事务并开启事务
            tx = session.beginTransaction();
//            // 第二种获取事务方式
//            tx = session.getTransaction();
//            // 开启事务
//            tx.begin();
            //创建实例
            User user = new User();
            user.setName("zhangsan");
            user.setPwd("123");
            session.save(user);
//            User user = session.get(User.class,1);
//            System.out.println(user);
            //提交事务
            tx.commit();
        }catch (Exception e){
            //回滚事务
            tx.rollback();
        }finally {
            if(session != null && session.isOpen())
                session.close();
        }
    }

    /**
     * new -> save -> close -> update
     */
    @Test
    public void test01(){
        Session session = null;
        Transaction tx = null;
        User user = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();

            user = new User();//瞬时状态，内存有，session没有，数据库没有
            user.setName("lisi");
            user.setPwd("123");

            session.save(user);//持久状态，内存有，session有，数据库有


            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
            tx.rollback();
        }finally {
            HibernateUtil.closeSession();//游离状态，内存有，session没有，数据库有
        }
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();

            //这里只是为了学习，实际开发中不规范
            user.setName("赵六");//持续状态，内存有，session有，数据库有
            session.update(user);

            tx.commit();
        }catch (Exception e){
            e.printStackTrace();
            tx.rollback();
        }finally {
            HibernateUtil.closeSession();//游离状态，内存有，session没有，数据库有
        }
    }

    /**
     * get/load -> clear/evict
     */
    @Test
    public void test02() {
        Session session = null;
        User user = null;
        try {
            session = HibernateUtil.getSession();

            //get/load请求获得的对象持久状态，内存有，session有，数据库有
            //get请求是立即加载的过程，先查询缓存，缓存没有查询数据库
            user = session.get(User.class,1);
            //load请求懒加载的过程，如果使用到该对象才会去进行查询，先查询缓存，缓存没有查询数据库
            user = session.load(User.class,1);
            System.out.println(user);

            //清除缓存
            session.clear();//游离状态，内存有，session没有，数据库有
            session.evict(user);//清除某个缓存

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            HibernateUtil.closeSession();//游离状态，内存有，session没有，数据库有
        }
    }

    /**
     * update
     */
    @Test
    public void testUpdate() {
        Session session = null;
        Transaction tx = null;
        User user = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();

            //违规操作
//            user = new User();
//            user.setId(6);
//            user.setName("刘德华");
            //先查询再修改
            user = session.get(User.class,1);
            if(null != user){
                user.setName("张学友");
                session.update(user);
            }

            session.update(user); //持久状态

            tx.commit();

        }catch (Exception e){
            e.printStackTrace();
            tx.rollback();
        }finally {
            HibernateUtil.closeSession();//游离状态，内存有，session没有，数据库有
        }
    }

    /**
     * delete
     */
    @Test
    public void testDelete() {
        Session session = null;
        Transaction tx = null;
        User user = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();

            //违规操作
//            user = new User();
//            user.setId(6);
            //先查询再删除
            user = session.get(User.class,1);
            if(null != user){
                session.delete(user);//瞬时状态，内存有，session没有，数据库没有
            }

            tx.commit();

        }catch (Exception e){
            e.printStackTrace();
            tx.rollback();
        }finally {
            HibernateUtil.closeSession();//游离状态，内存有，session没有，数据库有
        }
    }

    @Test
    public void testCreateDB(){
        //5.1.x版本至目前最新版
        //创建注册服务对象
        //.configure()如果不写参数，表示默认获取的是hibernate.cfg.xml
        //配置文件的名字是不能改的，如果改掉之后，就应该在configure()方法中传入配置文件名字，有必要的话还要传入路径
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().configure().build();
        //生成Metadata构建元信息
        Metadata metadata = new MetadataSources(serviceRegistry).buildMetadata();
        SchemaExport schemaExport = new SchemaExport();
        //生成表结构
        schemaExport.create(EnumSet.of(TargetType.DATABASE),metadata);
    }

    /**
     * 单向多对一
     */
    @Test
    public void testSingleManyToOne() {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();

            //创建实例对象
            Grade grade1 = new Grade();
            grade1.setName("基础");

            Grade grade2 = new Grade();
            grade2.setName("中级");



            Student student = new Student();
            student.setName("张三");
            student.setAge(18);
            student.setGrade(grade1);

            Student student2 = new Student();
            student2.setName("李四");
            student2.setAge(18);
            student2.setGrade(grade1);

            Student student3 = new Student();
            student3.setName("王五");
            student3.setAge(18);
            student3.setGrade(grade2);

            //存储的顺序是根据外键约束而定的，如果外键不可以为空，必须先存储外键的一端
            //如果外键可以为空，随意存储，但是建议先存储外键的一端，因为会多执行update
            session.save(grade1);
            session.save(grade2);

            session.save(student);
            session.save(student2);
            session.save(student3);



            tx.commit();

        }catch (Exception e){
            e.printStackTrace();
            tx.rollback();
        }finally {
            HibernateUtil.closeSession();
        }
    }

    /**
     * 获取
     * 查询不用开启事务，会降低性能
     */
    @Test
    public void testSingleGetManyToOne() {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();

            Student student = session.get(Student.class,1);
            System.out.println("stuName:" +student.getName() + ",grade:" +student.getGrade().getName());


            tx.commit();

        }catch (Exception e){
            e.printStackTrace();
            tx.rollback();
        }finally {
            HibernateUtil.closeSession();
        }
    }

}