import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');

  return {
    plugins: [react()],
    server: {
      allowedHosts: ['nangpago.site', '.nangpago.site'],
      port: env.VITE_FRONT_SERVER_PORT,
      host: true,
      watch: {
        usePolling: true,
      },
      proxy: {
        '/api': {
          target: env.VITE_HOST,
          changeOrigin: true,
        },
      },
    },
    build: {
      target: ['es2015', 'safari11'],
      outDir: 'dist',
      assetsDir: 'assets',
      minify: 'esbuild',
      terserOptions: {
        compress: {
          drop_console: true,
        },
      },
    },
  };
});
