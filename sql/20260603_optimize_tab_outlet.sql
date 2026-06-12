-- tab_outlet first-pass schema optimization.
-- Goals:
-- 1. Keep current column names so existing Java/MyBatis/Vue code keeps working.
-- 2. Convert real numeric fields from varchar to DECIMAL.
-- 3. Convert map coordinates from varchar to DECIMAL and fix swapped comments.
-- 4. Add indexes used by list/search/map pages.

-- IMPORTANT: MySQL DDL statements such as ALTER TABLE cause implicit commits.
-- Run this during a maintenance window and verify the backup before applying.
CREATE TABLE IF NOT EXISTS tab_outlet_backup_20260603 AS
SELECT *
FROM tab_outlet;

-- Normalize existing dirty values before type changes.
UPDATE tab_outlet
SET
  total_sales_avg = NULL
WHERE total_sales_avg IS NOT NULL
  AND (
    TRIM(total_sales_avg) = ''
    OR TRIM(total_sales_avg) NOT REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
  );

UPDATE tab_outlet
SET
  hw_sales_avg = NULL
WHERE hw_sales_avg IS NOT NULL
  AND (
    TRIM(hw_sales_avg) = ''
    OR TRIM(hw_sales_avg) NOT REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
  );

UPDATE tab_outlet
SET
  lat = NULL
WHERE lat IS NOT NULL
  AND (
    TRIM(lat) = ''
    OR TRIM(lat) NOT REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
  );

UPDATE tab_outlet
SET
  lng = NULL
WHERE lng IS NOT NULL
  AND (
    TRIM(lng) = ''
    OR TRIM(lng) NOT REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
  );

-- Keep id as INT UNSIGNED to stay compatible with existing foreign keys.
ALTER TABLE tab_outlet
  MODIFY COLUMN id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  MODIFY COLUMN jp_company_name VARCHAR(255) NOT NULL COMMENT '会社名',
  MODIFY COLUMN short_company_name VARCHAR(255) NULL COMMENT '会社名简称',
  MODIFY COLUMN region VARCHAR(50) NULL COMMENT 'エリア',
  MODIFY COLUMN headquarters_address VARCHAR(500) NULL COMMENT '住所（本社）',
  MODIFY COLUMN contact_person VARCHAR(100) NULL COMMENT '责任人',
  MODIFY COLUMN total_sales_avg DECIMAL(10,2) NULL DEFAULT 0.00 COMMENT '整体销售规模（月平均）',
  MODIFY COLUMN breakthrough_status VARCHAR(20) NULL COMMENT '突破情况',
  MODIFY COLUMN hw_sales_avg DECIMAL(10,2) NULL DEFAULT 0.00 COMMENT 'HW产品销售规模（月平均）',
  MODIFY COLUMN abbreviation VARCHAR(255) NULL COMMENT '简称',
  MODIFY COLUMN cpn_type VARCHAR(50) NULL COMMENT '会社種類',
  MODIFY COLUMN sales_record VARCHAR(50) NULL COMMENT '販売実績',
  MODIFY COLUMN lat DECIMAL(10,7) NULL COMMENT '緯度',
  MODIFY COLUMN lng DECIMAL(10,7) NULL COMMENT '経度';

-- MySQL does not support IF NOT EXISTS for indexes in every 8.0 deployment.
-- If an index already exists, skip the corresponding CREATE INDEX manually.
CREATE INDEX idx_tab_outlet_region ON tab_outlet(region);
CREATE INDEX idx_tab_outlet_company_name ON tab_outlet(jp_company_name);
CREATE INDEX idx_tab_outlet_cpn_type ON tab_outlet(cpn_type);
CREATE INDEX idx_tab_outlet_sales_record ON tab_outlet(sales_record);
CREATE INDEX idx_tab_outlet_location ON tab_outlet(lat, lng);
CREATE INDEX idx_tab_outlet_updated_at ON tab_outlet(updated_at);
