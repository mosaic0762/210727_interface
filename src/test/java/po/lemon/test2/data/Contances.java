package po.lemon.test2.data;

/**
 * @author mosaic
 * @date 2021/7/22-17:51
 */
public class Contances {

    //日志输出配置：控制台(false) or 日志文件中(true)
    public static final boolean LOG_TO_FILE=true;

    //Excel文件的路径
    public static final String EXCEL_FILE_PATH = "src/test/resources/api_testcases_futureloan_v4.xls";

    //接口BaseUrl地址
    public static final String BASE_URI= "http://api.lemonban.com/futureloan";

    //数据库baseuri
    public static final String DB_BASE_URI="api.lemonban.com";

    //数据库名
    public static final String DB_NAME="futureloan";
    public static final String DB_USERNAME="future";
    public static final String DB_PWD="123456";
}
