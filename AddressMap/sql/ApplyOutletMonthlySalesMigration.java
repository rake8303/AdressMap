import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ApplyOutletMonthlySalesMigration {
    public static void main(String[] args) throws Exception {
        String sql = new String(
                Files.readAllBytes(Paths.get("20260614_outlet_monthly_sales.sql")),
                StandardCharsets.UTF_8);

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/addressmap?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Tokyo",
                "root",
                "654321");
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
            addColumnIfMissing(connection, statement, "updated_by", "VARCHAR(64) NULL COMMENT '最后修改人'");
            addColumnIfMissing(connection, statement, "updated_at", "DATETIME NULL COMMENT '最后修改时间'");
            System.out.println("tab_outlet_monthly_sales=ready");
        }
    }

    private static void addColumnIfMissing(
            Connection connection,
            Statement statement,
            String columnName,
            String columnDefinition) throws Exception {
        String checkSql = "select count(*) from information_schema.columns " +
                "where table_schema = database() " +
                "and table_name = 'tab_outlet_monthly_sales' " +
                "and column_name = '" + columnName + "'";
        try (ResultSet rs = statement.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                statement.execute("alter table tab_outlet_monthly_sales add column " +
                        columnName + " " + columnDefinition);
            }
        }
    }
}
