package com.atguigu.gulimall.auth;

import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.gulimall.auth.vo.UserRegisterVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallAuthServerApplicationTests {


    @Autowired
    private MemberFeignService memberFeignService;

    @Test
    void contextLoads() {
        UserRegisterVo userRegisterVo = new UserRegisterVo();
        userRegisterVo.setUserName("11");
        userRegisterVo.setPassword("11");
        userRegisterVo.setCode("11");
        userRegisterVo.setPhone("11");
        memberFeignService.register(userRegisterVo);
        System.out.println();
    }

}
