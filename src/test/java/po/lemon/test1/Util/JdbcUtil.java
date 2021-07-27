package po.lemon.test1.Util;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.testng.annotations.Test;

import javax.xml.bind.SchemaOutputResolver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author mosaic
 * @date 2021/7/24-12:25
 */
public class JdbcUtil {

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

    //1、创建数据库连接对象
    Connection connection = getConnection();

    //2、实例化数据操作对象
    QueryRunner queryRunner = new QueryRunner();

    @Test
    public void SQL_update() {

        //新增sql语句
        String sql_insert = "insert into member value(9545658,'xx','25D55AD283AA400AF464C76D713C07AD','13313120002',1,1000.00,'2021-07-24 9:24:20')";

        //更新sql语句
        String sql_update = "update member set reg_name = '小可爱' where id = 9545658;";
        //3.对数据库进行更新操作
        try {
            queryRunner.update(connection, sql_update);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void SQL_MapHandler() {
        //查询sql语句
        String sql_select = "select * from member where id = 9545658;";

        try {
            Map<String, Object> result = queryRunner.query(connection, sql_select, new MapHandler());
            System.out.println(result);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void SQL_MapListHandler( ) {
        //查询sql语句
        String sql_select = "select * from member where id < 10;";


        List<Map<String, Object>> result = null;
        try {
            result = queryRunner.query(connection, sql_select, new MapListHandler());

            System.out.println(result);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void SQL_ScalarHandler() {
        //查询sql语句
        String sql_select = "select count(*) from member where id < 20;";

        try {
            Object result = queryRunner.query(connection, sql_select, new ScalarHandler<Long>());
            System.out.println(result);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}

