package com.example.graduate_project.service.impl;

import com.example.graduate_project.dao.NamosunUserDao;
import com.example.graduate_project.dao.SettingDao;
import com.example.graduate_project.dao.enity.CheckUserResult;
import com.example.graduate_project.dao.enity.NamoSunUser;
import com.example.graduate_project.dao.enity.ResponseResult;
import com.example.graduate_project.dao.enity.Settings;
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

    @Autowired
    private SettingDao settingDao;

    /**
     * 验证用户cookie看是否登入过，如果没有，生成一个id给用户
     *
     * @return
     */
    public ResponseResult checkUser() {
        HttpServletRequest request = getRequest();
        HttpServletResponse response = getResponse();
        String cookie = CookieUtils.getCookie(request, ConstantUtils.NAMO_SUM_KEY);
        List<NamoSunUser> allById = null;
        //没有cookie
        Settings oneByKey = settingDao.findOneById(ConstantUtils.Setting.WEB_VIEW_COUNT);
        int viewCount = Integer.parseInt(oneByKey.getValue());
        viewCount++;
        settingDao.updateViewCount();
        if (cookie == null) {
            cookie = idWorker.nextId() + "";
            CookieUtils.setUpCookie(response, ConstantUtils.NAMO_SUM_KEY, cookie);
        } else {
            allById = fileDao.findAllByUserId(cookie);
        }
        return ResponseResult.SUCCESS().setData(new CheckUserResult(allById, viewCount));
    }
}
