package board;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BoardDAO {
	private Connection conn;
    private ResultSet rs;
    
 
    public BoardDAO() {
    	try {
			String dbURL = "jdbc:mysql://203.255.177.208:3306/test3?serverTimezone=UTC";
			// test3데이터베이스에 접속하는 3306포트(=내컴퓨터의 mysql포트)
			String dbID = "test3";
			String dbPassword = "test1234";// 자신이 설정한 test3 비밀번호 입력
			Class.forName("com.mysql.jdbc.Driver"); // Driver는 매개체역할 Library
			conn = DriverManager.getConnection(dbURL, dbID, dbPassword);// 데이터베이스에 접속하는부분

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    public String getDate() {
        String SQL= "SELECT NOW()";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL); //SQL문장을 실행 준비단계로 만들어준다.
            rs=pstmt.executeQuery();//rs에 실제로 실행한 뒤에 나올 결과를 저장.
            if(rs.next()) //결과값이 있는 경우.
            {
                return rs.getString(1); //현재 시간을 반환
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return ""; // 데이터베이스 오류
    }
    public int getNext() {
        String SQL= "SELECT wordsNo FROM Board ORDER BY wordsNo DESC"; //bbs테이블에서 내림차순으로 wordsNo속성을 정렬
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL); //SQL문장을 실행 준비단계로 만들어준다.
            rs=pstmt.executeQuery();//rs에 실제로 실행한 뒤에 나올 결과를 저장.
            if(rs.next()) //결과값이 있는 경우.
            {
                return rs.getInt(1) +1; //이전값에서 1 추가된 숫자반환
            }
            return 1; //첫번째 게시물인 경우. 현재위치를 반환한다
        }catch(Exception e) {
            e.printStackTrace();
        }
        return -1; // 데이터베이스 오류. 번호로는 적당하지않은 음수를 반환해서 데이터베이스 오류임을 알게한다.
    }
    
    public int write(String wordsEng, String wordsKor, String userID, String wordsContent) {
        String SQL= "INSERT INTO Board VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL); //SQL문장을 실행 준비단계로 만들어준다.
            pstmt.setInt(1, getNext());
            pstmt.setString(2, wordsEng);
            pstmt.setString(3, wordsKor);
            pstmt.setString(4, userID);
            pstmt.setString(5, getDate());
            pstmt.setString(6, wordsContent);
            pstmt.setInt(7, 1); //wordsAvailabe; 처음 작성시에 글이 보이도록 1을 지정.
            return pstmt.executeUpdate();//성공하면 0이상의 값을 반환한다.
            
        }catch(Exception e) {
            e.printStackTrace();
        }
        return -1; // 데이터베이스 오류
    }
    public ArrayList<Board> getList(int pageNumber){
        String SQL = "SELECT * FROM Board WHERE wordsNo < ? AND wordsAvailable=1 ORDER BY wordsNo DESC LIMIT 15 "; //15개까지의 단어를 표시
        ArrayList<Board> list= new ArrayList<Board>(); //Bbs클래스의 인스턴스를 보관하는 list를 담는 객체 생성.
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL); // SQL문장을 실행 준비단계로 만들어준다.
            pstmt.setInt(1, getNext() - (pageNumber -1) * 15 ); // 만약 5개의 단어가있다면 getNext는 6, pageNumber는 15개의 단어가 안됬으므로 1. 따라서 6을반환한다. 
            rs=pstmt.executeQuery(); //select문은 결과값이표시되므로 executeQuery사용.
            while (rs.next()) // 결과값이 있는 경우.
            {
                Board bbs = new Board(); //Bbs클래스의 객체생성.
                bbs.setWordsNo(rs.getInt(1));
                bbs.setWordsEng(rs.getString(2));
                bbs.setWordsKor(rs.getString(3));
                bbs.setUserID(rs.getString(4));
                bbs.setWordsDate(rs.getString(5));
                bbs.setWordsContent(rs.getString(6));
                bbs.setWordsAvailable(rs.getInt(7));
                list.add(bbs); //list array 객체에 해당 내용을 담는다.
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean nextPage(int pageNumber) { //페이징처
        String SQL = "SELECT * FROM Board WHERE wordsNo < ? AND wordsAvailable=1 ORDER BY wordsNo DESC LIMIT 15 "; //15개까지의 단어를 표시
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL); // SQL문장을 실행 준비단계로 만들어준다.
            pstmt.setInt(1, getNext() - (pageNumber -1) * 15 ); // 만약 5개의 단어가있다면 getNext는 6, pageNumber는 15개의 단어가 안됬으므로 1. 따라서 6을반환한다. 
            rs=pstmt.executeQuery(); //select문은 결과값이표시되므로 executeQuery사용.
            if (rs.next()) {
                return true; //결과값이 있다면 true반환.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Board getBbs(int wordsNo) { //글 내용을 불러오는 함수 
        String SQL = "SELECT * FROM Board WHERE wordsNo=?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL); // SQL문장을 실행 준비단계로 만들어준다.
            pstmt.setInt(1, wordsNo);
            rs=pstmt.executeQuery(); //select문은 결과값이표시되므로 executeQuery사용.
            if (rs.next()) {
                Board bbs = new Board();  //함수내에서 Bbs클래스의 객체 bbs를 생성.SQL데이터를 받아와 bbs에 저장. 다시 함수를 소환한 문장에 데이터 전달. 
                bbs.setWordsNo(rs.getInt(1));
                bbs.setWordsEng(rs.getString(2));
                bbs.setWordsKor(rs.getString(3));
                bbs.setUserID(rs.getString(4));
                bbs.setWordsDate(rs.getString(5));
                bbs.setWordsContent(rs.getString(6));
                bbs.setWordsAvailable(rs.getInt(7));
                return bbs; 
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null; //해당 글이 존재하지 않는 경우
    }
}






