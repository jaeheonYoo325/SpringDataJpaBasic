package study.datajpa.controller;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberRepository memberRepository;
	
	@GetMapping("/members/{id}")
	public String findMember(@PathVariable("id") Long id) {
		Member member = memberRepository.findById(id).get();
		return member.getUsername();
	}
	
	//권장 안함.
	@GetMapping("/members2/{id}")
	public String findMember2(@PathVariable("id") Member member) {
		return member.getUsername();
	}
	
	@GetMapping("/members")
	public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
		
		return memberRepository.findAll(pageable)
				.map(MemberDto::new);

//	2차 수정.				
//		return memberRepository.findAll(pageable)
//				.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
		
//	1차 수정.				
//		Page<Member> page = memberRepository.findAll(pageable);
//		Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
//		return map;
	}
	
//	@PostConstruct
	public void init() {
		for(int i = 0; i < 100; i++) {
			memberRepository.save(new Member("user" + i, i));
		}
	}
}
