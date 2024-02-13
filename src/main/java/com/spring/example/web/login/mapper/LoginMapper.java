package com.spring.example.web.login.mapper;

import com.spring.example.web.login.model.LoginVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginMapper {

    LoginVO loginInfo(String userId);
}
