package tech.huit.uuc.service.dirty;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zlib.text.TextKit;

import java.util.*;

/*
 *类说明：关键字过滤器 
 */
public class KeyWordFilter {
    private static final Logger logger = LoggerFactory.getLogger(KeyWordFilter.class);

    private static String EM = "", SD = "*";

    private int machType = 1;

    private Map<Object, Object> match = new HashMap<Object, Object>();

    /**
     * 添加需要屏蔽的字
     *
     * @param ls
     */
    public void addMatch(List<String> ls) {

        String str[] = new String[ls.size()];
        ls.toArray(str);
        for (int i = 0; i < str.length; i++) {
            Map<Object, Object> newHash = match;
            String key = str[i].trim();
            int len = key.length();
            for (int j = 0; j < len; j++) {
                char word = key.charAt(j);
                Object obj = newHash.get(word);
                if (obj == null) {
                    Map<Object, Object> mp = new HashMap<Object, Object>();
                    mp.put("isEnd", 0);
                    newHash.put(word, mp);
                    newHash = mp;
                } else {
                    newHash = (Map<Object, Object>) obj;
                }
                if (j == len - 1) {
                    newHash.put("isEnd", 1);
                }

            }

        }

    }

    /**
     * 检测匹配 1 部分匹配  2 全匹配
     *
     * @param text
     * @param begin
     * @param type
     * @return
     */
    public int checkMatch(String text, int begin, int type) {
        int len = text.length();
        Map<Object, Object> newHash = match;
        int maxMath = 0;
        int res = 0;
        for (int i = begin; i < len; i++) {
            char word = text.charAt(i);
            Object obj = newHash.get(word);
            if (obj == null) {
                newHash = null;
                text = null;
                return maxMath;
            }
            res++;
            @SuppressWarnings("unchecked")
            Map<Object, Object> ne = (Map<Object, Object>) obj;
            newHash = ne;
            if ((Integer) ne.get("isEnd") == 1) {
                if (type == machType) {
                    newHash = null;
                    text = null;
                    return res;
                } else {
                    maxMath = res;
                }

            }
        }
        newHash = null;
        text = null;
        return maxMath;
    }

    /**
     * 抓取匹配
     */
    public Set<Object> getSubString(String text) {

        int len = text.length();
        Set<Object> set = new HashSet<Object>();
        for (int i = 0; i < len; i++) {
            int length = this.checkMatch(text, i, machType);
            if (length > 0) {

                set.add(text.substring(i, i + length));
            }

        }
        text = null;
        return set;


    }

    /**
     * 检测匹配
     *
     * @param key
     * @return
     */
    public boolean isHaveKey(String key) {
        int len = key.length();
        for (int i = 0; i < len; i++) {

            int le = this.checkMatch(key, i, machType);
            if (le > 0) {
                return true;
            }

        }
        return false;
    }

    public String getText(String text) {
        if(StringUtils.isEmpty(text)){
            return text;
        }
        Set<Object> set = this.getSubString(text);
        String str[] = new String[set.size()];
        set.toArray(str);
        String trim = text;
        for (int i = 0; i < str.length; i++) {
            trim = TextKit.replaceAll(text, str[i], getStar(str[i]));
        }
        if (null != trim && trim.length() != text.length()) {
            logger.debug("filterDirty->src:{} trim:{}", text, trim);
        }
        return trim;
    }

    /**
     * 获取星号个数
     *
     * @param str
     * @return
     */
    private String getStar(String str) {
        if (str == null) return EM;
        int n = str.length();
        str = EM;
        for (int i = 0; i < n; i++)
            str += SD;
        return str;
    }

    public static void main(String[] args) {
        KeyWordFilter filter = new KeyWordFilter();
        List<String> keywords = new ArrayList<String>();
        keywords.add("中国人");
        keywords.add("中国男人");
        filter.addMatch(keywords);
        String txt = "中国人民站起来了中国男人 中国人 ";
        boolean boo = filter.isHaveKey(txt);
        System.out.println(boo);
        System.out.println(filter.getText(txt));


    }

    public int getMachType() {
        return machType;
    }

    public void setMachType(int machType) {
        this.machType = machType;
    }
}
