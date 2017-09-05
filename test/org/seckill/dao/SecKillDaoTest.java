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
import org.seckill.entity.SecKill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SecKillDaoTest {
	
	//ע��Daoʵ��������
	@Resource
	private SecKillDao secKillDao;
	
	@Test
	public void testReduceNumber(){
		Date killTime=new Date();
		int updateCount=secKillDao.reduceNumber(1000L,killTime);
		System.out.println("updateCount"+updateCount);
	}
	
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
}
