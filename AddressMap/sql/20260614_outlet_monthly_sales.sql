CREATE TABLE IF NOT EXISTS tab_outlet_monthly_sales (
  sales_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  outlet_id INT UNSIGNED NOT NULL COMMENT '販売店ID',
  sales_month CHAR(7) NOT NULL COMMENT '年月(yyyy-MM)',
  quantity INT NULL COMMENT '月次販売台数',
  product_name VARCHAR(100) NULL COMMENT '商品名称',
  remark VARCHAR(500) NULL COMMENT '備考',
  updated_by VARCHAR(64) NULL COMMENT '最后修改人',
  updated_at DATETIME NULL COMMENT '最后修改时间',
  PRIMARY KEY (sales_id),
  KEY idx_outlet_monthly_sales_outlet_id (outlet_id),
  KEY idx_outlet_monthly_sales_month (sales_month),
  CONSTRAINT fk_outlet_monthly_sales_outlet
    FOREIGN KEY (outlet_id) REFERENCES tab_outlet (id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='販売店月次販売台数';
