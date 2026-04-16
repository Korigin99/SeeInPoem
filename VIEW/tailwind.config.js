/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts}'],
  theme: {
    extend: {
      fontFamily: {
        serif: ['"Noto Serif KR"', 'Georgia', 'serif'],
        sans: ['"Noto Sans KR"', 'Inter', 'system-ui', 'sans-serif'],
      },
      colors: {
        cream: '#FAFAF8',
        ink: {
          DEFAULT: '#1C1C1E',
          light: '#6B6B6B',
          lighter: '#9B9B9B',
        },
        indigo: {
          DEFAULT: '#4F46E5',
          light: '#6366F1',
          50: '#EEF2FF',
          100: '#E0E7FF',
        }
      },
      typography: {
        DEFAULT: {
          css: {
            color: '#1C1C1E',
          }
        }
      }
    },
  },
  plugins: [],
}
