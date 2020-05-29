package cn.edu.scujcc.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.scujcc.UserExistException;
import cn.edu.scujcc.model.Response;
import cn.edu.scujcc.model.User;
import cn.edu.scujcc.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	private final static Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private UserService service;
	
	
	@PostMapping("/register")
	public Response register(@RequestBody User u) {
		Response result = new Response();
		logger.debug("用户注册："+u);
		try {
			User saved = service.register(u);
			result.setStatus(Response.STATUS_OK);
			result.setData(saved);
		} catch (UserExistException e) {
			logger.error("用户已存在，不能注册。");
			result.setStatus(Response.STATUS_ERROR);
			result.setMessage("用户已存在，不能注册。");
		}
		return result;
	}
	
	@GetMapping("/login/{username}/{password}")
	public Response<String> login(@PathVariable("username") String username, @PathVariable("password") String password) {
		Response<String> result = new Response<>();
		User saved = service.login(username, password);
		if (saved != null) { //登录成功
			String uid = service.checkIn(username);
			result.setStatus(Response.STATUS_OK);
			result.setData(uid);
			result.setMessage("登录成功：");
		} else {//登录失败
			logger.error("用户已存在，不能注册。");
			result.setStatus(Response.STATUS_ERROR);
			result.setMessage("密码错误");
		}
		return result;
	}
}