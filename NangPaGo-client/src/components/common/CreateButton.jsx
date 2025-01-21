import { FaPen } from 'react-icons/fa';

function CreateButton({ onClick, isTopButtonVisible, icon }) {
  return (
    <button
      onClick={onClick}
      className={`
        absolute bg-secondary text-white w-12 h-12 rounded-full shadow-lg flex items-center justify-center z-50
        transition-all duration-300 ease-in-out transform
        ${isTopButtonVisible ? 'bottom-[135px] md:bottom-[110px]' : 'bottom-20 md:bottom-14'} right-4
      `}
      aria-label="Create new post"
    >
      {icon || <FaPen className="text-lg" />}
    </button>
  );
}

export default CreateButton;
