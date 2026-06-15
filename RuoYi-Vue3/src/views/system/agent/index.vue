<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryRef"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item :label="$t('agent.hqName')" prop="hqName">
        <el-input
          v-model="queryParams.hqName"
          :placeholder="$t('agent.placeholderHqName')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('agent.branchName')" prop="branchName">
        <el-input
          v-model="queryParams.branchName"
          :placeholder="$t('agent.placeholderBranchName')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('agent.region')" prop="region">
        <el-select
          v-model="queryParams.region"
          :placeholder="$t('agent.placeholderRegion')"
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
          >{{ $t('common.search') }}</el-button
        >
        <el-button icon="Refresh" @click="resetQuery">{{ $t('common.reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['system:agent:add']"
          >{{ $t('common.add') }}</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:agent:edit']"
          >{{ $t('common.edit') }}</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:agent:remove']"
          >{{ $t('common.delete') }}</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['system:agent:export']"
          >{{ $t('common.export') }}</el-button
        >
      </el-col>
      <right-toolbar
        v-model:showSearch="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="agentList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('agent.id')" align="center" prop="id" />
      <el-table-column :label="$t('agent.hqName')" align="center" prop="hqName" />
      <el-table-column :label="$t('agent.branchName')" align="center" prop="branchName" />
      <el-table-column :label="$t('agent.address')" align="center" prop="address" />
      <el-table-column :label="$t('agent.region')" align="center" prop="region">
        <template #default="scope">
          <dict-tag :options="region" :value="scope.row.region" />
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('common.operation')"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template #default="scope">
          <el-button
            link
            type="primary"
            icon="Edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:agent:edit']"
            >{{ $t('common.edit') }}</el-button
          >
          <el-button
            link
            type="primary"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:agent:remove']"
            >{{ $t('common.delete') }}</el-button
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

    <!-- 添加或修改代理店管理对话框 -->
    <!-- 代理店编辑改为独立页面（/agent/detail/:id、/agent/add） -->
  </div>
</template>

<script setup name="Agent">
import {
  listAgent,
  delAgent,
} from "@/api/system/agent";
import useUserStore from "@/store/modules/user";
import { t } from "@/i18n";
import { useRouter } from "vue-router";
const { proxy } = getCurrentInstance();
const { region, abbreviation } = proxy.useDict("region", "abbreviation");

const agentList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const userStore = useUserStore();
const specialPermissions = ["admin", "common", "readonly"].includes(
  userStore.roles[0]
);

const router = useRouter();

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    hqName: null,
    branchName: null,
    address: null,
    region: null,
  },
});

const { queryParams } = toRefs(data);

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
  router.push({ name: "AgentAdd" });
}

/** 修改按钮操作 */
function handleUpdate(row) {
  const _id = row.id || ids.value;
  router.push({ name: "AgentDetail", params: { id: _id } });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal
    .confirm(t('agent.deleteConfirm', { ids: _ids }))
    .then(function () {
      return delAgent(_ids);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(t('common.successDelete'));
    })
    .catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    "system/agent/export",
    {
      ...queryParams.value,
    },
    `agent_${new Date().getTime()}.xlsx`
  );
}

/** 查询代理店管理列表 */
function getList() {
  loading.value = true;

  if (specialPermissions) {
    listAgent(queryParams.value).then((response) => {
      agentList.value = response.rows;
      total.value = response.total;
      loading.value = false;
    });
  } else {
    const params = { ...queryParams.value };
    params.abbreviation = userStore.roles[0];
    listAgent(params).then((response) => {
      agentList.value = response.rows;
      total.value = response.total;
      loading.value = false;
    });
  }
}

getList();
</script>
