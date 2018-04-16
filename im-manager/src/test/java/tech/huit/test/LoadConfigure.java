package tech.huit.test;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"/spring/spring-context.xml", "/spring/spring-mvc.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
// @TransactionConfiguration(defaultRollback = false)
// @Transactional()
public class LoadConfigure extends Assert {
}