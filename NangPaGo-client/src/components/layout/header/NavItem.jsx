import clsx from 'clsx';
import { HEADER_STYLES } from '../../../common/styles/Header';
import { useLocation } from 'react-router-dom';

function NavItem({ to, label, Icon, onClick }) {
  const location = useLocation();
  const isActive = location.pathname === to || location.pathname.startsWith(`${to}/`);

  return (
    <button
      onClick={() => onClick(to)}
      className={clsx(
        HEADER_STYLES.responsiveButton,
        isActive ? HEADER_STYLES.activeButton : HEADER_STYLES.inactiveButton
      )}
    >
      <Icon
        className={clsx(
          HEADER_STYLES.iconSize,
          HEADER_STYLES.buttonIcon,
          isActive ? "text-primary" : "text-text-900"
        )}
      />
      <span className={clsx(
        HEADER_STYLES.buttonText,
        isActive ? "text-primary" : "text-text-900"
      )}>
        {label}
      </span>
    </button>
  );
}

export default NavItem;
