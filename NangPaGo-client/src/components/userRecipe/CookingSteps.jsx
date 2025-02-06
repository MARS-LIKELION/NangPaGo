function CookingSteps({ steps, stepImages }) {
  return (
    <div className="cooking-steps mt-1">
      {steps.map((step, index) => (
        <div key={index} className={`mb-4 ${index === 0 ? 'mt-1' : 'mt-2'}`}>
          <p className="text-text-600 text-sm mb-1">{step.description}</p>
          {stepImages[index] && (
            <img
              src={stepImages[index]}
              alt={`Step ${index + 1}`}
              className="w-full rounded-md"
            />
          )}
        </div>
      ))}
    </div>
  );
}

export default CookingSteps;