<template>
  <div class="outlet-detail-page">
    <div class="page-header">
      <el-page-header @back="goBack" :content="title" />
    </div>

    <div class="page-content">
      <el-form ref="outletRef" :model="form" :rules="rules" label-width="150px">
        <el-card class="mb-3">
          <template #header>
            <div class="card-header">
              <span>{{ $t('outlet.basicInfo') }}</span>
              <div class="card-actions">
                <el-button
                  v-if="canManageRelatedData && !isBasicInfoEditing"
                  type="primary"
                  plain
                  @click="startEditBasicInfo"
                >
                  编辑
                </el-button>
                <template v-else-if="isBasicInfoEditing">
                  <el-button type="primary" @click="submitForm">
                    {{ $t('common.save') }}
                  </el-button>
                  <el-button @click="goBack">{{ $t('common.back') }}</el-button>
                </template>
              </div>
            </div>
          </template>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item :label="$t('outlet.companyName')" prop="jpCompanyName">
                <el-input
                  v-model="form.jpCompanyName"
                  :placeholder="$t('outlet.placeholderCompanyName')"
                  :disabled="!basicInfoEditable"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('outlet.shortCompanyName')" prop="shortCompanyName">
                <el-input
                  v-model="form.shortCompanyName"
                  :placeholder="$t('outlet.placeholderShortCompanyName')"
                  :disabled="!basicInfoEditable"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item :label="$t('outlet.region')" prop="region">
                <el-select
                  v-model="form.region"
                  :placeholder="$t('outlet.placeholderRegion')"
                  style="width: 100%"
                  :disabled="!basicInfoEditable"
                >
                  <el-option
                    v-for="dict in localizedRegion"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('outlet.contactPerson')" prop="contactPerson">
                <el-input
                  v-model="form.contactPerson"
                  :placeholder="$t('outlet.placeholderContactPerson')"
                  :disabled="!basicInfoEditable"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item :label="$t('outlet.companyType')" prop="cpnType">
                <el-select
                  v-model="form.cpnType"
                  :placeholder="$t('outlet.placeholderCompanyType')"
                  style="width: 100%"
                  :disabled="!basicInfoEditable"
                >
                  <el-option
                    v-for="dict in localizedCpnType"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('outlet.agent')" prop="agentList">
                <el-select
                  v-model="selectedAgent"
                  :placeholder="$t('outlet.placeholderAgent')"
                  style="width: 100%"
                  :disabled="!basicInfoEditable"
                >
                  <el-option
                    v-for="agent in OUTLET_AGENTS"
                    :key="agent.value"
                    :label="agent.label"
                    :value="agent.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item :label="$t('outlet.address')" prop="headquartersAddress">
            <el-input
              v-model="form.headquartersAddress"
              type="textarea"
              :placeholder="$t('outlet.placeholderAddress')"
              :disabled="!basicInfoEditable"
            />
          </el-form-item>
        </el-card>

        <el-card class="mb-3">
          <template #header>
            <div class="card-header">
              <span>月次販売台数</span>
              <!-- 权限修改前：这里的新增/删除/编辑按钮未按角色做前端显隐控制。 -->
              <div>
                <el-button v-if="canManageRelatedData" type="primary" icon="Plus" @click="handleAddMonthlySales">
                  {{ $t('common.add') }}
                </el-button>
                <el-button v-if="canManageRelatedData" type="danger" icon="Delete" @click="handleDeleteMonthlySales">
                  {{ $t('common.delete') }}
                </el-button>
              </div>
            </div>
          </template>

          <el-table
            :data="monthlySalesList"
            :row-class-name="rowMonthlySalesIndex"
            @selection-change="handleMonthlySalesSelectionChange"
          >
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column :label="$t('common.serialNumber')" align="center" prop="index" width="60" />
            <el-table-column label="年月" prop="salesMonth" min-width="120" />
            <el-table-column label="台数" prop="quantity" min-width="100" />
            <el-table-column label="商品名称" prop="productName" min-width="160" show-overflow-tooltip />
            <el-table-column label="备注" prop="remark" min-width="180" show-overflow-tooltip />
            <el-table-column label="最后修改人" prop="updatedBy" min-width="150" show-overflow-tooltip />
            <el-table-column label="最后修改时间" prop="updatedAt" min-width="170" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="scope">
                <el-button v-if="canManageRelatedData" link type="primary" @click="handleEditMonthlySales(scope.row)">
                  修改
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card class="mb-3">
          <template #header>
            <div class="card-header">
              <span>{{ $t('outlet.visitHistory') }}</span>
              <!-- 权限修改前：这里的新增/删除按钮未按角色做前端显隐控制。 -->
              <div>
                <el-button v-if="canManageRelatedData" type="primary" icon="Plus" @click="handleAddVisitHistory">
                  {{ $t('common.add') }}
                </el-button>
                <el-button v-if="canManageRelatedData" type="danger" icon="Delete" @click="handleDeleteVisitHistory">
                  {{ $t('common.delete') }}
                </el-button>
              </div>
            </div>
          </template>

          <el-table
            :data="outletHistoryList"
            :row-class-name="rowOutletHistoryIndex"
            @selection-change="handleOutletHistorySelectionChange"
            height="calc(100vh - 300px)"
            max-height="600px"
          >
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column :label="$t('common.serialNumber')" align="center" prop="index" width="60" />
            <el-table-column :label="$t('outlet.remark')" prop="remark" min-width="400" show-overflow-tooltip />
            <el-table-column :label="$t('outlet.personAgent')" prop="updatedBy" min-width="140" />
            <el-table-column :label="$t('outlet.lastUpdateTime')" prop="updatedAt" width="180" />
          </el-table>
        </el-card>
      </el-form>
    </div>

    <el-dialog
      :title="$t('outlet.addVisit')"
      v-model="visitDialogVisible"
      width="500px"
      append-to-body
    >
      <el-form :model="visitForm" label-width="80px">
        <el-form-item :label="$t('outlet.visitRemark')" prop="remark">
          <el-input
            v-model="visitForm.remark"
            type="textarea"
            :rows="4"
            :placeholder="$t('outlet.placeholderVisitRemark')"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="visitDialogVisible = false">{{ $t('common.cancel') }}</el-button>
          <el-button v-if="canManageRelatedData" type="primary" @click="saveVisitHistory">{{ $t('common.save') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
      :title="monthlySalesDialogTitle"
      v-model="monthlySalesDialogVisible"
      width="560px"
      append-to-body
    >
      <el-form :model="monthlySalesForm" label-width="100px" class="monthly-sales-form">
        <el-form-item label="年月" prop="salesMonth" required>
          <el-date-picker
            v-model="monthlySalesForm.salesMonth"
            type="month"
            value-format="YYYY-MM"
            format="YYYY-MM"
            placeholder="年月"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="台数" prop="quantity" required>
          <el-input-number
            v-model="monthlySalesForm.quantity"
            :min="0"
            :precision="0"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="monthlySalesForm.productName" placeholder="商品名称" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="monthlySalesForm.remark"
            type="textarea"
            :rows="3"
            placeholder="备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="monthlySalesDialogVisible = false">{{ $t('common.cancel') }}</el-button>
          <el-button v-if="canManageRelatedData" type="primary" @click="saveMonthlySales">{{ $t('common.save') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, ref, reactive, onMounted, getCurrentInstance } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { getOutlet, updateOutlet } from "@/api/system/outlet";
import { addHistory, delHistory, listHistory } from "@/api/system/history";
import {
  addMonthlySales,
  delMonthlySales,
  listMonthlySales,
  updateMonthlySales,
} from "@/api/system/monthlySales";
import useUserStore from "@/store/modules/user";
import { t } from "@/i18n";
import {
  OUTLET_AGENTS,
  applySelectedOutletAgents,
  getCurrentBusinessFlowRole,
  getPrimaryBusinessFlow,
  getSelectedOutletAgents,
  canViewAllData,
} from "@/utils/outletAgents";
import { loadGoogleMapsScript } from "@/utils/googleMaps";

const route = useRoute();
const router = useRouter();
const { te } = useI18n();
const userStore = useUserStore();
const specialPermissions = computed(() => canViewAllData(userStore));
const currentBusinessFlowRole = computed(() => getCurrentBusinessFlowRole(userStore));
const canManageRelatedData = computed(() => specialPermissions.value || !!currentBusinessFlowRole.value);

const { proxy } = getCurrentInstance();
const { region, cpn_type } = proxy.useDict("region", "cpn_type");

const dictKeyMaps = {
  region: {
    "蜈ｨ驕ｸ": "all",
    "蜈ｨ騾・": "all",
    "荳ｭ蝗ｽ": "chugoku",
    "荳ｭ驛ｨ": "chubu",
    "荵晏ｷ・": "kyushu",
    "蛹鈴匣": "hokuriku",
    "蛹鈴刎": "hokuriku",
    "蝗帛嵜": "shikoku",
    "譚ｱ蛹・": "tohoku",
    "荳懷圏": "tohoku",
    "髢｢譚ｱ": "kanto",
    "蜈ｳ荳・": "kanto",
    "髢｢隘ｿ": "kansai",
    "蜈ｳ隘ｿ": "kansai",
    "蛹玲ｵｷ驕・": "hokkaido",
  },
  companyType: {
    "莉｣逅・ｺ・": "agent",
    "雋ｩ螢ｲ蠎・": "outlet",
    "雍ｩ蜊門ｺ・": "outlet",
  },
};

function localizeDictOptions(options, group) {
  const map = dictKeyMaps[group] || {};
  return (options || []).map((item) => {
    const key = map[item.value] || map[item.label];
    const i18nPath = key ? `outlet.dict.${group}.${key}` : "";
    return {
      ...item,
      label: key && te(i18nPath) ? t(i18nPath) : item.label,
    };
  });
}

const localizedRegion = computed(() => localizeDictOptions(region.value, "region"));
const localizedCpnType = computed(() => localizeDictOptions(cpn_type.value, "companyType"));
const title = computed(() => t("outlet.detailTitle"));
const isBasicInfoEditing = ref(false);
const basicInfoEditable = computed(() => canManageRelatedData.value && isBasicInfoEditing.value);

const form = ref({});
const formSnapshot = ref(null);
const selectedAgent = ref("");
const outletHistoryList = ref([]);
const checkedOutletHistory = ref([]);
const monthlySalesList = ref([]);
const checkedMonthlySales = ref([]);
const visitDialogVisible = ref(false);
const monthlySalesDialogVisible = ref(false);
const monthlySalesDialogTitle = ref("新增月次販売台数");

const visitForm = reactive({
  remark: "",
});

const monthlySalesForm = reactive({
  salesId: null,
  outletId: "",
  salesMonth: "",
  quantity: 0,
  productName: "",
  remark: "",
});

const rules = computed(() => ({
  jpCompanyName: [
    { required: true, message: t("outlet.requiredCompanyName"), trigger: "blur" },
  ],
  shortCompanyName: [
    { required: true, message: t("outlet.requiredShortCompanyName"), trigger: "blur" },
  ],
  region: [{ required: true, message: t("outlet.requiredRegion"), trigger: "change" }],
  headquartersAddress: [
    { required: true, message: t("outlet.requiredAddress"), trigger: "blur" },
  ],
}));

let geocoder = null;

onMounted(() => {
  getDetail();
  initGeocoder();
});

function initGeocoder() {
  if (typeof google !== "undefined" && google.maps && google.maps.Geocoder) {
    geocoder = new google.maps.Geocoder();
  } else {
    loadGoogleMapsScript().then(() => {
      geocoder = new google.maps.Geocoder();
    });
  }
}

async function getAddressCoordinates(address) {
  return new Promise((resolve, reject) => {
    const normalizedAddress = (address || "").trim();
    if (!normalizedAddress) {
      reject(new Error("请输入完整地址"));
      return;
    }
    if (!geocoder) {
      initGeocoder();
      setTimeout(() => {
        if (!geocoder) {
          reject(new Error(t("agent.mapLoadFailed")));
          return;
        }
        performGeocode(normalizedAddress, resolve, reject);
      }, 1000);
    } else {
      performGeocode(normalizedAddress, resolve, reject);
    }
  });
}

function performGeocode(address, resolve, reject) {
  geocoder.geocode({ address }, (results, status) => {
    if (status === "OK" && results[0]) {
      if (results[0].partial_match) {
        reject(new Error("地址不够准确，请重新填写完整地址"));
        return;
      }
      const location = results[0].geometry.location;
      resolve({
        lat: location.lat(),
        lng: location.lng(),
      });
    } else if (status === "ZERO_RESULTS") {
      reject(new Error(t("agent.addressNotFound")));
    } else {
      reject(new Error(`地址解析失败(${status})，请稍后重试`));
    }
  });
}

function goBack() {
  router.go(-1);
}

function rowOutletHistoryIndex({ row, rowIndex }) {
  row.index = rowIndex + 1;
}

function rowMonthlySalesIndex({ row, rowIndex }) {
  row.index = rowIndex + 1;
}

function handleOutletHistorySelectionChange(selection) {
  checkedOutletHistory.value = selection.map((item) => item.index);
}

function handleMonthlySalesSelectionChange(selection) {
  checkedMonthlySales.value = selection.map((item) => item.index);
}

function getRolesAsString() {
  if (userStore.roles && userStore.roles.length > 0) {
    return userStore.roles.join(",");
  }
  return t("common.unassignedRole");
}

function currentUserLabel() {
  return `${userStore.name || t("common.unknownUser")}(${getRolesAsString()})`;
}

function cloneSnapshot(snapshot) {
  return snapshot ? JSON.parse(JSON.stringify(snapshot)) : null;
}

function formatCurrentMinute() {
  return new Date()
    .toLocaleString("zh-CN", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
    })
    .replace(/\//g, "-");
}

function stripRemovedOutletFields(outlet) {
  const {
    breakthroughStatus,
    hwSalesAvg,
    salesRecord,
    estimatedIncrease,
    salesPlan,
    remarks,
    installerEventParticipation,
    outletRemark,
    monthlySalesList,
    outletHistoryList,
    ...visibleOutlet
  } = outlet;
  return visibleOutlet;
}

function refreshMonthlySales() {
  listMonthlySales({ outletId: route.params.id, pageNum: 1, pageSize: 1000 }).then((res) => {
    monthlySalesList.value = res.rows || [];
  });
}

function startEditBasicInfo() {
  formSnapshot.value = cloneSnapshot(form.value);
  selectedAgent.value = getPrimaryBusinessFlow(form.value);
  isBasicInfoEditing.value = true;
}

function getDetail() {
  const id = route.params.id;
  getOutlet(id)
    .then((response) => {
      const detail = {
        ...response.data,
        agentList: getSelectedOutletAgents(response.data),
      };
      form.value = detail;
      selectedAgent.value = getPrimaryBusinessFlow(detail);
      formSnapshot.value = JSON.parse(JSON.stringify(detail));
      isBasicInfoEditing.value = false;
    })
    .catch((error) => {
      ElMessage.error(error?.message || "无权查看该店铺详情");
      goBack();
    });
  refreshMonthlySales();
  refreshVisitHistory();
}

function refreshVisitHistory() {
  const params = specialPermissions.value
    ? { outletId: route.params.id }
    : { outletId: route.params.id, agent: currentBusinessFlowRole.value };
  listHistory(params).then((res) => {
    outletHistoryList.value = res.rows || [];
  });
}

function handleAddMonthlySales() {
  if (!canManageRelatedData.value) {
    return;
  }
  monthlySalesDialogTitle.value = "新增月次販売台数";
  monthlySalesForm.salesId = null;
  monthlySalesForm.outletId = route.params.id;
  monthlySalesForm.salesMonth = "";
  monthlySalesForm.quantity = 0;
  monthlySalesForm.productName = "";
  monthlySalesForm.remark = "";
  monthlySalesDialogVisible.value = true;
}

function handleEditMonthlySales(row) {
  if (!canManageRelatedData.value) {
    return;
  }
  monthlySalesDialogTitle.value = "编辑月次販売台数";
  monthlySalesForm.salesId = row.salesId;
  monthlySalesForm.outletId = row.outletId;
  monthlySalesForm.salesMonth = row.salesMonth;
  monthlySalesForm.quantity = row.quantity;
  monthlySalesForm.productName = row.productName;
  monthlySalesForm.remark = row.remark;
  monthlySalesDialogVisible.value = true;
}

function saveMonthlySales() {
  if (!canManageRelatedData.value) {
    return;
  }
  if (!monthlySalesForm.salesMonth) {
    ElMessage.error("请选择年月");
    return;
  }
  const payload = {
    ...monthlySalesForm,
    outletId: route.params.id,
    updatedBy: currentUserLabel(),
    updatedAt: formatCurrentMinute(),
  };
  const request = payload.salesId ? updateMonthlySales(payload) : addMonthlySales(payload);
  request.then(() => {
    monthlySalesDialogVisible.value = false;
    refreshMonthlySales();
    ElMessage.success(payload.salesId ? t("common.successEdit") : t("common.successAdd"));
  });
}

function handleDeleteMonthlySales() {
  if (!canManageRelatedData.value) {
    return;
  }
  if (checkedMonthlySales.value.length == 0) {
    ElMessage.error("请选择要删除的月次販売台数");
    return;
  }
  const salesIds = monthlySalesList.value
    .filter((item) => checkedMonthlySales.value.includes(item.index))
    .map((item) => item.salesId)
    .filter((salesId) => salesId !== undefined && salesId !== null);
  if (salesIds.length === 0) {
    ElMessage.error("月次販売台数ID未找到");
    return;
  }
  delMonthlySales(salesIds.join(",")).then(() => {
    checkedMonthlySales.value = [];
    refreshMonthlySales();
    ElMessage.success(t("common.successDelete"));
  });
}

function handleAddVisitHistory() {
  if (!canManageRelatedData.value) {
    return;
  }
  visitForm.remark = "";
  visitDialogVisible.value = true;
}

function saveVisitHistory() {
  if (!canManageRelatedData.value) {
    return;
  }
  if (!visitForm.remark.trim()) {
    ElMessage.error(t("outlet.remarkRequired"));
    return;
  }

  const obj = {
    remark: visitForm.remark,
    updatedBy: currentUserLabel(),
    createdBy: userStore.name || t("common.unknownUser"),
    outletId: route.params.id,
    agent: specialPermissions.value ? userStore.roles[0] : currentBusinessFlowRole.value,
    updatedAt: formatCurrentMinute(),
  };

  addHistory(obj)
    .then(() => {
      refreshVisitHistory();
      visitDialogVisible.value = false;
      ElMessage.success(t("common.successAdd"));
    })
    .catch((error) => {
      ElMessage.error(t("outlet.addFailed", { message: error.message || "unknown" }));
    });
}

function handleDeleteVisitHistory() {
  if (!canManageRelatedData.value) {
    return;
  }
  if (checkedOutletHistory.value.length == 0) {
    ElMessage.error(t("outlet.selectHistoryFirst"));
    return;
  }
  const historyIds = outletHistoryList.value
    .filter((item) => checkedOutletHistory.value.includes(item.index))
    .map((item) => item.historyId)
    .filter((historyId) => historyId !== undefined);
  if (historyIds.length === 0) {
    ElMessage.error(t("outlet.historyIdMissing"));
    return;
  }
  delHistory(historyIds.join(","))
    .then(() => {
      checkedOutletHistory.value = [];
      refreshVisitHistory();
      ElMessage.success(t("common.successDelete"));
    })
    .catch((error) => {
      ElMessage.error(t("outlet.deleteFailed", { message: error.message || "unknown" }));
    });
}

function submitForm() {
  if (!basicInfoEditable.value) {
    return;
  }
  proxy.$refs["outletRef"].validate((valid) => {
    if (!valid) {
      return;
    }
    getAddressCoordinates(form.value.headquartersAddress)
      .then(({ lat, lng }) => {
        form.value.agentList = selectedAgent.value ? [selectedAgent.value] : [];
        const formData = {
          ...stripRemovedOutletFields(applySelectedOutletAgents(form.value)),
          lat,
          lng,
        };

        updateOutlet(formData).then(() => {
          ElMessage.success(t("common.successEdit"));
          isBasicInfoEditing.value = false;
          formSnapshot.value = cloneSnapshot(form.value);
          router.go(-1);
        });
      })
      .catch((error) => {
        ElMessage.error(error.message);
      });
  });
}
</script>

<style scoped>
.outlet-detail-page {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
  background-color: white;
  padding: 15px 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.page-content {
  margin-bottom: 20px;
}

.page-footer {
  text-align: center;
  padding: 20px;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.mb-3 {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.agent-multi-select :deep(.el-select__selection) {
  flex-wrap: nowrap;
  overflow-x: auto;
  padding-bottom: 2px;
}

.agent-multi-select :deep(.el-select__selected-item) {
  flex: 0 0 auto;
}

.agent-multi-select :deep(.el-tag) {
  max-width: none;
}

.monthly-sales-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  column-gap: 16px;
}

.monthly-sales-form > .el-form-item:nth-child(n + 3) {
  grid-column: 1 / -1;
}

@media (max-width: 640px) {
  .monthly-sales-form {
    grid-template-columns: 1fr;
  }

  .monthly-sales-form > .el-form-item:nth-child(n + 3) {
    grid-column: auto;
  }
}

:deep(.el-card__header) {
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}
</style>
