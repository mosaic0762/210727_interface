package api.lemon.test;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
public class LoginTest {

    @BeforeTest
    public void setup(){            //这里没用数据驱动

        //全局变量：这两个比不可少，config是断言；baseURI是接口地址，用例中只写了后半段地址
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";

        //前置条件读取Excel里的数据
        File file = new File("C:\\Users\\tao_c\\Desktop\\自动化工具软件&文档\\接口文档资料\\api_testcases_futureloan_v2.xls");

        //调用readSpecifyExcelData方法获取ExcelPojo对象集合ListDatas
        // ListDatas里面存放的是ExcelPojo对象，每一个ExcelPojo对象代表一行excel用例
        List<ExcelPojo> ListDatas = readSpecifyExcelData(file,2,1,1);

        //获取excelPojo对象
        ExcelPojo excelPojo = ListDatas.get(0);

        //获取excelPojo对象的getInputParams属性
        String excelPojoInputParams = excelPojo.getInputParams();

        //获取excelPojo对象的getRequestHeader属性
        String excelPojoRequestHeader = excelPojo.getRequestHeader();
        Map <String, Object> RequestHeaderMap = JSON.parseObject(excelPojoRequestHeader);

        //获取excelPojo对象的getUrl属性
        String excelPojoUrl = excelPojo.getUrl();

        //执行【注册】接口请求
        Response res =
            given().
                    body(excelPojoInputParams).
                    //使用 headers需要传入Map但是excelPojoRequestHeader是字符串，需要转为Map
                    headers(RequestHeaderMap).
            when().
                    post(excelPojoUrl).
            then().
                    log().all().
                    extract().response();
    }

    @Test(dataProvider = "getLoginDatas")
    public void TestLogin(ExcelPojo excelPojo) {

        //获取excelPojo对象的getInputParams属性
        String excelPojoInputParams = excelPojo.getInputParams();

        //获取excelPojo对象的getUrl属性
        String excelPojoUrl = excelPojo.getUrl();

        //获取excelPojo对象的getRequestHeader属性
        String excelPojoRequestHeader = excelPojo.getRequestHeader();
        Map<String,Object> requestHeaderMap = JSON.parseObject(excelPojoRequestHeader);

        //把期望响应结果转成Map
        String excelPojoExcepted = excelPojo.getExcepted();
        Map<String,Object> exceptedMap = JSON.parseObject(excelPojoExcepted);

        Response res =
            given().
                    body(excelPojoInputParams).          //用例也是json字符串，可以直接填入
                    headers(requestHeaderMap).       //使用 headers传入Map
            when().
                    post(excelPojoUrl).
            then().
                    log().all().
                    extract().response();

        //断言
        //获取map中的entrySet
        Set<Map.Entry<String, Object>> entrySet = exceptedMap.entrySet();
        for (Object obj:entrySet) {

             //此处返回带泛型的Map.Entry<String,Object>，键是String类型,值是Object类型
             //此处泛型定义结果影响getKey/getValue返回值类型
            Map.Entry<String,Object> entry = (Map.Entry) obj;

            //获取map里面的key
            String key = entry.getKey();
            //获取map里面的期望值exceptedvalue
            Object exceptvalue = entry.getValue();           //返回值类型泛型限制为Object
            //获取接口返回的实际值value（JSONPath）
            Object acturalvalue = res.jsonPath().get(key);           //此处key必须是字符串，get返回值没要求
            //断言,由于加了 RestAssured.config全局变量，此处哪怕是小数也可以正常做断言
            Assert.assertEquals(exceptvalue,acturalvalue);
        }

    }
    @DataProvider
    public Object[] getLoginDatas(){

        File file = new File("C:\\Users\\tao_c\\Desktop\\自动化工具软件&文档\\接口文档资料\\api_testcases_futureloan_v2.xls");
/*        //导入的参数对象
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(1);
        //读取Excel,表示当前集合中每一个元素都是ExcelPojo的对象
        List<ExcelPojo> listDatas = ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
        return listDatas.toArray();        //返回一个Object类型的数组，数组的每一个元素都是ExcelPojo的对象
*/
        List<ExcelPojo> ListDatas = readSpecifyExcelData(file,2,2,13);
        return ListDatas.toArray();
    }

    /**
     *读取Excel指定sheet里面的所有数据
     * @param file  文件对象
     * @param sheetNum  sheet编号
     */
     public List<ExcelPojo> readAllExcelData(File file,int sheetNum){
          //导入的参数对象
         ImportParams importParams = new ImportParams();
         //读取第二个sheet
         importParams.setStartSheetIndex(sheetNum-1);
         //读取Excel
         List<ExcelPojo> listDatas = ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
         return listDatas;

     }

    /**
     *
     * @param file  文件对象
     * @param sheetNum   sheet编号
     * @param startRow   起始行号
     * @param readRow    读入行号
     */
     public List<ExcelPojo> readSpecifyExcelData(File file,int sheetNum,int startRow, int readRow){

         ImportParams importParams = new ImportParams();
         //读取第二个sheet
         importParams.setStartSheetIndex(sheetNum-1);
         //设置读取的行数
         importParams.setStartRows(startRow-1);
         //设置读取的行数
         importParams.setReadRows(readRow);
         //读取Excel
         List<ExcelPojo> listDatas = ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
         return listDatas;
     }

 /*
    public static void main(String[] args) {


        File file = new File("C:\\Users\\tao_c\\Desktop\\自动化工具软件&文档\\接口文档资料\\api_testcases_futureloan_v1.xls");
        //导入的参数对象
        ImportParams importParams = new ImportParams();
        //设置读取的sheet
        importParams.setStartSheetIndex(0);
        //设置读取的起始行
        importParams.setStartRows(3);
        //设置读取的行数
        importParams.setReadRows(1);
        //读取Excel
        List<ExcelPojo> listDatas = ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
        for (ExcelPojo excelPojo:listDatas) {
            System.out.println(excelPojo);
        }

    }
*/

}
