ALTER TABLE tab_outlet_monthly_sales
  ADD COLUMN IF NOT EXISTS updated_by VARCHAR(64) NULL COMMENT '最后修改人',
  ADD COLUMN IF NOT EXISTS updated_at DATETIME NULL COMMENT '最后修改时间';
