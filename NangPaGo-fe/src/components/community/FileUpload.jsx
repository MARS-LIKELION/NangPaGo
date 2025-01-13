import { styles } from '../common/Image'

function FileUpload({ file, onChange, imagePreview }) {
  return (
    <div className="flex flex-col items-center mb-4">
      <label
        htmlFor="file-upload"
        className="w-full h-40 border border-gray-300 rounded-md flex items-center justify-center bg-gray-100 cursor-pointer relative overflow-hidden"
      >
        {imagePreview ? (
          <img
            src={imagePreview}
            alt="Uploaded Preview"
            className={styles.mainImage}
          />
        ) : (
          <span className="text-gray-400">사진 업로드</span>
        )}
        <input
          id="file-upload"
          type="file"
          accept="image/*"
          className="absolute inset-0 opacity-0 cursor-pointer"
          onChange={onChange}
        />
      </label>
    </div>
  );
}

export default FileUpload;
