<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="user.UserDAO"%>
<%@ page import="java.io.PrintWriter"%>
<%
    request.setCharacterEncoding("UTF-8");
%>
<jsp:useBean id="user" class="user.User" scope="page" />
<jsp:setProperty name="user" property = "userID" />
<jsp:setProperty name="user" property = "userPassword" />
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>뇌에 때려박는 단어장 프로젝트</title>
</head>
<body>
    <%
  //null 값이 있는지 검사
    if (user.getUserID()==null || user.getUserPassword()==null){
    	PrintWriter script = response.getWriter();
        script.println("<script>");
        script.println("alert(' 사용자명 또는 비밀번호를 입력 하지 않았습니다.')");
        script.println("history.back()");
        script.println("</script>");
    }
    else{
        UserDAO userDAO = new UserDAO();
        int result = userDAO.login(user.getUserID(), user.getUserPassword());
        if (result == 1) { //로그인 성공
        	session.setAttribute("userID",user.getUserID()); 
            PrintWriter script = response.getWriter();
            script.println("<script>");
            script.println("location.href = 'Gapyeong.jsp'");
            script.println("</script>");
        } else if (result == 0) {//비밀번호 불일치
            PrintWriter script = response.getWriter();
            script.println("<script>");
            script.println("alert('비밀번호가 틀립니다.')");
            script.println("history.back()");
            script.println("</script>");
        } else if (result == -1) { //아이디 없음
            PrintWriter script = response.getWriter();
            script.println("<script>");
            script.println("alert('존재하지 않는 아이디입니다.')");
            script.println("history.back()");
            script.println("</script>");
        } else if (result == -2) { //데이터베이스 오류
            PrintWriter script = response.getWriter();
            script.println("<script>");
            script.println("alert('데이터베이스 오류가 발생했습니다.')");
            script.println("history.back()");
            script.println("</script>");
        }
    }
    %>
</body>
</html>




