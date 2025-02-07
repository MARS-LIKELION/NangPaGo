import React, { useEffect, useState, useRef, forwardRef } from 'react';
import Slider from 'react-slick';
import CookingSteps from './CookingSteps';
import SliderArrowButton from '../button/SliderArrowButton.jsx';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';

const CookingStepsSlider = forwardRef(
  ({ manuals = [], manualImages = [], isUserRecipe = false }, ref) => {
    const [sliderKey, setSliderKey] = useState(0);
    const [currentSlide, setCurrentSlide] = useState(0);
    const sliderRef = useRef(null);
    const [isMobile, setIsMobile] = useState(window.innerWidth < 768);

    useEffect(() => {
      const handleResize = () => setIsMobile(window.innerWidth < 768);
      window.addEventListener('resize', handleResize);
      return () => window.removeEventListener('resize', handleResize);
    }, []);

    useEffect(() => {
      if (ref && ref.current !== sliderRef.current) {
        ref.current = sliderRef.current;
      }
    }, [ref]);

    useEffect(() => {
      const handleResize = () => setSliderKey(prevKey => prevKey + 1);
      window.addEventListener('resize', handleResize);
      return () => window.removeEventListener('resize', handleResize);
    }, []);

    useEffect(() => {
      if (sliderRef.current?.slickGoTo) {
        sliderRef.current.slickGoTo(currentSlide);
      }
    }, [sliderKey, currentSlide]);

    const formattedManuals = manuals;

    if (isMobile) {
      return (
        <div className="flex flex-col space-y-4">
          {formattedManuals.map((step, index) => (
            <div key={index}>
              <CookingSteps
                steps={[step]}
                stepImages={[manualImages[index]]}
                slideIndex={index}
              />
            </div>
          ))}
        </div>
      );
    }

    const sliderSettings = {
      dots: true,
      infinite: false,
      draggable: true,
      slidesToShow: 1,
      centerMode: true,
      centerPadding: '20%',
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
      <div className="slider-wrapper" style={{ position: 'relative', padding: '50px 0', overflow: 'hidden' }}>
        <Slider {...sliderSettings} key={sliderKey} ref={sliderRef}>
          {formattedManuals.map((step, index) => (
            <div key={index}>
              <CookingSteps
                steps={[step]}
                stepImages={[manualImages[index]]}
                slideIndex={index}
              />
            </div>
          ))}
        </Slider>
        <style jsx global="true">{`
          .slick-list {
            overflow-x: hidden !important;
            overflow-y: visible !important;
          }
          .slick-slide > div .cooking-step-card {
            transform: scale(1);
            transition: transform 0.5s ease;
          }
        `}</style>
      </div>
    );
  }
);

export default CookingStepsSlider;
