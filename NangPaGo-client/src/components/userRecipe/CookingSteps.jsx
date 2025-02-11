import React from 'react';
import CookingStepCard from './CookingStepCard';

function CookingSteps({ steps, stepImages, slideIndex = 0 }) {
  return (
    <div className="cooking-steps mt-1">
      {steps.map((step, idx) => (
        <CookingStepCard
          step={step}
          image={stepImages[idx]}
          index={slideIndex + idx}
        />
      ))}
    </div>
  );
}

export default CookingSteps;
