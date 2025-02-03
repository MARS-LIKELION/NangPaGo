import Slider from 'react-slick';
import CookingSteps from './CookingSteps';
import SliderArrowButton from '../button/SliderArrowButton.jsx';
import { useEffect, useState, useRef, forwardRef } from 'react';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';

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
    prevArrow: (
      <SliderArrowButton
        direction="left"
        currentStep={currentSlide + 1}
        totalSteps={manuals.length}
      />
    ),
    nextArrow: (
      <SliderArrowButton
        direction="right"
        currentStep={currentSlide + 1}
        totalSteps={manuals.length}
      />
    ),
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
