import { useState } from 'react';
import { IMAGE_STYLES } from '../../common/styles/Image';
import { FaTimes } from 'react-icons/fa';
import OnlyImageUploadModal from '../../components/modal/OnlyImageUploadModal';

const IMAGE_ALLOWED_EXTENSIONS = [
  "jpg", "jpeg", "png", "gif", "webp", "heic", "heif", "tiff", "tif", "bmp", "raw", "cr2", "nef", "arw", "dng", "rw2", "orf", "sr2"
];

function FileUpload({ file, onChange, imagePreview, onCancel }) {
  const [showOnlyImageUpload, setShowOnlyImageUpload] = useState(false);
  const handleCancel = (e) => {
    e.preventDefault();
    onCancel();
  };

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];

    if (selectedFile) {
      const fileExtension = selectedFile.name.split('.').pop().toLowerCase();
      if (!IMAGE_ALLOWED_EXTENSIONS.includes(fileExtension)) {
        setShowOnlyImageUpload(true);
        e.target.value = '';
        return;
      }
      onChange(e);
    }
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
          onChange={handleFileChange}
          key={file ? file.name : 'file-upload'}
        />
      </label>

      <OnlyImageUploadModal
        isOpen={showOnlyImageUpload}
        onClose={() => setShowOnlyImageUpload(false)}
      />
    </div>
  );
}

export default FileUpload;
