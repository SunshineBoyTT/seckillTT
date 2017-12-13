package org.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SecKillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.SecKill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

@Service
public class SeckillServiceImpl implements SeckillService {
	
	@Autowired
	private SecKillDao secKillDao;
	@Autowired
	private SuccessKilledDao successKilledDao;
	@Autowired
	RedisDao redisDao;
	
	private final String slat="safdfaegae8766632777*&%%34561";
	
	public List<SecKill> getSeckillList() {
		// TODO Auto-generated method stub
		return secKillDao.queryAll(0, 4);
	}

	public SecKill getById(long seckillId) {
		// TODO Auto-generated method stub
		return secKillDao.queryById(seckillId);
	}

	public Exposer exportSeckillUrl(long seckillId) {
		// TODO Auto-generated method stub
		//优化点:缓存优化
		/**
		 * 1.访问redis
		 */
		SecKill secKill=redisDao.getSecKill(seckillId);
		if (secKill==null) {
			//2.访问数据库
			secKill=secKillDao.queryById(seckillId);
			if (secKill==null) {
				return new Exposer(false, seckillId);
			}else{
				//放入redis
				redisDao.putSecKill(secKill);
			}
		}
		Date startTime=secKill.getStartTime();
		Date endTime=secKill.getEndTime();
		Date nowTime=new Date();
		if (nowTime.getTime()<startTime.getTime()||endTime.getTime()<nowTime.getTime()) {
			return new Exposer(false, seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
		}
		String md5=getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}
	
	private String getMD5(long seckillId){
		String base = seckillId+"/"+slat;
		String md5=DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}
	@Transactional
	/**
	 * 使用注解控制事务方法的优点
	 * 1.开发团队达成一致约定，明确标注事务方法的编程风格
	 * 2.保证事务方法的执行时间尽可能短，不要穿插其他的网路操作RPC/HTTP请求，或者剥离到事务方法外部。
	 * 3.不是所有的方法都需要事务，如只有一条修改操作。只读操作不需要事务控制。
	 */
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		// TODO Auto-generated method stub
		if (md5==null||!md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		//执行秒杀逻辑。减库存+记录购买行为
		Date nowTime=new Date();
		try{

            //否则更新了库存，秒杀成功,增加明细
            int insertCount=successKilledDao.insertSuccessKilled(seckillId,userPhone);
            //看是否该明细被重复插入，即用户是否重复秒杀
            if (insertCount<=0)
            {
                throw new RepeatKillException("seckill repeated");
            }else {

                //减库存,热点商品竞争
                int updateCount=secKillDao.reduceNumber(seckillId,nowTime);
                if (updateCount<=0)
                {
                    //没有更新库存记录，说明秒杀结束 rollback
                    throw new SeckillCloseException("seckill is closed");
                }else {
                    //秒杀成功,得到成功插入的明细记录,并返回成功秒杀的信息 commit
                    SuccessKilled successKilled=successKilledDao.queryByIdWithSecKill(seckillId,userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successKilled);
                }

            }


        }catch (SeckillCloseException e1)
        {
            throw e1;
        }catch (RepeatKillException e2)
        {
            throw e2;
        }catch (Exception e)
        {
            //所以编译期异常转化为运行期异常
            throw new SeckillException("seckill inner error :"+e.getMessage());
        }
	}

	public SeckillExecution executeSeckillByProdure(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		// TODO Auto-generated method stub
		if (md5==null||!md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		//执行秒杀逻辑。减库存+记录购买行为
		Date killTime=new Date();
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result",null);
		try {
			secKillDao.killByProdure(map);
			//获取result
			int result=MapUtils.getInteger(map, "result",-2);
			if(result==1) {
				SuccessKilled sk=successKilledDao.queryByIdWithSecKill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,sk);
			}else {
				return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
			}
		} catch (Exception e) {
			// TODO: handle exception
			return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
		}
	}

}
