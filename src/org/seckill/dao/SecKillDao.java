package org.seckill.dao;

import java.util.Date;
import java.util.List;

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
}
