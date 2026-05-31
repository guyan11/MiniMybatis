package com.mini.batis.session;

public interface SqlSessionFactory {

    SqlSession openSession();

    void closeSession() throws Exception;
}
