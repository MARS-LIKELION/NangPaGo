function ErrorMessage({ error }) {
  if (!error) return null;
  return <p className="text-red-500 text-sm mb-4">{error}</p>;
}

export default ErrorMessage;
