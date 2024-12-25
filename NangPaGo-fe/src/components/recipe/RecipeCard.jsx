import { Link } from 'react-router-dom';

function RecipeCard({ image }) {
  const dummyDescription =
    '이 레시피는 맛있고 건강한 한끼 식사를 위한 완벽한 선택입니다. 신선한 재료들과 특별한 양념의 조화로 특별한 맛을 선사합니다. 조리 과정도 간단해서 초보자도 쉽게 따라할 수 있답니다. 가족들과 함께 즐거운 식사시간을 보내보세요.';

  return (
    <Link
      to={`/recipe/${image.id}`}
      className="block overflow-hidden rounded-lg shadow-lg hover:shadow-xl transition-shadow duration-300"
    >
      <img
        src={image.url}
        alt={image.title}
        className="w-full h-48 object-cover"
      />
      <div className="p-4 flex flex-col gap-2">
        <h3 className="text-lg font-semibold">{image.title}</h3>
        <p className="text-sm text-gray-600 overflow-hidden line-clamp-3">
          {image.description || dummyDescription}
        </p>
      </div>
    </Link>
  );
}

export default RecipeCard;
