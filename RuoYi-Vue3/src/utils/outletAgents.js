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

const BUSINESS_FLOW_ORDER = OUTLET_AGENTS.map((item) => item.value);

export function normalizeBusinessFlow(value = "") {
  return String(value || "").trim();
}

export function sortBusinessFlows(agentList = []) {
  const normalized = [...new Set((Array.isArray(agentList) ? agentList : []).map(normalizeBusinessFlow).filter(Boolean))];
  return normalized.sort((left, right) => {
    const leftIndex = BUSINESS_FLOW_ORDER.indexOf(left);
    const rightIndex = BUSINESS_FLOW_ORDER.indexOf(right);
    if (leftIndex === -1 && rightIndex === -1) return left.localeCompare(right);
    if (leftIndex === -1) return 1;
    if (rightIndex === -1) return -1;
    return leftIndex - rightIndex;
  });
}

export function getSelectedOutletAgents(data = {}) {
  if (!Array.isArray(data.agentList)) {
    return [];
  }
  return sortBusinessFlows(data.agentList);
}

export function getPrimaryBusinessFlow(data = {}) {
  return getSelectedOutletAgents(data)[0] || "";
}

export function applySelectedOutletAgents(data = {}) {
  const primaryAgent = getPrimaryBusinessFlow(data);
  return { ...data, agentList: primaryAgent ? [primaryAgent] : [] };
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
  const matchedRole = currentRoles.find((role) => !ALL_DATA_ROLE_KEYS.includes(role));
  return normalizeBusinessFlow(matchedRole);
}

export function getVisibleBusinessFlows(agentList = [], userStore = {}) {
  const sortedAgents = sortBusinessFlows(agentList);
  if (canViewAllData(userStore)) {
    return sortedAgents;
  }

  const currentFlow = getCurrentBusinessFlowRole(userStore);
  if (!currentFlow) {
    return [];
  }

  return sortedAgents.filter((agent) => agent === currentFlow);
}
