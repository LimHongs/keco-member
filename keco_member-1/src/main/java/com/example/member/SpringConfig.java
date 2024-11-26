package com.example.member;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.member.Service.LoginService;
import com.example.member.Service.MemberService;
import com.example.member.repository.JdbcMemberRepository;
import com.example.member.repository.MemberRepository;

@Configuration
public class SpringConfig {
    private final DataSource dataSource;

    // 생성자를 통해 DataSource를 주입받음 (Spring Boot가 자동으로 주입)
    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public MemberService memberService() {
        // MemberRepository를 생성자에 주입하여 MemberService 생성
        return new MemberService(memberRepository());
    }
    
    @Bean
    public LoginService loginService() {
        return new LoginService(memberRepository());
    }


    @Bean
    public MemberRepository memberRepository() {
        // JDBC 구현체를 반환
        return new JdbcMemberRepository(dataSource);
    }
}
