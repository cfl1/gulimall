package com.atguigu.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000119649811";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCNbGHl9nkRvLvibJboQZtYkVlMKFLWy4e2PCipxWmteLOJbBi/UnyZCqsx9okJ4jkoK0qzInBNxgZK1OhPF40AH53KiheNPfm6L40UmEBLR+uVwTmvSfYdi1SZbcNccrnB0+9K00jA78gnXQ+l4Zx/3VqnLNWsQMOhnxRXR1GXjIAGznQXUFs6k/uP/8/p+lSzaRAF+pRNQgMWFoa5RWAOEXiJ1C6M9DOmsjl1kpNmB0GQsmE0I+0cGEvlHNH+mSNtCCVimqXBdFSAs1zFEpFuKgwlAwv7v1ebFHkZ0lQCPMiidOR4w0cM0v4LRz2h2Km2PPbD98O+Xlle8Kv+lltRAgMBAAECggEAONHx524GqJy2tDtCymAJpSUIxrNwzQILLamwAL3jPqI/KBh5PO1MSETkEXfnCUSOCFiUc7KDgovsXUZ7a8OfkAwj3otq+QGMg0S1ny+orVZhDdMLdST3MFmZjOdv1YX2oiyfeOW41mPAfChEsDYcJbduw7ocw09mClqa1pI98vAUMuUFqCwMIFJ5KncDr/S9UuAon2o3zra45LPbHVHixPr9G2MCdaYlKF47PvWVxfE5K16bUydBwy1TKtEIJx8XBbADTkYamcetf9F7VbwO58N6b7W7AD0ftGLM+J6HFIxnQ/gmrdZnLoFYByQk0iA9ogcxe2Aphv0OPizknZ9o/QKBgQDHe4J0rdFMlWthBDF5ucH987SCTqHkh0bin58WdEjN+hUFy6YDqh9WwGLfwyf1ajERkKp49P+QllrRNip1reyHlb7mu2motndhIxv7FTIuBOU/ehMYG6nALGhpZxFs2+1fqcYIIZvY6nPq5DQhVAYFRLaOc3h10APf2+kkrsQdywKBgQC1fdbFBIDLa8fwZTTWl5WS4rECgW17CjKgP+WzrEvu/IdDdZUKoWnXPvbt2D92SrbhXpVNgfBY9bZDCnOrJwVuV6Z9/dpJFTGiLcvaDsXZh7cr73z6hBmFGcV43vJVnVYrPYrpJ5diG8tAJKH2PP4nALBTTv5n8lp2cnETpjvH0wKBgQCv732yfK7w9YO8EB+2tR+kY/GbT9C3JpxdOdzsx+iCSgOKlf84ki2LrKhHo+61I1BdppLDlYllM0ar6DLwkVHgjzDMfRSGKVAGyxldjGcycTzjjD4U8Cj6BfCcrbuPoDSuP6UZ/NJgMP/FdjmH/3xD30OHRc5DUhBouqLYqI7tmwKBgBf2dn3q20qAf5SY+EIBH6mWLsLi7d6Z+Rc5eI2hiSETzJQ2mSgfKcdWs+mxbKeks8LL5zCloGNOhfMSFTWa4FeyUPQ/qSuPFc+Q9mJjIvJWvfOaGklFWc0+Gtmbrilpra/l30prhMxaWRqt+4h0gyYJLD7Dpkl5MUKEV7O9dhUrAoGAKUoPEdCjtIykIZlUZ4RSHd0XPER5cDEpeUNv4L3yg0/Zg9trJSebVDwMXr+RbN0wEPjI9KlF1vvf1kJGsZ0nR70gUWU7kwQ79/WPp7Po+3RF+ToDShQv8id/XP4Y/ENKCkA6GpJLgGdxU0keUNGP1VUW4tKc44uAe5Sgta5BBWU=";
	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjlcmPkE9zOrZJbfjqVjWsIiz249Rg+uv36u2RLE9GMV+oFi15y5Y4IEOLXLwD1PTLE0X/vRPMSRL1iHqcf+XD91QNgtMvNt8eJGiBtTSp9x9BkTGpxuTPSJAWwtyYA2lMgH51XUq+DEjvAYGi1SdXBg4pHdUvXxbrXuhiMV2wnKuyaqJmOngh2YZ8521HwV4hCfjkL8H/k0pD2hyQ0P2VgNp0NjPXaWXtFL1srQLymt6YbKaWkMpe9UdiEHGIl6sB7ujUBsRTnUuzs1TLVLr48A0YuKQIIw1xggJKQrR3pchWLXVNVil4BRAnquy7Ksux0vekKxLdi6IXFd4BMDKyQIDAQAB";
	// 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "https://ckxs3luybf.51xd.pub/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url = "http://member.gulimall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 自动关单时间
    private String timeout = "15m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        // 30分钟内不付款就会自动关单
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\"" + timeout + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        return result;
    }
}
