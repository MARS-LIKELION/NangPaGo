import React from 'react';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';

function BuildVersionModal({ isOpen, onClose, version }) {
  if (!isOpen) return null;

  const renderBuildInfo = () => {
    switch (version.fetchState) {
      case 'idle':
      case 'loading': // 동일 동작 처리
        return (
          <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-primary"></div>
        );
      case 'error':
        return (
          <p className="text-center text-red-500 text-sm mb-4 mt-4">
            {version.message}
          </p>
        );
      case 'success':
        return (
          <>
            <p className="text-center text-lg font-semibold text-gray-600 mb-2">
              배포 버전
            </p>
            <p className="text-center text-sm text-gray-500">
              {version.version}
            </p>
          </>
        );
      default:
        return null;
    }
  };


  return ReactDOM.createPortal(
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
      <div className="bg-white p-8 rounded-lg relative flex flex-col items-center max-w-[300px] w-[calc(100%-32px)]">
        {renderBuildInfo()}
        {version.fetchState !== 'loading' && (
          <button
            onClick={onClose}
            className="mt-4 bg-primary text-white px-5 py-3 rounded-lg"
          >
            닫기
          </button>
        )}
      </div>
    </div>,
    document.body
  );
}

BuildVersionModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  version: PropTypes.shape({
    fetchState: PropTypes.oneOf(['idle', 'loading', 'success', 'error'])
      .isRequired,
    message: PropTypes.string,
    version: PropTypes.string,
  }).isRequired,
};

export default BuildVersionModal;
