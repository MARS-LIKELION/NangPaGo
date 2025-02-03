/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}', './src/globals.css'],
  theme: {
    extend: {
      screens: {
        sm: '640px',
        md: '768px',
        lg: '1024px',
      },
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
        },
      },
      fontFamily: {
        sans: ['맑은 고딕', 'sans-serif'],
      },
    },
  },
  plugins: [require('@tailwindcss/forms')],
};
