function Footer() {
  return (
    <footer className="bg-gray-100 p-4 mt-6 flex flex-col items-center text-center">
      <div className="flex gap-4 mb-2">
        <a
          href="https://github.com/MARS-LIKELION/NangPaGo"
          className="text-[var(--secondary-color)] text-sm"
        >
          팀 소개
        </a>
        <button className="text-[var(--secondary-color)] text-sm">
          서비스 피드백하기
        </button>
      </div>
      <p className="text-gray-600 text-sm">
        © 2024 NANGPAGO. ALL RIGHTS RESERVED.
      </p>
    </footer>
  );
}

export default Footer;
