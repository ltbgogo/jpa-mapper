package org.github.jpaMapper.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

//@JpaMapperScan(basePackages = "org.jpa.mapper.test.book", entityManagerFactoryRef = "bookEntityManager")
//@JpaMapperScan(basePackages = "org.jpa.mapper.test.user", entityManagerFactoryRef = "userEntityManager")
@SpringBootApplication
@Slf4j
public class AppStarter implements ApplicationListener<ApplicationReadyEvent> {

    public static void main(String[] args ) throws Exception {
		SpringApplication.run(AppStarter.class, args);
    }

    @Autowired
    private SecondService secondService;


    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
//        secondService.doJtaMapper();
    }
}
