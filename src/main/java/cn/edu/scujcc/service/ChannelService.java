package cn.edu.scujcc.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.edu.scujcc.api.ChannelController;
import cn.edu.scujcc.dao.ChannelRepository;
import cn.edu.scujcc.model.Channel;

@Service
public class ChannelService {
	@Autowired
	private ChannelRepository repo;
	public static final Logger logger = LoggerFactory.getLogger(ChannelService.class);

	//获取所有频道
	public List<Channel> getAllChannels(){
		return repo.findAll();
	}
	
	
	/**
	 * 获取一个频道
	 * 
	 * @param id 
	 * @return
	 */
	public Channel getChannel(String channelId){
		Optional<Channel> result = repo.findById(channelId);
		
		if (result.isPresent()) {
			return result.get();
		} else {
			return null;
		}
		
	}
	
	public List<Channel> searchByTitle(String title) {
		return repo.findByTitle(title);
	}
	
	public List<Channel> searchByQuality(String quality) {
		return repo.findByQuality(quality);
	}
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public boolean deleteChannel(String channelId) {
		boolean result = true;
		repo.deleteById(channelId);
		
		return result;
	}
	
	//保存
	public Channel createChannel(Channel c) {
		return repo.save(c);
	}
	
	/**
	 * 更新指定频道信息。
	 * @param c 新的频道信息，用于更新已存在的同一频道。
	 * @return 更新后的频道信息
	 */
	public Channel updateChannel(Channel c) {
		Channel saved = getChannel(c.getId());
		if (saved != null) {
			if (c.getTitle() != null) {
				saved.setTitle(c.getTitle());
			}
			if (c.getQuality() != null) {
				saved.setQuality(c.getQuality());
			}
			if (c.getUrl() != null) {
				saved.setUrl(c.getUrl());
			}
			if (c.getComments() != null) {
				saved.getComments().addAll(c.getComments());
			}else {
				saved.setComments(c.getComments());
			}
		}
		logger.debug(saved.toString());
		return repo.save(saved);
	}
	
	/**
	 * 找出今天有评论的频道
	 * 
	 * @return 频道列表
	 */
	public List<Channel> getLatestCommentsChannel() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime today = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 0, 0);
		return repo.findByCommentsDtAfter(today);
	}
}
