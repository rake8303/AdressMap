-- 测试数据脱敏：用战国时期风格名称替换真实点位、代理店、商流名称。

UPDATE tab_outlet_agent
SET agent_name = CASE agent_name
  WHEN 'XSOL' THEN '織田家'
  WHEN 'DMM' THEN '豊臣家'
  WHEN 'WWB' THEN '徳川家'
  WHEN '高島' THEN '武田家'
  WHEN '韓華' THEN '上杉家'
  ELSE agent_name
END;

UPDATE tab_outlet o
JOIN (
  SELECT 0 seq, '尾張' province, '清洲城' castle, '尾張清洲城下商館' shop_name, '中部' region, 35.220000 lat, 136.845000 lng UNION ALL
  SELECT 1, '三河', '岡崎城', '三河岡崎城下商館', '中部', 34.956000, 137.160000 UNION ALL
  SELECT 2, '武蔵', '江戸城', '武蔵江戸城下商館', '関東', 35.685000, 139.752000 UNION ALL
  SELECT 3, '甲斐', '躑躅ヶ崎館', '甲斐躑躅ヶ崎館下商館', '中部', 35.680000, 138.580000 UNION ALL
  SELECT 4, '越後', '春日山城', '越後春日山城下商館', '北陸', 37.150000, 138.240000 UNION ALL
  SELECT 5, '摂津', '大坂城', '摂津大坂城下商館', '関西', 34.687000, 135.526000 UNION ALL
  SELECT 6, '山城', '二条城', '山城二条城下商館', '関西', 35.014000, 135.748000 UNION ALL
  SELECT 7, '近江', '安土城', '近江安土城下商館', '関西', 35.155000, 136.140000 UNION ALL
  SELECT 8, '安芸', '広島城', '安芸広島城下商館', '中国', 34.402000, 132.459000 UNION ALL
  SELECT 9, '筑前', '福岡城', '筑前福岡城下商館', '九州', 33.584000, 130.383000 UNION ALL
  SELECT 10, '肥後', '熊本城', '肥後熊本城下商館', '九州', 32.806000, 130.705000 UNION ALL
  SELECT 11, '薩摩', '鹿児島城', '薩摩鹿児島城下商館', '九州', 31.596000, 130.557000 UNION ALL
  SELECT 12, '陸前', '青葉城', '陸前青葉城下商館', '東北', 38.250000, 140.850000 UNION ALL
  SELECT 13, '蝦夷', '松前館', '蝦夷松前館下商館', '北海道', 41.430000, 140.110000 UNION ALL
  SELECT 14, '伊予', '松山城', '伊予松山城下商館', '四国', 33.845000, 132.765000
) p ON MOD(o.id - 1, 15) = p.seq
SET
  o.jp_company_name = CONCAT(p.shop_name, ' 第', o.id, '陣'),
  o.short_company_name = CONCAT(p.castle, '商館'),
  o.abbreviation = CONCAT(p.province, '第', o.id, '陣'),
  o.headquarters_address = CONCAT(p.province, '国 ', p.castle, '城下'),
  o.region = p.region,
  o.lat = p.lat + ((MOD(o.id * 37, 100) - 50) / 10000),
  o.lng = p.lng + ((MOD(o.id * 53, 100) - 50) / 10000);
