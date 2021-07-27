package po.lemon.test2.testcases;

import com.alibaba.fastjson.JSON;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import po.lemon.test2.Util.PhoneRandomUtil;
import po.lemon.test2.common.BaseTest;
import po.lemon.test2.data.Environment;
import po.lemon.test2.pojo.ExcelPojo;

import java.util.List;
import java.util.Map;

/**
 * @author mosaic
 * @date 2021/7/21-18:24
 */
public class LoginTest extends BaseTest {

    @BeforeClass
    public void setup(){

        //生成一个没有被注册的手机号
        String Phone = PhoneRandomUtil.getUnregisterPhone();
        //★★★将手机号保存到环境变量中
        Environment.envData.put("phone",Phone);

        //调用readSpecifyExcelData方法获取ExcelPojo对象excelPojo
        ExcelPojo excelPojo = readSpecifyExcelData(2,1,1).get(0);

        //参数替换{{phone}}
        caseReplace(excelPojo);

        //执行【注册】接口请求
        Response resRegest = request(excelPojo,"Login");

        //★★★将对应的接口返回字段提取到环境变量
        extractToEnvironment(excelPojo, resRegest);
    }

    @Test(dataProvider = "getLoginDatas")
    public void TestLogin(ExcelPojo excelPojo) {

        //参数替换{{phone}}
        caseReplace(excelPojo);

        //执行【登陆】接口请求
        Response resLogin = request(excelPojo,"Login");

        //登陆断言:把期望响应结果转成Map
        assertResponse(excelPojo,resLogin );

    }
    @DataProvider
    public Object[] getLoginDatas(){

        List<ExcelPojo> ListDatas = readSpecifyExcelData(2,2);      //调用重载方法
        return ListDatas.toArray();
    }

}
