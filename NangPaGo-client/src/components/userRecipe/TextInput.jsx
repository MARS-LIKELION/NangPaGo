function TextInput({ value, onChange, placeholder }) {
  return (
    <input
      type="text"
      className="w-full p-2 border border-text-400 rounded-md mb-4"
      placeholder={placeholder}
      value={value}
      onChange={onChange}
    />
  );
}

export default TextInput;
