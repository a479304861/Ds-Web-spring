package com.example.graduate_project.service.impl;

import com.example.graduate_project.dao.NamosunUserDao;
import com.example.graduate_project.dao.enity.NamoSunUser;
import com.example.graduate_project.dao.enity.ResponseResult;
import com.example.graduate_project.utiles.ConstantUtils;
import com.example.graduate_project.utiles.CookieUtils;
import com.example.graduate_project.utiles.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Transactional
@Service

public class UserServiceImpl extends BaseService {

    @Autowired
    private SnowflakeIdWorker idWorker;

    @Autowired
    private NamosunUserDao fileDao;


    public ResponseResult checkUser() {
        HttpServletRequest request = getRequest();
        HttpServletResponse response = getResponse();
        String cookie = CookieUtils.getCookie(request, ConstantUtils.NAMO_SUM_KEY);
        List<NamoSunUser> allById = null;
        if (cookie == null) {
            //没有cookie
            cookie = idWorker.nextId() + "";
            CookieUtils.setUpCookie(response, ConstantUtils.NAMO_SUM_KEY, cookie);
        } else {
            allById = fileDao.findAllByUserId(cookie);
        }
        //TODO：从数据库取出当前的序列
        return ResponseResult.SUCCESS().setData(allById);
    }
}
