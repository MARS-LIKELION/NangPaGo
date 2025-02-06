import { IMAGE_STYLES } from '../../common/styles/Image';
import { FaTimes } from 'react-icons/fa';

function FileUpload({ file, onChange, imagePreview, onCancel }) {
  const handleCancel = (e) => {
    e.preventDefault();
    onCancel();
  };

  return (
    <div className="flex flex-col items-center mb-4">
      <label
        htmlFor="file-upload"
        className="w-full h-40 border border-text-400 rounded-md flex items-center justify-center bg-gray-100 cursor-pointer relative overflow-hidden"
      >
        {imagePreview ? (
          <>
            <img
              src={imagePreview}
              alt="Uploaded Preview"
              className={`${IMAGE_STYLES.mainImage} object-contain`}
            />
            <button
              type="button"
              onClick={handleCancel}
              className="absolute top-2 right-2 bg-gray-400 text-white w-6 h-6 flex items-center justify-center rounded-full z-10 text-base opacity-70"
            >
              <FaTimes className="w-4 h-4" />
            </button>
          </>
        ) : (
          <span className="text-gray-400">사진 업로드</span>
        )}
        <input
          id="file-upload"
          type="file"
          accept="image/*"
          className="absolute inset-0 opacity-0 cursor-pointer"
          onChange={onChange}
          key={file ? file.name : 'file-upload'}
        />
      </label>
    </div>
  );
}

export default FileUpload;
