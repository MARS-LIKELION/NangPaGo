import clsx from 'clsx';
import { HEADER_STYLES } from '../../../common/styles/Header';

function NavItem({ to, isActive, label, Icon, onClick }) {
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
          isActive ? "text-primary" : "text-text-900 group-hover:text-primary"
        )}
      />
      <span className={clsx(
        HEADER_STYLES.buttonText,
        isActive ? "text-primary" : "text-text-900 group-hover:text-primary"
      )}>
        {label}
      </span>
    </button>
  );
}

export default NavItem;
