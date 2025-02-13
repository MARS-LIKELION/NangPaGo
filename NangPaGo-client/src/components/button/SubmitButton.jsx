function SubmitButton({ onClick, label, disabled }) {
  return (
    <button
      onClick={onClick}
      disabled={disabled}
      className={`w-full py-3 text-lg font-medium ${
        disabled ? 'bg-gray-300 cursor-not-allowed' : 'bg-primary hover:bg-primary-dark'
      }`}
    >
      {label}
    </button>
  );
}

export default SubmitButton;
