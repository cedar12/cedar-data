/**
 *	  Copyright 2020 cedar12.zxd@qq.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.cedar.data;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.*;
import java.util.*;


/**
 * @author cedar12.zxd@qq.com
 */
public class JdbcManager {

	private static String url = null;

	private static String user = null;

	private static String password = null;

	private static String driverClass = null;

	public static boolean isAutoClose=true;

	private Connection connection;

	static {
		register();
	}

	public static String getUrl() {
		return url;
	}

	public static String getUser() {
		return user;
	}

	public static String getDriverClass() {
		return driverClass;
	}

	protected  static String getPassword(){return password;}

	protected static DataSource dataSource=null;

	private static boolean isProperty=false;

	public static void setDataSource(DataSource dataSource){
		JdbcManager.dataSource=dataSource;
	}

	public JdbcManager(){
		if(dataSource==null){
			setDataSource();
		}
		//getConn();
	}
	public JdbcManager(DataSource dataSource){
		JdbcManager.dataSource=dataSource;
		getConn();
	}

	private void setDataSource(){
		dataSource=init();
	}

	public DataSource init(){return null;}


	private static void register(){
		try {
			Properties prop = new Properties();
			Class clazz = JdbcManager.class;
			InputStream in = clazz.getResourceAsStream("/jdbc.properties");
			prop.load(in);
			url = prop.getProperty("url");
			user = prop.getProperty("user");
			password = prop.getProperty("password");
			driverClass = prop.getProperty("driverClass");
			isProperty=true;
			Class.forName(driverClass.trim());
		} catch (Exception e) {
			isProperty=false;
		}
	}

	/**
	 * enabled transaction
	 * @param autoCommit
	 */
	public final void setAutoCommit(boolean autoCommit){
		isAutoClose=autoCommit;
		try {
			getConn().setAutoCommit(autoCommit);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * commit transaction
	 */
	public final void commit(){
		try {
			connection.commit();
			connection.setAutoCommit(false);
			close(connection,null,null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		isAutoClose=true;
	}

	/**
	 * rollback transaction
	 */
	public final void rollback(){
		try {
			connection.rollback();
			connection.setAutoCommit(false);
			close(connection,null,null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		isAutoClose=true;
	}

	/**
	 * connection is closed
	 * @return
	 */
	public final boolean isClosed(){
		try {
			if(connection==null||connection.isClosed()){
				return true;
			}else{
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * return connection
	 * @return
	 */
	public Connection getConnection() {
		try {
			return dataSource!=null?dataSource.getConnection():DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private Connection getConn(){
		if(isClosed()){
			connection = getConnection();
		}
		return connection;
	}
	
	/**
	 * execute DQL
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> excuteQuery(String sql,Object... params) {
		Connection connection=getConn();
		List<Map<String,Object>> results=new ArrayList<Map<String,Object>>();
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			ps=connection.prepareStatement(sql);
			for(int i=1;i<=params.length;i++) {
				ps.setObject(i, params[i-1]);
			}
			rs=ps.executeQuery();
			ResultSetMetaData rmd=rs.getMetaData();
			int count=rmd.getColumnCount();
			while (rs.next()) {
				Map<String,Object> columnMap=new HashMap<String,Object>();
				for(int i=1;i<=count;i++) {
					columnMap.put(rmd.getColumnLabel(i), rs.getObject(i));
				}
				results.add(columnMap);
			}
			return results;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(isAutoClose){
				close(connection,ps,rs);
			}else{
				close(null,ps,rs);
			}
		}
		return results;
	}
	
	/**
	 * execute DML
	 * @param sql
	 * @param params
	 * @return
	 */
	public int excute(String sql,Object... params) {
		Connection connection=getConn();
		PreparedStatement ps=null;
		try {
			ps=connection.prepareStatement(sql);
			for(int i=1;i<=params.length;i++) {
				ps.setObject(i, params[i-1]);
			}
			if(sql.trim().startsWith("select")||sql.trim().startsWith("SELECT")) {
				return ps.getGeneratedKeys().getInt(1);
			}
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(isAutoClose){
				close(connection,ps,null);
			}else{
				close(null,ps,null);
			}
		}
		return 0;
	}

	/**
	 * execute DML and return generated key
	 * @param sql
	 * @param params
	 * @return
	 */
	public int excuteGetGeneratedKey(String sql,Object... params) {
		Connection connection=getConn();
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			ps=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			for(int i=1;i<=params.length;i++) {
				ps.setObject(i, params[i-1]);
			}
			int rows=ps.executeUpdate();
			if(rows>0){
				rs = ps.getGeneratedKeys();
				if(rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(isAutoClose){
				close(connection,ps,rs);
			}else{
				close(null,ps,rs);
			}
		}
		return 0;
	}
	
	/**
	 * exucte DQL return a data
	 * @param sql
	 * @param params
	 * @return
	 */
	public  Map<String,Object> excuteQueryOne(String sql,Object... params) {
		Connection connection=getConn();
		Map<String,Object> columnMap=new HashMap<String,Object>();
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			ps=connection.prepareStatement(sql);
			for(int i=1;i<=params.length;i++) {
				ps.setObject(i, params[i-1]);
			}
			rs=ps.executeQuery();
			ResultSetMetaData rmd=rs.getMetaData();
			int count=rmd.getColumnCount();
			if (rs.next()) {
				for(int i=1;i<=count;i++) {
					columnMap.put(rmd.getColumnLabel(i), rs.getObject(i));
				}
				return columnMap;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(isAutoClose){
				close(connection,ps,rs);
			}else{
				close(null,ps,rs);
			}
		}
		return columnMap;
	}
	
	
	/**
	 * exucte DQL return a data
	 * @param sql
	 * @param params
	 * @return
	 */
	public long excuteQueryCount(String sql,Object... params) {
		Connection connection=getConn();
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			ps=connection.prepareStatement(sql);
			for(int i=1;i<=params.length;i++) {
				ps.setObject(i, params[i-1]);
			}
			rs=ps.executeQuery();
			if (rs.next()) {
				return Long.parseLong(String.valueOf(rs.getObject(1)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(isAutoClose){
				close(connection,ps,rs);
			}else{
				close(null,ps,rs);
			}
		}
		return new Long(0);
	}

	/**
	 * close the stream
	 * @param conn
	 * @param ps
	 * @param rs
	 */
	public static void close(Connection conn,PreparedStatement ps,ResultSet rs) {
		if(rs!=null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(ps!=null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(conn!=null) {
			try {
				if(!conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	   *
	   *
	   * @param map
	   * @return
	   */
	  public static Map<String, Object> formatHumpName(Map<String, Object> map) {
	  	if(map==null){return map;}
	    Map<String, Object> newMap = new HashMap<String, Object>();
	    Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
	    while (it.hasNext()) {
	      Map.Entry<String, Object> entry = it.next();
	      String key = entry.getKey();
	      String newKey = toFormatCol(key);
	      newMap.put(newKey, entry.getValue());
	    }
	    return newMap;
	  }

	  public static String toFormatCol(String colName) {
	    StringBuilder sb = new StringBuilder();
	    if(!colName.contains("_")){
	    	return colName;
		}
	    String[] str = colName.toLowerCase().split("_");
	    int i = 0;
	    for (String s : str) {
	      if (s.length() == 1) {
	        s = s.toUpperCase();
	      }
	      i++;
	      if (i == 1) {
	        sb.append(s);
	        continue;
	      }
	      if (s.length() > 0) {
	        sb.append(s.substring(0, 1).toUpperCase());
	        sb.append(s.substring(1));
	      }
	    }
	    return sb.toString();
	  }

	  /**
	   *
	   *
	   * @param list
	   * @return
	   */
	  public static List<Map<String, Object>> formatHumpNameForList(List<Map<String, Object>> list) {
	    List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
	    for (Map<String, Object> o : list) {
	      newList.add(formatHumpName(o));
	    }
	    return newList;
	  }
	
}