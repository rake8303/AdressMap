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
    private static final String[] FLOWS = {"XSOL", "DMM", "WWB", "高島", "韓華"};
    private static final Anchor[] DEFAULT_MAINLAND_ANCHORS = {
            new Anchor(43.061771, 141.354451), // Sapporo
            new Anchor(40.824623, 140.740593), // Aomori
            new Anchor(39.703619, 141.152684), // Morioka
            new Anchor(38.268215, 140.869356), // Sendai
            new Anchor(39.718614, 140.102364), // Akita
            new Anchor(38.240436, 140.363634), // Yamagata
            new Anchor(37.760833, 140.474728), // Fukushima
            new Anchor(36.341813, 140.446793), // Mito
            new Anchor(36.565725, 139.883565), // Utsunomiya
            new Anchor(36.391208, 139.060156), // Maebashi
            new Anchor(35.861729, 139.645482), // Saitama
            new Anchor(35.607404, 140.106536), // Chiba
            new Anchor(35.681236, 139.767125), // Tokyo
            new Anchor(35.443708, 139.638026), // Yokohama
            new Anchor(37.902552, 139.023095), // Niigata
            new Anchor(36.695951, 137.213677), // Toyama
            new Anchor(36.561325, 136.656205), // Kanazawa
            new Anchor(36.064067, 136.219493), // Fukui
            new Anchor(35.663511, 138.568449), // Kofu
            new Anchor(36.648583, 138.194766), // Nagano
            new Anchor(35.423298, 136.760654), // Gifu
            new Anchor(34.975562, 138.382759), // Shizuoka
            new Anchor(35.170915, 136.881537), // Nagoya
            new Anchor(34.730283, 136.508591), // Tsu
            new Anchor(35.004531, 135.868590), // Otsu
            new Anchor(35.011636, 135.768029), // Kyoto
            new Anchor(34.693738, 135.502165), // Osaka
            new Anchor(34.690083, 135.195511), // Kobe
            new Anchor(34.685087, 135.805000), // Nara
            new Anchor(34.230511, 135.170808), // Wakayama
            new Anchor(35.501133, 134.235091), // Tottori
            new Anchor(35.472296, 133.050499), // Matsue
            new Anchor(34.655146, 133.919502), // Okayama
            new Anchor(34.385203, 132.455293), // Hiroshima
            new Anchor(34.178496, 131.473727), // Yamaguchi
            new Anchor(34.070270, 134.554844), // Tokushima
            new Anchor(34.342787, 134.046574), // Takamatsu
            new Anchor(33.839157, 132.765575), // Matsuyama
            new Anchor(33.559706, 133.531079), // Kochi
            new Anchor(33.590355, 130.401716), // Fukuoka
            new Anchor(33.263482, 130.300858), // Saga
            new Anchor(32.750286, 129.877667), // Nagasaki
            new Anchor(32.803100, 130.707891), // Kumamoto
            new Anchor(33.238172, 131.612619), // Oita
            new Anchor(31.907673, 131.420241), // Miyazaki
            new Anchor(31.596554, 130.557116)  // Kagoshima
    };
    private static final String[] ANCHOR_REGIONS = {
            "北海道",
            "東北", "東北", "東北", "東北", "東北", "東北",
            "関東", "関東", "関東", "関東", "関東", "関東", "関東",
            "北陸", "北陸", "北陸", "北陸",
            "中部", "中部", "中部", "中部", "中部",
            "関西", "関西", "関西", "関西", "関西", "関西", "関西",
            "中国", "中国", "中国", "中国", "中国",
            "四国", "四国", "四国", "四国",
            "九州", "九州", "九州", "九州", "九州", "九州", "九州"
    };

    public static void main(String[] args) throws Exception {
        Random random = new Random(20260604L);
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

            int updatedLocations = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "update tab_outlet " +
                            "set lat = ?, lng = ?, region = ? " +
                            "where id = ?")) {
                for (Long id : outletIds) {
                    int anchorIndex = (int) ((id - 1) % DEFAULT_MAINLAND_ANCHORS.length);
                    Anchor anchor = DEFAULT_MAINLAND_ANCHORS[anchorIndex];
                    ps.setDouble(1, round(jitter(anchor.lat, random)));
                    ps.setDouble(2, round(jitter(anchor.lng, random)));
                    ps.setString(3, ANCHOR_REGIONS[anchorIndex]);
                    ps.setLong(4, id);
                    updatedLocations += ps.executeUpdate();
                }
            }

            int deleted;
            try (Statement st = conn.createStatement()) {
                deleted = st.executeUpdate("delete from tab_outlet_agent");
            }

            int inserted = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "insert into tab_outlet_agent(outlet_id, agent_name) values(?, ?)")) {
                for (int i = 0; i < outletIds.size(); i++) {
                    Long outletId = outletIds.get(i);
                    int flowCount = chooseFlowCount(i, random);
                    List<String> selected = new ArrayList<>(List.of(FLOWS));
                    Collections.shuffle(selected, random);
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
            System.out.println("outlets=" + outletIds.size());
            System.out.println("locations_updated=" + updatedLocations);
            System.out.println("old_flows_deleted=" + deleted);
            System.out.println("new_flows_inserted=" + inserted);
        }
    }

    private static int chooseFlowCount(int index, Random random) {
        if (index % 5 == 0) {
            return 3;
        }
        if (index % 3 == 0) {
            return 2;
        }
        return 1 + random.nextInt(2);
    }

    private static double round(double value) {
        return Math.round(value * 1_000_000d) / 1_000_000d;
    }

    private static double jitter(double value, Random random) {
        return value + (random.nextDouble() - 0.5d) * 0.08d;
    }

    private record Anchor(double lat, double lng) {
    }
}
