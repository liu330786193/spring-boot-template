package com.lyl.core.enable;

import io.lettuce.core.RedisClient;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * @Author lyl
 * @Description
 * @Date 2018/10/7 下午5:22
 */
public class RedisTemplate {

    @Autowired
    private RedisClient redisClient;

    //连接池采用默认连接 8个连接
    private GenericObjectPool<StatefulRedisConnection<String, String>> pool = ConnectionPoolSupport.createGenericObjectPool(() -> redisClient.connect(), new GenericObjectPoolConfig());


    public boolean set(String key, String value, long seconds){
        try (StatefulRedisConnection connection = pool.borrowObject()){
            String result = connection.sync().set(key, value, SetArgs.Builder.ex(seconds).nx());
            if (Objects.equals(result, "OK")){
                return true;
            }
            return false;
        } catch (Exception ex){
            return false;
        }
    }

    public boolean delete(String key){
        try (StatefulRedisConnection connection = pool.borrowObject()){
            Long result = connection.sync().del(key);
            if (result == 1){
                return true;
            }
            return false;
        } catch (Exception ex){
            return false;
        }
    }


}
