package po.lemon.test2.common;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import po.lemon.test2.Util.JUMT;
import po.lemon.test2.data.Contances;
import po.lemon.test2.data.Environment;
import po.lemon.test2.pojo.ExcelPojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigDecimal;
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
 * @date 2021/7/22-16:07
 */
public class BaseTest {

    @BeforeTest
    public void GlobalSetup() throws FileNotFoundException {           //BeforeTest全局配置代码

        //返回json为Decimal数据类型
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        //BaseUrl全局配置
        RestAssured.baseURI = Contances.BASE_URI;
    }

    /**
     * 对get/post请求方式进行二次封装&日志重定向
     * @param excelPojo                    excelPojo对象
     * @param interfaceModuleName          日志存放地址
     * @return                             接口响应结果
     */
    public Response request(ExcelPojo excelPojo, String interfaceModuleName) {

        //为每一个请求单独的做日志保存
        //System.getProperty("user,dir")获取项目的绝对路径
        String logFilePath;

        if (Contances.LOG_TO_FILE) {

            //创建文件目录
            File FilePath = new File(System.getProperty("user.dir") + "\\log\\" + interfaceModuleName);
            if (!FilePath.exists()) {
                FilePath.mkdirs();         //将mkdir改为mkdirs
            }

            //生成文件地址
            logFilePath = FilePath+ "\\" + excelPojo.getCaseId() + ".log";

            PrintStream fileOutPutStream = null;
            try {
                fileOutPutStream = new PrintStream(new File(logFilePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //重定向日志文件
            RestAssured.config = RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));
        }

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
            res = given().log().all().headers(HeadersMap).
                    when().get(url).
                    then().log().everything().extract().response();
        } else if ("post".equalsIgnoreCase(method)) {
            res = given().log().all().body(params).headers(HeadersMap).
                    when().post(url).
                    then().log().everything().extract().response();
        } else if ("patch".equalsIgnoreCase(method)) {
            res = given().log().all().body(params).headers(HeadersMap).
                    when().patch(url).
                    then().log().all().extract().response();
        } else if ("put".equalsIgnoreCase(method)) {
            res = given().log().all().body(params).headers(HeadersMap).
                    when().put(url).
                    then().log().all().extract().response();
        }

        //将日志添加到Allure中   ---放置在请求之后
        if (Contances.LOG_TO_FILE) {      //日志输出配置：控制台(false) or 日志文件中(true)
            try {
                Allure.addAttachment("接口请求和响应信息", new FileInputStream(logFilePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return res;
    }

    /**
     * 将对应的接口返回字段提取到环境变量中
     * @param excelPojo excelPojo对象
     * @param res       响应结果
     */
//    public void extractToEnvironment(String extract,Response res){
    public void extractToEnvironment(ExcelPojo excelPojo, Response res) {    //修改形参为ExcelPojo类型

        Map<String, Object> ExtractMap = JSON.parseObject(excelPojo.getExtract());
        //方法二：写法一
        for (String key : ExtractMap.keySet()) {

            Object obj = ExtractMap.get(key);      //获取excel中Gpath的路径表达式

            Object value = res.jsonPath().get(obj.toString());       //从响应数据中获取需要存储到环境变量中的值

            Environment.envData.put(key, value);
        }
    }

    /**
     * 对用例数据进行替换（入参+请求头+接口地址+期望结果）；对正则替换进行封装
     *
     * @param excelPojo excelPojo对象
     * @return 替换后的excelPojo对象
     */
    public ExcelPojo caseReplace(ExcelPojo excelPojo) {


        //正则替换 -->参数输入
        String InputParams = regexReplace(excelPojo.getInputParams());
        excelPojo.setInputParams(InputParams);

        //正则替换 -->请求头输入
        String RequestHeader = regexReplace(excelPojo.getRequestHeader());
        excelPojo.setRequestHeader(RequestHeader);

        //正则替换 -->url输入
        String Url = regexReplace(excelPojo.getUrl());               //在url中有入参member_id
        excelPojo.setUrl(Url);

        //正则替换 -->期望结果
        String Excepted = regexReplace(excelPojo.getExcepted());       //注册时候断言手机号是否正确
        excelPojo.setExcepted(Excepted);

        //正则替换 -->期望结果
        String DbAssert = regexReplace(excelPojo.getDbAssert());       //注册时候断言手机号是否正确
        excelPojo.setDbAssert(DbAssert);

        return excelPojo;
    }

    /**
     * 正则替换
     *
     * @param orgStr 输入的json字符串
     * @return 替换后的json字符串
     */
//    public static String regexReplace(String orgStr, String replaceStr) {    //正则替换
    public static String regexReplace(String orgStr) {    //正则替换,去掉 String replaceStr

        if (orgStr != null) {        //如果excel用例中提取的json字符串是null则仍旧返回null，否则进行正则替换
            // 获取正则表达式提取器对象 pattern
            Pattern pattern = Pattern.compile("\\{\\{(.*?)}}");
            //matcher : 匹配原始字符串,得到需要匹配的字符串的对象
            Matcher matcher = pattern.matcher(orgStr);

            String result = orgStr;

            while (matcher.find()) {              //matcher.find()调用一次查找一次,返回boolean值

                //matcher.group(0)表示获取到整个匹配的内容
                String outerStr = matcher.group(0);

                //matcher.group(1)表示获取到{{}}包裹的内容
                String innerStr = matcher.group(1);

                //从环境变量中取到实际的值 member_id
                //此处决定了inputParams和extract中的member_id必须一致
                Object replaceStr = Environment.envData.get(innerStr);  //如果匹配不到就是null,返回的也是null，相当于没匹配

//            result1 = orgStr.replace(outerStr, replaceStr);        //无法满足当前需求
                result = result.replace(outerStr, replaceStr.toString());         //使用被替换后的字符串进行再次替换

            }
            return result;
        }
        return orgStr;
    }

    /**
     * 响应结果断言
     * @param excelPojo     excelPojo对象
     * @param response       接口响应断言结果
     */
    public void assertResponse(ExcelPojo excelPojo, Response response) {

        String Excepted = excelPojo.getExcepted();
        if (Excepted != null) {
            Map<String, Object> exceptedMap = JSON.parseObject(Excepted);
            for (String key : exceptedMap.keySet()) {

                Object exceptedvalue = exceptedMap.get(key);

                Object acturalvalue = response.jsonPath().get(key);

                Assert.assertEquals(acturalvalue, exceptedvalue);
            }
        }
    }

    /**
     * 数据库断言   ---对部分字段做断言
     * @param excelPojo excelPojo对象
     */
    public void assertSQL(ExcelPojo excelPojo) {
        String dbAssert = excelPojo.getDbAssert();
        //数据库断言
        if (dbAssert != null) {                                     //表格中数据库期望为空不做断言
            Map<String, Object> ExtractMap = JSON.parseObject(dbAssert);
            for (String key : ExtractMap.keySet()) {
                //key其实就是我们执行的sql语句
                //value就是数据库断言的期望值
                Object expectedValue1 = ExtractMap.get(key);
                //System.out.println("expectedValue类型::" + expectedValue.getClass());
                if (expectedValue1 instanceof BigDecimal) {                         //对数据库中的数据类型进行判断
                    Object actualValue = JUMT.querySingleData(key);
                    //System.out.println("actualValue类型:" + actualValue.getClass());
                    Assert.assertEquals(actualValue, expectedValue1);
                } else if (expectedValue1 instanceof Integer) {
                    //此时从excel里面读取到的是integer类型
                    //从数据库里面拿到的是Long类型
                    Long expectedValue2 = ((Integer) expectedValue1).longValue();
                    Object actualValue = JUMT.querySingleData(key);
                    Assert.assertEquals(actualValue, expectedValue2);
                }
            }
        }
    }

    /**
     * 读取Excel指定sheet指定行和列里面的所有数据
     *
     * @param sheetNum sheet编号
     * @param startRow 起始行号
     * @param readRow  读入行号
     */
    public List<ExcelPojo> readSpecifyExcelData(int sheetNum, int startRow, int readRow) {

        //调用静态常量 ：EXCEL_FILE_PATH
        File file = new File(Contances.EXCEL_FILE_PATH);

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
     * 读取Excel指定sheet指定行开始以后的所有数据
     *
     * @param sheetNum sheet编号
     * @param startRow 起始行号
     */
    public static List<ExcelPojo> readSpecifyExcelData(int sheetNum, int startRow) {

        //调用静态常量 ：EXCEL_FILE_PATH
        File file = new File(Contances.EXCEL_FILE_PATH);

        ImportParams importParams = new ImportParams();
        //读取第二个sheet
        importParams.setStartSheetIndex(sheetNum - 1);
        //设置读取的行数
        importParams.setStartRows(startRow - 1);
        //读取Excel
        return ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
    }

}
