package study.datajpa.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import study.datajpa.entity.Member;

@RunWith(SpringRunner.class)
@Transactional
@Rollback(false)
@SpringBootTest
class MemberRepositoryTest {

	@Autowired MemberRepository memberRepositry;
	
	@Test
	void testMember() {
		Member member = new Member("memberA");
		Member savedMember = memberRepositry.save(member);
		
		Member findMember = memberRepositry.findById(savedMember.getId()).get();
		
		Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
		Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		Assertions.assertThat(findMember).isEqualTo(member);
	}

}
