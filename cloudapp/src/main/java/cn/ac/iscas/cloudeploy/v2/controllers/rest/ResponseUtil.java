package cn.ac.iscas.cloudeploy.v2.controllers.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public final class ResponseUtil {
	public static void NotFound(HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	}
	
	public static void Sucess(String message,HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json;charset=utf-8");
		try {
			response.getWriter().write(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
