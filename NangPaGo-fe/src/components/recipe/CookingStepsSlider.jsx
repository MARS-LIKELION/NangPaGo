import Slider from 'react-slick';
import CookingSteps from './CookingSteps';

function CookingStepsSlider({ manuals, manualImages }) {
  const sliderSettings = {
    dots: true,
    infinite: false,
    slidesToShow: 1,
    slidesToScroll: 1,
  };

  return (
    <div>
      <div className="block lg:hidden">
        {manuals.map((step, index) => (
          <div key={index} className="mt-4">
            <CookingSteps steps={[step]} stepImages={[manualImages[index]]} />
          </div>
        ))}
      </div>
      <div className="hidden lg:block">
        <Slider {...sliderSettings}>
          {manuals.map((step, index) => (
            <div key={index}>
              <CookingSteps steps={[step]} stepImages={[manualImages[index]]} />
            </div>
          ))}
        </Slider>
      </div>
    </div>
  );
}

export default CookingStepsSlider;
