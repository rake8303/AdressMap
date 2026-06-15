import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SeedOutletMapData {
    private static final String URL = "jdbc:mysql://localhost:3306/addressmap?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Tokyo";
    private static final String USER = "root";
    private static final String PASSWORD = "654321";
    private static final int VISIBLE_OUTLET_COUNT = 80;
    private static final int UNDECIDED_FLOW_COUNT = 20;
    private static final int MULTI_FLOW_COUNT = 15;
    private static final double LOCATION_STDDEV = 8.5d;
    private static final double FLOW_STDDEV = 1.0d;
    private static final String SALES_UNDER_10 = "10\u4ee5\u4e0b";
    private static final String SALES_OVER_50 = "50\u4ee5\u4e0a";
    private static final String[] FLOWS = {
            "\u7e54\u7530\u5bb6",
            "\u8c4a\u81e3\u5bb6",
            "\u5fb3\u5ddd\u5bb6",
            "\u6b66\u7530\u5bb6",
            "\u4e0a\u6749\u5bb6"
    };

    private static final Anchor[] LOCATION_ANCHORS = {
            new Anchor(41.4300, 140.1100),
            new Anchor(40.3800, 141.2600),
            new Anchor(39.7200, 140.1200),
            new Anchor(38.2500, 140.8500),
            new Anchor(36.3700, 140.4800),
            new Anchor(36.5600, 139.8900),
            new Anchor(36.4100, 138.9600),
            new Anchor(35.6850, 139.7520),
            new Anchor(35.2500, 139.1500),
            new Anchor(37.1500, 138.2400),
            new Anchor(36.6950, 137.2130),
            new Anchor(36.5650, 136.6600),
            new Anchor(35.9900, 136.3000),
            new Anchor(35.6800, 138.5800),
            new Anchor(36.2400, 137.9700),
            new Anchor(34.9800, 138.3830),
            new Anchor(35.2200, 136.8450),
            new Anchor(34.9560, 137.1600),
            new Anchor(35.4330, 136.7820),
            new Anchor(34.7300, 136.5100),
            new Anchor(35.1550, 136.1400),
            new Anchor(35.0140, 135.7480),
            new Anchor(34.6870, 135.5260),
            new Anchor(34.8390, 134.6930),
            new Anchor(34.6500, 135.7800),
            new Anchor(34.2280, 135.1710),
            new Anchor(35.5100, 134.2400),
            new Anchor(35.3600, 133.1950),
            new Anchor(34.6650, 133.9350),
            new Anchor(34.4020, 132.4590),
            new Anchor(34.1780, 131.4730),
            new Anchor(34.0750, 134.5550),
            new Anchor(34.3500, 134.0500),
            new Anchor(33.8450, 132.7650),
            new Anchor(33.5600, 133.5320),
            new Anchor(33.5840, 130.3830),
            new Anchor(33.2490, 130.3000),
            new Anchor(32.8060, 130.7050),
            new Anchor(33.2380, 131.6120),
            new Anchor(31.6260, 131.3550),
            new Anchor(31.5960, 130.5570)
    };

    public static void main(String[] args) throws Exception {
        Random random = new Random(20260614L);
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);

            List<Long> outletIds = new ArrayList<>();
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("select id from tab_outlet order by id")) {
                while (rs.next()) {
                    outletIds.add(rs.getLong(1));
                }
            }

            int visibleCount = Math.min(VISIBLE_OUTLET_COUNT, outletIds.size());
            List<String> salesRecordPlan = createSalesRecordPlan(
                    Math.max(0, visibleCount - UNDECIDED_FLOW_COUNT),
                    random);

            int hiddenOutlets;
            try (Statement st = conn.createStatement()) {
                hiddenOutlets = st.executeUpdate(
                        "update tab_outlet set lat = null, lng = null, total_sales_avg = null, hw_sales_avg = null, sales_record = null where id not in (" +
                                "select id from (select id from tab_outlet order by id limit " + visibleCount + ") visible_outlets" +
                                ")");
            }

            int updatedOutlets = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "update tab_outlet " +
                            "set lat = ?, lng = ?, total_sales_avg = ?, hw_sales_avg = ?, sales_record = ? " +
                            "where id = ?")) {
                for (int i = 0; i < visibleCount; i++) {
                    Long id = outletIds.get(i);
                    Anchor anchor = LOCATION_ANCHORS[normalIndex(LOCATION_ANCHORS.length, random, LOCATION_STDDEV)];
                    double[] position = spread(anchor, random);
                    String salesRecord = i < UNDECIDED_FLOW_COUNT ? null : salesRecordPlan.get(i - UNDECIDED_FLOW_COUNT);
                    Integer totalSales = salesRecord == null ? null : randomSalesByRecord(salesRecord, random);
                    Integer hwSales = totalSales == null ? null : Math.max(1, Math.min(totalSales, (int) Math.round(totalSales * (0.22d + random.nextDouble() * 0.56d))));

                    ps.setDouble(1, round(position[0]));
                    ps.setDouble(2, round(position[1]));
                    if (totalSales == null) {
                        ps.setNull(3, java.sql.Types.INTEGER);
                    } else {
                        ps.setInt(3, totalSales);
                    }
                    if (hwSales == null) {
                        ps.setNull(4, java.sql.Types.INTEGER);
                    } else {
                        ps.setInt(4, hwSales);
                    }
                    ps.setString(5, salesRecord);
                    ps.setLong(6, id);
                    updatedOutlets += ps.executeUpdate();
                }
            }

            int deleted;
            try (Statement st = conn.createStatement()) {
                deleted = st.executeUpdate("delete from tab_outlet_agent");
            }

            int inserted = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "insert into tab_outlet_agent(outlet_id, agent_name) values(?, ?)")) {
                int multiFlowLimit = Math.min(UNDECIDED_FLOW_COUNT + MULTI_FLOW_COUNT, visibleCount);
                for (int i = UNDECIDED_FLOW_COUNT; i < visibleCount; i++) {
                    Long outletId = outletIds.get(i);
                    int flowCount = i < multiFlowLimit ? 2 + random.nextInt(2) : 1;
                    List<String> selected = normalFlows(flowCount, random);
                    for (int j = 0; j < flowCount; j++) {
                        ps.setLong(1, outletId);
                        ps.setString(2, selected.get(j));
                        ps.addBatch();
                        inserted++;
                    }
                }
                ps.executeBatch();
            }

            conn.commit();
            System.out.println("total_outlets=" + outletIds.size());
            System.out.println("visible_outlets=" + visibleCount);
            System.out.println("hidden_outlets=" + hiddenOutlets);
            System.out.println("outlets_updated=" + updatedOutlets);
            System.out.println("undecided_flows=" + Math.min(UNDECIDED_FLOW_COUNT, visibleCount));
            System.out.println("multi_flow_outlets=" + Math.max(0, Math.min(MULTI_FLOW_COUNT, visibleCount - UNDECIDED_FLOW_COUNT)));
            System.out.println("single_flow_outlets=" + Math.max(0, visibleCount - UNDECIDED_FLOW_COUNT - MULTI_FLOW_COUNT));
            System.out.println("sales_record_distribution=" + salesRecordSummary(salesRecordPlan));
            System.out.println("old_flows_deleted=" + deleted);
            System.out.println("new_flows_inserted=" + inserted);
        }
    }

    private static double round(double value) {
        return Math.round(value * 1_000_000d) / 1_000_000d;
    }

    private static List<String> createSalesRecordPlan(int count, Random random) {
        String[] labels = {SALES_UNDER_10, "10-30", "30-50", SALES_OVER_50};
        double[] weights = {0.16d, 0.34d, 0.34d, 0.16d};
        List<String> plan = new ArrayList<>();
        int allocated = 0;
        for (int i = 0; i < labels.length; i++) {
            int bucketCount = i == labels.length - 1 ? count - allocated : (int) Math.round(count * weights[i]);
            allocated += bucketCount;
            for (int j = 0; j < bucketCount; j++) {
                plan.add(labels[i]);
            }
        }
        Collections.shuffle(plan, random);
        return plan;
    }

    private static int randomSalesByRecord(String salesRecord, Random random) {
        return switch (salesRecord) {
            case SALES_UNDER_10 -> 1 + random.nextInt(10);
            case "10-30" -> 10 + random.nextInt(21);
            case "30-50" -> 30 + random.nextInt(21);
            case SALES_OVER_50 -> 50 + random.nextInt(31);
            default -> 0;
        };
    }

    private static String salesRecordSummary(List<String> salesRecordPlan) {
        int under10 = 0;
        int tenTo30 = 0;
        int thirtyTo50 = 0;
        int over50 = 0;
        for (String salesRecord : salesRecordPlan) {
            switch (salesRecord) {
                case SALES_UNDER_10 -> under10++;
                case "10-30" -> tenTo30++;
                case "30-50" -> thirtyTo50++;
                case SALES_OVER_50 -> over50++;
                default -> {
                }
            }
        }
        return SALES_UNDER_10 + "=" + under10 + ",10-30=" + tenTo30 + ",30-50=" + thirtyTo50 + "," + SALES_OVER_50 + "=" + over50;
    }

    private static double[] spread(Anchor anchor, Random random) {
        double lat = anchor.lat + random.nextGaussian() * 0.090d;
        double lng = anchor.lng + random.nextGaussian() * 0.090d * lngScale(anchor.lat);
        return new double[]{lat, lng};
    }

    private static List<String> normalFlows(int count, Random random) {
        List<String> selected = new ArrayList<>();
        while (selected.size() < count) {
            String flow = FLOWS[normalIndex(FLOWS.length, random, FLOW_STDDEV)];
            if (!selected.contains(flow)) {
                selected.add(flow);
            }
        }
        return selected;
    }

    private static int normalIndex(int size, Random random, double stddev) {
        int center = size / 2;
        int index = (int) Math.round(center + random.nextGaussian() * stddev);
        return Math.max(0, Math.min(size - 1, index));
    }

    private static double lngScale(double lat) {
        double scale = Math.cos(Math.toRadians(lat));
        return scale == 0 ? 1 : 1 / scale;
    }

    private record Anchor(double lat, double lng) {
    }
}
