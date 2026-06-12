import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckOutletFlows {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/addressmap?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Tokyo",
                "root",
                "654321");
             Statement s = c.createStatement()) {
            try (ResultSet rs = s.executeQuery(
                    "select count(*) outlets, " +
                            "sum(case when cnt = 1 then 1 else 0 end) single_flow, " +
                            "sum(case when cnt > 1 then 1 else 0 end) multi_flow " +
                            "from (select outlet_id, count(*) cnt from tab_outlet_agent group by outlet_id) x")) {
                while (rs.next()) {
                    System.out.println("outlets=" + rs.getInt(1)
                            + ", single_flow=" + rs.getInt(2)
                            + ", multi_flow=" + rs.getInt(3));
                }
            }

            try (ResultSet rs = s.executeQuery(
                    "select outlet_id, group_concat(agent_name order by agent_name) flows " +
                            "from tab_outlet_agent group by outlet_id order by outlet_id limit 8")) {
                while (rs.next()) {
                    System.out.println(rs.getLong(1) + ": " + rs.getString(2));
                }
            }
        }
    }
}
