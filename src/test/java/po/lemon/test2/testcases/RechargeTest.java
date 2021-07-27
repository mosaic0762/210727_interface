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
 * @date 2021/7/21-23:58
 */

public class RechargeTest extends BaseTest {

    @BeforeClass
    public void setup() {

        //生成三个没有被注册的手机号
        String phone = PhoneRandomUtil.getUnregisterPhone();
        //★★★将手机号保存到环境变量中
        Environment.envData.put("phone",phone);

        //调用readSpecifyExcelData方法获取ExcelPojo对象excelPojo
        ExcelPojo excelPojo = readSpecifyExcelData(3, 1, 2).get(0);
        ExcelPojo excelPojo1 = readSpecifyExcelData(3, 1, 2).get(1);

        //参数替换{{phone}}
        caseReplace(excelPojo);

        //执行【注册】接口请求
        Response resRegest = request(excelPojo,"Recharge");
        //将对应的接口返回字段提取到环境变量
        extractToEnvironment(excelPojo, resRegest);

        //参数替换{{phone}}
        caseReplace(excelPojo1);

        //执行【登录】接口请求
        Response resLogin = request(excelPojo1,"Recharge");
        //将对应的接口返回字段提取到环境变量
        extractToEnvironment(excelPojo1, resLogin);
    }

    @Test(dataProvider = "getLoginDatas")
    public void TestRecharge(ExcelPojo excelPojo) {

        //参数替换{{member_id}}
        caseReplace(excelPojo);

        //执行【充值】接口请求
        Response resRecharge = request(excelPojo,"Recharge");

        //充值断言:把期望响应结果转成Map
        assertResponse(excelPojo,resRecharge);
    }

    @DataProvider
    public Object[] getLoginDatas() {
        List<ExcelPojo> ListDatas = readSpecifyExcelData(3, 3);
        return ListDatas.toArray();
    }
}