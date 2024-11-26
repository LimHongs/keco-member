package com.example.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.member.Service.LoginService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

	private final LoginService loginService;

	@Autowired
	public LoginController(LoginService loginService) {
		this.loginService = loginService;
	}

	// 첫 화면 - 로그인 페이지
	@GetMapping("/")
	public String showLoginPage(HttpSession session) {
		// 로그인된 사용자가 있으면 home으로 이동
		if (session.getAttribute("loggedInUser") != null) {
			return "redirect:/home";
		}
		return "Members/login"; // 로그인 페이지 템플릿 반환
	}

	@PostMapping("/members/login")
	@ResponseBody
	public Map<String, Object> login(
	        @RequestParam("mem_id") String memId,
	        @RequestParam("mem_name") String memName,
	        HttpSession session
	) {
	    Map<String, Object> response = new HashMap<>();

	    try {
	        int failedAttempts = loginService.authenticate(memId, memName);

	        if (failedAttempts == 0) {
	            // 로그인 성공
	            session.setAttribute("loggedInUser", memName);
	            session.setAttribute("loggedInUserId", memId);
	            response.put("success", true);
	            response.put("redirect", "/home");
	        } else {
	            // 로그인 실패
	            response.put("success", false);
	            response.put("message", "로그인 실패. 실패 횟수: " + failedAttempts);
	            response.put("failedAttempts", failedAttempts); // 실패 횟수 추가
	        }
	    } catch (IllegalStateException e) {
	        // 계정 잠금 상태 처리
	        response.put("success", false);
	        response.put("message", e.getMessage());
	    }

	    return response;
	}



}