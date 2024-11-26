package com.example.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.member.Service.UpdateMemberService;
import com.example.member.model.Member;

import jakarta.servlet.http.HttpSession;

@Controller
public class UpdateMemberController {

    private final UpdateMemberService updateMemberService;

    @Autowired
    public UpdateMemberController(UpdateMemberService updateMemberService) {
        this.updateMemberService = updateMemberService;
    }

    @GetMapping(value = "/members/update")
    public String showUpdateForm(@RequestParam("mem_id") String memId, HttpSession session, Model model) {
        // 세션 검증
        if (session.getAttribute("loggedInUserId") == null || !session.getAttribute("loggedInUserId").equals(memId)) {
            return "redirect:/"; // 세션 없거나 ID 불일치 시 로그인 페이지로
        }
        Member member = updateMemberService.findById(memId);
        model.addAttribute("member", member);
        return "members/UpdateMember";
    }

    @PostMapping(value = "/members/update")
    public String updateMember(@ModelAttribute Member member) {
        updateMemberService.updateMember(member);
        return "redirect:/home"; // 수정 후 홈 화면으로 리다이렉트
    }
}
