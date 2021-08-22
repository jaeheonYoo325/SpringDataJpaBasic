package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
	
	//open projections
	//@Value("#{target.username + ' ' + target.age}")
	String getUsername();
}
