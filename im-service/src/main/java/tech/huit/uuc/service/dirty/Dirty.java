package tech.huit.uuc.service.dirty;


import zmyth.excel.ExcelSample;
import zmyth.excel.ExcelSampleFactory;

/**
 * 类描述：　[基类的控制器]<br/>
 * 项目名称：[Dirty]<br/>
 * 包名：　　[tech.huit.entity]<br/>
 * 创建人：　[黄勇(yong.huang@gmail.com)]<br/>
 * 创建时间：[2018/03/22 ]<br/>
 */
public class Dirty extends ExcelSample {

    public static ExcelSampleFactory factory = new ExcelSampleFactory("id");

    private int id;

    private String words;

    public long getId() {
        return id;
    }

    @Override
    public ExcelSampleFactory getFactory() {
        return factory;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }
}
