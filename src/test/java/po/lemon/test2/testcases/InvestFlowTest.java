package po.lemon.test2.testcases;

import io.restassured.response.Response;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import po.lemon.test2.Util.PhoneRandomUtil;
import po.lemon.test2.common.BaseTest;
import po.lemon.test2.data.Environment;
import po.lemon.test2.pojo.ExcelPojo;

import java.util.List;


/**
 * @author mosaic
 * @date 2021/7/24-4:27
 */
public class InvestFlowTest extends BaseTest {

    @BeforeClass
    public void setup(){

        //生成三个没有被注册的手机号
        String borrower_phone = PhoneRandomUtil.getUnregisterPhone();
        String admin_phone = PhoneRandomUtil.getUnregisterPhone();
        String invest_phone = PhoneRandomUtil.getUnregisterPhone();

        //★★★将手机号保存到环境变量中
        Environment.envData.put("borrowerphone",borrower_phone);
        Environment.envData.put("adminphone",admin_phone);
        Environment.envData.put("investphone",invest_phone);

        //1、读取用例数1-9行
        List<ExcelPojo> ListDatas = readSpecifyExcelData(5,1,9);

        //投资前置用例
        //2、读取前1-9个ExcelPojo
        for (ExcelPojo excelPojo: ListDatas) {

            //3、环境变量替换入参
            excelPojo = caseReplace(excelPojo);       //这一行代码的位置比较重要；首次运行环境变量没有值，不进行正则替换

            //4、发起请求
            Response response = request(excelPojo, "Invest");

            //5、判断是否需要提取相应数据
            if (excelPojo.getExtract() != null) {
                extractToEnvironment(excelPojo, response);
            }
        }
    }

    @Test
    public void testInvest(){
        //1、读取第十行用例
        List<ExcelPojo> ListData = readSpecifyExcelData(5,10);

        //2、读取第10个ExcelPojo
        ExcelPojo excelPojo = ListData.get(0);

        //3、环境变量替换入参
        caseReplace(excelPojo);

        //4、发起投资请求
        Response invest = request(excelPojo, "Invest");

        //5、响应断言
        assertResponse(excelPojo,invest);

        //6、数据库断言
        assertSQL(excelPojo);
    }

    @AfterTest
    public void teardown(){

    }
}