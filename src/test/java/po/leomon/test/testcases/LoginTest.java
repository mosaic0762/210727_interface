package po.leomon.test.testcases;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import po.leomon.test.common.BaseTest;
import po.leomon.test.pojo.ExcelPojo;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

/**
 * @author mosaic
 * @date 2021/7/21-18:24
 */
public class LoginTest extends BaseTest {

    @BeforeTest
    public void setup(){            //这里没用数据驱动

        //全局变量：这两个比不可少，config是断言；baseURI是接口地址，用例中只写了后半段地址
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";

        //调用readSpecifyExcelData方法获取ExcelPojo对象集合ListDatas
        // ListDatas里面存放的是ExcelPojo对象，每一个ExcelPojo对象代表一行excel用例
        List<ExcelPojo> ListDatas = readSpecifyExcelData(2,1,1);

        //执行【注册】接口请求
        Response resRegest = request(ListDatas.get(0));

    }

    @Test(dataProvider = "getLoginDatas")
    public void TestLogin(ExcelPojo excelPojo) {

        //执行【登陆】接口请求
        Response resLogin = request(excelPojo);

        //登陆断言:把期望响应结果转成Map
        Map<String,Object> exceptedMap = JSON.parseObject(excelPojo.getExcepted());
        for (String key:exceptedMap.keySet()) {

            Object exceptedvalue = exceptedMap.get(key);
            Object acturalvalue = resLogin.jsonPath().get(key);
            Assert.assertEquals(exceptedvalue,acturalvalue);
        }

    }
    @DataProvider
    public Object[] getLoginDatas(){

        List<ExcelPojo> ListDatas = readSpecifyExcelData(2,2);      //调用重载方法
        return ListDatas.toArray();
    }

}
