import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckOutletCoordinates {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/addressmap?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Tokyo",
                "root",
                "654321");
             Statement s = c.createStatement()) {
            try (ResultSet rs = s.executeQuery(
                    "select count(*) total, min(lat), max(lat), min(lng), max(lng) from tab_outlet")) {
                while (rs.next()) {
                    System.out.println("total=" + rs.getInt(1)
                            + ", lat_range=" + rs.getDouble(2) + ".." + rs.getDouble(3)
                            + ", lng_range=" + rs.getDouble(4) + ".." + rs.getDouble(5));
                }
            }
            try (ResultSet rs = s.executeQuery(
                    "select id, jp_company_name, lat, lng from tab_outlet order by id limit 10")) {
                while (rs.next()) {
                    System.out.println(rs.getLong(1) + ": " + rs.getDouble(3) + ", " + rs.getDouble(4));
                }
            }
        }
    }
}
