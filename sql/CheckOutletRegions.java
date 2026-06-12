import java.sql.*;
public class CheckOutletRegions {
  public static void main(String[] args) throws Exception {
    Class.forName("com.mysql.cj.jdbc.Driver");
    try (Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/addressmap?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Tokyo", "root", "654321"); Statement s = c.createStatement(); ResultSet rs = s.executeQuery("select region, count(*) from tab_outlet group by region order by count(*) desc")) {
      while (rs.next()) System.out.println(rs.getString(1) + "=" + rs.getInt(2));
    }
  }
}