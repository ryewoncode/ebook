package dao;

import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import member.Member;

public class MemberDao {
	private JdbcTemplate jdbcTemplate;

	public MemberDao(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public int checkID(String mid) {
		String ck = null;
		try {
			ck = jdbcTemplate.queryForObject("select mid from member where mid=?", String.class, mid);
			return 0;
		} catch (Exception e) {
			return 1;
		}
	}

	public void memberJoin(String mid, String pw, String name, String gender, String phone, String hint, String answer) {
		jdbcTemplate.update("insert into member(mid, pw, name, gender, phone, hint, answer, point, mexist) values(?, ?, ?, ?, ?, ?, ?, 0, 'yes')", mid, pw, name, gender, phone, hint, answer);
	}
	
	//아이디와 패스워드가 일치하는 정보가 있는지 확인
	public String checkLogin(String mid, String pw) {
			String result = null;
			try {
				result = jdbcTemplate.queryForObject("select mid from member where mid=? and pw=? and mexist = 'yes'", String.class, mid, pw);
				return result;
			} catch (Exception e) {
				return "no";
			}
	}
	
	public Member memberInfo(String mid) {
		List<Member> results = jdbcTemplate.query("select * from member where mexist = 'yes' and mid = ?", new RowMapper<Member>() {
			@Override
			public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
				Member member = new Member(rs.getString("mid"), rs.getString("pw"), rs.getString("name"),
						rs.getString("gender"), rs.getString("phone"), rs.getString("hint"), rs.getString("answer"),
						rs.getInt("point"), rs.getDate("mmod_date"), rs.getDate("mdel_date"), rs.getString("mexist"));
				return member;
			}
		}, mid);
		return results.isEmpty() ? null : results.get(0);
	}
	
	public void chargePoint(String mid, int cur_point, int point, int aft_point) {
		jdbcTemplate.update("update member set point=?", aft_point);
		jdbcTemplate.update("insert into point values(?, ?, '충전', sysdate, ?)", point, cur_point, mid);
	}
}
