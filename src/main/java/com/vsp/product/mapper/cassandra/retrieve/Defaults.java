package com.vsp.product.mapper.cassandra.retrieve;


import com.vsp.product.mapper.cassandra.retrieve.denormalizers.subDenormalizers.DefaultRetrieveDenormalizers;
import com.vsp.product.mapper.cassandra.retrieve.normalizers.subNormalizers.DefaultRetrieveNormalizers;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


@Component
public class Defaults implements ApplicationContextAware {

    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        ctx = applicationContext;
    }

    public static DefaultRetrieveDenormalizers getDefaultRetrieveDenormalizers() {
        return ctx.getBean(DefaultRetrieveDenormalizers.class);
    }

    public static DefaultRetrieveNormalizers getDefaultRetrieveNormalizers() {
        return ctx.getBean(DefaultRetrieveNormalizers.class);
    }



}
