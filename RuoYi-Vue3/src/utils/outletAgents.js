export const OUTLET_AGENTS = [
  { label: "織田家", value: "織田家" },
  { label: "豊臣家", value: "豊臣家" },
  { label: "徳川家", value: "徳川家" },
  { label: "武田家", value: "武田家" },
  { label: "上杉家", value: "上杉家" }
]

export const UNDECIDED_BUSINESS_FLOW = "浪人衆"

export const OUTLET_AGENT_COLORS = {
  織田家: "#C7000B",
  豊臣家: "#7E57C2",
  徳川家: "#1E5AA8",
  武田家: "#D9822B",
  上杉家: "#111827",
  [UNDECIDED_BUSINESS_FLOW]: "#FFFFFF"
}

export function getSelectedOutletAgents(data = {}) {
  if (Array.isArray(data.agentList)) {
    return data.agentList
  }

  return []
}

export function applySelectedOutletAgents(data = {}) {
  const agentList = getSelectedOutletAgents(data)
  return { ...data, agentList }
}
