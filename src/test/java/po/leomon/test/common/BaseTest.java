package po.leomon.test.common;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import io.restassured.response.Response;
import po.leomon.test.data.Contances;
import po.leomon.test.data.Environment;
import po.leomon.test.pojo.ExcelPojo;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

/**
 * @author mosaic
 * @date 2021/7/22-16:07
 */
public class BaseTest {

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
                    then().log().everything().extract().response();
        } else if ("post".equalsIgnoreCase(method)) {
            res = given().body(params).headers(HeadersMap).
                    when().post(url).
                    then().log().everything().extract().response();
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

    /**
     * 将对应的接口返回字段提取到环境变量中
     * @param excelPojo   excelPojo对象
     * @param res    响应结果
     */
//    public void extractToEnvironment(String extract,Response res){
    public void extractToEnvironment(ExcelPojo excelPojo,Response res){    //修改形参为ExcelPojo类型

        Map<String, Object> ExtractMap = JSON.parseObject(excelPojo.getExtract());
        //方法二：写法一
        for (String key:ExtractMap.keySet()) {

            Object obj = ExtractMap.get(key);      //获取excel中Gpath的路径表达式
            Object value = res.jsonPath().get(obj.toString());    //从响应数据中获取需要存储到环境变量中的值

            Environment.envData.put(key,value);
        }
    }

    /**
     *对用例数据进行替换（入参+请求头+接口地址+期望结果）；对正则替换进行封装
     * @param excelPojo   excelPojo对象
     * @return   替换后的excelPojo对象
     */
    public ExcelPojo caseReplace(ExcelPojo excelPojo){

        //正则替换 -->参数输入
        String InputParams = regexReplace(excelPojo.getInputParams());
        excelPojo.setInputParams(InputParams);

        //正则替换 -->请求头输入
        String RequestHeader = regexReplace(excelPojo.getRequestHeader());
        excelPojo.setRequestHeader(RequestHeader);

        //正则替换 -->url输入
        String Url = regexReplace(excelPojo.getUrl());               //在url中有入参member_id
        excelPojo.setRequestHeader(Url);

        //正则替换 -->期望结果
        String Excepted = regexReplace(excelPojo.getExcepted());       //注册时候断言手机号是否正确
        excelPojo.setRequestHeader(Excepted);

        return excelPojo;
    }
    /**
     * 正则替换
     * @param orgStr   输入的json字符串
     * @return      替换后的json字符串
     */
//    public static String regexReplace(String orgStr, String replaceStr) {    //正则替换
    public static String regexReplace(String orgStr) {    //正则替换,去掉 String replaceStr

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
            Object replaceStr = Environment.envData.get(innerStr);

//            result1 = orgStr.replace(outerStr, replaceStr);        //无法满足当前需求
            result = result.replace(outerStr, replaceStr.toString());         //使用被替换后的字符串进行再次替换

        }
        return result;
    }

    /**
     * 读取Excel指定sheet指定行和列里面的所有数据
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
     * @param sheetNum sheet编号
     * @param startRow 起始行号
     */
    public List<ExcelPojo> readSpecifyExcelData(int sheetNum, int startRow) {

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
