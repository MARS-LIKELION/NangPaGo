function TextArea({ value, onChange, placeholder, rows }) {
  return (
    <textarea
      className="w-full p-2 border border-gray-300 rounded mb-4"
      placeholder={placeholder}
      rows={rows}
      value={value}
      onChange={onChange}
    />
  );
}

export default TextArea;
