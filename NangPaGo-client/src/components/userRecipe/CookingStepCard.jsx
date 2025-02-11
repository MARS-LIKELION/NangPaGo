import React from 'react';

function CookingStepCard({ step, image, index }) {
  return (
    <div className="cooking-step-card-container space-y-4">
      <div
        className={`relative cooking-step-card p-4 border rounded-lg bg-white shadow-md`}
      >
        {/* 이미지 컨테이너 */}
        {image && (
          <div className="w-full h-80 flex items-center justify-center">
            <img
              src={image}
              alt={`Step image ${index + 1}`}
              className="max-w-full max-h-full object-contain"
            />
          </div>
        )}
      </div>

      {/* Step 번호와 설명 */}
      <div
        className={`step-info-container px-4 grid grid-cols-[auto,1fr] gap-x-4 items-start items-center`}
      >
        <span className="bg-green-800 text-white rounded-full text-sm font-bold px-3 py-1 self-start">
          Step {index + 1}
        </span>
        <p className="text-gray-800 text-sm leading-relaxed break-words">
          {step.description}
        </p>
      </div>
    </div>
  );
}

export default CookingStepCard;
