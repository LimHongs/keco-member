package com.example.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.member.Service.MemberService;
import com.example.member.model.Member;

@Controller
public class MemberController {
	// MemberService 를 주입받기 위한 필드 선언
	private final MemberService memberService;

	// 생성자를 통해 의존성 주입
	@Autowired
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	// 회원 목록 list 보기
	@GetMapping(value = "/members/list")
	public String list(Model model) {
		System.out.println("Controller - 회원 목록 호출");
		model.addAttribute("members", memberService.findMembers());
		return "members/list";
	}

	@GetMapping(value = "/members/new")
	public String createForm() {
		return "members/createMemberForm";
	}

	// 회원가입 요청을 처리하는 POST 요청 메서드
	@PostMapping(value = "/members/new")
	public String create(@ModelAttribute Member member) {
		memberService.join(member);
		return "redirect:/";
	}
	
	
	@GetMapping(value = "members/check-id")
	public ResponseEntity<Boolean> checkDuplicateId(@RequestParam("mem_id") String memId) {
	    boolean isDuplicate = memberService.isDuplicateId(memId);
	    return ResponseEntity.ok(isDuplicate);
	}

	
	
}
