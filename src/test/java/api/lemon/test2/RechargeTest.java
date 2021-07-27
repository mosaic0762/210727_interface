package api.lemon.test2;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

/**
 * @author mosaic
 * @date 2021/7/21-23:58
 */
public class RechargeTest {

    @BeforeTest
    public void setup() {

        //全局变量：这两个比不可少，config是断言；baseURI是接口地址，用例中只写了后半段地址
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";

        //前置条件读取Excel里的数据
        File file = new File("C:\\Users\\tao_c\\Desktop\\自动化工具软件&文档\\接口文档资料\\api_testcases_futureloan_v2.xls");

        //调用readSpecifyExcelData方法获取ExcelPojo对象集合ListDatas
        // ListDatas里面存放的是ExcelPojo对象，每一个ExcelPojo对象代表一行excel用例
        List<ExcelPojo> ListDatas = readSpecifyExcelData(file, 3, 1, 2);

        //执行【注册】接口请求
        Response resRegest = request(ListDatas.get(0));

        //执行【登录】接口请求
        Response resLogin = request(ListDatas.get(1));

        //获取全局变量存到环境变量中
        Environment.memberId = resLogin.jsonPath().get("data.id");
        Environment.token = resLogin.jsonPath().get("data.token_info.token");
    }

    @Test(dataProvider = "getLoginDatas")
    public void TestRecharge(ExcelPojo excelPojo) {

        //调用regexReplaceParams方法将入参变量替换为环境变量
        String regexReplaceParams = regexReplace(excelPojo.getInputParams(), Environment.memberId + "");

        //将替换后的json字符串回写到ExcelPojo中
        excelPojo.setInputParams(regexReplaceParams);

        //执行【充值】接口请求
        Response resRecharge = request(excelPojo);
    }

    @DataProvider
    public Object[] getLoginDatas() {
        File file = new File("C:\\Users\\tao_c\\Desktop\\自动化工具软件&文档\\接口文档资料\\api_testcases_futureloan_v2.xls");
        List<ExcelPojo> ListDatas = readSpecifyExcelData(file, 3, 3, 1);
        return ListDatas.toArray();
    }

    /**
     * 读取Excel指定sheet指定行和列里面的所有数据
     *
     * @param file     文件对象
     * @param sheetNum sheet编号
     * @param startRow 起始行号
     * @param readRow  读入行号
     */
    public List<ExcelPojo> readSpecifyExcelData(File file, int sheetNum, int startRow, int readRow) {

        ImportParams importParams = new ImportParams();
        //读取第二个sheet
        importParams.setStartSheetIndex(sheetNum - 1);
        //设置读取的行数
        importParams.setStartRows(startRow - 1);
        //设置读取的行数
        importParams.setReadRows(readRow);
        //读取Excel
        return ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
    }

    /**
     * //正则替换
     *
     * @param orgStr     原始json字符串
     * @param replaceStr 替换字符串
     * @return 返回替换后的字符串
     */
    public static String regexReplace(String orgStr, String replaceStr) {    //正则替换

        // 获取正则表达式提取器对象 pattern
        Pattern pattern = Pattern.compile("\\{\\{(.*?)}}");
        //matcher : 匹配原始字符串,得到需要匹配的字符串的对象
        Matcher matcher = pattern.matcher(orgStr);

        String result1 = "";
        String result = orgStr;

        while (matcher.find()) {              //matcher.find()调用一次查找一次,返回boolean值

            //matcher.group(0)表示获取到整个匹配的内容
            String outerStr = matcher.group(0);

            //matcher.group(1)表示获取到{{}}包裹的内容
            String innerStr = matcher.group(1);

//            result1 = orgStr.replace(outerStr, replaceStr);
            result = result.replace(outerStr, replaceStr);         //使用被替换后的字符串进行再次替换

        }
        return result;
    }

    /**
     * 对get/post请求方式进行二次封装
     * @param excelPojo    ExcelPojo对象
     */
    public Response request(ExcelPojo excelPojo) {

        //接口请求方法
        String method = excelPojo.getMethod();
        //接口请求参数
        String params = excelPojo.getInputParams();
        //接口请求头
        Map<String, Object> HeadersMap = JSON.parseObject(excelPojo.getRequestHeader());
        //接口请求地址
        String url = excelPojo.getUrl();

        //对get、post、patch、put做封装
        Response res = null;
        if ("get".equalsIgnoreCase(method)) {
            res = given().headers(HeadersMap).
                    when().get(url).
                    then().log().all().extract().response();
        } else if ("post".equalsIgnoreCase(method)) {
            res = given().body(params).headers(HeadersMap).
                    when().post(url).
                    then().log().all().extract().response();
        } else if ("patch".equalsIgnoreCase(method)) {
            res = given().body(params).headers(HeadersMap).
                    when().patch(url).
                    then().log().all().extract().response();
        } else if ("put".equalsIgnoreCase(method)) {
            res = given().body(params).headers(HeadersMap).
                    when().put(url).
                    then().log().all().extract().response();
        }
        return res;
    }
}