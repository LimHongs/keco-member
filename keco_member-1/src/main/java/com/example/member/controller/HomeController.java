package com.example.member.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/home")
	public String homePage(HttpSession session, Model model) {
	    // 세션에서 사용자 정보를 가져옵니다.
	    String loggedInUserId = (String) session.getAttribute("loggedInUserId");
	    String loggedInUserName = (String) session.getAttribute("loggedInUser");

	    // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
	    if (loggedInUserId == null) {
	        return "redirect:/";
	    }

	    // 모델에 사용자 정보 추가
	    model.addAttribute("username", loggedInUserName);
	    model.addAttribute("userId", loggedInUserId);

	    return "home"; // home.html 반환
	}


}
