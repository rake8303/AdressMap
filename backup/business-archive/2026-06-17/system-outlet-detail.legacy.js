// Archived on 2026-06-17 from:
// G:\AdressMap\RuoYi-Vue3\src\views\system\outlet\detail.vue
//
// These handlers were kept as a pre-permission-tightening implementation.
// They are no longer referenced by the active page and were moved out of the
// runtime file to reduce maintenance noise while preserving the old logic.

function legacyGetDetail() {
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

function legacyRefreshVisitHistory() {
  const params = specialPermissions.value
    ? { outletId: route.params.id }
    : { outletId: route.params.id, agent: currentBusinessFlowRole.value };
  listHistory(params).then((res) => {
    outletHistoryList.value = res.rows || [];
  });
}

function legacyRefreshMonthlySales() {
  listMonthlySales({ outletId: route.params.id, pageNum: 1, pageSize: 1000 }).then((res) => {
    monthlySalesList.value = res.rows || [];
  });
}

function legacyStartEditBasicInfo() {
  formSnapshot.value = cloneSnapshot(form.value);
  isBasicInfoEditing.value = true;
}

function legacyHandleAddMonthlySales() {
  monthlySalesDialogTitle.value = "新增月次販売台数";
  monthlySalesForm.salesId = null;
  monthlySalesForm.outletId = route.params.id;
  monthlySalesForm.salesMonth = "";
  monthlySalesForm.quantity = 0;
  monthlySalesForm.productName = "";
  monthlySalesForm.remark = "";
  monthlySalesDialogVisible.value = true;
}

function legacyHandleEditMonthlySales(row) {
  monthlySalesDialogTitle.value = "编辑月次販売台数";
  monthlySalesForm.salesId = row.salesId;
  monthlySalesForm.outletId = row.outletId;
  monthlySalesForm.salesMonth = row.salesMonth;
  monthlySalesForm.quantity = row.quantity;
  monthlySalesForm.productName = row.productName;
  monthlySalesForm.remark = row.remark;
  monthlySalesDialogVisible.value = true;
}

function legacySaveMonthlySales() {
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

function legacyHandleDeleteMonthlySales() {
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

function legacyHandleAddVisitHistory() {
  visitForm.remark = "";
  visitDialogVisible.value = true;
}

function legacySaveVisitHistory() {
  if (!visitForm.remark.trim()) {
    ElMessage.error(t("outlet.remarkRequired"));
    return;
  }

  const obj = {
    remark: visitForm.remark,
    updatedBy: currentUserLabel(),
    createdBy: userStore.name || t("common.unknownUser"),
    outletId: route.params.id,
    agent: userStore.roles[0],
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

function legacyHandleDeleteVisitHistory() {
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

function legacySubmitForm() {
  if (!basicInfoEditable.value) {
    return;
  }
  proxy.$refs["outletRef"].validate((valid) => {
    if (!valid) {
      return;
    }
    getAddressCoordinates(form.value.headquartersAddress)
      .then(({ lat, lng }) => {
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
