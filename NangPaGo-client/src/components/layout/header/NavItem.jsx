import clsx from 'clsx';
import { HEADER_STYLES } from '../../../common/styles/Header';

function NavItem({ to, isActive, label, Icon, onClick, badgeCount = 0, additionalProps = {} }) {
  return (
    <button
      onClick={() => onClick(to)}
      className={clsx(
        HEADER_STYLES.baseButton,
        isActive ? HEADER_STYLES.activeButton : HEADER_STYLES.inactiveButton
      )}
      {...additionalProps}
    >
      <span className="inline-flex items-center justify-center">
        <Icon size={23} />
        {badgeCount > 0 && (
          <span className="absolute top-0 right-[6px] bg-red-500 w-1 h-1 rounded-full"></span>
        )}
      </span>
      <span>{label}</span>
    </button>
  );
}

export default NavItem;
