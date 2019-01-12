package org.github.jpaMapper;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * 返回结果是JSONObject
 * @author LiuTongbin
 * @date 2018/3/24 0024 20:20
 */
@AllArgsConstructor
public class JpaMapperFactoryBean implements FactoryBean {

    private Class<?> mapperClass;
    private String entityManagerFactoryRef;

    @Override
    public Object getObject() throws Exception {
        JpaMapperInvoker invoker = new JpaMapperInvoker(mapperClass, entityManagerFactoryRef);
        Object proxyInstance = Proxy.newProxyInstance(
                JpaMapperFactoryBean.class.getClassLoader(),
                new Class[] {this.mapperClass},
                (proxy, method, args) -> invoker.invoke(method, args));
        return proxyInstance;
    }

    @Override
    public Class<?> getObjectType() {
        return this.mapperClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }


}
