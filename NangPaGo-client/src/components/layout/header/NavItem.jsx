import clsx from 'clsx';
import { HEADER_STYLES } from '../../../common/styles/Header';

function NavItem({ to, isActive, label, Icon, onClick, additionalProps = {} }) {
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
      </span>
      <span>{label}</span>
    </button>
  );
}

export default NavItem;
