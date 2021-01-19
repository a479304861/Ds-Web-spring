package com.example.graduate_project.task;

import com.example.graduate_project.service.impl.DsWebServicesImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private DsWebServicesImpl fileServices;

//    @Scheduled(fixedRate = ConstantUtils.DELETE_TIME)
//    public void task() {
//        System.out.println("deleteAll");
//        fileServices.deleteAll();
//    }
}
