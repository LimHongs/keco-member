package com.example.member.Service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.member.model.Member;
import com.example.member.repository.MemberRepository;

@Service
public class LoginService {

    private final MemberRepository memberRepository;

    public LoginService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public int authenticate(String memId, String memName) {
        Optional<Member> optionalMember = memberRepository.findByMemId(memId);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();

            // 계정 잠금 상태 확인
            if (member.isLocked()) {
                throw new IllegalStateException("계정이 잠겼습니다. 관리자에게 문의하세요.");
            }

            // 이름 확인
            if (member.getMem_name().equals(memName)) {
                resetFailedAttempts(member); // 로그인 성공 시 실패 횟수 초기화
                return 0; // 성공
            } else {
                return incrementFailedAttempts(member); // 이름 틀릴 시 실패 증가
            }
        }

        // ID가 존재하지 않을 때
        return -1;
    }

    private int incrementFailedAttempts(Member member) {
        int newFailedAttempts = member.getFailedAttempts() + 1;

        // 실패 횟수 업데이트
        memberRepository.updateFailedAttempts(newFailedAttempts, member.getMem_id());

        // 실패 횟수가 5 이상이면 계정 잠금
        if (newFailedAttempts >= 5) {
            memberRepository.lockAccount(member.getMem_id());
            throw new IllegalStateException("계정이 잠겼습니다. 관리자에게 문의하세요.");
        }

        return newFailedAttempts;
    }

    private void resetFailedAttempts(Member member) {
        memberRepository.updateFailedAttempts(0, member.getMem_id());
    }
}