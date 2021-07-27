package po.lemon.test1.Util;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author mosaic
 * @date 2021/7/24-16:36
 */
public class JUMT {

    public static Connection getConnection() {

        //定义数据库连接
        //Oracle：jdbc:oracle:thin:@localhost:1521:DBName
        //SqlServer：jdbc:microsoft:sqlserver://localhost:1433; DatabaseName=DBName
        //MySql：jdbc:mysql://localhost:3306/DBName
        String url = "jdbc:mysql://api.lemonban.com/futureloan?useUnicode=true&characterEncoding=utf-8";
        String user = "future";
        String password = "123456";

        //定义数据库连接对象
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * sql的更新操作（包括了增加+修改+删除）
     * @param str_sql        SQL语句
     */
    public static void queryupdate(String str_sql) {

        //1、创建数据库连接对象
        Connection connection = getConnection();

        //2、实例化数据操作对象
        QueryRunner queryRunner = new QueryRunner();

        //3.对数据库进行增删改查操作
        try {
            queryRunner.update(connection, str_sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
        }
    }

    /**
     * 查询所有的结果集合
     * @param str_sql      SQL语句
     * @return            List数组
     */
    public static List<Map<String, Object>> queryDatas(String str_sql) {

        //1、创建数据库连接对象
        Connection connection = getConnection();

        //2、实例化数据操作对象
        QueryRunner queryRunner = new QueryRunner();

         //3.对数据库进行查询所有的结果集合
        List<Map<String, Object>> result = null;
        try {
            result = queryRunner.query(connection, str_sql, new MapListHandler());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
        }

        return result;
    }

    /**
     * 查询所有的结果集合中的第一条
     * @param str_sql      SQL语句
     * @return            集合中的第一条
     */
    public static Map<String, Object> queryoneline(String str_sql) {

        //1、创建数据库连接对象
        Connection connection = getConnection();

        //2、实例化数据操作对象
        QueryRunner queryRunner = new QueryRunner();

        //3.对数据库查询所有的结果集合
        Map<String, Object> result = null;
        try {
            result = queryRunner.query(connection, str_sql, new MapHandler());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
        }

        return result;
    }

    /**
     * 对数据库查询返回单个值
     * @return            返回单个值
     */
    public static Object querySingleData(String str_sql) {

        //1、创建数据库连接对象
        Connection connection = getConnection();

        //2、实例化数据操作对象
        QueryRunner queryRunner = new QueryRunner();

         //3.对数据库查询返回单个值
        Object result = null;
        try {
            result = queryRunner.query(connection, str_sql, new ScalarHandler<Object>());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {

        }
        return result;
    }
}
