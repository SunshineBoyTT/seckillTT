package org.seckill.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SecKill;

public interface SecKillDao {
	/**
	 * �����
	 * @param secKillId
	 * @param killTime
	 * @return
	 */
	int reduceNumber(@Param("secKillId") long secKillId,@Param("killTime") Date killTime);
	/**
	 * ����Id��ѯ
	 * @param secKillId
	 * @param killTime
	 * @return
	 */
	SecKill queryById(long secKillId);
	/**
	 * ��ɱ��Ʒ
	 * @param secKillId
	 * @param killTime
	 * @return
	 */
	List<SecKill> queryAll(@Param("offset") int offset,@Param("limit") int limit);
	
	/**
	 * 使用存储过程执行秒杀
	 * 
	 */
	void killByProdure(Map<String, Object> paramMap);
}
