--秒杀执行存储过程
DELIMITER $$ --console;转换为$$
--定义存储过程:in输入参数；out输出参数
--row_count():返回上一条修改类型sql得影响行数
--row_count:未修改数据>0,表示修改的行数;<0；sql错误/未执行修改sql
CREATE PROCEDURE `seckill`.`execute_seckill`  
    (in v_seckill_id bigint,in v_phone bigint,  
     in v_kill_time timestamp, out r_result int)  
     BEGIN  
         DECLARE insert_count int DEFAULT 0;  
         START TRANSACTION;  
         insert ignore into success_killed  
          (seckill_id,user_phone,create_time)  
         value (v_seckill_id,v_phone,v_kill_time);  
         select row_count() into insert_count;  
         IF (insert_count=0) THEN  
           ROLLBACK;  
           set r_result = -1;  
         ELSEIF(insert_count<0) THEN  
           ROLLBACK;  
           set r_result = -2;  
         ELSE  
           update seckill  
           set number = number-1  
           where seckill_id=v_seckill_id  
           and v_kill_time > start_time  
           and v_kill_time < end_time  
           and number > 0;  
           select row_count() into insert_count;  
           IF (insert_count = 0 ) THEN  
             ROLLBACK;  
             set r_result = 0;  
           ELSEIF (insert_count <0) THEN  
             ROLLBACK;  
             set r_result = -2;  
           ELSE  
             COMMIT;  
             set r_result = 1;  
           END IF;  
        END IF;  
     END;  
$$  

set @r_result=-3;
select @r_result
call execute_seckill(1003,13758291155,now(),@r_result);
	select @r_result;