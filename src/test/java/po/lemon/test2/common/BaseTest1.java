package po.lemon.test2.common;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.testng.annotations.BeforeTest;
import po.lemon.test2.data.Contances;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

/**
 * @author mosaic
 * @date 2021/7/26-23:11
 */
public class BaseTest1 {

    @BeforeTest
    public void GlobalSetup() throws FileNotFoundException {           //BeforeTest全局配置代码

        //System.getProperty("user,dir")获取项目的绝对路径
        File file = new File(System.getProperty("user.dir") + "\\log");
        if (!file.exists()) {
            file.mkdir();
        }

//        PrintStream fileOutPutStream = new PrintStream(new File("test_all.log"));
        PrintStream fileOutPutStream = new PrintStream(new File("log/test_all.log"));
        RestAssured.filters(new RequestLoggingFilter(fileOutPutStream),new ResponseLoggingFilter(fileOutPutStream));
    }
}
