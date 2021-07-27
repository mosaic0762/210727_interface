package zom.lemon.test.day02;


import api.lemon.test.ExcelPojo;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
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
 * @date 2021/7/20-19:10
 */
public class DataProviderDemo_02 {


    @Test(dataProvider = "TestDateProvider")
    public void testAssert(ExcelPojo excelPojo) {
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";

//        String json = "{\"mobile_phone\":\""+mobile_phone+"\",\"pwd\":\""+pwd+"\"}";
        //接口入参
        String inputParams = excelPojo.getInputParams();
        //接口地址
        String url = excelPojo.getUrl();
        //请求头
        String requestHeader = excelPojo.getRequestHeader();
        Map requestHeaderMap = (Map) JSON.parse(requestHeader);


        Response res =
            given().
                    body(inputParams).          //用例也是json字符串，可以直接填入
//                        header("Content-Type", "application/json").
//                        header("X-Lemonban-Media-Type", "lemonban.v2").
                    headers(requestHeaderMap).       //使用 headers传入Map
            when().
                    post(url).
            then().
                    log().all().
                    extract().response();

        //断言思路：
        //1、循环变量响应map，取到里面每一个key（实际上就是我们设计的jsonPath表达式）
        //2、通过res.jsonPath.get(key)取到实际的结果，再跟期望的结果做对比（key对应的value）


        //把期望响应结果转成Map
        String excepted = excelPojo.getExcepted();
        Map exceptedMap = (Map) JSON.parse(excepted);
       //获取map中的entrySet
        Set entrySet = exceptedMap.entrySet();
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

/*
        String excepted = excelPojo.getExcepted();   //获取json字符串
        //把期望响应结果转成Map，获取其key值
        //将json字符串转为map集合；影响get(key)返回值类型，定义键为String类型
        Map<String,Object> exceptedMap = (Map) JSON.parse(excepted);

//        写法一：返回一个字符串类型的key的Set集合（泛型）
//        Set<String> exceptedSet = exceptedMap.keySet();
//        for (String key:exceptedSet) {                   //String表示要遍历的元素的类型

        //写法二：自动识别返回的set集合类型为String
        for (String key:exceptedMap.keySet()) {

            //获取期望结果中map里面的期望值exceptedvalue
            Object exceptedvalue = exceptedMap.get(key);         //返回值类型泛型限制为Object；参数key为map中的，无要求
            //获取接口返回的实际值value（JSONPath）
            Object acturalvalue = res.jsonPath().get(key);       //此处key必须是字符串，get返回值没要求
            //断言,由于加了 RestAssured.config全局变量，此处哪怕是小数也可以正常做断言
            Assert.assertEquals(exceptedvalue,acturalvalue);
        }
*/

    }
    @DataProvider
    public Object[] TestDateProvider(){       //必须是一个Object类型

        File file = new File("C:\\Users\\tao_c\\Desktop\\自动化工具软件&文档\\接口文档资料\\api_testcases_futureloan_v1.xls");
        //导入的参数对象
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(1);
        //读取Excel
        List<ExcelPojo> listDatas =       //表示当前集合中每一个元素都是ExcelPojo的对象
                ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);

        Object[] objects = listDatas.toArray();

        return listDatas.toArray();        //返回一个Object类型的数组，数组的每一个元素都是ExcelPojo的对象
    }

//    public static void main(String[] args) {
//        File file = new File("C:\\Users\\tao_c\\Desktop\\api_testcases_futureloan_v1.xls");
//        //导入的参数对象
//        ImportParams importParams = new ImportParams();
//        importParams.setStartSheetIndex(0);
//        //读取Excel
//        List<Object> listDatas = ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
//        for (Object obj:listDatas) {
//            System.out.println(obj);
//        }
//    }
}
