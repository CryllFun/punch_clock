package com.zerfu.demo.helper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author wuzf
 * @create 2020/7/17
 * @Description: 用来实例化对象
 */
@Component
public class ApplicationContextHelper implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHelper.applicationContext = applicationContext;
    }

    /**
     * 根据类名实例化对象
     * @param className
     * @return
     * @throws BeansException
     * @throws IllegalArgumentException
     */
    public static Object getBean(String className) throws BeansException,IllegalArgumentException {
        if(className==null || className.length()<=0) {
            throw new IllegalArgumentException("className为空");
        }

        String beanName = null;
        if(className.length() > 1) {
            beanName = className.substring(0, 1).toLowerCase() + className.substring(1);
        } else {
            beanName = className.toLowerCase();
        }
        return applicationContext != null ? applicationContext.getBean(beanName) : null;
    }
}