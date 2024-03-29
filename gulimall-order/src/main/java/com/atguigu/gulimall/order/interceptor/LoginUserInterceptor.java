package com.atguigu.gulimall.order.interceptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.vo.MemberRsepVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * <p>Title: LoginUserInterceptor</p>
 * Description：
 * date：2020/6/29 22:40
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

	public static ThreadLocal<MemberRsepVo> threadLocal = new ThreadLocal<>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		String uri = request.getRequestURI();
		// 这个请求直接放行
		AntPathMatcher antPathMatcher = new AntPathMatcher();
		boolean match = antPathMatcher.match("/order/order/status/**", uri);
		boolean match1 = antPathMatcher.match("/payed/notify", uri);
		if (match || match1) {
			return true;
		}
		HttpSession session = request.getSession();
		MemberRsepVo memberRsepVo = (MemberRsepVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
		if(memberRsepVo != null){
			threadLocal.set(memberRsepVo);
			return true;
		}else{
			// 没登陆就去登录
			session.setAttribute("msg", AuthServerConstant.NOT_LOGIN);
			response.sendRedirect("http://auth.gulimall.com/login.html");
			return false;
		}
	}
}
