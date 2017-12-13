package org.seckill.service;
/**
 * 业务接口:站在使用者角度设计接口
 * 三个方面：方法定义粒度，参数，返回类型（return 类型/异常）
 * @author Administrator
 *
 */

import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.SecKill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

public interface SeckillService {
	/**
	 * 查询所有秒杀记录
	 */
	List<SecKill> getSeckillList();
	
	/**
	 * 查询单个秒杀记录
	 */
	SecKill getById(long seckillId);
	
	/**
	 * 秒杀开启输出秒杀接口地址
	 * 否则属于系统时间和秒杀时间
	 */
	Exposer exportSeckillUrl(long seckillId);
	
	/**
	 * 执行秒杀结果
	 */
	SeckillExecution executeSeckill(long seckillId,long userPhone,String md5) throws SeckillException,RepeatKillException,SeckillCloseException;
	
	/**
	 * 执行秒杀结果 通过存储过程
	 */
	SeckillExecution executeSeckillByProdure(long seckillId,long userPhone,String md5) throws SeckillException,RepeatKillException,SeckillCloseException;
}
