package com.example.graduate_project.task;

import com.example.graduate_project.service.impl.FileServicesImpl;
import com.example.graduate_project.utiles.ConstantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private FileServicesImpl fileServices;

    @Scheduled(fixedRate = ConstantUtils.DELETE_TIME)
    public void task() {
        System.out.println("deleteAll");
        fileServices.deleteAll();
    }
}
