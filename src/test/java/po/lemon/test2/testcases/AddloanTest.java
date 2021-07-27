package po.lemon.test2.testcases;

import com.alibaba.fastjson.JSON;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import po.lemon.test2.Util.PhoneRandomUtil;
import po.lemon.test2.common.BaseTest;
import po.lemon.test2.data.Environment;
import po.lemon.test2.pojo.ExcelPojo;

import java.util.List;
import java.util.Map;

public class AddloanTest extends BaseTest {
    @BeforeClass
    public void setup(){
        //1、生成两个角色的随机手机号码（借款人+管理员）
        String borrowserPhone = PhoneRandomUtil.getUnregisterPhone();
        String adminPhone = PhoneRandomUtil.getUnregisterPhone();
        //2、将手机号保存到环境变量中
        Environment.envData.put("borrower_phone",borrowserPhone);
        Environment.envData.put("admin_phone",adminPhone);

        //3、读取用例数前4行
        List<ExcelPojo> list = readSpecifyExcelData(4,1,4);
        //投资前置用例
        //4、读取前4个ExcelPojo
        for (ExcelPojo excelPojo: list){
            //5、环境变量替换入参
            excelPojo = caseReplace(excelPojo);
            //6、发起请求
            Response response = request(excelPojo,"AddLoan");
            //7、判断是否需要提取相应数据
            if(excelPojo.getExtract() != null){
                extractToEnvironment(excelPojo,response);
            }
        }
    }

    @Test(dataProvider = "getAddLoanDatas")
    public void testAddLoan(ExcelPojo excelPojo){

        //参数替换
        excelPojo = caseReplace(excelPojo);

        //执行【加标】接口请求
        Response resAddLoan = request(excelPojo,"AddLoan");

        //断言
        assertResponse(excelPojo,resAddLoan);
    }

    @DataProvider
    public Object[] getAddLoanDatas(){
        List<ExcelPojo> listDatas = readSpecifyExcelData(4,5);
        //把集合转换为一个一维数组
        return listDatas.toArray();
    }

    @AfterTest
    public void teardown(){

    }
}
