//package caimi.web.controller;
//
//import java.sql.Timestamp;
//
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import caimi.common.UUIDUtil;
//import caimi.web.repository.entity.UserEntity;
//import caimi.web.service.auth.UserService;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class LoginControllerTest {
//
//	private static final String ID_PREFIX_USR = "user_";
//	@Autowired
//	private UserService userService;
//	
//	@Test
//	public void testInsert() throws Exception{
//		UserEntity userentity = new UserEntity();
//		userentity.setEmail("123@facebook.com");
//		userentity.setId(ID_PREFIX_USR + UUIDUtil.genId());
//		userentity.setUsername("Tom");
//		userentity.setPassword("Tom123456");
//		userentity.setEnable(true);
//		userentity.setUserface("porkerface");
//		userentity.setRegTime(new Timestamp(System.currentTimeMillis()));
//		Assert.assertTrue(0 == userService.reg(userentity));
//	}
//}
