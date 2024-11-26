package com.example.member.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.member.model.Member;
import com.example.member.repository.MemberRepository;

@Service
public class MemberService {
	private final MemberRepository memberRepository;

	// 생성자 주입
	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	// 중복회원 검증
	public String join(Member member) {
		validateDuplicateMember(member);
		memberRepository.save(member);
		return member.getMem_name();
	}

	// 존재하는 회원인지 확인
	private void validateDuplicateMember(Member member) {
		memberRepository.findByName(member.getMem_name()).ifPresent(m -> {
			throw new IllegalStateException("이미 존재하는 회원입니다.");
		});
	}
	
	// 전체 회원 조회
	public List<Member> findMembers() {
		System.out.println("service - findMembers 호출");
		List<Member> members = memberRepository.findAll();
		System.out.println("가져온 회원 수: " + members.size());
		return members;
	}
	
	  public boolean isDuplicateId(String memid) {
	        return memberRepository.existsById(memid);
	    }


}