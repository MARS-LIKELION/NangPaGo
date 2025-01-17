function Footer() {
  const links = [
    {
      href: 'https://github.com/MARS-LIKELION/NangPaGo',
      label: '팀 소개',
    },
    {
      href: 'https://naver.me/IgJx1Jmu',
      label: '서비스 피드백',
    },
  ];

  const linkClass =
    'text-secondary text-sm hover:underline transition-colors duration-200';
  const footerContainerClass =
    'sticky bottom-0 right-0 bg-gray-100 p-4 flex flex-col items-center text-center md:flex-row md:justify-between';

  return (
    <footer className={footerContainerClass}>
      <div className="flex gap-4 mb-2 md:mb-0 z-10">
        {links.map((link, index) => (
          <a
            key={index}
            href={link.href}
            target="_blank"
            rel="noopener noreferrer"
            className={linkClass}
          >
            {link.label}
          </a>
        ))}
      </div>
      <p className="text-text-900 text-sm">
        © 2024 NANGPAGO. ALL RIGHTS RESERVED.
      </p>
    </footer>
  );
}

export default Footer;
