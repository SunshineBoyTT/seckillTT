package org.seckill.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.jdbc.Null;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.SecKill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/seckill")//url:/模块/资源/{id}/细分/seckill/list
public class SeckillController {
	
	@Autowired
	private SeckillService seckillService;
	
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public String list(Model model){
		//获取列表页
		List<SecKill> list=seckillService.getSeckillList();
		model.addAttribute("list", list);
		return "list";
	}
	
	@RequestMapping(value="/{seckillId}/detail",method=RequestMethod.GET)
	public String detail(@PathVariable("seckillId")Long seckillId,Model model){
		if (seckillId==null) {
			return "redirect:/seckill/list";
		}
		SecKill secKill=seckillService.getById(seckillId);
		if (secKill==null) {
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill",secKill);
		return "detail";
	}
	
	@RequestMapping(value="/{seckill}/exposer",produces="application/json;charset=UTF-8")
	@ResponseBody
	public SeckillResult<Exposer> exposer(Long seckillId){
		SeckillResult<Exposer> result = null;
		try {
			Exposer exposer=seckillService.exportSeckillUrl(seckillId);
			result=new SeckillResult<Exposer>(true, exposer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result=new SeckillResult<Exposer>(false, e.getMessage());
		}
		return result;
	}
	
	 @RequestMapping(value = "/{seckillId}/{md5}/execution",
	            method = RequestMethod.POST,
	            produces = {"application/json;charset=UTF-8"})
	    @ResponseBody
	    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
	                                                   @PathVariable("md5") String md5,
	                                                   @CookieValue(value = "userPhone",required = false) Long userPhone)
	    {
	        if (userPhone==null)
	        {
	            return new SeckillResult<SeckillExecution>(false,"未注册");
	        }
	        SeckillResult<SeckillExecution> result;

	        try {
	            SeckillExecution execution = seckillService.executeSeckill(seckillId, userPhone, md5);
	            return new SeckillResult<SeckillExecution>(true, execution);
	        }catch (RepeatKillException e1)
	        {
	            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
	            return new SeckillResult<SeckillExecution>(true,execution);
	        }catch (SeckillCloseException e2)
	        {
	            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.END);
	            return new SeckillResult<SeckillExecution>(true,execution);
	        }
	        catch (Exception e)
	        {
	            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
	            return new SeckillResult<SeckillExecution>(true,execution);
	        }

	    }

	    //获取系统时间
	    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
	    @ResponseBody
	    public SeckillResult<Long> time()
	    {
	        Date now=new Date();
	        return new SeckillResult<Long>(true,now.getTime());
	    }
	    
	  //设置Cookie
//	    @RequestMapping(value = "/setCookie")
//	    public String setCookie(HttpServletRequest request,HttpServletResponse response,String userPhone,Long seckillId,Model model)
//	    {	
//	    	if (seckillId==null) {
//				return "redirect:/seckill/list";
//			}
//			SecKill secKill=seckillService.getById(seckillId);
//			if (secKill==null) {
//				return "forward:/seckill/list";
//			}
//			model.addAttribute("seckill",secKill);
//	    	Cookie cookie = new Cookie("userPhone",userPhone);
//	    	cookie.setDomain("localhost");
//	    	cookie.setPath("/seckill");
//	    	cookie.setMaxAge(3600*7);
//	    	response.addCookie(cookie);
//	    	return "/seckill/"+seckillId+"/detail";
//	    }
	    @RequestMapping(value = "/setCookie")
	    public Map setCookie(HttpServletRequest request,HttpServletResponse response,String userPhone)
	    {	
	    	Map map=new HashMap();	
	    	Cookie cookie = new Cookie("userPhone",userPhone);
	    	cookie.setDomain("localhost");
	    	cookie.setPath("/seckill");
	    	cookie.setMaxAge(3600*7);
	    	response.addCookie(cookie);
	    	return map;
	    }
}
