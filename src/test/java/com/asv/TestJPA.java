package com.asv;

import lombok.extern.slf4j.Slf4j;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
@EnableAutoConfiguration(exclude= SecurityAutoConfiguration.class)
public class TestJPA {
    //    @Autowired
    //    UserService userService;
    @Test
    public void testUserCondition() {
        String name = "lin";

    //        userService.findAllCondition(name, 0 , 10);

    }

}
