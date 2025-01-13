import { styles } from '../common/Image'

function CookingSteps({ steps, stepImages }) {
  return (
    <div className="cooking-steps mt-1">
      {steps.map((step, index) => (
        <div key={step.id} className={`mb-4 ${index === 0 ? 'mt-1' : 'mt-2'}`}>
          <p className="text-gray-700 text-sm mb-1">{step.manual}</p>{' '}
          {stepImages[index] && (
            <img
              src={stepImages[index].imageUrl}
              alt={`Step ${index + 1}`}
              className={styles.detailImage}
            />
          )}
        </div>
      ))}
    </div>
  );
}

export default CookingSteps;
