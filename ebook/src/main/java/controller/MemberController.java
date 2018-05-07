package controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import book.Book;
import dao.BookDao;
import dao.MemberDao;
import member.AuthInfo;
import member.Member;

/**
 * Handles requests for the application home page.
 */

@Controller
public class MemberController {
	MemberDao memberDao;
	BookDao bookDao;
	
	public void setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
	}
	
	public void setBookDao(BookDao bookDao) {
		this.bookDao = bookDao;
	}
	
	@RequestMapping(value = "/home")
	public String home(Model model) {
		String cate = "";
		List<Book> book = bookDao.cateBook(cate);
		model.addAttribute("book", book);
		return "home";
	}
	
	@RequestMapping(value = "/login")
	public String login() {
		
		return "/member/login";
	}
	
	@RequestMapping(value = "/join")
	public String join() {
		
		return "/member/join";
	}
	
	@RequestMapping(value = "/ckID")
	@ResponseBody
	public String ckID(String mid) {
		System.out.println(mid);
		int ck = memberDao.checkID(mid);
		return String.valueOf(ck);
	}
	
	@RequestMapping(value = "/memberJoin", method = RequestMethod.POST)
	public String memberJoin(HttpServletRequest request) {
		String mid = request.getParameter("mid");
		String pw = request.getParameter("pw");
		String name = request.getParameter("name");
		String gender = request.getParameter("gender");
		String phone = request.getParameter("phone");
		String hint = request.getParameter("hint2");
		String answer = request.getParameter("answer");

		memberDao.memberJoin(mid, pw, name, gender, phone, hint, answer);

		return "redirect:/home";
	}
	
	@RequestMapping(value = "/ckLogin") 
	public String ckLogin(String mid, String pw, Model model, HttpSession session) {
		String rs = memberDao.checkLogin(mid, pw);
		Member member = memberDao.memberInfo(mid);

		if (rs == "no") {
			model.addAttribute("mck", rs);
			return "member/login";
		} else {
			AuthInfo authInfo = new AuthInfo(member.getMid(), member.getPw(), member.getName(),
					member.getGender(), member.getPhone(), member.getHint(), member.getAnswer(), member.getPoint());
			session.setAttribute("authInfo", authInfo);
			return "redirect:/home";
		}
	}
	
	@RequestMapping(value = "/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/home";
	}
	
	@RequestMapping(value = "/charge") 
	public String charge(String mid, HttpSession session) {
		memberDao.chargePoint(mid);
		return "redirect:/home";
	}
	

}