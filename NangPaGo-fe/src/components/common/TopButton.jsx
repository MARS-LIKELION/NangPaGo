import { useState, useEffect } from 'react';
import { FaArrowUp } from 'react-icons/fa';

function TopButton({ offset = 100, containerClass = '', positionClass = '' }) {
  const [isVisible, setIsVisible] = useState(false);

  // 스크롤 위치 감지
  useEffect(() => {
    const handleScroll = () => {
      setIsVisible(window.scrollY > offset); // offset 이상 스크롤 시 버튼 표시
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, [offset]);

  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: 'smooth',
    });
  };

  return (
    isVisible && (
      <button
        onClick={scrollToTop}
        className={`fixed bg-[var(--secondary-color)] text-white w-12 h-12 rounded-full shadow-lg flex items-center justify-center z-50 ${containerClass} ${positionClass}`}
        aria-label="Scroll to top"
      >
        <FaArrowUp className="text-lg" />
      </button>
    )
  );
}

export default TopButton;
