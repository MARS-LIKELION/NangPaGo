import Slider from 'react-slick';
import CookingSteps from './CookingSteps';
import SliderArrowButton from '../button/SliderArrowButton.jsx';
import { useEffect, useState, useRef, forwardRef } from 'react';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';

const CookingStepsSlider = forwardRef(({ manuals = [], manualImages = [], isUserRecipe = false }, ref) => {
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

  const formattedManuals = isUserRecipe
    ? manuals.map((step) => ({ manual: step }))  // 문자열을 객체 형태로 변환
    : manuals;

  const formattedImages = isUserRecipe
    ? manualImages.map((img) => ({ imageUrl: img }))  // 문자열을 객체 형태로 변환
    : manualImages;

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
        totalSteps={formattedManuals.length}
      />
    ),
    nextArrow: (
      <SliderArrowButton
        direction="right"
        currentStep={currentSlide + 1}
        totalSteps={formattedManuals.length}
      />
    ),
  };

  return (
    <div>
      <div className="block md:hidden">
        {formattedManuals.map((step, index) => (
          <div key={index} className="mt-4">
            <CookingSteps steps={[step]} stepImages={[formattedImages[index]]} />
          </div>
        ))}
      </div>
      <div className="hidden md:block">
        <Slider {...sliderSettings} key={sliderKey} ref={sliderRef}>
          {formattedManuals.map((step, index) => (
            <div key={index}>
              <CookingSteps steps={[step]} stepImages={[formattedImages[index]]} />
            </div>
          ))}
        </Slider>
      </div>
    </div>
  );
});

export default CookingStepsSlider;