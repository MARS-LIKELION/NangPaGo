import clsx from 'clsx';

function NavItem({ to, isActive, label, Icon, onClick, additionalProps = {} }) {
  const baseClass = 'group flex-col items-center justify-center gap-1 text-sm font-medium bg-white';
  const dropDownClass = 'flex rounded-md overflow-hidden sm:flex md:inline-flex';
  const activeClass = isActive ? 'text-primary' : 'text-text-400';

  return (
    <button
      onClick={() => onClick(to)}
      className={clsx(baseClass, dropDownClass, activeClass)}
      {...additionalProps}
    >
      {Icon && (
        <span className="inline-flex items-center justify-center">
          <Icon size={24} />
        </span>
      )}
      <span>{label}</span>
    </button>
  );
}

export default NavItem;
