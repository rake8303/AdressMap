import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SeedOutletBusinessFlowHistory {
    private static final String[] VISIT_TOPICS = {
            "月次販売台数確認",
            "重点商材提案",
            "商流状況ヒアリング",
            "次回受注見込み確認",
            "販促施策フォロー",
            "新規案件の進捗確認"
    };

    private SeedOutletBusinessFlowHistory() {
    }

    public static String newHistoryId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String pickAgent(List<String> agents, Random random) {
        if (agents == null || agents.isEmpty()) {
            return "未設定";
        }
        return agents.get(random.nextInt(agents.size()));
    }

    public static String pickTopic(Random random) {
        return VISIT_TOPICS[random.nextInt(VISIT_TOPICS.length)];
    }

    public static String buildRemark(String outletName, String region, String agent, String topic) {
        return outletName + "（" + region + "）で" + agent + "と" + topic + "を実施。";
    }

    public static Timestamp randomVisitTime(Random random, int maxPastDays) {
        int daysBack = random.nextInt(Math.max(1, maxPastDays)) + 1;
        int hour = 9 + random.nextInt(8);
        int minute = random.nextInt(60);
        LocalDateTime dateTime = LocalDateTime.now()
                .minusDays(daysBack)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0);
        return Timestamp.valueOf(dateTime);
    }
}
