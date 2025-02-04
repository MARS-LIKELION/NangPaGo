export default {
  plugins: {
    'postcss-preset-env': {
      browsers: ['> 1%', 'last 2 versions', 'Firefox ESR', 'Safari >= 11'],
    },
    tailwindcss: {},
    autoprefixer: {
      flexbox: 'no-2009',
      grid: 'autoplace',
    },
  },
};
