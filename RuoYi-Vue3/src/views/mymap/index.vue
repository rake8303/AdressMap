<template>
  <div style="display: flex; flex-direction: column; height: 100vh;">
    <!-- 筛选区域 - 自适应高度 -->
    <div style="flex: 0 0 auto;">
      <!-- 筛选与统计 -->
      <div style="display: flex; flex-direction: column; gap: 10px; padding: 10px;">
        <!-- 第一行：统计信息 -->
        <div style="display: flex; align-items: center; flex-wrap: wrap; gap: 15px; padding: 2px 0;">
          <div style="display: flex; align-items: center; gap: 15px; flex-wrap: wrap;">
            <span style="white-space: nowrap; color: #F00; font-weight: bold;">{{ $t('map.total') }}: <span style="white-space: nowrap; color: #000;">{{ stats.total }}</span></span>
            <span style="white-space: nowrap; color: #F00; font-weight: bold;">{{ $t('map.outlet') }}: <span style="white-space: nowrap; color: #000;">{{ stats.sale }}</span></span>
          </div>
        </div>

        <!-- 分隔线 -->
        <div v-if="specialPermissions" style="border-top: 1px solid #ccc; width: 100%;"></div>

        <!-- 第三行：商流 -->
        <div v-if="specialPermissions" class="filter-row">
          <!-- 商流标题 -->
          <label class="filter-label">商流:</label>

          <div class="filter-pills">
            <button
              type="button"
              class="filter-pill"
              :class="{ 'is-active': selectAllBusinessFlows }"
              @click="toggleAllBusinessFlows"
            >
            {{ $t('common.all') }}
            </button>
            <button
              v-for="flow in businessFlows"
              :key="flow.value"
              type="button"
              class="filter-pill filter-pill--legend"
              :class="{ 'is-active': selectedBusinessFlows.includes(flow.value) }"
              @click="toggleBusinessFlow(flow.value)"
            >
              <span class="filter-pill__swatch" :style="{ backgroundColor: getBusinessFlowColor(flow.value) }"></span>
              {{ flow.label }}
            </button>
            <button
              type="button"
              class="filter-pill filter-pill--legend"
              :class="{ 'is-active': selectedBusinessFlows.includes(UNDECIDED_BUSINESS_FLOW) }"
              @click="toggleBusinessFlow(UNDECIDED_BUSINESS_FLOW)"
            >
              <span class="filter-pill__swatch filter-pill__swatch--white"></span>
              {{ $t('map.undecided') }}
            </button>
            <button
              type="button"
              class="filter-pill filter-pill--legend"
              :class="{ 'is-active': selectedBusinessFlows.includes(MULTI_BUSINESS_FLOW) }"
              @click="toggleBusinessFlow(MULTI_BUSINESS_FLOW)"
            >
              <span class="filter-pill__swatch filter-pill__swatch--multi"></span>
              複数商流
            </button>
          </div>
        </div>


      </div>
    </div>


    <!-- 地图区域 - 占据剩余空间 -->
    <div class="map-shell">
      <div class="region-legend">
        <button
          v-for="area in detailedRegionAreas"
          :key="area.name"
          type="button"
          class="region-legend__item"
          :class="{ 'is-active': isRegionLegendActive(area), 'is-muted': !isRegionLegendActive(area) }"
          @click="toggleRegionArea(area)"
        >
          <span class="region-legend__swatch" :style="{ backgroundColor: area.color }"></span>
          <span>{{ area.name }}</span>
        </button>
      </div>
      <div id="google-map" style="width: 100%; height: 100%;"></div>
    </div>

    <!-- 加载模态框 -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-modal">
        <div class="loading-spinner"></div>
        <p class="loading-text">Loading...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { listOutletAgentUnion,listOutletAgentUnionbybusinessflow } from '@/api/mymap/mymap'
import { listHistory } from "@/api/system/history";

import useUserStore from "@/store/modules/user";
import { useRouter } from 'vue-router'
import { OUTLET_AGENTS, OUTLET_AGENT_COLORS, UNDECIDED_BUSINESS_FLOW } from '@/utils/outletAgents'
import { loadGoogleMapsScript } from '@/utils/googleMaps'

const router = useRouter()
const outletHistoryList = ref([]);
const userStore = useUserStore();
const currentRole = computed(() => userStore.roles?.[0] || '')
const specialPermissions = computed(() => ["admin", "common", "readonly"].includes(currentRole.value));

// 选中的地图区域 key，由右下角图例控制
const selectedRegions = ref(['中国', '中部', '九州', '北陸', '四国', '東北', '関東', '関西', '北海道'])

// 商流筛选
const businessFlows = ref(OUTLET_AGENTS)
const MULTI_BUSINESS_FLOW = '__MULTI_BUSINESS_FLOW__'
const selectedBusinessFlows = ref([...businessFlows.value.map(flow => flow.value), UNDECIDED_BUSINESS_FLOW, MULTI_BUSINESS_FLOW]) // 默认全选
const selectAllBusinessFlows = ref(true) // 商流全选状态

// 加载状态
const loading = ref(true)

const map = ref(null)
const allMarkers = ref([]) // 存储所有标记
const infoWindow = ref(null) // 信息窗口
const regionLabelMarkers = ref([])
const regionDataFeatures = ref([])

const detailedRegionAreas = [
  {
    name: '北海道',
    keys: ['北海道'],
    color: '#1f5fbf',
    labelPosition: { lat: 43.3, lng: 142.7 },
    prefectures: ['北海道']
  },
  {
    name: '东北',
    keys: ['东北', '東北'],
    color: '#16833f',
    labelPosition: { lat: 39.15, lng: 140.65 },
    prefectures: ['青森県', '岩手県', '宮城県', '秋田県', '山形県', '福島県']
  },
  {
    name: '关东',
    keys: ['关东', '関東'],
    color: '#c96f1b',
    labelPosition: { lat: 35.8, lng: 139.55 },
    prefectures: ['茨城県', '栃木県', '群馬県', '埼玉県', '千葉県', '東京都', '神奈川県']
  },
  {
    name: '中部',
    keys: ['中部'],
    color: '#7834b8',
    labelPosition: { lat: 35.6, lng: 137.6 },
    prefectures: ['山梨県', '長野県', '岐阜県', '静岡県', '愛知県']
  },
  {
    name: '北陆',
    keys: ['北陆', '北陸'],
    color: '#0f8b80',
    labelPosition: { lat: 36.75, lng: 137.1 },
    prefectures: ['新潟県', '富山県', '石川県', '福井県']
  },
  {
    name: '关西',
    keys: ['关西', '関西', '近畿'],
    color: '#d43f3f',
    labelPosition: { lat: 34.75, lng: 135.55 },
    prefectures: ['三重県', '滋賀県', '京都府', '大阪府', '兵庫県', '奈良県', '和歌山県']
  },
  {
    name: '中国',
    keys: ['中国', '中國'],
    color: '#1689c7',
    labelPosition: { lat: 34.65, lng: 132.6 },
    prefectures: ['鳥取県', '島根県', '岡山県', '広島県', '山口県']
  },
  {
    name: '四国',
    keys: ['四国', '四國'],
    color: '#d49d00',
    labelPosition: { lat: 33.75, lng: 133.45 },
    prefectures: ['徳島県', '香川県', '愛媛県', '高知県']
  },
  {
    name: '九州',
    keys: ['九州'],
    color: '#15956b',
    labelPosition: { lat: 32.55, lng: 130.8 },
    prefectures: ['福岡県', '佐賀県', '長崎県', '熊本県', '大分県', '宮崎県', '鹿児島県', '沖縄県']
  }
]

const prefectureRegionMap = detailedRegionAreas.reduce((map, area) => {
  area.prefectures.forEach(prefecture => {
    map[prefecture] = area
  })
  return map
}, {})

const markerIcons = {
  '織田家': {
    path: 'M 0,0 m -5,0 a 5,5 0 1,0 10,0 a 5,5 0 1,0 -10,0',
    fillColor: '#C7000B',
    fillOpacity: 0.8,
    strokeColor: '#FFFFFF',
    strokeWeight: 2,
    scale: 1.5
  },
  '豊臣家': {
    path: 'M 0,0 m -5,0 a 5,5 0 1,0 10,0 a 5,5 0 1,0 -10,0',
    fillColor: '#7E57C2',
    fillOpacity: 0.8,
    strokeColor: '#FFFFFF',
    strokeWeight: 2,
    scale: 1.5
  },
  '徳川家': {
    path: 'M 0,0 m -5,0 a 5,5 0 1,0 10,0 a 5,5 0 1,0 -10,0',
    fillColor: '#1E5AA8',
    fillOpacity: 0.8,
    strokeColor: '#FFFFFF',
    strokeWeight: 2,
    scale: 1.5
  },
  '武田家': {
    path: 'M 0,0 m -5,0 a 5,5 0 1,0 10,0 a 5,5 0 1,0 -10,0',
    fillColor: '#D9822B',
    fillOpacity: 0.8,
    strokeColor: '#FFFFFF',
    strokeWeight: 2,
    scale: 1.5
  },
  '上杉家': {
    path: 'M 0,0 m -5,0 a 5,5 0 1,0 10,0 a 5,5 0 1,0 -10,0',
    fillColor: '#111827',
    fillOpacity: 0.8,
    strokeColor: '#FFFFFF',
    strokeWeight: 2,
    scale: 1.5
  },
  'default': {
    path: 'M 0,0 m -5,0 a 5,5 0 1,0 10,0 a 5,5 0 1,0 -10,0',
    fillColor: '#FFFFFF',
    fillOpacity: 0.8,
    strokeColor: '#4B5563',
    strokeWeight: 2,
    scale: 1.5
  }
}

const multiBusinessFlowIconUrl = 'data:image/svg+xml;charset=UTF-8,' + encodeURIComponent(`
    <svg xmlns="http://www.w3.org/2000/svg" width="34" height="42" viewBox="0 0 34 42">
      <defs>
        <filter id="shadow" x="-35%" y="-25%" width="170%" height="170%">
          <feDropShadow dx="0" dy="2.4" stdDeviation="2" flood-color="#111827" flood-opacity="0.25"/>
        </filter>
        <linearGradient id="rim" x1="7" y1="5" x2="27" y2="31" gradientUnits="userSpaceOnUse">
          <stop offset="0" stop-color="#D52B1E"/>
          <stop offset="0.55" stop-color="#B91C1C"/>
          <stop offset="1" stop-color="#7F1D1D"/>
        </linearGradient>
      </defs>
      <path d="M17 39s12-12.1 12-22.2C29 9.8 23.6 4 17 4S5 9.8 5 16.8C5 26.9 17 39 17 39z" fill="url(#rim)" filter="url(#shadow)"/>
      <circle cx="17" cy="16.7" r="9.5" fill="#FFFFFF" stroke="#F4C430" stroke-width="2.2"/>
      <path d="M17 9.1l2.05 4.15 4.58.67-3.31 3.23.78 4.57L17 19.56l-4.1 2.16.78-4.57-3.31-3.23 4.58-.67L17 9.1z" fill="#C7000B"/>
      <circle cx="17" cy="16.7" r="12.7" fill="none" stroke="#FFFFFF" stroke-width="1.6" opacity="0.9"/>
    </svg>
  `)

function handleSelectAllBusinessFlows() {
  if (selectAllBusinessFlows.value) {
            selectedBusinessFlows.value = [...businessFlows.value.map(flow => flow.value), UNDECIDED_BUSINESS_FLOW, MULTI_BUSINESS_FLOW]
  } else {
    selectedBusinessFlows.value = []
  }
  filterMarkers()
}

function toggleAllBusinessFlows() {
  selectAllBusinessFlows.value = !selectAllBusinessFlows.value
  handleSelectAllBusinessFlows()
}

function toggleBusinessFlow(flow) {
  toggleSelectedValue(selectedBusinessFlows, flow)
  handleBusinessFlowChange()
}

function toggleSelectedValue(targetRef, value) {
  const index = targetRef.value.indexOf(value)
  if (index >= 0) {
    targetRef.value.splice(index, 1)
  } else {
    targetRef.value.push(value)
  }
}

function handleBusinessFlowChange() {
  const allBusinessFlows = [...businessFlows.value.map(flow => flow.value), UNDECIDED_BUSINESS_FLOW, MULTI_BUSINESS_FLOW]
  selectAllBusinessFlows.value = selectedBusinessFlows.value.length === allBusinessFlows.length
  filterMarkers()
}

function createJapanRegionOverlays() {
  if (!window.google || !window.google.maps || !map.value) return

  regionLabelMarkers.value.forEach(marker => marker.setMap(null))
  regionDataFeatures.value.forEach(feature => map.value.data.remove(feature))
  regionLabelMarkers.value = []
  regionDataFeatures.value = []

  map.value.data.loadGeoJson('/japan-prefectures.geojson', null, features => {
    regionDataFeatures.value = features
    features.forEach(feature => {
      const prefectureName = feature.getProperty('nam_ja')
      const area = prefectureRegionMap[prefectureName]
      if (area) {
        feature.setProperty('regionName', area.name)
        feature.setProperty('regionKeys', area.keys)
        feature.setProperty('regionColor', area.color)
      }
    })
    updateRegionOverlays()
  })

  detailedRegionAreas.forEach(area => {
    const labelMarker = new window.google.maps.Marker({
      map: map.value,
      position: area.labelPosition,
      clickable: false,
      zIndex: 2,
      icon: {
        path: window.google.maps.SymbolPath.CIRCLE,
        scale: 0,
        fillOpacity: 0,
        strokeOpacity: 0
      },
      label: {
        text: area.name,
        color: '#1f2933',
        fontSize: '13px',
        fontWeight: '700'
      }
    })
    labelMarker.regionKeys = area.keys
    regionLabelMarkers.value.push(labelMarker)
  })

  updateRegionOverlays()
}

function isRegionOverlayVisible(regionKeys) {
  if (selectedRegions.value.length === 0) return false
  return regionKeys.some(key => selectedRegions.value.includes(key))
}

function isRegionLegendActive(area) {
  return area.keys.some(key => selectedRegions.value.includes(key))
}

function toggleRegionArea(area) {
  if (isRegionLegendActive(area)) {
    selectedRegions.value = selectedRegions.value.filter(region => !area.keys.includes(region))
  } else {
    area.keys.forEach(key => {
      if (!selectedRegions.value.includes(key)) {
        selectedRegions.value.push(key)
      }
    })
  }
  filterMarkers()
}

function isMarkerRegionVisible(region) {
  if (selectedRegions.value.length === 0) return false
  const area = detailedRegionAreas.find(regionArea => regionArea.keys.includes(region) || regionArea.name === region)
  if (!area) return selectedRegions.value.includes(region)
  return area.keys.some(key => selectedRegions.value.includes(key))
}

function getRegionFeatureStyle(feature) {
  const color = feature.getProperty('regionColor') || '#9aa5b1'
  const regionKeys = feature.getProperty('regionKeys') || []

  return {
    visible: isRegionOverlayVisible(regionKeys),
    fillColor: color,
    fillOpacity: 0.54,
    strokeColor: '#ffffff',
    strokeOpacity: 0.85,
    strokeWeight: 0.8,
    clickable: false,
    zIndex: 1
  }
}

function updateRegionOverlays() {
  if (map.value) {
    map.value.data.setStyle(getRegionFeatureStyle)
  }
  regionLabelMarkers.value.forEach(marker => {
    marker.setVisible(isRegionOverlayVisible(marker.regionKeys || []))
  })
}

function isBusinessFlowVisible(businessFlow) {
  const selectedFlows = selectedBusinessFlows.value
  if (selectedFlows.length === 0) return false
  if (businessFlow.length > 1) return selectedFlows.includes(MULTI_BUSINESS_FLOW)
  if (selectedFlows.includes(UNDECIDED_BUSINESS_FLOW) && businessFlow.length === 0) return true
  return selectedFlows
    .filter(flow => flow !== UNDECIDED_BUSINESS_FLOW && flow !== MULTI_BUSINESS_FLOW)
    .some(flow => businessFlow.includes(flow))
}

function filterMarkers() {
  updateRegionOverlays()

  if (!allMarkers.value || allMarkers.value.length === 0) return;

  allMarkers.value.forEach(marker => {
    marker.setVisible(false)
  })

  const selectedMarkers = allMarkers.value.filter(marker => {
    const region = marker.region
    const companyType = marker.companyType
    const businessFlow = marker.businessFlow || []

    const regionMatch = isMarkerRegionVisible(region)
    const companyTypeMatch = companyType === '販売店'
    const businessFlowMatch = isBusinessFlowVisible(businessFlow)

    return regionMatch && companyTypeMatch && businessFlowMatch
  })

  selectedMarkers.forEach(marker => {
    marker.setVisible(true)
  })

  updateMapBounds(selectedMarkers)
}


// 更新地图边界以适应可见标记
function updateMapBounds(visibleMarkers) {
  // 检查 Google Maps API 是否已加载
  if (!window.google || !window.google.maps) {
    console.warn('Google Maps API 尚未加载完成')
    return
  }

  if (!map.value) {
    console.warn('地图尚未初始化')
    return
  }

  if (visibleMarkers && visibleMarkers.length > 0) {
    const bounds = new window.google.maps.LatLngBounds()
    visibleMarkers.forEach(marker => {
      bounds.extend(marker.getPosition())
    })
    map.value.fitBounds(bounds)

    // 限制缩放级别
    window.google.maps.event.addListenerOnce(map.value, 'bounds_changed', function () {
      const zoom = map.value.getZoom()
      if (zoom > 10) {
        map.value.setZoom(10)
      } else if (zoom < 5) {
        map.value.setZoom(5)
      }
    })
  } else {
    map.value.setCenter({ lat: 37.6895, lng: 145.6917 })
    map.value.setZoom(6)
  }
}


function getBusinessFlowColor(flowValue) {
  return OUTLET_AGENT_COLORS[flowValue] || '#000000'
}

function getOutletMarkerIcon(outlet) {
  const businessFlows = outlet.businessflow
    ? outlet.businessflow.split(',').map(flow => flow.trim()).filter(Boolean)
    : []

  if (businessFlows.length > 1) {
    return {
      url: multiBusinessFlowIconUrl,
      scaledSize: new window.google.maps.Size(30, 37),
      anchor: new window.google.maps.Point(15, 36)
    }
  }

  return markerIcons[businessFlows[0]] || markerIcons.default
}

function hasValidLocation(outlet) {
  return outlet.lat && outlet.lng && !Number.isNaN(parseFloat(outlet.lat)) && !Number.isNaN(parseFloat(outlet.lng))
}

function initMap(addresses) {
  // 确保 Google Maps API 已加载
  if (!window.google || !window.google.maps) {
    console.error('Google Maps API 未加载完成');
    loading.value = false;
    return;
  }

  const center = { lat: 38.6895, lng: 139.6917 };
  map.value = new window.google.maps.Map(document.getElementById('google-map'), {
    zoom: 5,
    center,
  });
  createJapanRegionOverlays();

  // 创建信息窗口
  infoWindow.value = new window.google.maps.InfoWindow();

  // 监听地图点击事件，点击地图任意位置关闭信息窗口
  map.value.addListener('click', () => {
    if (infoWindow.value) {
      infoWindow.value.close();
    }
  });

  allMarkers.value = []; // 清空所有标记

  addresses.forEach(addr => {
    if (!hasValidLocation(addr)) return

    const marker = new window.google.maps.Marker({
      map: map.value,
      position: { lat: parseFloat(addr.lat), lng: parseFloat(addr.lng) },
      title: addr.headquartersAddress,
      icon: getOutletMarkerIcon(addr)
    });

    marker.region = addr.region || '其他';
    marker.companyType = addr.companyType || '其他';
    marker.businessFlow = addr.businessflow ? addr.businessflow.split(',') : [];
    marker.agentData = addr;
    marker.setVisible(true);

    marker.addListener('click', (e) => {
      e.stop();
      showMarkerInfo(marker);
    });

    allMarkers.value.push(marker);
  });

  // 初始化完成后调用过滤函数并隐藏加载框
  setTimeout(() => {
    filterMarkers();
    loading.value = false;
  }, 100);
}

function escapeHtml(value) {
  return String(value ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

const stats = computed(() => {
  if (!allMarkers.value || allMarkers.value.length === 0) {
    return { total: 0, sale: 0 }
  }

  const visibleMarkers = allMarkers.value.filter(marker => marker.getVisible())
  const saleCount = visibleMarkers.filter(marker => marker.companyType === '販売店').length

  return {
    total: visibleMarkers.length,
    sale: saleCount
  }
})

function showMarkerInfo(marker) {
  // 检查 Google Maps API 是否已加载
  if (!window.google || !window.google.maps) {
    return
  }

  const data = marker.agentData
  
  // 增加信息窗口宽度到450px
  let content = '<div style="max-width: 450px; font-family: \'Microsoft YaHei\', sans-serif;">'
  const companyName = escapeHtml(data.jpCompanyName)
  const businessflow = escapeHtml(data.businessflow || UNDECIDED_BUSINESS_FLOW)
  const hasBusinessFlow = Boolean(data.businessflow)

  // 会社名和查看详细按钮 - 同一行
  if (data.jpCompanyName) {
    content += `
      <div style="padding: 8px 12px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); 
                 color: white; border-radius: 6px 6px 0 0; font-size: 16px; font-weight: bold; 
                 display: flex; justify-content: space-between; align-items: center;">
        <div style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis; flex-grow: 1; text-align: center; margin-right: 20px;">
          ${companyName}
        </div>
        <button id="detail-button-in-popup" 
                style="background-color: #4CAF50; 
                       color: white; 
                       border: none; 
                       padding: 4px 8px; 
                       text-align: center; 
                       text-decoration: none; 
                       display: inline-block; 
                       font-size: 12px; 
                       border-radius: 4px; 
                       cursor: pointer;
                       font-weight: bold;
                       position: absolute;
                       top: 8px;
                       left: 8px;
                       z-index: 1000;">
          查看详细
        </button>
      </div>`
  }

  // 月次販売台数
  if (data.companyType === "販売店" && hasBusinessFlow) {
    const totalSales = escapeHtml(data.totalSalesAvg ? data.totalSalesAvg : '未定')
    
    content += `
      <div style="display: flex; gap: 10px; margin: 10px 0;">
        <div style="flex: 1; padding: 8px; border: 2px solid #667eea; border-radius: 6px; text-align: center; white-space: nowrap;">
          <div style="font-size: 12px; color: #333; font-weight: bold; margin-bottom: 4px;">月次販売台数</div>
          <div style="font-weight: bold; color: #000; font-size: 14px; overflow: hidden; text-overflow: ellipsis;">${totalSales}</div>
        </div>
      </div>`
  }

  // 商流
  if (data.companyType === "販売店" && hasBusinessFlow) {
    content += `
      <div style="display: flex; gap: 10px; margin-bottom: 10px;">
        <div style="flex: 1; padding: 8px; border: 2px solid #667eea; border-radius: 6px; text-align: center; white-space: nowrap;">
          <div style="font-size: 12px; color: #333; font-weight: bold; margin-bottom: 4px;">商流</div>
          <div style="font-weight: bold; color: #000; font-size: 14px; overflow: hidden; text-overflow: ellipsis;">${businessflow}</div>
        </div>
      </div>`
  }

  // 访问履历表格
  if (data.companyType === "販売店" && data.id) {
    // 显示访问记录表格，加宽滚动条
    content += `
      <div style="margin-top: 10px; border-top: 1px solid #ccc; padding-top: 10px;">
        <div style="font-weight: bold; color: #000; margin-bottom: 8px; text-align: center;">贩卖店訪問履歴</div>
        <div style="max-height: 120px; overflow-y: auto; scrollbar-width: auto; scrollbar-color: #667eea #f1f1f1;">
          <style>
            ::-webkit-scrollbar {
              width: 15px;
            }
            ::-webkit-scrollbar-track {
              background: #f1f1f1;
              border-radius: 6px;
            }
            ::-webkit-scrollbar-thumb {
              background: #667eea;
              border-radius: 6px;
            }
            ::-webkit-scrollbar-thumb:hover {
              background: #5566d9;
            }
          </style>
          <table style="width: 100%; border-collapse: collapse; font-size: 12px; table-layout: fixed;">
            <thead>
              <tr style="background-color: #f5f5f5; position: sticky; top: 0;">
                <th style="border: 1px solid #ddd; padding: 6px; text-align: center; width: 40%; color: #000; font-weight: bold;">备注</th>
                <th style="border: 1px solid #ddd; padding: 6px; text-align: center; width: 35%; color: #000; font-weight: bold;">人员（商流）</th>
                <th style="border: 1px solid #ddd; padding: 6px; text-align: center; width: 25%; color: #000; font-weight: bold;">最后修改时间</th>
              </tr>
            </thead>
            <tbody id="history-tbody">
              <tr>
                <td style="border: 1px solid #ddd; padding: 6px; text-align: center; color: #000;" colspan="3">加载中...</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>`
  }
  
  content += '</div>'

  // 设置信息窗口内容并打开
  infoWindow.value.setContent(content)
  infoWindow.value.open(map.value, marker)
  
  window.google.maps.event.addListenerOnce(infoWindow.value, 'domready', () => {
    const detailButton = document.getElementById('detail-button-in-popup');
    if (detailButton) {
      detailButton.addEventListener('click', () => {
        goToCompanyDetail(data)
      });
    }
    if (data.companyType === "販売店" && data.id) {
      updateHistoryTable(data.id)
    }
  });
}

function goToCompanyDetail(data) {
  if (!data || !data.id) return
  // 贩卖店：跳详情页
  if (data.companyType === '販売店') {
    router.push({
      name: 'OutletDetail',
      params: { id: data.id },
    })
    return
  }
}


// 更新访问记录表格
async function updateHistoryTable(id) {
  const historyList = await getDetail(id);
  const tbody = document.getElementById('history-tbody');
  if (!tbody) return

  tbody.replaceChildren()
  if (historyList && historyList.length > 0) {
    historyList.slice(0, 5).forEach(record => {
      const row = document.createElement('tr')
      ;[record.remark || '无', record.updatedBy || '无', record.updatedAt || '无'].forEach(value => {
        row.appendChild(createHistoryCell(value))
      })
      tbody.appendChild(row)
    })
    return
  }

  const row = document.createElement('tr')
  const cell = createHistoryCell('暂无访问记录')
  cell.colSpan = 3
  row.appendChild(cell)
  tbody.appendChild(row)
}

function createHistoryCell(value) {
  const cell = document.createElement('td')
  cell.style.border = '1px solid #ddd'
  cell.style.padding = '6px'
  cell.style.textAlign = 'center'
  cell.textContent = value
  return cell
}


/** 获取详情 */
function getDetail(id) {
  if (specialPermissions.value) {
    return listHistory({ outletId: id }).then((res) => {
      outletHistoryList.value = res.rows || [];
      return res.rows || [];
    });
  } else {
    return listHistory({ outletId: id, agent: currentRole.value }).then((res) => {
      outletHistoryList.value = res.rows || [];
      return res.rows || [];
    });
  }
}


onMounted(async () => {
  try {
    // 显示加载模态框
    loading.value = true;

    const res = specialPermissions.value
      ? await listOutletAgentUnion()
      : await listOutletAgentUnionbybusinessflow();

    await loadGoogleMapsScript()
    initMap(res.data || []);
  } catch (e) {
    console.error('获取数据失败:', e);
    try {
      await loadGoogleMapsScript()
      initMap([]);
    } catch {
      // 发生错误时也隐藏模态框
    }
    loading.value = false;
  }
});



</script>

<style scoped>
.filter-row {
  display: grid;
  grid-template-columns: 76px 1fr;
  align-items: start;
  gap: 8px 12px;
  padding: 0;
}

.filter-label {
  color: #f00;
  font-weight: 700;
  line-height: 28px;
  white-space: nowrap;
}

.filter-pills {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  min-width: 0;
}

.filter-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  min-width: 58px;
  height: 28px;
  padding: 0 12px;
  border: 1px solid #d4d9e2;
  border-radius: 4px;
  background: #fff;
  color: #303643;
  font-size: 13px;
  font-weight: 600;
  line-height: 1;
  cursor: pointer;
  white-space: nowrap;
  transition: border-color 0.15s ease, background-color 0.15s ease, color 0.15s ease;
}

.filter-pill:hover {
  border-color: #409eff;
  color: #1d4f91;
}

.filter-pill.is-active {
  border-color: #409eff;
  background: #eaf4ff;
  color: #1d4f91;
}

.filter-pill--legend {
  justify-content: flex-start;
}

.filter-pill__swatch {
  width: 14px;
  height: 14px;
  border: 1px solid #c6ccd6;
  border-radius: 50%;
  flex: 0 0 auto;
}

.filter-pill__swatch--white {
  background: #fff;
  border-color: #4b5563;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.95);
}

.filter-pill__swatch--multi {
  position: relative;
  width: 18px;
  height: 18px;
  background: radial-gradient(circle at 50% 45%, #ffffff 0 46%, transparent 48%),
    linear-gradient(135deg, #d52b1e 0%, #b91c1c 66%, #7f1d1d 100%);
  border-color: #f4c430;
  box-shadow: 0 1px 3px rgba(17, 24, 39, 0.22);
}

.filter-pill__swatch--multi::after {
  content: '★';
  position: absolute;
  inset: 1px 0 0 0;
  color: #c7000b;
  font-size: 12px;
  line-height: 18px;
  text-align: center;
}

.map-shell {
  position: relative;
  flex: 1 1 auto;
  min-height: 0;
}

.region-legend {
  position: absolute;
  right: 14px;
  top: 14px;
  z-index: 5;
  display: grid;
  grid-template-columns: repeat(2, auto);
  gap: 6px 12px;
  padding: 10px 12px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(31, 41, 51, 0.14);
  border-radius: 6px;
  box-shadow: 0 2px 10px rgba(15, 23, 42, 0.16);
  font-size: 12px;
  color: #1f2933;
}

.region-legend__item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 24px;
  padding: 4px 7px;
  border: 1px solid transparent;
  border-radius: 4px;
  background: transparent;
  color: #1f2933;
  font: inherit;
  white-space: nowrap;
  line-height: 1;
  cursor: pointer;
  transition: opacity 0.15s ease, border-color 0.15s ease, background-color 0.15s ease;
}

.region-legend__item:hover {
  border-color: rgba(64, 158, 255, 0.55);
  background: rgba(64, 158, 255, 0.1);
}

.region-legend__item.is-active {
  border-color: rgba(64, 158, 255, 0.7);
  background: rgba(64, 158, 255, 0.14);
  font-weight: 700;
}

.region-legend__item.is-muted {
  opacity: 0.45;
}

.region-legend__swatch {
  width: 12px;
  height: 12px;
  border-radius: 3px;
  border: 1px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 0 0 1px rgba(31, 41, 51, 0.12);
}

@media (max-width: 768px) {
  .filter-row {
    grid-template-columns: 1fr;
  }

  .filter-label {
    line-height: 1.2;
  }

  .filter-pill {
    min-width: 52px;
    padding: 0 10px;
  }

  .region-legend {
    right: 8px;
    top: 8px;
    grid-template-columns: repeat(2, auto);
    gap: 5px 8px;
    padding: 8px;
    font-size: 11px;
  }
}

/* 加载模态框样式 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.loading-modal {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.loading-spinner {
  display: inline-block;
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-text {
  margin-top: 15px;
  font-size: 16px;
  color: #333;
}




/* 滑动开关样式 */
.switch {
  position: relative;
  display: inline-block;
  width: 40px;
  height: 20px;
  margin: 0;
  vertical-align: middle;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.4s;
  border-radius: 20px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 16px;
  width: 16px;
  left: 2px;
  bottom: 2px;
  background-color: white;
  transition: 0.4s;
  border-radius: 50%;
}

.switch input:checked+.slider {
  background-color: #4CAF50;
}

.switch input:checked+.slider:before {
  transform: translateX(18px);
}

/* 旋转动画 */
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}
/* 详情按钮悬停效果 */
#detail-button:hover {
  background-color: #45a049;
}
</style>
