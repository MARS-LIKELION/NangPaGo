const SOCIAL_BUTTON_STYLES = {
  naver: {
    background: 'bg-[#03C75A] hover:bg-[#02B053]',
    text: 'text-white',
    logo: '/socialLogin/naver.svg',
    label: '네이버 계정으로 로그인',
  },
  google: {
    background: 'bg-white border border-[#DADCE0] hover:bg-[#F6F6F6]',
    text: 'text-[#3C4043]',
    logo: '/socialLogin/google.svg',
    label: '구글 계정으로 로그인',
  },
  kakao: {
    background: 'bg-[#FEE500] hover:bg-[#FFEE32]',
    text: 'text-black opacity-85',
    logo: '/socialLogin/kakao.svg',
    label: '카카오 계정으로 로그인',
  },
};

export default SOCIAL_BUTTON_STYLES;
