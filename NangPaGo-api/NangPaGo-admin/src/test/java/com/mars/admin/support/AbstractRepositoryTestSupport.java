package com.mars.admin.support;

import com.mars.admin.support.extend.RepositoryTestCondition;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(RepositoryTestCondition.class)
@ActiveProfiles("local")
@SpringBootTest
public abstract class AbstractRepositoryTestSupport {

}
