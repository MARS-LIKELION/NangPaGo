/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#f2ce1b',
        secondary: '#c4d925',
        text: {
          100: '#fff',
          300: '#888',
          400: '#999',
          500: '#666',
          600: '#222',
          900: '#000',
        }
      }
    }
  },
  plugins: [],
}

