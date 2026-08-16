import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  agentRules: false,
  output: "standalone",
  typedRoutes: true,
};

export default nextConfig;
