export const OUTLET_AGENTS = [
  { label: "織田家", value: "織田家" },
  { label: "豊臣家", value: "豊臣家" },
  { label: "徳川家", value: "徳川家" },
  { label: "武田家", value: "武田家" },
  { label: "上杉家", value: "上杉家" },
];

export const UNDECIDED_BUSINESS_FLOW = "浪人衆";
export const ALL_DATA_ROLE_KEYS = ["admin", "common", "readonly"];
export const ALL_DATA_USERNAMES = ["user1", "user2"];

export const OUTLET_AGENT_COLORS = {
  織田家: "#C7000B",
  豊臣家: "#7E57C2",
  徳川家: "#1E5AA8",
  武田家: "#D9822B",
  上杉家: "#111827",
  [UNDECIDED_BUSINESS_FLOW]: "#FFFFFF",
};

export function getSelectedOutletAgents(data = {}) {
  if (Array.isArray(data.agentList)) {
    return data.agentList;
  }

  return [];
}

export function applySelectedOutletAgents(data = {}) {
  const agentList = getSelectedOutletAgents(data).slice(0, 1);
  return { ...data, agentList };
}

export function canViewAllData(userStore = {}) {
  const currentRoles = Array.isArray(userStore.roles) ? userStore.roles : [];
  const currentUserName = userStore.name || userStore.userName || "";
  return currentRoles.some((role) => ALL_DATA_ROLE_KEYS.includes(role)) || ALL_DATA_USERNAMES.includes(currentUserName);
}

export function getCurrentBusinessFlowRole(userStore = {}) {
  if (canViewAllData(userStore)) {
    return "";
  }

  const currentRoles = Array.isArray(userStore.roles) ? userStore.roles : [];
  return currentRoles.find((role) => !ALL_DATA_ROLE_KEYS.includes(role)) || "";
}

export function getVisibleBusinessFlows(agentList = [], userStore = {}) {
  if (canViewAllData(userStore)) {
    return Array.isArray(agentList) ? agentList : [];
  }

  const currentFlow = getCurrentBusinessFlowRole(userStore);
  if (!currentFlow) {
    return [];
  }

  return (Array.isArray(agentList) ? agentList : []).filter((agent) => agent === currentFlow);
}
