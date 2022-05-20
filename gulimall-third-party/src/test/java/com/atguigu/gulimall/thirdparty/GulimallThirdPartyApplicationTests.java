package com.atguigu.gulimall.thirdparty;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.gulimall.thirdparty.component.SmsComponent;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Test
    void contextLoads() {

    }

    @Resource
    OSSClient ossClient;

    @Autowired
    SmsComponent smsComponent;


    @Test
    public void testSmsComponent() {
        smsComponent.sendCode("18070516157", "666666");
    }

    /**
     * 发送短信验证码
     * 示例： 您的验证码是：XXXXXX，10分钟内有效。如非本人操作请注意个人信息安全。
     *
     * @return 发送结果
     */
    @Test
    public void sendAli() {

        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "NOlCriukaUkvlm8X", "zpXIJIbEVO7rWuUu8rEMbImu0srNeV");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysProduct("Dysmsapi");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", "15179204125");
        request.putQueryParameter("SignName", "同行者");
        request.putQueryParameter("TemplateCode", "SMS_107070058");
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + 666666 + "\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());//{"RequestId":"51EE15A9-063F-5341-B2EC-D80E6CF0E305","Message":"OK","BizId":"167118146737237878^0","Code":"OK"}
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendSms() {
        String host = "https://fesms.market.alicloudapi.com";
        String path = "/sms/";
        String method = "GET";
        String appcode = "541707ddc9c8463eb9336c3bfc0624b3";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("code", "666666");
        querys.put("phone", "18070516157");
        querys.put("skin", "1");
        querys.put("sign", "1");
        //JDK 1.8示例代码请在这里下载：  http://code.fegine.com/Tools.zip
        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            //System.out.println(response.toString());如不输出json, 请打开这行代码，打印调试头部状态码。
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpload() throws FileNotFoundException {
      /*   改用spring-cloud-starter-alicloud-oss，这些步骤可以不写了
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "oss-cn-hangzhou.aliyuncs.com";
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = "LTAI5tRi6o9D4LmB5CcQitUN";
        String accessKeySecret = "in4q01WU8Ex6DYN5cuQQPPWX3FKyw5";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);*/

        // 上传文件流。
        InputStream inputStream = new FileInputStream("E:\\文件\\素材\\壁纸\\壁纸100张\\壁纸\\bailu.jpg");

        ossClient.putObject("gulimall-chenfl", "bailu.jpg", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();

        System.out.println("上传完成...");
    }
}
