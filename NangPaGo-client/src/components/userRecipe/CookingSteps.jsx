import React from 'react';
import CookingStepCard from './CookingStepCard';

function CookingSteps({ steps, stepImages, slideIndex = 0 }) {
  return (
    <div className="flex flex-col space-y-4">
      {steps.map((step, idx) => (
        // slideIndex를 활용해 전체 순서가 변경될 수 있도록 합니다.
        <CookingStepCard
          key={idx}
          step={step}
          image={stepImages[idx]}
          index={slideIndex + idx}
        />
      ))}
    </div>
  );
}

export default CookingSteps;
