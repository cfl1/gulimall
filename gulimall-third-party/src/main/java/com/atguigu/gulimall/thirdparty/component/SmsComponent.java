package com.atguigu.gulimall.thirdparty.component;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atguigu.common.utils.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: SmsComponent</p>
 * Description：
 * date：2020/6/25 14:23
 */
@Data
@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
@Component
public class SmsComponent {

	private String accessKeyId;

	private String secret;

	private String signName;

	private String templateCode;

	/**
	 * 发送短信验证码
	 * 示例： 您的验证码是：XXXXXX，10分钟内有效。如非本人操作请注意个人信息安全。
	 *
	 * @param phone 手机号
	 * @param code  验证码
	 * @return 发送结果
	 */
	public String sendCode(String phone, String code) {
		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, secret);
		IAcsClient client = new DefaultAcsClient(profile);

		CommonRequest request = new CommonRequest();
		request.setSysMethod(MethodType.POST);
		request.setSysDomain("dysmsapi.aliyuncs.com");
		request.setSysVersion("2017-05-25");
		request.setSysProduct("Dysmsapi");
		request.setSysAction("SendSms");
		request.putQueryParameter("RegionId", "cn-hangzhou");
		request.putQueryParameter("PhoneNumbers", phone);//接收短信的手机号码
		request.putQueryParameter("SignName", signName);//短信签名名称
		request.putQueryParameter("TemplateCode", templateCode);//短信模板CODE
		request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");//短信模板变量对应的实际值
		CommonResponse response = null;
		try {
			response = client.getCommonResponse(request);
			System.out.println(response.getData());//{"RequestId":"51EE15A9-063F-5341-B2EC-D80E6CF0E305","Message":"OK","BizId":"167118146737237878^0","Code":"OK"}
			return response.getData();
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}
		return "fail_" + response.getHttpStatus();
	}
	
	// public String sendSmsCode(String phone, String code){
	// 	String method = "GET";
	// 	Map<String, String> headers = new HashMap<String, String>();
	// 	//最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
	// 	headers.put("Authorization", "APPCODE " + this.appCode);
	// 	Map<String, String> querys = new HashMap<String, String>();
	// 	querys.put("code", code);
	// 	querys.put("phone", phone);
	// 	querys.put("skin", this.skin);
	// 	querys.put("sign", this.sign);
	// 	HttpResponse response = null;
	// 	try {
	// 		response = HttpUtils.doGet(this.host, this.path, method, headers, querys);
	// 		//获取response的body
	// 		if(response.getStatusLine().getStatusCode() == 200){
	// 			return EntityUtils.toString(response.getEntity());
	// 		}
	// 	} catch (Exception e) {
	// 		e.printStackTrace();
	// 	}
	// 	return "fail_" + response.getStatusLine().getStatusCode();
	// }
}
