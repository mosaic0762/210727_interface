package zom.lemon.test.day01;

import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;

/**
 * @author mosaic
 * @date 2021/7/19-20:57
 */
public class RestAssuredDemo {

    @Test
    public void firstGetRequest() {
        given().

                when().
                get("https://www.baidu.com/").
                then().
                log().body();


    }

    @Test
    public void getDemo() {
        given().

        when().
                get("http://httpbin.org/get").
        then().
                log().all();
    }

    @Test
    public void getDemo1() {              //get请求添加参数使用queryParam
        given().
                queryParam("mobilepuone", "15657150762").
                queryParam("pwd", "123456").
        when().
                get("http://httpbin.org/get").
        then().
                log().body();
    }

    @Test
    public void postDemo() {           //post请求方式，form格式
        given().         ////需要设置请求体格式  "Content-Type": "application/x-www-form-urlencoded"
                formParam("mobilepuone", "15657150762").
                formParam("pwd", "123456").
                contentType("application/x-www-form-urlencoded").

        when().
                post("http://httpbin.org/post").
        then().
                log().body();
    }

    @Test
    public void postDemo2() {                //post请求方式，json格式
        String JsonData = "{\"mobile_phone\": \"13323234545\",\"pwd\": \"123456\"}";

        given().                             //需要设置请求体格式
                body(JsonData).
//                contentType(ContentType.JSON).
                contentType("application/json").
        when().
                post("http://httpbin.org/post").
        then().
                log().body();
    }

    @Test
    public void postDemo3() {                //post请求方式，XML格式
        String xmlData="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<suite>\n" +
                "    <class>测试xml</class>\n" +
                "</suite>";

        given().                             //需要设置请求体格式
                body(xmlData).
                contentType("application/xml").
        when().
                post("http://httpbin.org/post").
        then().
                log().body();
    }


    @Test
    public void postDemo4() {                //post请求方式，多参数表单

        given().
                multiPart(new File("C:\\Users\\tao_c\\Desktop\\123.txt")).
        when().
                post("http://httpbin.org/post").
        then().
                log().body();
    }

}
