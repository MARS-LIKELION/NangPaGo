function CookingSteps({ steps, stepImages }) {
  return (
    <div className="cooking-steps mt-1">
      {steps.map((step, index) => (
        <div
          key={step.id}
          className={`mb-4 ${index === 0 ? 'mt-1' : 'mt-2'}`}
        >
          <p className="text-gray-700 text-sm mb-1">{step.manual}</p>{' '}
          {stepImages[index] && (
            <img
              src={stepImages[index].imageUrl}
              alt={`Step ${index + 1}`}
              className="w-full h-28 object-cover mt-2 rounded-md"
            />
          )}
        </div>
      ))}
    </div>
  );
}

export default CookingSteps;
