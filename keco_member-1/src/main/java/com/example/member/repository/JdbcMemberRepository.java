package com.example.member.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import com.example.member.model.Member;

public class JdbcMemberRepository implements MemberRepository {
	private final DataSource dataSource;

	public JdbcMemberRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Member save(Member member) {
		String sql = "INSERT INTO tbl_member (mem_id, mem_name, mem_phone, mem_zipcode, mem_addr1, mem_addr2, reg_date) VALUES (?, ?, ?, ?, ?, ?, NOW())";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// 모든 필드 값 설정
			pstmt.setString(1, member.getMem_id());
			pstmt.setString(2, member.getMem_name());
			pstmt.setString(3, member.getMem_phone());
			pstmt.setString(4, member.getMem_zipcode());
			pstmt.setString(5, member.getMem_addr1());
			pstmt.setString(6, member.getMem_addr2());

			pstmt.executeUpdate();
			return member;
		} catch (Exception e) {
			throw new IllegalStateException("데이터 저장 중 오류 발생", e);
		}
	}

	@Override
	public List<Member> findAll() {
		String sql = "SELECT * FROM tbl_member";
		List<Member> members = new ArrayList<>();

		try (Connection conn = dataSource.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				Member member = new Member();
				member.setMem_id(rs.getString("mem_id"));
				member.setMem_name(rs.getString("mem_name"));
				member.setMem_phone(rs.getString("mem_phone"));
				member.setMem_zipcode(rs.getString("mem_zipcode"));
				member.setMem_addr1(rs.getString("mem_addr1"));
				member.setMem_addr2(rs.getString("mem_addr2"));
				member.setRegdate(rs.getString("reg_date"));
				members.add(member);
			}
		} catch (SQLException e) {
			throw new IllegalStateException("전체 회원 조회 중 오류 발생", e);
		}
		return members;
	}

	// 이름으로 회원 정보를 조회하는 메서드
	@Override
	public Optional<Member> findByName(String mem_name) {
		String sql = "select * from tbl_member where mem_name = ?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, mem_name);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				Member member = new Member();
				member.setMem_id(rs.getString("mem_id"));
				member.setMem_name(rs.getString("mem_name"));
				return Optional.of(member);
			}
			return Optional.empty();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public boolean existsById(String mem_id) {
		String sql = "SELECT COUNT(*) FROM tbl_member WHERE mem_id = ?";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, mem_id);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0; // COUNT 값이 0보다 크면 true
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return false;
	}

	public Optional<Member> findByMemIdAndMemName(String memId, String memName) {
		String sql = "SELECT * FROM tbl_member WHERE mem_id = ? AND mem_name = ?";
		try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, memId);
			pstmt.setString(2, memName);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					Member member = new Member();
					member.setMem_id(rs.getString("mem_id"));
					member.setMem_name(rs.getString("mem_name"));
					// 필요한 필드 추가 설정
					return Optional.of(member);
				}
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Database query error", e);
		}
		return Optional.empty();
	}

	// id값으로 로그인 찾기
	@Override
	public Optional<Member> findByMemId(String memId) {
		System.out.println("DB 조회: mem_id=" + memId); // 로그 출력
		String sql = "SELECT * FROM tbl_member WHERE mem_id = ?";
		try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, memId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					Member member = new Member();
					member.setMem_id(rs.getString("mem_id"));
					member.setMem_name(rs.getString("mem_name"));
					member.setMem_phone(rs.getString("mem_phone"));
					member.setMem_zipcode(rs.getString("mem_zipcode"));
					member.setMem_addr1(rs.getString("mem_addr1"));
					member.setMem_addr2(rs.getString("mem_addr2"));
					return Optional.of(member);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	// 로그인 틀릴시 카운트 증가
	public void updateFailedAttempts(int failedAttempts, String memId) {
	    String sql = "UPDATE tbl_member SET failed_attempts = ? WHERE mem_id = ?";
	    try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, failedAttempts);
	        pstmt.setString(2, memId);
	        pstmt.executeUpdate();
	        System.out.println("Failed attempts updated for mem_id: " + memId + " to " + failedAttempts);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	// 로그인시 카운트 초기화
	public void resetFailedAttempts(String memId) {
		String sql = "UPDATE tbl_member SET failed_attempts = 0 WHERE mem_id = ?";
		try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, memId);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void lockAccount(String memId) {
		String sql = "UPDATE tbl_member SET is_locked = TRUE WHERE mem_id = ?";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, memId);
			pstmt.executeUpdate();
			System.out.println("Account locked for mem_id: " + memId); // 디버그용 로그 추가
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateMember(Member member) {
		String sql = "UPDATE tbl_member SET mem_name = ?, mem_phone = ?, mem_zipcode = ?, mem_addr1 = ?, mem_addr2 = ? WHERE mem_id = ?";
		try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, member.getMem_name());
			pstmt.setString(2, member.getMem_phone());
			pstmt.setString(3, member.getMem_zipcode());
			pstmt.setString(4, member.getMem_addr1());
			pstmt.setString(5, member.getMem_addr2());
			pstmt.setString(6, member.getMem_id());

			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected == 0) {
				throw new IllegalStateException("회원정보 업데이트 실패: 회원이 존재하지 않습니다.");
			}
		} catch (SQLException e) {
			throw new IllegalStateException("회원정보 수정 중 오류 발생", e);
		}
	}

	private Connection getConnection() {
		return DataSourceUtils.getConnection(dataSource);
	}

}
