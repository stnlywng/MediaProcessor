package com.stanley.media.processor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MySQLUtil {
	//
	private static String dbUrl;
	private static String dbUser;
	private static String dbPwd;
	
	/**
	 * 
	 * @param url
	 * @param usr
	 * @param pwd
	 */
	public static void init(String url, String usr, String pwd){
		dbUrl = url;
		dbUser = usr;
		dbPwd = pwd;
	}
	

	
	/**
	 * jdbc
	 * ====
	 */
	public static Statement statement = null;
	public static ResultSet resultSet = null;
	public static Connection connection = null;
	
	/**
	 * 
	 * @param stat
	 * @return
	 */
	public static String dbUpdate(String statements){ //select
		System.out.println("                        "+statements);
		
		String error = null;
		
		try {
			connection = getDBConnection();
			connection.setAutoCommit(false);
    		
			statement = connection.createStatement();
			for(String stat : statements.split("\\$\\$\\$\\$")){  
				if (stat==null || stat.length()==0){ 
					continue;
				}
				if (stat.endsWith(";")){
					stat = stat.substring(0, stat.length()-1);
				}
				statement.executeUpdate(stat);
			}
			
			connection.commit();
		}
		catch(Exception e){
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			System.out.println("error-"+e.getMessage());
			error = e.getMessage();
		}
		finally{
			cleanUp();
		}
		//
		return error;
	}

	
	/**
	 * 
	 * @param stat
	 * @return
	 * @throws Exception
	 */
	public static boolean dbQuery(String stat) {
		if (stat.endsWith(";")){
			stat = stat.substring(0, stat.length()-1);
		}
		try {
			connection = getDBConnection();
			connection.setAutoCommit(false);
			
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			resultSet = statement.executeQuery(stat);
		    return resultSet.next();
		}
		catch(Exception e){
			return false;
		}
	}

	/**
	 * 
	 * @param content
	 * @return
	 */
	public static String prep(String content){
		content = content.replaceAll("'", "''");
		return content;
	}
	
	
	/**
	 * This method helps to clean up the jdbc objects.
	 * 
	 */
	public static void cleanUp() {
		try {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
				} finally {
					if (connection != null) {
						connection.close();
					}
				}
			}
		} catch (SQLException ex) {
			System.out.println("error-"+ex.getMessage());
		}
	}	

	/**
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static Connection getDBConnection() throws ClassNotFoundException, SQLException {			
		String driver = "com.mysql.jdbc.Driver";
		//
		Class.forName (driver);		
		Connection con = DriverManager.getConnection (dbUrl, dbUser, dbPwd);
		return con;
	}

	
	
	/**
	 * Helper
	 * =======
	 */
	
	private static String DATE_FULL = "yyyy-MM-dd HH:mm:ss";
	public static String DATE_FULL_ORC = "YYYY-MM-DD HH24:MI:SS";
	
	private static String DATE_ONLY = "yyyy-MM-dd";
	public static String DATE_ONLY_ORC = "YYYY-MM-DD";
	
	protected static SimpleDateFormat sdfOnly = new SimpleDateFormat(DATE_ONLY); 
	protected static SimpleDateFormat sdfFull = new SimpleDateFormat(DATE_FULL); 

	protected static String formatFullDate(Date date){
		if (date == null){
			return "";
		}
		return sdfFull.format(date);
	}
	
	protected static String formatOnlyDate(Date date){
		if (date == null){
			return "";
		}
		return sdfOnly.format(date);
	}
	
	protected static String ORC_toOnlyDate(Date date){
		return "to_date('"+formatOnlyDate(date)+"', '"+DATE_ONLY_ORC+"')";
	}

	protected static String ORC_toFullDate(Date date){
		return "to_date('"+formatFullDate(date)+"', '"+DATE_FULL_ORC+"')";
	}
	
	/**
	 * 
	 */
	public static void waitForEnter() {
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
