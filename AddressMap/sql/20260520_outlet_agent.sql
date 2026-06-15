-- 代理店字段规范化：一条販売店可以关联多个代理店。
-- 旧字段 xsol_sales / dmm_sales / wwb_sales / takashima_sales / hanhwa_sales
-- 暂时保留作为迁移兼容字段，业务读写以 tab_outlet_agent 为准。

CREATE TABLE IF NOT EXISTS tab_outlet_agent (
    outlet_id INT UNSIGNED NOT NULL COMMENT '販売店ID',
    agent_name VARCHAR(32) NOT NULL COMMENT '代理店名称',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (outlet_id, agent_name),
    KEY idx_tab_outlet_agent_agent_name (agent_name),
    CONSTRAINT fk_tab_outlet_agent_outlet
        FOREIGN KEY (outlet_id) REFERENCES tab_outlet (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='販売店-代理店关联';

INSERT IGNORE INTO tab_outlet_agent (outlet_id, agent_name)
SELECT id, '織田家' FROM tab_outlet WHERE xsol_sales = 'Yes'
UNION ALL
SELECT id, '豊臣家' FROM tab_outlet WHERE dmm_sales = 'Yes'
UNION ALL
SELECT id, '徳川家' FROM tab_outlet WHERE wwb_sales = 'Yes'
UNION ALL
SELECT id, '武田家' FROM tab_outlet WHERE takashima_sales = 'Yes'
UNION ALL
SELECT id, '上杉家' FROM tab_outlet WHERE hanhwa_sales = 'Yes';

-- 确认线上没有旧代码依赖后，可在后续版本删除旧的五个布尔字段：
-- ALTER TABLE tab_outlet
--   DROP COLUMN xsol_sales,
--   DROP COLUMN dmm_sales,
--   DROP COLUMN wwb_sales,
--   DROP COLUMN takashima_sales,
--   DROP COLUMN hanhwa_sales;
