import Slider from 'react-slick';
import CookingSteps from './CookingSteps';
import { useEffect, useState, useRef, forwardRef } from 'react';
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';

function ArrowButton({ onClick, direction }) {
  return (
    <button
      onClick={onClick}
      className={`absolute top-1/2 transform -translate-y-1/2 z-10
        ${direction === 'left' ? 'left-2' : 'right-2'}
        bg-white/20 w-11 h-11 rounded-full shadow-md
        hover:bg-white/60 transition-colors
        text-gray-600 text-2xl flex items-center justify-center`}
    >
      {direction === 'left' ? <FaChevronLeft /> : <FaChevronRight />}
    </button>
  );
}

const CookingStepsSlider = forwardRef(({ manuals, manualImages }, ref) => {
  const [sliderKey, setSliderKey] = useState(0);
  const [currentSlide, setCurrentSlide] = useState(0);
  const sliderRef = useRef(null);

  useEffect(() => {
    if (ref && ref.current !== sliderRef.current) {
      ref.current = sliderRef.current;
    }
  }, [ref]);

  useEffect(() => {
    const handleResize = () => setSliderKey((prevKey) => prevKey + 1);
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  useEffect(() => {
    if (sliderRef.current?.slickGoTo) {
      sliderRef.current.slickGoTo(currentSlide);
    }
  }, [sliderKey, currentSlide]);

  const sliderSettings = {
    dots: true,
    infinite: false,
    draggable: false,
    slidesToShow: 1,
    slidesToScroll: 1,
    beforeChange: (_, next) => setCurrentSlide(next),
    prevArrow: <ArrowButton direction="left" />,
    nextArrow: <ArrowButton direction="right" />,
  };

  return (
    <div>
      <div className="hidden md:block">
        <Slider {...sliderSettings} key={sliderKey} ref={sliderRef}>
          {manuals.map((step, index) => (
            <div key={index}>
              <CookingSteps steps={[step]} stepImages={[manualImages[index]]} />
            </div>
          ))}
        </Slider>
      </div>
    </div>
  );
});

export default CookingStepsSlider;
