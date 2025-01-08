function SubmitButton({ onClick, label }) {
  return (
    <button
      onClick={onClick}
      className="w-full bg-[var(--primary-color)] text-white py-2 rounded text-center font-semibold"
    >
      {label}
    </button>
  );
}

export default SubmitButton;
