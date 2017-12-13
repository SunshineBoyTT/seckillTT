package org.seckill.dao.cache;

import java.io.Serializable;

import org.junit.experimental.theories.Theories;
import org.seckill.entity.SecKill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {
	private final Logger logger=LoggerFactory.getLogger(RedisDao.class);
	private final JedisPool jedisPool;
	public RedisDao(String ip,int port){
		jedisPool = new JedisPool(ip, port);
	}
	
	private RuntimeSchema<SecKill> schema=RuntimeSchema.createFrom(SecKill.class);
	public SecKill getSecKill(long seckillId){
		//redis操作逻辑
		Jedis jedis=jedisPool.getResource();
		try {
			String key="seckill:"+seckillId;
			//这里需要序列化，思考一下需要序列化的好处
			//get-byte-反序列化-object(seckill)
			//采用自定义序列化
			//protostuff:pojo 用此序列化比原生 Serializable 空間降至到10分之一，速度也大幅度提升
			byte[] bytes=jedis.get(key.getBytes());
			if (bytes!=null) {
				//空對象
				SecKill secKill=schema.newMessage();
				ProtostuffIOUtil.mergeFrom(bytes, secKill, schema);//反序列化
				//seckill被反序列
				return secKill;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(),e);
		}finally {
			jedis.close();
		}
		return null;
	}
	
	public String putSecKill(SecKill secKill){
		try {
			Jedis jedis=jedisPool.getResource();
			String key="seckill:"+secKill.getSecKillId();
			byte[] bytes = ProtostuffIOUtil.toByteArray(secKill,schema,LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
			//超时缓存 缓存1小时
			int timeout=60*60;
			String result=jedis.setex(key.getBytes(), timeout, bytes);
			return result;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(),e);
		}
		return null;
	}
}
