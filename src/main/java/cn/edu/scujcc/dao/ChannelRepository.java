package cn.edu.scujcc.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cn.edu.scujcc.model.Channel;


@Repository
public interface ChannelRepository extends MongoRepository<Channel,String>{
	List<Channel> findByTitle(String t);
	List<Channel> findByQuality(String q);
	
	//找出评论时间在指定日期之后的所有频道
	public List<Channel> findByCommentsDtAfter(LocalDateTime theDt);
	
	
	
}


