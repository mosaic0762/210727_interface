package zom.lemon.test.day02;

import io.restassured.RestAssured;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

/**
 * @author mosaic
 * @date 2021/7/20-16:51
 */
public class AssertDemo {

    int member_id;
    String token;

    @Test
    public void testLoginRecharge(){
        RestAssured.config=RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";

        String json = "{\"mobile_phone\":\"15657150765\",\"pwd\":\"lemon666\"}";
        Response res =
            given().
                    body(json).
                    header("Content-Type", "application/json").
                    header("X-Lemonban-Media-Type", "lemonban.v2").
            when().
                    post("/member/login").
            then().
                    log().all().
                    extract().response();
        //1、响应结果断言
            //整数类型
        int code = res.jsonPath().get("code");
        Assert.assertEquals(code,0);
        //2、字符串类型断言
        String msg = res.jsonPath().get("msg");
        Assert.assertEquals(msg,"OK");
        //3、小数类型断言使用BigDecimal
//        BigDecimal leave_amount = res.jsonPath().get("data.leave_amount");
//        BigDecimal excepted = BigDecimal.valueOf(930000.17);

        //配置了RestAssured.config，此处接收类型可以是Object
        Object leave_amount2 = res.jsonPath().get("data.leave_amount");
        Object excepted2 = BigDecimal.valueOf(930000.17);
        Assert.assertEquals(leave_amount2,excepted2);


        //2、数据库断言
        //获取menber_id
        member_id = res.jsonPath().get("data.id");
        //获取token
        token = res.jsonPath().get("data.token_info.token");

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
                    post("/member/recharge").
            then().
                    log().all().extract().response();

        //小数类型断言使用BigDecimal
        BigDecimal leave_amount1 = res2.jsonPath().get("data.leave_amount");
        BigDecimal excepted1 = BigDecimal.valueOf(1030000.17);
        Assert.assertEquals(leave_amount1,excepted1);

    }
}
