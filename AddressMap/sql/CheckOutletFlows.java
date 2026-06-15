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
                    "select " +
                            "sum(case when lat is not null and lng is not null then 1 else 0 end) visible_outlets, " +
                            "sum(case when lat is null or lng is null then 1 else 0 end) hidden_outlets " +
                            "from tab_outlet")) {
                while (rs.next()) {
                    System.out.println("visible_outlets=" + rs.getInt(1)
                            + ", hidden_outlets=" + rs.getInt(2));
                }
            }

            try (ResultSet rs = s.executeQuery(
                    "select " +
                            "(select count(*) from tab_outlet where lat is not null and lng is not null) visible_outlets, " +
                            "sum(case when cnt is null then 1 else 0 end) undecided_flow, " +
                            "sum(case when cnt = 1 then 1 else 0 end) single_flow, " +
                            "sum(case when cnt > 1 then 1 else 0 end) multi_flow " +
                            "from tab_outlet o " +
                            "left join (select outlet_id, count(*) cnt from tab_outlet_agent group by outlet_id) x on x.outlet_id = o.id " +
                            "where o.lat is not null and o.lng is not null")) {
                while (rs.next()) {
                    System.out.println("visible_outlets=" + rs.getInt(1)
                            + ", undecided_flow=" + rs.getInt(2)
                            + ", single_flow=" + rs.getInt(3)
                            + ", multi_flow=" + rs.getInt(4));
                }
            }

            try (ResultSet rs = s.executeQuery(
                    "select outlet_id, group_concat(agent_name order by agent_name) flows " +
                            "from tab_outlet_agent group by outlet_id order by outlet_id limit 8")) {
                while (rs.next()) {
                    System.out.println(rs.getLong(1) + ": " + rs.getString(2));
                }
            }

            try (ResultSet rs = s.executeQuery(
                    "select " +
                            "sum(case when x.cnt is null and o.sales_record is null then 1 else 0 end) undecided_without_sales, " +
                            "sum(case when x.cnt is not null and o.sales_record = '10以下' then 1 else 0 end) sales_under_10, " +
                            "sum(case when x.cnt is not null and o.sales_record = '10-30' then 1 else 0 end) sales_10_30, " +
                            "sum(case when x.cnt is not null and o.sales_record = '30-50' then 1 else 0 end) sales_30_50, " +
                            "sum(case when x.cnt is not null and o.sales_record = '50以上' then 1 else 0 end) sales_over_50 " +
                            "from tab_outlet o " +
                            "left join (select outlet_id, count(*) cnt from tab_outlet_agent group by outlet_id) x on x.outlet_id = o.id " +
                            "where o.lat is not null and o.lng is not null")) {
                while (rs.next()) {
                    System.out.println("undecided_without_sales=" + rs.getInt(1)
                            + ", 10以下=" + rs.getInt(2)
                            + ", 10-30=" + rs.getInt(3)
                            + ", 30-50=" + rs.getInt(4)
                            + ", 50以上=" + rs.getInt(5));
                }
            }
        }
    }
}
