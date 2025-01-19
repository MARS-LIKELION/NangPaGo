function NoResults({ searchTerm }) {
  return (
    <div className="text-center text-gray-500">
      <p>{searchTerm} 검색 결과가 없습니다.</p>
    </div>
  );
}

export default NoResults;
