import {
  FaArrowLeft,
  FaArrowRight,
  FaAngleDoubleLeft,
  FaAngleDoubleRight,
} from 'react-icons/fa';

function Pagination({ currentPage, totalPages, onPageChange }) {
  return (
    <div className="flex justify-center items-center gap-2 mt-6">
      <button
        onClick={() => onPageChange(0)}
        disabled={currentPage === 0}
        className={`px-1 py-2 bg-white ${
          currentPage === 0 ? 'text-text-400' : 'text-primary'
        }`}
      >
        <FaAngleDoubleLeft size={20} />
      </button>
      <button
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 0}
        className={`px-1 py-2 bg-white ${
          currentPage === 0 ? 'text-text-400' : 'text-primary'
        }`}
      >
        <FaArrowLeft size={20} />
      </button>
      <span>
        {currentPage + 1} / {totalPages}
      </span>
      <button
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage + 1 >= totalPages}
        className={`px-1 py-2 bg-white ${
          currentPage + 1 >= totalPages ? 'text-text-400' : 'text-primary'
        }`}
      >
        <FaArrowRight size={20} />
      </button>
      <button
        onClick={() => onPageChange(totalPages - 1)}
        disabled={currentPage + 1 >= totalPages}
        className={`px-1 py-2 bg-white ${
          currentPage + 1 >= totalPages ? 'text-text-400' : 'text-primary'
        }`}
      >
        <FaAngleDoubleRight size={20} />
      </button>
    </div>
  );
}

export default Pagination;
