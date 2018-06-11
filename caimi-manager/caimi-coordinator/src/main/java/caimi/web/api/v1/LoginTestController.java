package caimi.web.api.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class LoginTestController {

	private static final Logger logger = LoggerFactory.getLogger(LoginTestController.class);
	
	@RequestMapping("/")
	public String index() {
		return "home.html";
	}

	@RequestMapping("/hello/{name}")
	public String index(@PathVariable String name) {
		return "hello"+name+"!!!";
	}
}
