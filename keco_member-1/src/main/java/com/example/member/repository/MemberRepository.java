package com.example.member.repository;

import java.util.List;
import java.util.Optional;

import com.example.member.model.Member;

public interface MemberRepository {
	Member save(Member member);

	Optional<Member> findByName(String mem_Name);

	List<Member> findAll();

	boolean existsById(String mem_id);

	Optional<Member> findByMemIdAndMemName(String mem_id, String mem_name);

	Optional<Member> findByMemId(String memId);

	// 실패 횟수 업데이트
	void updateFailedAttempts(int failedAttempts, String memId);

	// 실패 횟수 초기화
	void resetFailedAttempts(String memId);

	// 계정 잠금
	void lockAccount(String memId);
	
	// 회원정보 수정
	void updateMember(Member member);
}