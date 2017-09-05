package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

public interface SuccessKilledDao {
	/**
	 * ���빺����ϸ,���ظ�����
	 * @param secKillId
	 * @param userPhone
	 * @return
	 */
	int insertSuccessKilled(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);
	/**
	 * ����id��ѯ��ѯ��ɱ��Ʒ����ʵ��
	 * @param secKillId
	 * @return
	 */
	SuccessKilled queryByIdWithSecKill(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);
}
