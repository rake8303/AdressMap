<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="创建人" prop="createdBy">
        <el-input
          v-model="queryParams.createdBy"
          placeholder="请输入创建人"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createdAt">
        <el-date-picker clearable
          v-model="queryParams.createdAt"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择创建时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="修改人" prop="updatedBy">
        <el-input
          v-model="queryParams.updatedBy"
          placeholder="请输入修改人"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="修改时间" prop="updatedAt">
        <el-date-picker clearable
          v-model="queryParams.updatedAt"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择修改时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="代理店" prop="agent">
        <el-input
          v-model="queryParams.agent"
          placeholder="请输入代理店"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
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
          v-hasPermi="['system:history:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:history:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:history:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['system:history:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="historyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="子表主键" align="center" prop="historyId" />
      <el-table-column label="关联主表ID" align="center" prop="outletId" />
      <el-table-column label="创建人" align="center" prop="createdBy" />
      <el-table-column label="创建时间" align="center" prop="createdAt" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createdAt, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="修改人" align="center" prop="updatedBy" />
      <el-table-column label="修改时间" align="center" prop="updatedAt" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.updatedAt, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="代理店備考" align="center" prop="remark" />
      <el-table-column label="代理店" align="center" prop="agent" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:history:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:history:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改販売店管理-历史记录对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="historyRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="创建人" prop="createdBy">
          <el-input v-model="form.createdBy" placeholder="请输入创建人" />
        </el-form-item>
        <el-form-item label="创建时间" prop="createdAt">
          <el-date-picker clearable
            v-model="form.createdAt"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择创建时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="修改人" prop="updatedBy">
          <el-input v-model="form.updatedBy" placeholder="请输入修改人" />
        </el-form-item>
        <el-form-item label="修改时间" prop="updatedAt">
          <el-date-picker clearable
            v-model="form.updatedAt"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择修改时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="代理店備考" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="代理店" prop="agent">
          <el-input v-model="form.agent" placeholder="请输入代理店" />
        </el-form-item>
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

<script setup name="History">
import { listHistory, getHistory, delHistory, addHistory, updateHistory } from "@/api/system/history";

const { proxy } = getCurrentInstance();

const historyList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    outletId: null,
    createdBy: null,
    createdAt: null,
    updatedBy: null,
    updatedAt: null,
    agent: null
  },
  rules: {
    outletId: [
      { required: true, message: "关联主表ID不能为空", trigger: "blur" }
    ],
    createdBy: [
      { required: true, message: "创建人不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询販売店管理-历史记录列表 */
function getList() {
  loading.value = true;
  listHistory(queryParams.value).then(response => {
    historyList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    historyId: null,
    outletId: null,
    createdBy: null,
    createdAt: null,
    updatedBy: null,
    updatedAt: null,
    remark: null,
    agent: null
  };
  proxy.resetForm("historyRef");
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
  ids.value = selection.map(item => item.historyId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加販売店管理-历史记录";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _historyId = row.historyId || ids.value
  getHistory(_historyId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改販売店管理-历史记录";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["historyRef"].validate(valid => {
    if (valid) {
      if (form.value.historyId != null) {
        updateHistory(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addHistory(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _historyIds = row.historyId || ids.value;
  proxy.$modal.confirm('是否确认删除販売店管理-历史记录编号为"' + _historyIds + '"的数据项？').then(function() {
    return delHistory(_historyIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('system/history/export', {
    ...queryParams.value
  }, `history_${new Date().getTime()}.xlsx`)
}

getList();
</script>
