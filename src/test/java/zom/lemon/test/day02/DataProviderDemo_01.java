package zom.lemon.test.day02;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

/**
 * @author mosaic
 * @date 2021/7/20-18:54
 */
public class DataProviderDemo_01 {
    @Test(dataProvider = "TestDateProvider")
    public void testAssert(String mobile_phone,String pwd) {
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";

        String json = "{\"mobile_phone\":\""+mobile_phone+"\",\"pwd\":\""+pwd+"\"}";
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
    }
    @DataProvider
    public Object[][] TestDateProvider(){

        Object[][] datas = new Object[][]{
                {"15657150765","lemon666"},
                {"15657150705","leon66"},
                {"15657150265","lemon66"},
                {"15657150215","lemo666"}
        };
        return datas;
    }
}
