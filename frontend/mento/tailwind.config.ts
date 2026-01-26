import type { Config } from "tailwindcss";

export default {
  darkMode: "class",
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        /* Brand */
        primary: {
          500: "#4de2ff",
          400: "#6ae9ff",
          300: "#8ff0ff",
          200: "#b8f6ff",
          100: "#e6fcff",
          DEFAULT: "var(--primary)",
          foreground: "var(--primary-foreground)",
        },

        /* Pastel */
        "pastel-blue": {
          200: "#beeffc",
          100: "#ccf8ff",
        },
        "pastel-purple": {
          200: "#e6e3fa",
          100: "#f2f0ff",
        },
        "pastel-green": {
          200: "#bfeedd",
          100: "#dffaf0",
        },

        /* Semantic */
        background: "var(--background)",
        foreground: "var(--foreground)",
        card: "var(--card)",
        border: "var(--border)",
        input: "var(--input)",
        ring: "var(--ring)",
        muted: "var(--muted)",
        accent: "var(--accent)",
        destructive: "var(--destructive)",

        /* Text */
        "text-primary": "var(--text-primary)",
        "text-secondary": "var(--text-secondary)",

        /* Background helpers */
        "bg-main": "var(--bg-main)",
        "dark-bg": "var(--dark-bg)",

        /* Chart */
        chart: {
          1: "var(--chart-1)",
          2: "var(--chart-2)",
          3: "var(--chart-3)",
          4: "var(--chart-4)",
          5: "var(--chart-5)",
        },
      },

      borderRadius: {
        lg: "var(--radius)",
        md: "calc(var(--radius) - 2px)",
        sm: "calc(var(--radius) - 4px)",
      },
    },
  },
  plugins: [],
} satisfies Config;
