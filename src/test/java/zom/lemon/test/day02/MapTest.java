package zom.lemon.test.day02;

import api.lemon.test.ExcelPojo;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

import static io.restassured.RestAssured.given;

/**
 * @author mosaic
 * @date 2021/7/21-10:48
 */
public class MapTest  {

    @Test(dataProvider = "TestDateProvider")
    public void testMap(ExcelPojo excelPojo){

        //把响应结果转成Map
        String excepted = excelPojo.getExcepted();
        Map exceptedMap = (Map) JSON.parse(excepted);

        //Map中的方法
/*
        value()：Map获取value的list集合
          遍历list获取value单个的值

        keySet()：Map获取key的set集合，
          遍历set获取key单个的值
          get(key)：遍历set获取value单个的值

        entrySet()：Map获取entrySet的set集合
        getKey()&getValue() ：遍历获取entry的键和值
*/

          //获取map中的key和value方式一
/*
       //方式一 转化为entrySet
        Set entrySet = exceptedMap.entrySet();
           //for循环
        for (Object obj:entrySet) {
            Map.Entry entry = (Map.Entry) obj;
            System.out.println(entry.getKey() + "===>" + entry.getValue());
        }

          //iterator
        Iterator iterator = entrySet.iterator();
        while (iterator.hasNext()){
            Object obj = iterator.next();
            //entrySet集合中的元素都是entry
            Map.Entry entry1 = (Map.Entry) obj;
            System.out.println(entry1.getKey() + "===>" + entry1.getValue());

        }
*/

          //获取map中的key和value方式二
/*
         //方式二 ：先取key，再取value
        Set keySet = exceptedMap.keySet();
            //for循环
        for (Object key:keySet) {
            Object value = exceptedMap.get(key);
            System.out.println(key + "===>" + value);

            //iterator
        Iterator iterator = keySet.iterator();
        while (iterator.hasNext()){
            Object key1 = iterator.next();
            Object value1 = exceptedMap.get(key1);
            System.out.println(key1 + "===>" + value1);
        }
*/
          //遍历所有的value
        Collection values = exceptedMap.values();
        Iterator iterator = values.iterator();
        while (iterator.hasNext()){
            Object obj = iterator.next();
            System.out.println(obj);
                   }

    }

    @DataProvider
    public Object[] TestDateProvider(){       //必须是一个Object类型

        File file = new File("C:\\Users\\tao_c\\Desktop\\api_testcases_futureloan_v1.xls");
        //导入的参数对象
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(0);
        //读取Excel
        List<ExcelPojo> listDatas =       //表示当前集合中每一个元素都是ExcelPojo的对象
                ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
        return listDatas.toArray();        //返回一个Object类型的数组，数组的每一个元素都是ExcelPojo的对象
    }
}
