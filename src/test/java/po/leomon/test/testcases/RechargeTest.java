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
import po.leomon.test.data.Environment;
import po.leomon.test.pojo.ExcelPojo;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

/**
 * @author mosaic
 * @date 2021/7/21-23:58
 */
public class RechargeTest extends BaseTest {

    @BeforeTest
    public void setup() {

        //全局变量：这两个比不可少，config是断言；baseURI是接口地址，用例中只写了后半段地址
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";

        //调用readSpecifyExcelData方法获取ExcelPojo对象集合ListDatas
        // ListDatas里面存放的是ExcelPojo对象，每一个ExcelPojo对象代表一行excel用例
        List<ExcelPojo> ListDatas = readSpecifyExcelData(3, 1,2);

        //执行【注册】接口请求
        Response resRegest = request(ListDatas.get(0));

        //将对应的接口返回字段提取到环境变量
//        extractToEnvironment(ListDatas.get(0).getExtract(),resRegest);
        extractToEnvironment(ListDatas.get(0),resRegest);

/**
 *调用regexReplaceParams方法将入参变量替换为环境变量    ---封装成方法
        String regexReplaceParams = regexReplace(ListDatas.get(1).getInputParams());   //这里是get(1)
        //将替换后的json字符串回写到ExcelPojo中
        ListDatas.get(1).setInputParams(regexReplaceParams);
*/
//        //参数替换{{phone}}
        caseReplace(ListDatas.get(1));

        //执行【登录】接口请求
        Response resLogin = request(ListDatas.get(1));

/**
 *  将对应的接口返回字段提取到环境变量中方法一     ---环境变量定义为Map&封装为方法
        Map<String, Object> ExtractMap = JSON.parseObject(extractStr);

        //方法一：写法一
        for (String key:ExtractMap.keySet()) {

            Object obj = ExtractMap.get(key);      //获取excel中Gpath的路径表达式
            Object value = resLogin.jsonPath().get(obj.toString());    //从响应数据中获取需要存储到环境变量中的值

            Environment.envData.put(key,value);
        }


        //方法一：写法二、获取Gpath的路径表达式
        Object memberIdPath = ExtractMap.get("member_id");
        int memberId = resRegest.jsonPath().get(memberIdPath.toString());

        Object tokenPath = ExtractMap.get("token");
        String token = resRegest.jsonPath().get(tokenPath.toString());
*/
        //将对应的接口返回字段提取到环境变量
        // extractToEnvironment(ListDatas.get(1).getExtract(),resLogin);
        extractToEnvironment(ListDatas.get(1),resLogin);

/**
 * 将对应的接口返回字段提取到环境变量中方法二    ---环境变量定义为属性
         //方法二：Gpath的路径表达式直接写在代码中，如 ："data.id"、"data.token_info.token"
        //获取全局变量存到环境变量中
        Environment.memberId = resLogin.jsonPath().get("data.id");
        Environment.token = resLogin.jsonPath().get("data.token_info.token");
*/
    }

    @Test(dataProvider = "getLoginDatas")
    public void TestRecharge(ExcelPojo excelPojo) {

        //调用regexReplaceParams方法将入参变量替换为环境变量
//        String regexReplaceParams = regexReplace(excelPojo.getInputParams(), Environment.envData.get("member_id") + "");
        caseReplace(excelPojo);

        //执行【充值】接口请求
        Response resRecharge = request(excelPojo);

        //充值断言:把期望响应结果转成Map
        Map<String,Object> exceptedMap = JSON.parseObject(excelPojo.getExcepted());
        for (String key:exceptedMap.keySet()) {

            Object exceptedvalue = exceptedMap.get(key);
            Object acturalvalue = resRecharge.jsonPath().get(key);
            Assert.assertEquals(exceptedvalue,acturalvalue);
        }

    }

    @DataProvider
    public Object[] getLoginDatas() {
        List<ExcelPojo> ListDatas = readSpecifyExcelData(3, 3);
        return ListDatas.toArray();
    }

}