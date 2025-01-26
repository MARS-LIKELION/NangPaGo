import React, { useState } from 'react';
import { FaPen, FaTimes } from 'react-icons/fa';
import { BUTTON_STYLES } from '../../common/styles/ListPage';

function ToggleButton({ actions = [] }) {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const toggleMenu = () => setIsMenuOpen(!isMenuOpen);
  const isSingleAction = actions.length === 1;

  return (
    <aside className={BUTTON_STYLES.wrapper}>
      <button
        onClick={isSingleAction ? actions[0]?.onClick : toggleMenu}
        className={`
          absolute bg-secondary text-white w-12 h-12 rounded-full shadow-lg flex items-center justify-center z-50
          transition-all duration-300 ease-in-out transform bottom-20 md:bottom-14 right-4
          opacity-100 translate-y-0'
        `}
      >
        {isSingleAction ? (
          <FaPen className="text-lg" />
        ) : isMenuOpen ? (
          <FaTimes className="text-xl" />
        ) : (
          <FaPen className="text-lg" />
        )}
      </button>

      {!isSingleAction && (
        <ul
          className={`absolute bottom-12 right-20 flex flex-col items-end gap-3 ${
            isMenuOpen ? 'opacity-100 visible' : 'opacity-0 invisible'
          } transition-opacity duration-300`}
        >
          {actions.map((action, index) => (
            <li key={index}>
              <button
                onClick={action.onClick}
                className="bg-secondary text-white px-4 py-2 rounded-md shadow-md hover:bg-opacity-90 transform transition-all duration-300"
              >
                {action.label}
              </button>
            </li>
          ))}
        </ul>
      )}
    </aside>
  );
}

export default ToggleButton;
