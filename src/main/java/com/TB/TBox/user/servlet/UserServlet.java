package com.TB.TBox.user.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartRequest;
import com.TB.TBox.dataUtils.FileUploadUtil;
import com.TB.TBox.note.interfaceTo.InterfaceToUser;
import com.TB.TBox.note.interfaceTo.interfaceToImp.InterfaceToUserImp;
import com.TB.TBox.user.bean.Mood_color;
import com.TB.TBox.user.bean.User;
import com.TB.TBox.user.service.UserService;
import com.google.gson.Gson;

/**
 * Servlet implementation class UserServlet
 */

@Controller
@RequestMapping("/user")
@Scope("prototype")
public class UserServlet {
	

	@Autowired
	private InterfaceToUser interfaceToUser = new InterfaceToUserImp();
	@Autowired
	private UserService userService;
	@Autowired
	private FileUploadUtil fileUtil;
	@Autowired
	private User user;
	@Autowired
	private Mood_color mood_color;
	private Logger log = Logger.getLogger(UserServlet.class);
	Gson gson = new Gson();

	/**
	 * 用户注册
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	public void addUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 截取字符串
		String number = null;
		String password = request.getParameter("password");
		String repassword = request.getParameter("repassword");
		String phone = request.getParameter("phone");
		String place = request.getParameter("place");

		while (true) {
			// 获取一个随机数
			double rand = Math.random();
			// 将随机数转换为字符串
			String str = String.valueOf(rand).replace("0.", "");
			// 截取字符串
			number = str.substring(0, 10);
			if (userService.selectUserByNumber(number) == null)
				break;
		}

		System.out.println(number + " " + password + " " + repassword + " " + phone);
		if ((password.isEmpty()) || (phone.isEmpty()) || (phone.isEmpty())) {
			response.setContentType("text/json");
			PrintWriter out = response.getWriter();
			out.print("用户注册信息填写不完整,请填写完整！");
			out.flush();
			out.close();
		} else {
			// 判断密码的长度
			if (password.length() < 6) {
				response.setContentType("text/json");
				PrintWriter out = response.getWriter();
				out.print("密码位数太少，最少为6位！");
				out.flush();
				out.close();
				// 重复密码和密码是否一致
			} else if (password.equals(repassword)) {

				// 得到默认的头像
				List<byte[]> ufacings = interfaceToUser.sehImage(0);
				for (byte[] ufacing : ufacings) {
					user.setUfacing(ufacing);
				}
				user.setNumber(number);
				user.setPassword(password);
				user.setPhone(phone);
				user.setPlace(place);
				userService.addUser(user);
				user = userService.selectUserByNumber(number);
				System.out.println("注册成功");
				response.setContentType("text/json");
				PrintWriter out = response.getWriter();
				out.print(user.toJson());
				out.flush();
				out.close();
			} else {
				response.setContentType("text/json");
				PrintWriter out = response.getWriter();
				out.print("密码和重复密码不一致！");
				out.flush();
				out.close();
			}
		}

	}

	/**
	 * 创建角色（根据以前注册的信息补全信息）
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/createRole", method = RequestMethod.POST)
	public void createRole(HttpServletRequest request, MultipartRequest re, HttpServletResponse response)
			throws IOException {
		String uuid = request.getParameter("uid");
		int uid = Integer.parseInt(uuid);
		user = userService.selectUserByID(uid);

		String username = request.getParameter("username");
		user.setUsername(username);
		String constellation = request.getParameter("constellation");
		user.setConstellation(constellation);
		String blood = request.getParameter("blood");
		user.setBlood(blood);
		String signature = request.getParameter("signature");
		user.setSignature(signature);
		String birthday = request.getParameter("birthday");
		user.setBirthday(birthday);
		String hobby = request.getParameter("hobby");
		user.setHobby(hobby);
		String job = request.getParameter("job");
		user.setJob(job);
		String gender = request.getParameter("gender");
		user.setGender(gender);
		String personalPassword = request.getParameter("personalPassword");
		user.setPersonalPassword(personalPassword);
		String uage = request.getParameter("age");
		int age = Integer.parseInt(uage);
		user.setAge(age);

		// 获取图片
		List<byte[]> b3List = null;
		b3List = fileUtil.MultiPartFileUpLoad(re);
		for (byte[] b3 : b3List) {
			user.setUfacing(b3);
			// OutputStream out = new
			// FileOutputStream("C:/Users/MrDu/Desktop/faa.jpg");
			// BufferedOutputStream buf = new BufferedOutputStream(out);
			// buf.write(b3);
			// buf.flush();
			// buf.close();
		}

<<<<<<< HEAD
		 log.debug(user.toJson());
		 userService.createRole(user);
		
=======
		log.debug(user.toJson());
		userService.createRole(user);
		// 创建默认心情颜色
		mood_color.setUid(uid);
		mood_color.setHappy("#FF0000");
		mood_color.setAngry("#3CB371");
		mood_color.setSad("#1E90FF");
		mood_color.setScard("#9400D3");
		mood_color.setCommen("#FFA07A");
		userService.addUserMoodColor(mood_color);

>>>>>>> 4658790eef2b75a140b15b9dc542a0ede78ed0fb
		response.setContentType("text/json");
		PrintWriter out1 = response.getWriter();
		out1.print(user.toJson());
		out1.flush();
		out1.close();

	}

	/**
	 * 
	 * 修改私人密码
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/updatePersionalPassword", method = RequestMethod.POST)
	public void updatePersionalPassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String uuid = request.getParameter("uid");
		int uid = Integer.parseInt(uuid);
		user = userService.selectUserByID(uid);
		String personalPassword = request.getParameter("personalPassword");
		user.setPersonalPassword(personalPassword);
		userService.updateRole(user);
		System.out.println(user.toJson());
		response.setContentType("text/json");
		PrintWriter out = response.getWriter();
		out.print(user.toJson());
		out.flush();
		out.close();

	}

	/**
	 * 
	 * 修改密码
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/updatePasswprd", method = RequestMethod.POST)
	public void updatePasswprd(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String uuid = request.getParameter("uid");
		int uid = Integer.parseInt(uuid);
		user = userService.selectUserByID(uid);
		String password = request.getParameter("password");
		user.setPassword(password);
		userService.updateRole(user);
		System.out.println(user.toJson());
		response.setContentType("text/json");
		PrintWriter out = response.getWriter();
		out.print(user.toJson());
		out.flush();
		out.close();

	}

	/**
	 * 查看用户信息
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/selectUser", method = RequestMethod.POST)
	public void selectUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String uuid = request.getParameter("uid");
		int uid = Integer.parseInt(uuid);
		user = userService.selectUserByID(uid);
		User formuser = new User();
		formuser.setUid(user.getUid());
		formuser.setPhone(user.getPhone());
		formuser.setPlace(user.getPlace());
		formuser.setUfacing(user.getUfacing());
		formuser.setNumber(user.getNumber());
		System.out.println(formuser.toJson());
		response.setContentType("text/json");
		PrintWriter out = response.getWriter();
		out.print(formuser.toJson());
		out.flush();
		out.close();
	}

	/**
	 * 查看角色信息
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/selectRole", method = RequestMethod.POST)
	public void selectRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String uuid = request.getParameter("uid");
		int uid = Integer.parseInt(uuid);
		user = userService.selectUserByID(uid);
		User formRole = new User();
		formRole.setUsername(user.getUsername());
		formRole.setConstellation(user.getConstellation());
		formRole.setBlood(user.getBlood());
		formRole.setSignature(user.getSignature());
		formRole.setBirthday(user.getBirthday());
		formRole.setUfacing(user.getUfacing());
		formRole.setHobby(user.getHobby());
		formRole.setJob(user.getJob());
		formRole.setGender(user.getGender());
		formRole.setAge(user.getAge());
		System.out.println(formRole.toJson());
		response.setContentType("text/json");
		PrintWriter out = response.getWriter();
		out.print(formRole.toJson());
		out.flush();
		out.close();
	}

	/**
	 * 修改用户信息
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/updateUserData", method = RequestMethod.POST)
	public void updateUserData(HttpServletRequest request, MultipartRequest re, HttpServletResponse response)
			throws IOException {
		String uuid = request.getParameter("uid");
		int uid = Integer.parseInt(uuid);
		user = userService.selectUserByID(uid);
		String phone = request.getParameter("phone");
		user.setPhone(phone);
		String place = request.getParameter("place");
		user.setPlace(place);
		// 获取图片
		List<byte[]> b3List = null;
		b3List = fileUtil.MultiPartFileUpLoad(re);
		for (byte[] b3 : b3List) {
			user.setUfacing(b3);
			// OutputStream out = new
			// FileOutputStream("C:/Users/MrDu/Desktop/faa.jpg");
			// BufferedOutputStream buf = new BufferedOutputStream(out);
			// buf.write(b3);
			// buf.flush();
			// buf.close();
		}

		log.debug(user.toJson());
		userService.updateRole(user);
		response.setContentType("text/json");
		PrintWriter out1 = response.getWriter();
		out1.print(user.toJson());
		out1.flush();
		out1.close();

	}

	/**
	 * 修改角色信息
	 * 
	 * @param request
	 * @param re
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/updateRoleData", method = RequestMethod.POST)
	public void updateRoleData(HttpServletRequest request, MultipartRequest re, HttpServletResponse response)
			throws IOException {
		String uuid = request.getParameter("uid");
		int uid = Integer.parseInt(uuid);
		user = userService.selectUserByID(uid);
		String username = request.getParameter("username");
		user.setUsername(username);
		String constellation = request.getParameter("constellation");
		user.setConstellation(constellation);
		String blood = request.getParameter("blood");
		user.setBlood(blood);
		String signature = request.getParameter("signature");
		user.setSignature(signature);
		String birthday = request.getParameter("birthday");
		user.setBirthday(birthday);
		String hobby = request.getParameter("hobby");
		user.setHobby(hobby);
		String job = request.getParameter("job");
		user.setJob(job);
		String gender = request.getParameter("gender");
		user.setGender(gender);
		String personalPassword = request.getParameter("personalPassword");
		user.setPersonalPassword(personalPassword);
		String uage = request.getParameter("age");
		int age = Integer.parseInt(uage);
		user.setAge(age);

		// 获取图片
		List<byte[]> b3List = null;
		b3List = fileUtil.MultiPartFileUpLoad(re);
		for (byte[] b3 : b3List) {
			user.setUfacing(b3);
			// OutputStream out = new
			// FileOutputStream("C:/Users/MrDu/Desktop/faa.jpg");
			// BufferedOutputStream buf = new BufferedOutputStream(out);
			// buf.write(b3);
			// buf.flush();
			// buf.close();
		}

		log.debug(user.toJson());
		userService.updateRole(user);

		response.setContentType("text/json");
		PrintWriter out1 = response.getWriter();
		out1.print(user.toJson());
		out1.flush();
		out1.close();
	}

	/**
	 * 登陆功能
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String number = request.getParameter("number");
		String password = request.getParameter("password");
		user = userService.selectUserByNumber(number);
		if (user == null) {
			response.setContentType("text/json");
			PrintWriter out = response.getWriter();
			out.print("您输入的账号不存在！");
			out.flush();
			out.close();
		} else {
			if (password.equals(user.getPassword())) {
				response.setContentType("text/json");
				PrintWriter out = response.getWriter();
				out.print("登陆成功！" + user.toJson());
				out.flush();
				out.close();
			} else {
				response.setContentType("text/json");
				PrintWriter out = response.getWriter();
				out.print("您输入的密码不正确！");
				out.flush();
				out.close();
			}
		}
	}

	// ========================用户心情颜色模块==================================
	/**
	 * 修改用户颜色心情表
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/updateUserMoodColoor", method = RequestMethod.POST)
	public void updateUserMoodColoor(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String formuid = ("uid");
		int uid = Integer.parseInt(formuid);
		String happy = request.getParameter("happy");
		String angry = request.getParameter("angry");
		String sad = request.getParameter("sad");
		String scard = request.getParameter("scard");
		String commen = request.getParameter("commen");
		mood_color = userService.selectUserMoodColor(uid);
		mood_color.setHappy(happy);
		mood_color.setAngry(angry);
		mood_color.setSad(sad);
		mood_color.setScard(scard);
		mood_color.setCommen(commen);
		userService.updateMoodColor(mood_color);
		response.setContentType("text/json");
		PrintWriter out = response.getWriter();
		out.print("修改成功！");
		out.flush();
		out.close();
	}

	/**
	 * 
	 * 查看用户心情颜色表
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/selectUserMoodColoor", method = RequestMethod.POST)
	public void selectUserMoodColoor(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String formuid = request.getParameter("uid");
		int uid = Integer.parseInt(formuid);
		mood_color = userService.selectUserMoodColor(uid);
		response.setContentType("text/json");
		PrintWriter out = response.getWriter();
		out.print(gson.toJson(mood_color));
		out.flush();
		out.close();
	}

	// ===========================测试方法===============================
	@Test
	public void a() {
		// String number = "1334610525";
		// String password = "erererer";
		// user = userService.selectUserByNumber(number);
		// if (user == null) {
		// System.out.println("您输入的账号不存在！");
		// } else {
		// if (password.equals(user.getPassword())) {
		// System.out.println("登陆成功！");
		// } else {
		// System.out.println("您输入的密码不正确！");
		// }
		// }
		// Mood_color mood_color = new Mood_color();

		// String formuid = ("1");
		// int uid = Integer.parseInt(formuid);
		// String formuid = ("1");
		// int uid = Integer.parseInt(formuid);
		// mood_color = userService.selectUserMoodColor(uid);
		// log.info(gson.toJson(mood_color));
		// mood_color.setHappy("happy");
		// mood_color.setAngry("angry");
		// mood_color.setSad("sad");
		// mood_color.setScard("scard");
		// mood_color.setCommen("commen");
		// userService.updateMoodColor(mood_color);
		// log.info(mood_color);
		User user = new User();
		UserService userService = new UserService();
		// 截取字符串
		String number = null;
		String password = ("");
		String repassword = ("123123");
		String phone = ("1234567888");
		String place = ("北京市");

		while (true) {
			// 获取一个随机数
			double rand = Math.random();
			// 将随机数转换为字符串
			String str = String.valueOf(rand).replace("0.", "");
			// 截取字符串
			number = str.substring(0, 10);
			if (userService.selectUserByNumber(number) == null)
				break;
		}

		System.out.println(number + " " + password + " " + repassword + " " + phone);
		if ((password.isEmpty()) || (phone.isEmpty()) || (phone.isEmpty())) {
			System.out.println("用户注册信息填写不完整,请填写完整！");

		} else {
			// 判断密码的长度
			if (password.length() < 6) {
				System.out.println("密码位数太少，最少为6位！");
				// 重复密码和密码是否一致
			} else if (password.equals(repassword)) {

				// 得到默认的头像
				List<byte[]> ufacings = interfaceToUser.sehImage(0);
				for (byte[] ufacing : ufacings) {
					user.setUfacing(ufacing);
				}
				user.setNumber(number);
				user.setPassword(password);
				user.setPhone(phone);
				user.setPlace(place);
				userService.addUser(user);
				user = userService.selectUserByNumber(number);
				System.out.println("注册成功");
			} else {
				System.out.println("密码和重复密码不一致！");
			}
		}
	}

}
