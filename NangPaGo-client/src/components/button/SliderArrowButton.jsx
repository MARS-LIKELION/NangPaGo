import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';

function SliderArrowButton({ onClick, direction, currentStep, totalSteps }) {
  const isFirstStep = currentStep === 1;
  const isLastStep = currentStep === totalSteps;

  if ((isFirstStep && direction === 'left') || (isLastStep && direction === 'right')) {
    return null;
  }

  return (
    <button
      onClick={onClick}
      className={`absolute top-1/2 transform -translate-y-1/2 z-10
        ${direction === 'left' ? 'left-2' : 'right-2'}
        bg-white/50 w-14 h-14 rounded-full shadow-md
        hover:bg-white/80 transition-colors flex items-center justify-center`}
    >
      <span
        className={`text-gray-600 ${
        direction === 'left' ? 'mr-1' : 'ml-1'
      } text-4xl flex items-center`}>
        {direction === 'left' ? <FaChevronLeft /> : <FaChevronRight />}
      </span>
    </button>
  );
}

export default SliderArrowButton;
