package com.atguigu.gulimall.thirdparty.controller;

import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>Title: SmsSendController</p>
 * Description：
 * date：2020/6/25 14:53
 */
@Controller
@RequestMapping("/sms")
public class SmsSendController {

	@Autowired
	private SmsComponent smsComponent;

	/**
	 * 提供给别的服务进行调用的
	 */
	@GetMapping("/sendcode")
	public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code){
		if(!"fail".equals(smsComponent.sendCode(phone, code).split("_")[0])){
			return R.ok();
		}
		return R.error(BizCodeEnum.SMS_SEND_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_SEND_CODE_EXCEPTION.getMsg());
	}
}
