package study.datajpa.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

@RunWith(SpringRunner.class)
@Transactional
@Rollback(false)
@SpringBootTest
class MemberRepositoryTest {

	@Autowired MemberRepository memberRepository;
	@Autowired TeamRepository teamRepository;
	@PersistenceContext
	EntityManager em; 
	
	@Test
	void testMember() {
		System.out.println("memberRepositry = " + memberRepository.getClass());
		Member member = new Member("memberA");
		Member savedMember = memberRepository.save(member);
		
		Member findMember = memberRepository.findById(savedMember.getId()).get();
		
		Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
		Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		Assertions.assertThat(findMember).isEqualTo(member);
	}
	
	@Test
	public void basicCRUD() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");
		
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		//단건 조회 검증
		Member findMember1 = memberRepository.findById(member1.getId()).get();
		Member findMember2 = memberRepository.findById(member2.getId()).get();		
		Assertions.assertThat(findMember1).isEqualTo(member1);
		Assertions.assertThat(findMember2).isEqualTo(member2);
		
		//리스트 조회 검증
		List<Member> all = memberRepository.findAll();
		Assertions.assertThat(all.size()).isEqualTo(2);
		
		//카운트 검증
		long count = memberRepository.count();
		Assertions.assertThat(count).isEqualTo(2);
		
		//삭제 검증
		memberRepository.delete(member1);
		memberRepository.delete(member2);
		
		long deletedCount = memberRepository.count();
		Assertions.assertThat(deletedCount).isEqualTo(0);
	}
	
	@Test
	public void findByUsernameAndAgeGreaterThen() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);
		
		List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
		
		Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
		Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
		Assertions.assertThat(result.size()).isEqualTo(1);
	}

	@Test
	public void findHelloBy() {
		List<Member> helloBy = memberRepository.findHelloBy();
	}
	
	@Test
	public void findTop3HelloBy() {
		List<Member> helloBy = memberRepository.findTop3HelloBy();
	}
	
	@Test
	public void testNamedQuery() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);
		
		List<Member> result = memberRepository.findByUsername("AAA");
		Member findMember = result.get(0);
		Assertions.assertThat(findMember).isEqualTo(m1);
	}
	
	@Test
	public void testQuery() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);
		
		List<Member> result = memberRepository.findUser("AAA", 10);
		Assertions.assertThat(result.get(0)).isEqualTo(m1);
		
	}
	
	@Test
	public void findUsernameList() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);
		
		List<String> usernameList = memberRepository.findUsernameList();
		for( String s : usernameList ) {
			System.out.println("s = " + s);
		}
		
	}
	
	@Test
	public void findMemberDto() {

		Team team = new Team("teamA");
		teamRepository.save(team);
		
		Member m1 = new Member("AAA", 10);
		m1.setTeam(team);
		memberRepository.save(m1);
		
		List<MemberDto> memberDto = memberRepository.findMemberDto();
		for( MemberDto dto : memberDto ) {
			System.out.println("dto = " + dto);
		}
	}
	
	@Test
	public void findByNames() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);
		
		List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
		for( Member member : result ) {
			System.out.println("member = " + member);
		}
		
	}
	
	@Test
	public void returnType() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);
		
		List<Member> findMemberList = memberRepository.findListByUsername("AAA");
		for( Member member : findMemberList ) {
			System.out.println("findMemberList = " + member);
		}
		
		Member findMember = memberRepository.findMemberByUsername("AAA");
		System.out.println("findMember = " + findMember);
		
		Optional<Member> findOptionalMember = memberRepository.findOptionByUsername("AAA");
		System.out.println("findOptionalMember = " + findOptionalMember);
	}
	
	@Test
	public void paging() {
		//given
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 10));
		memberRepository.save(new Member("member3", 10));
		memberRepository.save(new Member("member4", 10));
		memberRepository.save(new Member("member5", 10));
		
		int age = 10;
		
		PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
		
		//when
		Page<Member> page = memberRepository.findByAge(age, pageRequest);

		Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));
		
		//then
		List<Member> content = page.getContent();
		long totalElements = page.getTotalElements();
		
		Assertions.assertThat(content.size()).isEqualTo(3);
		Assertions.assertThat(page.getTotalElements()).isEqualTo(5);
		Assertions.assertThat(page.getNumber()).isEqualTo(0);
		Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
		Assertions.assertThat(page.isFirst()).isTrue();
		Assertions.assertThat(page.hasNext()).isTrue();
		
	}
	
	@Test
	public void bulkUpdate() {
		//given
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 19));
		memberRepository.save(new Member("member3", 20));
		memberRepository.save(new Member("member4", 21));
		memberRepository.save(new Member("member5", 40));
		
		//when
		int resultCount = memberRepository.bulkAgePlus(20);
//		em.flush();
//		em.clear();
		
		List<Member> result = memberRepository.findByUsername("member5");
		Member member5 = result.get(0);
		System.out.println("member5 = " + member5);
		
		//then
		Assertions.assertThat(resultCount).isEqualTo(3);
	}
	
	@Test
	public void findMemberLazy() {
		//given
		//member1 -> teamA
		//member2 -> teamB
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);
		
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 10, teamB);		
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		em.flush();
		em.clear();
		
		//when N + 1
		//select Member 1 
		List<Member> members = memberRepository.findAll();
		
		for( Member member : members ) {
			System.out.println("member = " + member.getUsername());
			System.out.println("member.teamClass = " + member.getTeam().getClass());
			System.out.println("member.team = " + member.getTeam().getName());
		}
		
	}
	
	@Test
	public void findMemberFetchJoin() {
		//given
		//member1 -> teamA
		//member2 -> teamB
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);
		
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 10, teamB);		
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		em.flush();
		em.clear();
		
		//when		
		List<Member> members = memberRepository.findMemberFetchJoin();
		
		for( Member member : members ) {
			System.out.println("member = " + member.getUsername());
			System.out.println("member.teamClass = " + member.getTeam().getClass());
			System.out.println("member.team = " + member.getTeam().getName());
		}
		
	}
	
	@Test
	public void findMemberEntityGraph() {
		//given
		//member1 -> teamA
		//member2 -> teamB
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);
		
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 10, teamB);		
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		em.flush();
		em.clear();
		
		//when		
		List<Member> members = memberRepository.findMemberEntityGraph();
		
		for( Member member : members ) {
			System.out.println("member = " + member.getUsername());
			System.out.println("member.teamClass = " + member.getTeam().getClass());
			System.out.println("member.team = " + member.getTeam().getName());
		}
	}
	
	@Test
	public void findMemberEntityGraphByUsername() {
		//given
		//member1 -> teamA
		//member2 -> teamB
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);
		
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member1", 10, teamB);		
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		em.flush();
		em.clear();
		
		//when		
		List<Member> members = memberRepository.findEntityGraphByUsername("member1");
		
		for( Member member : members ) {
			System.out.println("member = " + member.getUsername());
			System.out.println("member.teamClass = " + member.getTeam().getClass());
			System.out.println("member.team = " + member.getTeam().getName());
		}
	}
	
	@Test
	public void findMemberNamedEntityGraphByUsername() {
		//given
		//member1 -> teamA
		//member2 -> teamB
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);
		
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member1", 10, teamB);		
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		em.flush();
		em.clear();
		
		//when		
		List<Member> members = memberRepository.findNamedEntityGraphByUsername("member1");
		
		for( Member member : members ) {
			System.out.println("member = " + member.getUsername());
			System.out.println("member.teamClass = " + member.getTeam().getClass());
			System.out.println("member.team = " + member.getTeam().getName());
		}
	}
	
	@Test
	public void queryHint() {
		
		//given
		Member member1 = new Member("member1", 10); 
		memberRepository.save(member1);
		em.flush();
		em.clear();
		
		//when
		Member findMember = memberRepository.findReadOnlyByUsername("member1");
		findMember.setUsername("member2");
		
		em.flush();
		
	}
	
	@Test
	public void lock() {
		
		//given
		Member member1 = new Member("member1", 10); 
		memberRepository.save(member1);
		em.flush();
		em.clear();
		
		//when
		List<Member> result = memberRepository.findLockByUsername("member1");
		
	}
	
	@Test
	public void callCustom() {
		List<Member> result = memberRepository.findMemberCustom();
	}
	
	@Test
	public void QueryByExample() {
		
		//given
		Team teamA = new Team("teamA");
		em.persist(teamA);
		
		Member m1 = new Member("m1", 0, teamA);
		Member m2 = new Member("m2", 0, teamA);
		em.persist(m1);
		em.persist(m2);
		
		em.flush();
		em.clear();
		
		//when
		//Probe
		Member member = new Member("m1");
		Team team = new Team("teamA");
		member.setTeam(team);
		
		ExampleMatcher matcher = ExampleMatcher.matching()
				.withIgnorePaths("age");
		
		Example<Member> example = Example.of(member, matcher);
	
		List<Member> result = memberRepository.findAll(example);
		Assertions.assertThat(result.get(0).getUsername()).isEqualTo("m1");
	}
	
	@Test
	public void projections() {
		//given
		Team teamA = new Team("teamA");
		em.persist(teamA);
		
		Member m1 = new Member("m1", 0, teamA);
		Member m2 = new Member("m2", 0, teamA);
		em.persist(m1);
		em.persist(m2);
		
		em.flush();
		em.clear();
		
		//when
		List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");
		
		for( UsernameOnly usernameOnly : result ) {
			System.out.println("usernameOnly = " + usernameOnly.getUsername());
		}
	}
	
	@Test
	public void projectionsDto() {
		//given
		Team teamA = new Team("teamA");
		em.persist(teamA);
		
		Member m1 = new Member("m1", 0, teamA);
		Member m2 = new Member("m2", 0, teamA);
		em.persist(m1);
		em.persist(m2);
		
		em.flush();
		em.clear();
		
		//when
		List<UsernameOnlyDto> result = memberRepository.findProjectionsDtoByUsername("m1");
		
		for( UsernameOnlyDto usernameOnlyDto : result ) {
			System.out.println("usernameOnlyDto = " + usernameOnlyDto.getUsername());
		}
	}
	
	@Test
	public void dynamicProjections() {
		//given
		Team teamA = new Team("teamA");
		em.persist(teamA);
		
		Member m1 = new Member("m1", 0, teamA);
		Member m2 = new Member("m2", 0, teamA);
		em.persist(m1);
		em.persist(m2);
		
		em.flush();
		em.clear();
		
		//when
		List<UsernameOnlyDto> result = memberRepository.findDynamicProjectionsByUsername("m1", UsernameOnlyDto.class);
		
		for( UsernameOnlyDto usernameOnlyDto : result ) {
			System.out.println("usernameOnlyDto = " + usernameOnlyDto.getUsername());
		}
	}
	
	@Test
	public void nativeQuery() {
		//given
		Team teamA = new Team("teamA");
		em.persist(teamA);
		
		Member m1 = new Member("m1", 0, teamA);
		Member m2 = new Member("m2", 0, teamA);
		em.persist(m1);
		em.persist(m2);
		
		em.flush();
		em.clear();
		
		//when
		Member result = memberRepository.findByNativeQuery("m1");
		System.out.println("result = " + result);
	}
	
	@Test
	public void nativeQueryPaging() {
		//given
		Team teamA = new Team("teamA");
		em.persist(teamA);
		
		Member m1 = new Member("m1", 0, teamA);
		Member m2 = new Member("m2", 0, teamA);
		em.persist(m1);
		em.persist(m2);
		
		em.flush();
		em.clear();
		
		//when
		Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
		List<MemberProjection> content = result.getContent();
		
		for( MemberProjection memberProjection : content ) {
			System.out.println("memberProjection.getUsername = " + memberProjection.getUsername());
			System.out.println("memberProjection.getTeamname = " + memberProjection.getTeamname());
		}
	}
}
