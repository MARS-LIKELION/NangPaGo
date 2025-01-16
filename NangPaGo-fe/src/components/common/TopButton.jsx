import { useState, useEffect } from 'react';
import { FaArrowUp } from 'react-icons/fa';

function TopButton({ offset = 100 }) {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setIsVisible(window.scrollY > offset);
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
    <button
      onClick={scrollToTop}
      className={`fixed bg-primary text-white w-12 h-12 rounded-full shadow-lg flex items-center justify-center z-50
    transition-all duration-300 ease-in-out transform
    ${isVisible ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-4'}
    bottom-20 sm:bottom-14 right-4`}
      aria-label="Scroll to top"
    >
      <FaArrowUp className="text-lg" />
    </button>
  );
}

export default TopButton;
