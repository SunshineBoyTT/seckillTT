package org.seckill.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
/**
 * ����spring��junit����,junit����ʱ����spring����
 * @author Administrator
 *
 */
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.SecKill;
import org.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SecKillDaoTest {
	
	//ע��Daoʵ��������
	@Resource
	private SecKillDao secKillDao;
	
	@Autowired
	private SeckillService seckillService;
//	@Test
//	public void testReduceNumber(){
//		Date killTime=new Date();
//		int updateCount=secKillDao.reduceNumber(1000L,killTime);
//		System.out.println("updateCount"+updateCount);
//	}
	
//	@Test
//	public void testQueryById(){
//		long id=1000;
//		SecKill secKill=secKillDao.queryById(id);
//		System.out.println(secKill.getName());
//	}
	
//	@Test
//	public void testQueryAll(){
//		List<SecKill> list=secKillDao.queryAll(0, 100);
//		list.forEach(c->System.out.println(c.getName()));
//	}
	
	@Test
	public void executeSeckillProdure(){
		long seckillId=1001;
		long phone = 13758246589L;
		Exposer exposer= seckillService.exportSeckillUrl(seckillId);
		if (exposer.isExposed()) {
			String md5=exposer.getMd5();
			System.err.println(md5);
			SeckillExecution execution=seckillService.executeSeckillByProdure(seckillId, phone, md5);
			System.err.println(execution.getStateInfo());
		}
	}
}
