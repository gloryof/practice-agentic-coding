import type { Preview } from "@storybook/nextjs-vite";

import "../app/globals.css";

const preview: Preview = {
  parameters: {
    a11y: {
      test: "error",
    },
    layout: "padded",
    viewport: {
      options: {
        mobile: { name: "モバイル 360px", styles: { width: "360px", height: "800px" } },
        tablet: { name: "タブレット 768px", styles: { width: "768px", height: "900px" } },
        desktop: { name: "デスクトップ 1280px", styles: { width: "1280px", height: "900px" } },
      },
    },
  },
};

export default preview;
