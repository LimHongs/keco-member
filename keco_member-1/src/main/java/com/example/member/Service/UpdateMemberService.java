package com.example.member.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.member.model.Member;
import com.example.member.repository.MemberRepository;

@Service
public class UpdateMemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public UpdateMemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 회원정보 조회
    public Member findById(String memId) {
        System.out.println("findById 호출: mem_id=" + memId); // 로그 출력
        return memberRepository.findByMemId(memId).orElseThrow(() -> 
            new IllegalArgumentException("회원 정보를 찾을 수 없습니다.")
        );
    }

    // 회원정보 수정
    public void updateMember(Member member) {
        memberRepository.updateMember(member); // 저장소에 업데이트 요청
    }
}
