import React from 'react';

function CookingStepCard({ step, image, index, extraClassName = '' }) {
  return (
    <div className={`relative cooking-step-card p-10 border rounded-lg bg-white shadow-md mx-2 ${extraClassName}`}>

      <div className="absolute top-0 left-0 mt-2 ml-3 bg-green-800 text-white px-3 py-1 rounded-full text-sm font-bold">
        Step {index + 1}
      </div>
      
      {/* 메뉴얼 설명 */}
      <p className="text-gray-800 mb-4">{step.description}</p>
      
      {image && (
        // 이미지 컨테이너: 고정 높이를 주어 모든 카드가 동일한 크기로 보이게 하고, overflow‑hidden 제거
        <div className="w-full h-80 flex items-center justify-center">
          <img
            src={image}
            alt={`Step image ${index + 1}`}
            className="max-w-full max-h-full object-contain"
          />
        </div>
      )}
    </div>
  );
}

export default CookingStepCard;
