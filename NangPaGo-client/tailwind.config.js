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
      keyframes: {
        'heart-bounce': {
          '0%, 100%': { transform: 'scale(1)' },
          '50%': { transform: 'scale(1.3)' }
        }
      },
      animation: {
        'heart-bounce': 'heart-bounce 0.3s ease-in-out'
      }
    },
  },
  plugins: [require('@tailwindcss/forms')],
};
