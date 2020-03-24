package cn.cedar.data;

import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class JdbcManager {

	private static String url = null;

	private static String user = null;

	private static String password = null;

	private static String driverClass = null;

	public static boolean isAutoClose=true;

	protected Connection connection;

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

	public void setConnection(Connection connection){
		this.connection=connection;
	}

	/**
	 * 注册驱动
	 */
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
			Class.forName(driverClass.trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置事务是否开启
	 * @param autoCommit
	 */
	public final void setAutoCommit(boolean autoCommit){
		isAutoClose=autoCommit;
		try {
			getConnection().setAutoCommit(autoCommit);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 事务提交
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
	 * 事务回滚
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
	 * 连接是否关闭
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
	 * 获取连接
	 * @return
	 */
	public Connection getConnection() {
		try {
			if(isClosed()){
				connection = DriverManager.getConnection(url, user, password);
			}
			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 执行结果为多条数据的DQL
	 * @param sql
	 * @param params
	 * @return
	 */
	public final List<Map<String,Object>> excuteQuery(String sql,Object... params) {
		Connection conn=getConnection();
		List<Map<String,Object>> results=new ArrayList<Map<String,Object>>();
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			ps=conn.prepareStatement(sql);
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
				close(conn,ps,rs);
			}else{
				close(null,ps,rs);
			}
		}
		return results;
	}
	
	/**
	 * 执行DML
	 * @param sql
	 * @param params
	 * @return
	 */
	public final int excute(String sql,Object... params) {
		Connection conn=getConnection();
		PreparedStatement ps=null;
		try {
			ps=conn.prepareStatement(sql);
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
				close(conn,ps,null);
			}else{
				close(null,ps,null);
			}
		}
		return 0;
	}

	/**
	 * 执行DML并返回自增长id
	 * @param sql
	 * @param params
	 * @return
	 */
	public final int excuteGetGeneratedKe(String sql,Object... params) {
		Connection conn=getConnection();
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
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
				close(conn,ps,rs);
			}else{
				close(null,ps,rs);
			}
		}
		return 0;
	}
	
	/**
	 * 执行结果为一条数据的DQL
	 * @param sql
	 * @param params
	 * @return
	 */
	public final Map<String,Object> excuteQueryOne(String sql,Object... params) {
		Connection conn=getConnection();
		Map<String,Object> columnMap=new HashMap<String,Object>();
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			ps=conn.prepareStatement(sql);
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
				close(conn,ps,rs);
			}else{
				close(null,ps,rs);
			}
		}
		return columnMap;
	}
	
	
	/**
	 * 执行结果为一条数据的DQL
	 * @param sql
	 * @param params
	 * @return
	 */
	public final long excuteQueryCount(String sql,Object... params) {
		Connection conn=getConnection();
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			ps=conn.prepareStatement(sql);
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
				close(conn,ps,rs);
			}else{
				close(null,ps,rs);
			}
		}
		return new Long(0);
	}

	/**
	 * 关闭流
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
	   * 将Map中的key由下划线转换为驼峰
	   *
	   * @param map
	   * @return
	   */
	  public static Map<String, Object> formatHumpName(Map<String, Object> map) {
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
	   * 将List中map的key值命名方式格式化为驼峰
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