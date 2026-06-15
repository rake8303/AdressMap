-- Move outlet agent selections fully to tab_outlet_agent.
-- Old columns are backed up with a del_ prefix before removal:
--   xsol_sales / dmm_sales / wwb_sales / takashima_sales / hanhwa_sales

CREATE TABLE IF NOT EXISTS del_tab_outlet_agent_flags_20260603 AS
SELECT
  id AS outlet_id,
  xsol_sales,
  dmm_sales,
  wwb_sales,
  takashima_sales,
  hanhwa_sales,
  NOW() AS backed_up_at
FROM tab_outlet;

CREATE TABLE IF NOT EXISTS tab_outlet_agent (
  outlet_id INT UNSIGNED NOT NULL COMMENT '販売店ID',
  agent_name VARCHAR(32) NOT NULL COMMENT '代理店/商流名称',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (outlet_id, agent_name),
  KEY idx_tab_outlet_agent_agent_name (agent_name),
  CONSTRAINT fk_tab_outlet_agent_outlet
    FOREIGN KEY (outlet_id) REFERENCES tab_outlet (id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='販売店-代理店/商流关联';

INSERT IGNORE INTO tab_outlet_agent (outlet_id, agent_name)
SELECT id, 'XSOL' FROM tab_outlet WHERE xsol_sales = 'Yes'
UNION ALL
SELECT id, 'DMM' FROM tab_outlet WHERE dmm_sales = 'Yes'
UNION ALL
SELECT id, 'WWB' FROM tab_outlet WHERE wwb_sales = 'Yes'
UNION ALL
SELECT id, '高島' FROM tab_outlet WHERE takashima_sales = 'Yes'
UNION ALL
SELECT id, '韓華' FROM tab_outlet WHERE hanhwa_sales = 'Yes';

ALTER TABLE tab_outlet
  DROP COLUMN xsol_sales,
  DROP COLUMN dmm_sales,
  DROP COLUMN wwb_sales,
  DROP COLUMN takashima_sales,
  DROP COLUMN hanhwa_sales;
