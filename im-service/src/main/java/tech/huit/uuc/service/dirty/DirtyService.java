package tech.huit.uuc.service.dirty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;
import tech.huit.json.Json;
import tech.huit.util.encrypt.SignUtils;
import zmyth.excel.ExcelSample;
import zmyth.excel.ExcelUtil;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 类描述：　[基类的控制器]<br/>
 * 项目名称：[DirtyService]<br/>
 * 包名：　　[tech.huit.uuc.service.dirty]<br/>
 * 创建人：　[黄勇(yong.huang@gmail.com)]<br/>
 * 创建时间：[2018/03/22 ]<br/>
 */
@Service
public class DirtyService {
    private static final Logger logger = LoggerFactory.getLogger(DirtyService.class);
    KeyWordFilter filter = new KeyWordFilter();
    Properties props = new Properties();
    List keyWords = new ArrayList() ;
    public static final String  CACHE_DIRTY = "CACHE_DIRTY_SERVICE";

    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void init () {
        Resource resource = new ClassPathResource("dirty.properties");
        try {
            InputStream is = resource.getInputStream();
            try {
                BufferedReader bf = new BufferedReader(new InputStreamReader(is));
                props.load(bf);

            } finally {
                is.close();
            }
        } catch (IOException e) {
            logger.error("dirtyLoadError->", e);
        }
        Enumeration enumeration = props.propertyNames() ;
        while (enumeration.hasMoreElements()) {
           String key = (String) enumeration.nextElement();
            keyWords.add(props.get(key));
        }

        filter.addMatch(keyWords);
        String txt = "嫩中国人民站起来了中国男人 中国人嫩女 ";
        filter.setMachType(1);
        logger.info("testDirty->{}", filter.getText(txt));
        final byte[] keyByte = CACHE_DIRTY.getBytes();
        Long len = (Long) redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                connection.select(0);
                return connection.lLen(keyByte);
            }
        });
        for (int i = 0 ; i < len ; i ++ ) {
            byte[]  valByte =(byte[]) redisTemplate.execute(new RedisCallback<byte[]>() {
                @Override
                public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.select(0);
                    return connection.lPop(keyByte);
                }
            });
            if (valByte == null)
                continue;
            try {
                keyWords.add(new String(valByte,"utf-8"));
            } catch (Exception e) {

            }
        }
       // this.addKeyWord("操你妈卖逼");
    }

    public  void  addKeyWord (String word)  {
         synchronized (keyWords) {
             keyWords.add(word);

             final String keyf = (String) word;
             Object object = null;
             final byte[] keyByte = CACHE_DIRTY.getBytes();
             final byte[] valByte = keyf.getBytes();
             redisTemplate.execute(new RedisCallback<Object>() {
                 @Override
                 public Object doInRedis(RedisConnection connection) throws DataAccessException {
                     connection.select(0);
                     connection.lPush(keyByte,valByte);
                     return null;
                 }
             });

         }
    }

    public  String  filterText ( String txt) {
        return filter.getText(txt) ;
    }

}
