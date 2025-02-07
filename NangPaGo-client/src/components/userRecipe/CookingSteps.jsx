import React from 'react';
import CookingStepCard from './CookingStepCard';

function CookingSteps({ steps, stepImages, slideIndex = 0 }) {
  return (
    <div className="flex flex-col space-y-4">
      {steps.map((step, idx) => (
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
