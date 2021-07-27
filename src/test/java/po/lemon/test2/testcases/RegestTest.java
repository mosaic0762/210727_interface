package po.lemon.test2.testcases;

import com.alibaba.fastjson.JSON;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import po.lemon.test2.Util.JUMT;
import po.lemon.test2.Util.PhoneRandomUtil;
import po.lemon.test2.common.BaseTest;
import po.lemon.test2.data.Environment;
import po.lemon.test2.pojo.ExcelPojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static po.lemon.test2.common.BaseTest.readSpecifyExcelData;

/**
 * @author mosaic
 * @date 2021/7/26-13:43
 */
public class RegestTest extends BaseTest {

    @BeforeClass
    public void setup() throws InterruptedException {

        //随机生成没有注册过的手机号码
        String phone1 = PhoneRandomUtil.getUnregisterPhone();
        Thread.sleep(500);
        String phone2 = PhoneRandomUtil.getUnregisterPhone();
        Thread.sleep(500);
        String phone3 = PhoneRandomUtil.getUnregisterPhone();
        Thread.sleep(500);
        //保存到环境变量中
        Environment.envData.put("phone1", phone1);
        Environment.envData.put("phone2", phone2);
        Environment.envData.put("phone3", phone3);

    }
/**
 * 添加Allure日志
    @Test(dataProvider = "getRegestDatas")
    public void TestRegest(ExcelPojo excelPojo) throws FileNotFoundException {
        //为每一个请求单独的做日志保存
        //System.getProperty("user,dir")获取项目的绝对路径
        File file = new File(System.getProperty("user.dir") + "\\log");
        if (!file.exists()) {
            file.mkdir();
        }

        String logFilePath = System.getProperty("user.dir") + "\\log\\test" + excelPojo.getCaseId() + ".log";
        PrintStream fileOutPutStream = new PrintStream(new File(logFilePath));
        RestAssured.config=RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));
*/

    @Test(dataProvider = "getRegestDatas")
    public void TestRegest(ExcelPojo excelPojo) throws FileNotFoundException {

        //参数替换{{phone}}
        caseReplace(excelPojo);              //此处也将数据库断言单元格中的手机号做了替换

        //执行【登陆】接口请求
        Response resRegest = request(excelPojo,"Regest");

/**
 * 添加Allure日志
        //将日志添加到Allure中   ---放置在请求之后
        Allure.addAttachment("接口请求和响应信息", new FileInputStream(logFilePath));
*/

        //登陆断言:把期望响应结果转成Map
        assertResponse(excelPojo, resRegest);

        //数据库断言   ---对部分字段做断言
        assertSQL(excelPojo);
    }

    @DataProvider
    public Object[] getRegestDatas() {

        List<ExcelPojo> ListDatas = readSpecifyExcelData(1, 1);      //调用重载方法
        return ListDatas.toArray();
    }

    @AfterTest
    public void teardown() {           //也可以不清空，登陆自动替换同名环境变量
        //清空掉环境变量
        Environment.envData.clear();
    }

}
