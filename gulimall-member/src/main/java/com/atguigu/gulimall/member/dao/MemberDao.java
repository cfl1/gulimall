package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author chenfl
 * @email 1571539116@qq.com
 * @date 2019-10-08 09:47:05
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
