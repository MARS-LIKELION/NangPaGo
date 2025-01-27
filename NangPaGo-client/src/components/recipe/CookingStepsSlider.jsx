import Slider from 'react-slick';
import CookingSteps from './CookingSteps';
import { useEffect, useState, useRef, forwardRef } from 'react';

const CookingStepsSlider = forwardRef(({ manuals, manualImages }, ref) => {
  const sliderSettings = {
    dots: true,
    infinite: false,
    slidesToShow: 1,
    slidesToScroll: 1,
    beforeChange: (current, next) => setCurrentSlide(next),
  };

  const [sliderKey, setSliderKey] = useState(0);
  const [currentSlide, setCurrentSlide] = useState(0);
  const sliderRef = useRef(null);

  useEffect(() => {
    if (ref && ref.current !== sliderRef.current) {
      ref.current = sliderRef.current;
    }
  }, []);

  useEffect(() => {
    const handleResize = () => {
      setSliderKey((prevKey) => prevKey + 1);
    };

    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, []);

  useEffect(() => {
    if (sliderRef.current && sliderRef.current.slickGoTo) {
      sliderRef.current.slickGoTo(currentSlide);
    }
  }, [sliderKey, currentSlide]);

  return (
    <div>
      <div className="block md:hidden">
        {manuals.map((step, index) => (
          <div key={index} className="mt-4">
            <CookingSteps steps={[step]} stepImages={[manualImages[index]]} />
          </div>
        ))}
      </div>

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
