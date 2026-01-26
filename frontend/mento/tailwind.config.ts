import type { Config } from "tailwindcss";

export default {
  darkMode: "class",
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        /* Brand */
        primary: {
          500: "var(--primary-500)",
          400: "var(--primary-400)",
          300: "var(--primary-300)",
          200: "var(--primary-200)",
          100: "var(--primary-100)",
          DEFAULT: "var(--primary)",
          foreground: "var(--primary-foreground)",
        },

        /* Pastel */
        "pastel-blue": {
          200: "var(--pastel-blue-200)",
          100: "var(--pastel-blue-100)",
        },
        "pastel-purple": {
          200: "var(--pastel-purple-200)",
          100: "var(--pastel-purple-100)",
        },
        "pastel-green": {
          200: "var(--pastel-green-200)",
          100: "var(--pastel-green-100)",
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
