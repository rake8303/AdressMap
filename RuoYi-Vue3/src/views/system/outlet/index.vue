<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryRef"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="会社名" prop="jpCompanyName">
        <el-input
          v-model="queryParams.jpCompanyName"
          placeholder="请输入会社名"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="エリア" prop="region">
        <el-select
          v-model="queryParams.region"
          placeholder="请选择エリア"
          clearable
          style="width: 200px"
        >
          <el-option
            v-for="dict in region"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['system:outlet:add']"
          >新增</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:outlet:edit']"
          >修改</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:outlet:remove']"
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['system:outlet:export']"
          >导出</el-button
        >
      </el-col>
      <right-toolbar
        v-model:showSearch="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="outletList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" />
      <el-table-column label="会社名" align="center" prop="jpCompanyName" />
      <el-table-column label="エリア" align="center" prop="region">
        <template #default="scope">
          <dict-tag :options="region" :value="scope.row.region" />
        </template>
      </el-table-column>
      <el-table-column label="住所" align="center" prop="headquartersAddress" />
      <el-table-column
        label="更新时间"
        align="center"
        prop="updatedAt"
        width="180"
      >
        <template #default="scope">
          <span>{{ parseTime(scope.row.updatedAt, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template #default="scope">
          <el-button
            link
            type="primary"
            icon="Edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:outlet:edit']"
            >修改</el-button
          >
          <el-button
            link
            type="primary"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:outlet:remove']"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改販売店管理对话框 -->
    <el-dialog :title="title" v-model="open" width="1000px" append-to-body>
      <el-form ref="outletRef" :model="form" :rules="rules" label-width="150px">
        <div style="max-height: 550px; overflow-y: auto; padding-right: 10px">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="会社名" prop="jpCompanyName">
                <el-input v-model="form.jpCompanyName" placeholder="请输入会社名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="会社名简称" prop="shortCompanyName">
                <el-input v-model="form.shortCompanyName" placeholder="请输入会社名简称" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="エリア" prop="region">
                <el-select v-model="form.region" placeholder="请选择エリア" style="width: 100%">
                  <el-option
                    v-for="dict in region"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="责任人" prop="contactPerson">
                <el-input v-model="form.contactPerson" placeholder="请输入责任人" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="会社種類" prop="cpnType">
                <el-select v-model="form.cpnType" placeholder="请选择会社種類" style="width: 100%">
                  <el-option
                    v-for="dict in cpn_type"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="canViewBusinessFlowInfo">
              <el-form-item label="商流" prop="agentList">
                <el-select
                  v-model="selectedAgent"
                  placeholder="请选择商流"
                  style="width: 100%"
                >
                  <el-option
                    v-for="agent in OUTLET_AGENTS"
                    :key="agent.value"
                    :label="agent.label"
                    :value="agent.value"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="住所" prop="headquartersAddress">
            <el-input
              v-model="form.headquartersAddress"
              type="textarea"
              placeholder="请输入内容"
            />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Outlet">
import { computed, ref, reactive, onMounted, getCurrentInstance, toRefs } from "vue";
import {
  listOutlet,
  listByBusinessFlow,
  getOutlet,
  delOutlet,
  addOutlet,
  updateOutlet,
} from "@/api/system/outlet";
import useUserStore from "@/store/modules/user";
import {
  OUTLET_AGENTS,
  applySelectedOutletAgents,
  canViewAllData
} from "@/utils/outletAgents";

const userStore = useUserStore();
console.log("用户信息:", userStore.name, userStore.roles);
const specialPermissions = computed(() => canViewAllData(userStore));
const canViewBusinessFlowInfo = computed(() => canViewAllData(userStore));
console.log("specialPermissions:", specialPermissions.value);

// 在组件挂载时确保用户信息已加载
onMounted(() => {
  // 如果用户信息还未加载，手动触发一次加载
  if (!userStore.name && userStore.getInfo) {
    userStore
      .getInfo()
      .then(() => {
        console.log("用户信息加载完成:", userStore.name, userStore.roles);
      })
      .catch((err) => {
        console.error("用户信息加载失败:", err);
      });
  }
});

function getRolesAsString() {
  if (userStore.roles && userStore.roles.length > 0) {
    return userStore.roles.join(",");
  }
  return "未分配角色";
}

const { proxy } = getCurrentInstance();
const { region, cpn_type, business_flow } = proxy.useDict(
  "region",
  "cpn_type",
  "business_flow"
);

const outletList = ref([]);
const outletHistoryList = ref([]);
const selectedAgent = ref("");
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const checkedOutletHistory = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

// Google Maps Geocoder 实例
let geocoder = null;

// 初始化 Google Maps Geocoder
function initGeocoder() {
  if (typeof google !== 'undefined' && google.maps && google.maps.Geocoder) {
    geocoder = new google.maps.Geocoder();
  } else {
    // 动态加载 Google Maps API
    const script = document.createElement('script');
    script.src = 'https://maps.googleapis.com/maps/api/js?key=AIzaSyAMI_ZtKYiIbOXO25apqOOBs3QkNx0gLFw&libraries=marker';
    script.onload = () => {
      geocoder = new google.maps.Geocoder();
    };
    document.head.appendChild(script);
  }
}

// 组件挂载时初始化 Geocoder
onMounted(() => {
  initGeocoder();
});

// 获取地址坐标的函数
async function getAddressCoordinates(address) {
  return new Promise((resolve, reject) => {
    const normalizedAddress = (address || "").trim();
    if (!normalizedAddress) {
      reject(new Error("请输入有效的地址"));
      return;
    }
    if (!geocoder) {
      initGeocoder();
      setTimeout(() => {
        if (!geocoder) {
          reject(new Error('地图服务加载失败，请稍后重试'));
          return;
        }
        performGeocode(normalizedAddress, resolve, reject);
      }, 1000);
    } else {
      performGeocode(normalizedAddress, resolve, reject);
    }
  });
}

// 执行地理编码
function performGeocode(address, resolve, reject) {
  geocoder.geocode({ address: address }, (results, status) => {
    if (status === 'OK' && results[0]) {
      if (results[0].partial_match) {
        reject(new Error('地址不够准确，请重新填写完整地址'));
        return;
      }
      const location = results[0].geometry.location;
      resolve({
        lat: location.lat(),
        lng: location.lng()
      });
    } else if (status === 'ZERO_RESULTS') {
      reject(new Error('找不到该地址，请确认'));
    } else {
      reject(new Error(`地址解析失败(${status})，请稍后重试`));
    }
  });
}


const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    jpCompanyName: null,
    region: null,
    headquartersAddress: null,
  },
  rules: {
    jpCompanyName: [
      { required: true, message: "会社名不能为空", trigger: "blur" },
    ],
    shortCompanyName: [
      { required: true, message: "会社名简称不能为空", trigger: "blur" },
    ],
    region: [{ required: true, message: "エリア不能为空", trigger: "change" }],
    headquartersAddress: [
      { required: true, message: "住所不能为空", trigger: "blur" },
    ],
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 查询販売店管理列表 */
function getList() {
  loading.value = true;

  if (specialPermissions.value) {
    // 用户具有特殊权限，调用 listOutlet 获取数据
    listOutlet(queryParams.value).then((response) => {
      outletList.value = response.rows;
      total.value = response.total;
      loading.value = false;
    });
  } else {
    // 用户不具有特殊权限，调用 listByBusinessFlow 获取数据
    listByBusinessFlow(queryParams.value).then((response) => {
      outletList.value = response.rows;
      total.value = response.total;
      loading.value = false;
    });
  }
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
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
    ...visibleOutlet
  } = outlet;
  return visibleOutlet;
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    jpCompanyName: null,
    shortCompanyName: null,
    region: null,
    headquartersAddress: null,
    contactPerson: null,
    agentList: [],
    totalSalesAvg: null,
    breakthroughStatus: null,
    hwSalesAvg: null,
    estimatedIncrease: null,
    salesPlan: null,
    remarks: null,
    installerEventParticipation: null,
    createdAt: null,
    updatedAt: null,
    abbreviation: null,
    cpnType: null,
    outletRemark: null,
    salesRecord: null,
  };
  selectedAgent.value = "";
  outletHistoryList.value = [];
  proxy.resetForm("outletRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加販売店管理";
}

/** 修改按钮操作 */

/** 修改按钮操作 */
function handleUpdate(row) {
  const id = row.id || ids.value[0];
  // 跳转到详情页面，而不是打开弹出框
  proxy.$router.push({
    name: "OutletDetail",
    params: { id: id },
  });
}

/**
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value
  getOutlet(_id).then(response => {
    form.value = response.data;
    outletHistoryList.value = response.data.outletHistoryList;
    open.value = true;
    title.value = "修改販売店管理";
  });
}
**/
/** 提交按钮 */
/** 提交按钮 */
function submitForm() {
  proxy.$refs["outletRef"].validate((valid) => {
    if (valid) {
      // 在提交前先获取地址的经纬度
      getAddressCoordinates(form.value.headquartersAddress)
        .then(({ lat, lng }) => {
          form.value.agentList = selectedAgent.value ? [selectedAgent.value] : [];
          // 将经纬度添加到表单数据中
          const formData = {
            ...stripRemovedOutletFields(applySelectedOutletAgents(form.value)),
            lat: lat,
            lng: lng,
            outletHistoryList: outletHistoryList.value,
          };
          if (form.value.id != null) {
            updateOutlet(formData).then((response) => {
              proxy.$modal.msgSuccess("修改成功");
              open.value = false;
              getList();
            });
          } else {
            addOutlet(formData).then((response) => {
              proxy.$modal.msgSuccess("新增成功");
              open.value = false;
              getList();
            });
          }
        })
        .catch(error => {
          proxy.$modal.msgError(error.message);
        });
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal
    .confirm('是否确认删除販売店管理编号为"' + _ids + '"的数据项？')
    .then(function () {
      return delOutlet(_ids);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}

/** 販売店管理-历史记录序号 */
function rowOutletHistoryIndex({ row, rowIndex }) {
  row.index = rowIndex + 1;
}

/** 販売店管理-历史记录添加按钮操作 */
function handleAddOutletHistory() {
  let obj = {};
  obj.remark1 = "";
  // 动态获取当前用户信息
  const currentUser = useUserStore();
  obj.updatedBy = currentUser.name || "未知用户"; // 自动带入登录人
  // 带上时和分的时间格式
  obj.updatedAt = new Date()
    .toLocaleString("zh-CN", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
    })
    .replace(/\//g, "-"); // 自动带入当前时间(包含时分)
  obj.createdBy = "";
  obj.createdAt = "";
  obj.agent = ""; // 保留agent字段，但实际显示使用角色信息
  outletHistoryList.value.push(obj);
}

/** 販売店管理-历史记录删除按钮操作 */
function handleDeleteOutletHistory() {
  if (checkedOutletHistory.value.length == 0) {
    proxy.$modal.msgError("请先选择要删除的販売店管理-历史记录数据");
  } else {
    const outletHistorys = outletHistoryList.value;
    const checkedOutletHistorys = checkedOutletHistory.value;
    outletHistoryList.value = outletHistorys.filter(function (item) {
      return checkedOutletHistorys.indexOf(item.index) == -1;
    });
  }
}

/** 复选框选中数据 */
function handleOutletHistorySelectionChange(selection) {
  checkedOutletHistory.value = selection.map((item) => item.index);
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    "system/outlet/export",
    {
      ...queryParams.value,
    },
    `outlet_${new Date().getTime()}.xlsx`
  );
}
getList();
</script>
