function CookingSteps({ steps, stepImages }) {
  return (
    <div className="cooking-steps mt-4">
      <h2 className="text-lg font-semibold">요리 과정</h2>
      {steps.map((step, index) => (
        <div key={step.id} className="mt-2">
          <p className="text-gray-700 text-sm">{step.manual}</p>
          {stepImages[index] && (
            <img
              src={stepImages[index].imageUrl}
              alt={`Step ${index + 1}`}
              className="w-full h-32 object-cover mt-2 rounded-md"
            />
          )}
        </div>
      ))}
    </div>
  );
}

export default CookingSteps;
