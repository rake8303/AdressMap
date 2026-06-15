import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class RestoreRealOutletAddresses {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/addressmap?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Tokyo",
                "root",
                "654321");
             Statement s = c.createStatement()) {
            int updated = s.executeUpdate(
                    "update tab_outlet o " +
                            "join tab_outlet_backup_20260603 b on b.id = o.id " +
                            "set o.jp_company_name = b.jp_company_name, " +
                            "o.short_company_name = b.short_company_name, " +
                            "o.abbreviation = b.abbreviation, " +
                            "o.headquarters_address = b.headquarters_address, " +
                            "o.region = b.region");

            System.out.println("updated_outlets=" + updated);

            try (ResultSet rs = s.executeQuery(
                    "select count(*) total, " +
                            "sum(case when lat is not null and lng is not null then 1 else 0 end) visible_outlets, " +
                            "sum(case when headquarters_address is not null and headquarters_address <> '' then 1 else 0 end) address_count " +
                            "from tab_outlet")) {
                if (rs.next()) {
                    System.out.println("total=" + rs.getInt(1));
                    System.out.println("visible_outlets=" + rs.getInt(2));
                    System.out.println("address_count=" + rs.getInt(3));
                }
            }

            try (ResultSet rs = s.executeQuery(
                    "select id, jp_company_name, headquarters_address, lat, lng " +
                            "from tab_outlet order by id limit 5")) {
                while (rs.next()) {
                    System.out.println(rs.getLong(1) + ": " +
                            rs.getString(2) + " | " +
                            rs.getString(3) + " | " +
                            rs.getString(4) + "," + rs.getString(5));
                }
            }
        }
    }
}
