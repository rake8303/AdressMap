import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SeedOutletMonthlySalesJanJun {
    private static final String URL = "jdbc:mysql://localhost:3306/addressmap?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Tokyo";
    private static final String USER = "root";
    private static final String PASSWORD = "654321";
    private static final int TARGET_OUTLET_COUNT = 88;
    private static final int START_YEAR = 2026;

    public static void main(String[] args) throws Exception {
        Random random = new Random(20260614L);
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);

            List<OutletRow> outlets = loadOutlets(conn);
            int outletCount = Math.min(TARGET_OUTLET_COUNT, outlets.size());
            List<OutletRow> selectedOutlets = outlets.subList(0, outletCount);

            int deleted;
            try (Statement st = conn.createStatement()) {
                deleted = st.executeUpdate("delete from tab_outlet_monthly_sales");
            }

            int inserted = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "insert into tab_outlet_monthly_sales(outlet_id, sales_month, quantity, product_name, remark, updated_by, updated_at) values(?, ?, ?, ?, ?, ?, ?)")) {
                for (OutletRow outlet : selectedOutlets) {
                    double base = outlet.baseSales > 0 ? outlet.baseSales : 24 + random.nextInt(22);
                    double trend = 0.96d + random.nextDouble() * 0.12d;
                    double monthSlope = 0.03d + random.nextDouble() * 0.04d;

                    for (int month = 1; month <= 6; month++) {
                        double seasonal = 0.94d + (month * monthSlope);
                        int quantity = Math.max(1, (int) Math.round(base * trend * seasonal + random.nextGaussian() * 2.5d));
                        String salesMonth = String.format("%04d-%02d", START_YEAR, month);
                        String productName = pickProductName(outlet, random, month);
                        String remark = buildRemark(outlet, quantity, month);
                        Timestamp updatedAt = Timestamp.valueOf(LocalDate.of(START_YEAR, month, 20).atTime(10 + random.nextInt(7), random.nextInt(60)));

                        ps.setLong(1, outlet.id);
                        ps.setString(2, salesMonth);
                        ps.setInt(3, quantity);
                        ps.setString(4, productName);
                        ps.setString(5, remark);
                        ps.setString(6, "seed-bot");
                        ps.setTimestamp(7, updatedAt);
                        ps.addBatch();
                        inserted++;
                    }
                }
                ps.executeBatch();
            }

            conn.commit();
            System.out.println("outlets_selected=" + outletCount);
            System.out.println("old_monthly_sales_deleted=" + deleted);
            System.out.println("new_monthly_sales_inserted=" + inserted);
        }
    }

    private static List<OutletRow> loadOutlets(Connection conn) throws Exception {
        List<OutletRow> outlets = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "select id, jp_company_name, region, total_sales_avg from tab_outlet order by id")) {
            while (rs.next()) {
                outlets.add(new OutletRow(
                        rs.getLong("id"),
                        rs.getString("jp_company_name"),
                        rs.getString("region"),
                        rs.getObject("total_sales_avg") == null ? 0d : rs.getDouble("total_sales_avg")));
            }
        }
        return outlets;
    }

    private static String pickProductName(OutletRow outlet, Random random, int month) {
        String[] products = {
                "主力商材",
                "重点提案品",
                "定番商材",
                "新規提案品",
                "季節商材"
        };
        return products[(int) ((outlet.id + month + random.nextInt(products.length)) % products.length)];
    }

    private static String buildRemark(OutletRow outlet, int quantity, int month) {
        return outlet.name + "の" + month + "月実績は" + quantity + "件で、前年同月比を意識した自然な推移。";
    }

    private static class OutletRow {
        private final long id;
        private final String name;
        private final String region;
        private final double baseSales;

        private OutletRow(long id, String name, String region, double baseSales) {
            this.id = id;
            this.name = name == null ? "" : name;
            this.region = region == null ? "" : region;
            this.baseSales = baseSales;
        }
    }
}
