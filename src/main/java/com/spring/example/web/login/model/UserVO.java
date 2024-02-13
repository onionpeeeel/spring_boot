package com.spring.example.web.login.model;

import lombok.Data;

@Data
public class UserVO {

    private String userId;
    private String userNm;
    private String userPwd;
    private String roleId;
    private String roleNm;
}
