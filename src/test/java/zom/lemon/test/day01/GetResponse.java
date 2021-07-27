package zom.lemon.test.day01;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * @author mosaic
 * @date 2021/7/20-10:47
 */
public class GetResponse {

    @Test
    public void getresponse() {

        String json = "{\"mobile_phone\":\"15657150762\",\"pwd\":\"lemon666\"}";
        Response res =
                given().
                        body(json).
//                      contentType("application/json").
                        header("Content-Type", "application/json").        //推荐使用此方法
                        header("X-Lemonban-Media-Type", "lemonban.v1").
                when().
                        post("http://api.lemonban.com/futureloan/member/login").
                then().
                        log().all().extract().response();
        System.out.println(res.jsonPath().get("data.id").toString());
    }


    @Test
    public void getresponse2() {

        String json = "{\"mobile_phone\":\"15657150762\",\"pwd\":\"lemon666\"}";
        Response res =
            given().

            when().
                    get("http://httpbin.org/json").
            then().
                    log().all().extract().response();

        List<String> list = res.jsonPath().getList("slideshow.slides.title");
        System.out.println(list);
        System.out.println(list.get(0));
        System.out.println(list.get(1));
    }

    @Test
    public void getresponse3() {

        String json = "{\"mobile_phone\":\"15657150762\",\"pwd\":\"lemon666\"}";
        Response res =
                given().

                when().
                        get("https://www.baidu.com/").
                then().
                        log().all().extract().response();

        Object obj = res.htmlPath().get("html.head.meta[0].@http-equiv");
        System.out.println(obj);

    }



    @Test
    public void loginRecharge() {

        String json = "{\"mobile_phone\":\"15657150762\",\"pwd\":\"lemon666\"}";
        Response res =
                given().
                        body(json).
                        header("Content-Type", "application/json").        //推荐使用此方法
                        header("X-Lemonban-Media-Type", "lemonban.v2").
                when().
                        post("http://api.lemonban.com/futureloan/member/login").
                then().
                        extract().response();
        //获取menber_id
        int member_id = res.jsonPath().get("data.id");
        System.out.println(member_id);
        //获取token
        String token = res.jsonPath().get("data.token_info.token");
        System.out.println(token);
        System.out.println("*******************************************");



        //发起“充值”接口请求
        //""中套“”情况 ：“前加\表示字符串；“”内加+号表示变量
        String jsonData = "{\"member_id\":"+member_id+",\"amount\":100000.00}";    //100000.00写成了100000：00

        Response res2 =
                given().
                        body(jsonData).
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v2").
                        header("Authorization","Bearer "+token).
                when().
                        post("http://api.lemonban.com/futureloan/member/recharge").
                then().
                        log().all().extract().response();
        System.out.println("当前可用余额:"+res2.jsonPath().get("data.leave_amount"));

    }
}
