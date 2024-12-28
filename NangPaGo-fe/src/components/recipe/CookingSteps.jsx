function CookingSteps({ steps, stepImages }) {
  return (
    <div className="cooking-steps mt-1">
      {steps.map((step, index) => (
        <div
          key={step.id}
          className={`mb-4 ${index === 0 ? 'mt-1' : 'mt-2'}`} // 간격을 줄임
        >
          {/* 단계 텍스트 */}
          <p className="text-gray-700 text-sm mb-1">{step.manual}</p>{' '}
          {/* 텍스트와 이미지 간 간격 감소 */}
          {/* 해당 단계 이미지 */}
          {stepImages[index] && (
            <img
              src={stepImages[index].imageUrl}
              alt={`Step ${index + 1}`}
              className="w-full h-28 object-cover mt-2 rounded-md" // 이미지 크기와 간격을 줄임
            />
          )}
        </div>
      ))}
    </div>
  );
}

export default CookingSteps;
