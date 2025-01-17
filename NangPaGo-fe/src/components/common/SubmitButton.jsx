function SubmitButton({ onClick, label }) {
  return (
    <button
      onClick={onClick}
      className="w-full text-white py-2 text-center font-semibold"
    >
      {label}
    </button>
  );
}

export default SubmitButton;
