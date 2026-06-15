<template>
  <div class="app-container">
    <el-form ref="agentRef" :model="form" :rules="rules" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item :label="$t('agent.hqName')" prop="hqName">
            <el-input v-model="form.hqName" :placeholder="$t('agent.placeholderHqName')" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item :label="$t('agent.branchName')" prop="branchName">
            <el-input v-model="form.branchName" :placeholder="$t('agent.placeholderBranchName')" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item :label="$t('agent.address')" prop="address">
            <el-input v-model="form.address" type="textarea" :placeholder="$t('agent.placeholderAddress')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('agent.region')" prop="region">
            <el-select v-model="form.region" :placeholder="$t('agent.placeholderRegion')" style="width: 100%">
              <el-option v-for="dict in region" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('agent.abbreviation')" prop="abbreviation">
            <el-select v-model="form.abbreviation" :placeholder="$t('agent.placeholderAbbreviation')" style="width: 100%">
              <el-option v-for="dict in abbreviation" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <div style="display: flex; gap: 12px; justify-content: flex-end;">
        <el-button @click="goBack">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">{{ $t('common.confirm') }}</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup name="AgentDetail">
import { computed, onMounted, reactive, ref, toRefs } from "vue";
import { useRoute, useRouter } from "vue-router";
import { t } from "@/i18n";
import { addAgent, getAgent, updateAgent } from "@/api/system/agent";

const { proxy } = getCurrentInstance();
const { region, abbreviation } = proxy.useDict("region", "abbreviation");

const route = useRoute();
const router = useRouter();

const agentRef = ref();
const saving = ref(false);

const data = reactive({
  form: {
    id: undefined,
    hqName: "",
    branchName: "",
    address: "",
    region: "",
    abbreviation: "",
  },
});
const { form } = toRefs(data);

const isEdit = computed(() => route.name === "AgentDetail" && !!route.params.id);
const agentId = computed(() => Number(route.params.id));

const rules = computed(() => ({
  hqName: [{ required: true, message: t("agent.requiredHqName"), trigger: "blur" }],
  branchName: [{ required: true, message: t("agent.requiredBranchName"), trigger: "blur" }],
  address: [{ required: true, message: t("agent.requiredAddress"), trigger: "blur" }],
}));

function goBack() {
  router.push({ path: "/agent" });
}

async function loadDetail() {
  if (!isEdit.value) return;
  if (!Number.isFinite(agentId.value) || agentId.value <= 0) return;
  const resp = await getAgent(agentId.value);
  form.value = resp.data || {};
}

function submitForm() {
  if (!agentRef.value) return;
  agentRef.value.validate(async (valid) => {
    if (!valid) return;
    saving.value = true;
    try {
      const payload = { ...form.value };
      if (isEdit.value) {
        await updateAgent(payload);
        proxy.$modal.msgSuccess(t("common.updateSuccess"));
      } else {
        await addAgent(payload);
        proxy.$modal.msgSuccess(t("common.addSuccess"));
      }
      goBack();
    } finally {
      saving.value = false;
    }
  });
}

onMounted(loadDetail);
</script>

