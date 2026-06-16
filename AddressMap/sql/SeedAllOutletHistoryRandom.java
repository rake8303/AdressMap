import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SeedAllOutletHistoryRandom {
    private static final String URL = "jdbc:mysql://localhost:3306/addressmap?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Tokyo";
    private static final String USER = "root";
    private static final String PASSWORD = "654321";
    private static final int TARGET_OUTLET_COUNT = 88;
    private static final int TARGET_HISTORY_COUNT = 166;

    public static void main(String[] args) throws Exception {
        Random random = new Random(20260614L);
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);

            List<OutletRow> outlets = loadOutlets(conn);
            int outletCount = Math.min(TARGET_OUTLET_COUNT, outlets.size());
            List<OutletRow> selectedOutlets = outlets.subList(0, outletCount);

            Map<Long, List<String>> agentMap = loadOutletAgents(conn, selectedOutlets);

            int deleted;
            try (Statement st = conn.createStatement()) {
                deleted = st.executeUpdate("delete from tab_outlet_history");
            }

            List<Integer> historyPlan = buildHistoryPlan(outletCount, TARGET_HISTORY_COUNT, random);
            int inserted = 0;

            try (PreparedStatement ps = conn.prepareStatement(
                    "insert into tab_outlet_history(history_id, outlet_id, created_by, created_at, updated_by, updated_at, remark, agent) values(?, ?, ?, ?, ?, ?, ?, ?)")) {
                for (int i = 0; i < selectedOutlets.size(); i++) {
                    OutletRow outlet = selectedOutlets.get(i);
                    int historyCount = historyPlan.get(i);
                    List<String> agents = agentMap.get(outlet.id);
                    for (int j = 0; j < historyCount; j++) {
                        String agent = SeedOutletBusinessFlowHistory.pickAgent(agents, random);
                        String topic = SeedOutletBusinessFlowHistory.pickTopic(random);
                        java.sql.Timestamp visitTime = SeedOutletBusinessFlowHistory.randomVisitTime(random, 150);
                        String remark = SeedOutletBusinessFlowHistory.buildRemark(outlet.name, outlet.region, agent, topic);

                        ps.setString(1, SeedOutletBusinessFlowHistory.newHistoryId());
                        ps.setLong(2, outlet.id);
                        ps.setString(3, "seed-bot");
                        ps.setTimestamp(4, visitTime);
                        ps.setString(5, "seed-bot");
                        ps.setTimestamp(6, visitTime);
                        ps.setString(7, remark);
                        ps.setString(8, agent);
                        ps.addBatch();
                        inserted++;
                    }
                }
                ps.executeBatch();
            }

            conn.commit();
            System.out.println("outlets_selected=" + outletCount);
            System.out.println("old_histories_deleted=" + deleted);
            System.out.println("new_histories_inserted=" + inserted);
        }
    }

    private static List<OutletRow> loadOutlets(Connection conn) throws Exception {
        List<OutletRow> outlets = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "select id, jp_company_name, region from tab_outlet order by id")) {
            while (rs.next()) {
                outlets.add(new OutletRow(
                        rs.getLong("id"),
                        rs.getString("jp_company_name"),
                        rs.getString("region")));
            }
        }
        return outlets;
    }

    private static Map<Long, List<String>> loadOutletAgents(Connection conn, List<OutletRow> outlets) throws Exception {
        Map<Long, List<String>> agentMap = new HashMap<>();
        StringBuilder sql = new StringBuilder(
                "select outlet_id, agent_name from tab_outlet_agent where outlet_id in (");
        for (int i = 0; i < outlets.size(); i++) {
            if (i > 0) {
                sql.append(',');
            }
            sql.append('?');
        }
        sql.append(") order by outlet_id, agent_name");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < outlets.size(); i++) {
                ps.setLong(i + 1, outlets.get(i).id);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long outletId = rs.getLong("outlet_id");
                    String agentName = rs.getString("agent_name");
                    List<String> agents = agentMap.get(outletId);
                    if (agents == null) {
                        agents = new ArrayList<>();
                        agentMap.put(outletId, agents);
                    }
                    agents.add(agentName);
                }
            }
        }
        return agentMap;
    }

    private static List<Integer> buildHistoryPlan(int outletCount, int targetHistoryCount, Random random) {
        List<Integer> plan = new ArrayList<>();
        int remaining = targetHistoryCount;
        for (int i = 0; i < outletCount; i++) {
            int outletsLeft = outletCount - i;
            int minForRest = (outletsLeft - 1) * 1;
            int maxForRest = (outletsLeft - 1) * 3;
            int minAllowed = Math.max(1, remaining - maxForRest);
            int maxAllowed = Math.min(3, remaining - minForRest);
            int value;
            if (minAllowed >= maxAllowed) {
                value = Math.max(1, Math.min(3, minAllowed));
            } else {
                value = minAllowed + random.nextInt(maxAllowed - minAllowed + 1);
            }
            plan.add(value);
            remaining -= value;
        }
        Collections.shuffle(plan, random);
        return plan;
    }

    private static class OutletRow {
        private final long id;
        private final String name;
        private final String region;

        private OutletRow(long id, String name, String region) {
            this.id = id;
            this.name = name == null ? "" : name;
            this.region = region == null ? "" : region;
        }
    }
}
