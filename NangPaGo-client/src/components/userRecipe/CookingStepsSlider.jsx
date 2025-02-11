import React, { useRef, useState, forwardRef } from 'react';
import Slider from 'react-slick';
import CookingSteps from './CookingSteps';
import SliderArrowButton from '../button/SliderArrowButton.jsx';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';

const CookingStepsSlider = forwardRef(
  ({ manuals = [], manualImages = [] }, ref) => {
    const sliderRef = useRef(null);
    const [currentSlide, setCurrentSlide] = useState(0);

    const sliderSettings = {
      dots: true,
      infinite: false,
      draggable: true,
      slidesToShow: 1,
      centerMode: true,
      centerPadding: '10%',
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
        {/* 모바일 뷰 (TailwindCSS md:hidden) */}
        <div className="flex flex-col space-y-6 md:hidden">
          {manuals.map((step, index) => (
            <div key={index}>
              <CookingSteps
                steps={[step]}
                stepImages={[manualImages[index]]}
                slideIndex={index}
              />
            </div>
          ))}
        </div>

        {/* 데스크톱 뷰 (TailwindCSS hidden md:block) */}
        <div className="hidden md:block slider-wrapper" style={{ position: 'relative', overflow: 'hidden' }}>
          <Slider {...sliderSettings} ref={sliderRef}>
            {manuals.map((step, index) => (
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
              overflow: visible !important;
            }
            .slick-slide > div {
              padding: 10px; /* 슬라이드 간 간격 추가 */
            }
            .slick-slide > div .cooking-step-card {
              transform: scale(0.9);
              transition: transform 0.3s ease;
            }
            .slick-current > div .cooking-step-card {
              transform: scale(1);
            }
          `}</style>
        </div>
      </div>
    );
  }
);

export default CookingStepsSlider;
