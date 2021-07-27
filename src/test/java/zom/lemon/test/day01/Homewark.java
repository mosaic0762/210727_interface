package zom.lemon.test.day01;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * @author mosaic
 * @date 2021/7/20-15:36
 */
public class Homewark {

    //全局变量
    String mobilephone = "15657150705";
    String pwd = "lemon666";
    int type = 1;
    int member_id;
    String token;


//    @Test(priority = 1)      //priority优先级，值越小优先级越大
    @Test
    public void Register() {

        String json = "{\"mobile_phone\":\""+mobilephone+"\",\"pwd\":\""+pwd+"\",\"type\":"+type+"}";
        Response res =
                given().
                        body(json).
                        header("Content-Type", "application/json").
                        header("X-Lemonban-Media-Type", "lemonban.v2").
                when().
                        post("http://api.lemonban.com/futureloan/member/register").
                then().
                        log().all().extract().response();
    }

//    @Test(priority = 2)                      //priority优先级，值越小优先级越大
    @Test(dependsOnMethods = "Register")      //dependsOnMethods上一个方法执行结束后才执行
    public void Login() {

        String json = "{\"mobile_phone\":\""+mobilephone+"\",\"pwd\":\""+pwd+"\"}";
        Response res =
                given().
                        body(json).
                        header("Content-Type", "application/json").
                        header("X-Lemonban-Media-Type", "lemonban.v2").
                when().
                        post("http://api.lemonban.com/futureloan/member/login").
                then().
                        extract().response();
        //获取menber_id
        member_id = res.jsonPath().get("data.id");
        System.out.println(res.jsonPath().get("data.id").toString());
        //获取token
        token = res.jsonPath().get("data.token_info.token");
        System.out.println(res.jsonPath().get("data.token_info.token").toString());
    }


//    @Test(priority = 3)
    @Test(dependsOnMethods = "Login")
        public void Recharge() {
        //发起“充值”接口请求
        //""中套“”情况 ：“前加\表示字符串；“”内加+号表示变量
        String jsonData = "{\"member_id\":"+member_id+",\"amount\":10000.02}";    //100000.00写成了100000：00
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