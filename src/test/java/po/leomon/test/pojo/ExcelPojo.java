package po.leomon.test.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;

/**
 * @author mosaic
 * @date 2021/7/20-19:17
 */
public class ExcelPojo {      //一个ExcelPojo代表一行用例

    @Excel(name = "序号(caseId)")      //name值必须和excel中的保持一致包括小括号的中英文区别
    private int caseId;

    @Excel(name = "接口模块(interface)")
    private String interfacename;

    @Excel(name = "用例标题(title)")
    private String title;

    @Excel(name = "请求头(requestHeader)")
    private String requestHeader;

    @Excel(name = "请求方式(method)")
    private String method;

    @Excel(name = "接口地址(url)")
    private String url;

    @Excel(name = "参数输入(inputParams)")
    private String inputParams;

    @Excel(name = "期望返回结果(expected)")
    private String excepted;

    @Excel(name ="提取返回数据(extract)")
    private  String extract;

    public String getExtract() {
        return extract;
    }

    public void setExtract(String extract) {
        this.extract = extract;
    }

    public int getCaseId() {
        return caseId;
    }

    public String getInterfacename() {
        return interfacename;
    }

    public String getTitle() {
        return title;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getInputParams() {
        return inputParams;
    }

    public String getExcepted() {
        return excepted;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public void setInterfacename(String interfacename) {
        this.interfacename = interfacename;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setInputParams(String inputParams) {
        this.inputParams = inputParams;
    }

    public void setExcepted(String excepted) {
        this.excepted = excepted;
    }

    @Override
    public String toString() {
        return "ExcelPojo{" +
                "caseId=" + caseId +
                ", interfacename='" + interfacename + '\'' +
                ", title='" + title + '\'' +
                ", requestHeader='" + requestHeader + '\'' +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", inputParams='" + inputParams + '\'' +
                ", excepted='" + excepted + '\'' +
                ", extract='" + extract + '\'' +
                '}';
    }
}
