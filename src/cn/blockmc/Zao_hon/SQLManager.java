package cn.blockmc.Zao_hon;

import java.sql.Connection;
//import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.entity.Player;

public class SQLManager {
	private static final String CREATE_SIGNIN_TABLE = "CREATE TABLE IF NOT EXISTS signin (Name VARCHAR(30),UUID VARCHAR(40),Date VARCHAR(30),isLate BOOLEAN)";
	private static final String CREATE_PATCH_TABLE = "CREATE TABLE IF NOT EXISTS patch (Name VARCHAR(30),UUID VARCHAR(40),Patch INTEGER)";
	private static final String CREATE_REWARDS_TABLE = "CREATE TABLE IF NOT EXISTS rewards (Name VARCHAR(30),UUID VARCHAR(40),Reward VARCHAR(30))";
	private static final String INSERT_PLAYER_SIGNIN = "INSERT INTO signin VALUES(?,?,?,?)";
	private static final String INSERT_PLAYER_REWARD = "INSERT INTO rewards VALUES(?,?,?)";
	private static final String INSERT_NEW_PLAYER_PATCH = "INSERT INTO patch (Name,UUID,Patch) SELECT ?,?,0 WHERE NOT EXISTS (SELECT * FROM patch WHERE UUID = ?) ";
	private static final String SELECT_PLAYER_REWARDS = "SELECT Reward FROM rewards WHERE UUID = ?";
	private static final String SELECT_PLAYER_SIGNIN = "SELECT Date FROM signin WHERE UUID = ? ORDER BY Date";
	private static final String SELECT_ISSIGNIN_TODAY = "SELECT * FROM signin WHERE UUID = ? AND Date = ?";
	private static final String SELECT_PLAYER_PATCH = "SELECT Patch FROM patch WHERE UUID = ?";
	private static final String SELECT_FIRST_SIGNIN = "SELECT UUID FROM signin WHERE Date = ?";
	private static final String UPDATE_SET_PLAYER_PATCH = "UPDATE patch SET Patch = ? WHERE UUID = ?";
	private static final String UPDATE_ADD_PLAYER_PATCH = "UPDATE patch SET Patch = Patch + ? where UUID = ?";
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private ConnectionPool pool;
	// private Connection conn;

	public SQLManager(Signin plugin) {
		boolean b = plugin.getConfig().getBoolean("MYSQL.Enable");
		if (b) {
			String host = plugin.getConfig().getString("MYSQL.Host");
			String port = plugin.getConfig().getString("MYSQL.Port");
			String database = plugin.getConfig().getString("MYSQL.DatabaseName");
			String name = plugin.getConfig().getString("MYSQL.UserName");
			String password = plugin.getConfig().getString("MYSQL.Password");
			PoolConfig pconfig = new PoolConfig();
			pconfig.setDriverClassName(JDBC_DRIVER);
			pconfig.setJDBCUrl(
					"jdbc:mysql://" + host + ":" + port + "/" + database + "?characterEncoding=utf8&useSSL=false&");
			pconfig.setUserName(name);
			pconfig.setPassword(password);
			pconfig.setTestQuery("SELECT Name FROM Reward");
			pool = new ConnectionPool(pconfig);
		} else {
			PoolConfig pconfig = new PoolConfig();
			pconfig.setDriverClassName("org.sqlite.JDBC");
			pconfig.setJDBCUrl("jdbc:sqlite:" + plugin.getDataFolder() + "/Signin.db");
			pconfig.setTestQuery("SELECT Name FROM Reward");
			pool = new ConnectionPool(pconfig);
		}
		createTable();

	}

	public boolean isTodayFirstSignin() {
		Calendar c = Calendar.getInstance();
		String date = c.get(Calendar.YEAR) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.DAY_OF_MONTH);
		try {
			Connection conn = pool.getConnection();
			PreparedStatement s = conn.prepareStatement(SELECT_FIRST_SIGNIN);
			s.setString(1, date);
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				return false;
			} else {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public int getPlayerPatch(Player p) {
		int i = 0;
		try {
			Connection conn = pool.getConnection();
			PreparedStatement s = conn.prepareStatement(SELECT_PLAYER_PATCH);
			s.setString(1, p.getUniqueId().toString());
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				i = rs.getInt(1);
			}
			pool.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}

	public void addPlayerPatch(Player p, int i) {
		try {
			Connection conn = pool.getConnection();
			PreparedStatement s = conn.prepareStatement(UPDATE_ADD_PLAYER_PATCH);
			s.setInt(1, i);
			s.setString(2, p.getUniqueId().toString());
			s.execute();
			pool.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setPlayerPatch(Player p, int i) {
		try {
			Connection conn = pool.getConnection();
			PreparedStatement s = conn.prepareStatement(UPDATE_SET_PLAYER_PATCH);
			s.setInt(1, i);
			s.setString(2, p.getUniqueId().toString());
			s.execute();
			pool.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addNewPlayerPatch(Player p) {
		try {
			Connection conn = pool.getConnection();
			PreparedStatement s = conn.prepareStatement(INSERT_NEW_PLAYER_PATCH);
			s.setString(1, p.getName());
			s.setString(2, p.getUniqueId().toString());
			s.setString(3, p.getUniqueId().toString());
			s.execute();
			pool.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<String> getPlayerRewards(Player p) {
		List<String> list = new ArrayList<String>();
		try {
			Connection conn = pool.getConnection();
			PreparedStatement s = conn.prepareStatement(SELECT_PLAYER_REWARDS);
			s.setString(1, p.getUniqueId().toString());
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				list.add(rs.getString(1));
			}
			pool.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public void addPlayerReward(Player p, String reward) {
		try {
			Connection conn = pool.getConnection();
			PreparedStatement s = conn.prepareStatement(INSERT_PLAYER_REWARD);
			s.setString(1, p.getName());
			s.setString(2, p.getUniqueId().toString());
			s.setString(3, reward);
			s.execute();
			pool.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean isSignINToday(Player player) {
		boolean b = false;
		try {
			Connection conn = pool.getConnection();
			PreparedStatement p = conn.prepareStatement(SELECT_ISSIGNIN_TODAY);
			Calendar c = Calendar.getInstance();
			String date = c.get(Calendar.YEAR) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.DAY_OF_MONTH);
			p.setString(1, player.getUniqueId().toString());
			p.setString(2, date);
			ResultSet rs = p.executeQuery();
			if (rs.next()) {
				b = true;
			}
			pool.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return b;
	}

	public List<String> getAllPlayerSignin(Player p) {
		List<String> list = new ArrayList<String>();
		try {
			Connection conn = pool.getConnection();
			PreparedStatement s = conn.prepareStatement(SELECT_PLAYER_SIGNIN);
			s.setString(1, p.getUniqueId().toString());
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				list.add(rs.getString(1));
			}
			pool.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public void inserctPlayerSignin(Player p, String date, Boolean islate) {
		try {
			Connection conn = pool.getConnection();
			PreparedStatement s = conn.prepareStatement(INSERT_PLAYER_SIGNIN);
			s.setString(1, p.getName());
			s.setString(2, p.getUniqueId().toString());
			s.setString(3, date);
			s.setBoolean(4, islate);
			s.execute();
			pool.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		pool.close();
	}

	private void createTable() {
		try {
			pool.getConnection().prepareStatement(CREATE_PATCH_TABLE).execute();
			pool.getConnection().prepareStatement(CREATE_REWARDS_TABLE).execute();
			pool.getConnection().prepareStatement(CREATE_SIGNIN_TABLE).execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
