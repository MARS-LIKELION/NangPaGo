import { Link } from 'react-router-dom';

function NavItem({ to, isActive, label, Icon }) {
  return (
    <Link
      to={to}
      className={`group flex flex-col items-center justify-center gap-1 text-sm font-medium 
        ${isActive ? 'text-primary' : 'text-text-400'}`}
    >
      <span className="inline-flex items-center justify-center">
        <Icon size={24} />
      </span>
      <span>{label}</span>
    </Link>
  );
}

export default NavItem;
