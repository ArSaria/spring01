package cn.edu.scujcc.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.scujcc.model.Channel;
import cn.edu.scujcc.model.Comment;
import cn.edu.scujcc.service.ChannelService;
import cn.edu.scujcc.service.UserService;

@RestController
@RequestMapping("/channel")
public class ChannelController {
	public static final Logger logger = LoggerFactory.getLogger(ChannelController.class);
	
	@Autowired
	private ChannelService service;
	@Autowired
	private UserService userService;
	
	@Autowired
	private CacheManager cacheManager;
	
	@GetMapping
	public List<Channel> getAllChannels(@RequestHeader("token") String token) {
		logger.info("正在读取所有频道信息, token="+token);
		String user = userService.currentUser(token);
		logger.info("当前用户是："+ user);
		List<Channel> results = service.getAllChannels();
		
		return results;
	}
	
	/**
	 * 读取频道前必须先登录
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public Channel getChannel(@PathVariable String id, @RequestHeader("token") String token) {
		logger.info("正在读取频道："+id);
		String user = userService.currentUser(token);
		logger.debug("当前已登录用户是：" + user);
		Channel c = service.getChannel(id);
		if (c != null) {
			return c;
		} else {
			logger.error("找不到指定的频道。");
			return null;
		}
				
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteChannel(@PathVariable String id){
		System.out.println("即将删除频道，id="+id);
		boolean result = service.deleteChannel(id);
		if (result) {
			return ResponseEntity.ok().body("删除成功");
		}else {
			return ResponseEntity.ok().body("删除失败");
		}
	}
	
	@PostMapping
	public Channel createChannel(@RequestBody Channel c) {
		System.out.println("即将新建频道，频道数据：" + c);
		Channel saved = service.createChannel(c);
		return saved;
	}
	
	@PutMapping
	public Channel updateChannel(@RequestBody Channel c) {
		System.out.println("即将更新频道：频道数据：" + c);
		Channel updated = service.updateChannel(c);
		return updated;
	}
	
	@GetMapping("/t/{title}")
	public List<Channel> searchByTitle(@PathVariable String title) {
		System.out.println("正在根据标题：" + title + " 查找频道。");
		return service.searchByTitle(title);
	}
	
	@GetMapping("/q/{quality}")
	public List<Channel> searchByQuality(@PathVariable String quality) {
		System.out.println("正在根据清晰度：" + quality + " 查找频道。");
		return service.searchByQuality(quality);
	}
	
	@GetMapping("/hot")
	public List<Channel> getHotChannels() {
		return service.getLatestCommentsChannel();
	}
	
	/**
	 * 新增评论
	 * @param channelId 被评论的频道编号
	 * @param comment 将要新增的评论对象
	 */
	@PostMapping("/{channelId}/comment")
	public Channel addComment(@RequestHeader("token") String token, @PathVariable String channelId, @RequestBody Comment comment) {
		Channel result = null;
		String username = userService.currentUser(token);
		comment.setAuthor(username);
		logger.debug(username + "即将评论频道："+channelId+"，评论对象："+comment);
		//把评论保存到数据库
		result = service.addComment(channelId, comment);
		return result;
	}
	
	/**
	 * 获取指定频道的热门评论（前3条）
	 * @param channelId 指定的频道编号
	 * @return 3条热门评论的列表（数组）
	 */
	@GetMapping("/{channelId}/hotcomments")
	public List<Comment> hotComments(@PathVariable String channelId) {
		List<Comment> result = null;
		logger.debug("获取频道"+channelId+"的热门评论...");
		result = service.hotComments(channelId);
		return result;
	}
	
}
