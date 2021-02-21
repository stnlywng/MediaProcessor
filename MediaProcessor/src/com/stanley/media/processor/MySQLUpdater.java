package com.stanley.media.processor;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MySQLUpdater {

	public static long postMaxID = 0;
	public static long postmetaMaxID = 0;
	
	
	public static void catchMax(){
		if (postMaxID != 0 && postmetaMaxID != 0){
			return;
		}
		
		try {
			MySQLUtil.dbQuery("select max(id) from wp_posts");
			postMaxID = MySQLUtil.resultSet.getLong(1);
			//
			MySQLUtil.dbQuery("select max(meta_id) from wp_postmeta");
			postmetaMaxID = MySQLUtil.resultSet.getLong(1);
			//
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			MySQLUtil.cleanUp();
		}
	}
	
	
	private static String[] WP_POSTS_COL = {
			"id",				"post_author",			"post_date",		"post_date_gmt",	"post_content",			
			"post_title",		"post_excerpt",			"post_status",		"comment_status",	"ping_status",		
			"post_password",	"post_name",			"to_ping",			"pinged",			"post_modified",	
			"post_modified_gmt","post_content_filtered","post_parent",		"guid",				"menu_order",	
			"post_type",		"post_mime_type",		"comment_count"	
		};
	
	
	private static String[] WP_POSTS_SAMPLE = {	
			"###ID###",			"1",					"###NOW###",		"###NOW###",		"###CONTENT###",					
			"###TITLE###",		"",						"publish",			"closed",			"closed",			
			"",					"###NAME###",			"",					"",					"###NOW###",
			"###NOW###",		"",						"0",				"###GUID###",		"0",
			"###POSTTYPE###",	"", 					"0"
		};
	
	public static void insert(String title, String content){
		postMaxID ++;
		SimpleDateFormat SIMPLE_FULL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now = SIMPLE_FULL_DATE_FORMAT.format(new Date());
		
		//
		Map<String, String> varMap = new HashMap<String,String>();
		{
			varMap.put("###ID###",	postMaxID+"");
			varMap.put("###NOW###",	now);
			varMap.put("###CONTENT###",	content);
			varMap.put("###TITLE###",	title);
			varMap.put("###NAME###",	title);
			varMap.put("###POSTTYPE###","post");
			varMap.put("###GUID###",	"http://3.141.216.183?post_type=post&#038;p="+postMaxID);
		}//http://3.141.216.183?post_type=post&#038;p=5 
		
		String[] postsample = cloneArray(WP_POSTS_SAMPLE);
		
		String sqlupdate = "";
		//
		{
			sqlupdate = sqlupdate + "insert into wp_posts   (";
			for(int i=0; i<WP_POSTS_COL.length; i++){
				if (i>0){
					sqlupdate = sqlupdate + ", ";
				}
				sqlupdate = sqlupdate +WP_POSTS_COL[i] + " ";
			}
			//
			sqlupdate = sqlupdate + ") values (";
			//
			for(int i=0; i<postsample.length; i++){
				if (i>0){
					sqlupdate = sqlupdate + ", ";
				}
				for(String key : varMap.keySet()){
					postsample[i] = postsample[i].replace(key, varMap.get(key));
				}
				sqlupdate = sqlupdate + "'" + MySQLUtil.prep( postsample[i]  ) + "' ";
			}
			//
			sqlupdate = sqlupdate + ");$$$$";
		}
		
		String error = MySQLUtil.dbUpdate(sqlupdate);
		System.out.println("ERROR:\n        " + error);		
	}

	
	
	private static String[] cloneArray(String[] sample){
		String[] array = new String[sample.length];
		for(int i=0; i<sample.length; i++){
			array[i] = sample[i];
		}
		return array;
		
		
	}
	
	private String[][] cloneArray2D(String[][] sample){
		String[][] array = new String[sample.length][];
		for(int i=0; i<sample.length; i++){
			array[i] = new String[sample[i].length];
			for(int j=0; j<sample[i].length; j++){
				array[i][j] = sample[i][j];
			}
		}
		return array;
	}
	
	
}
