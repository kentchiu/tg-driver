module.exports = {
  content: [
    './src/components/**/*.{js,ts,jsx,tsx}',
    './src/features/**/*.{js,ts,jsx,tsx}',
    './src/pages/**/*.{js,ts,jsx,tsx}'
  ],
  theme: {},
  plugins: [require('@tailwindcss/forms'), require('@tailwindcss/line-clamp')]
};
