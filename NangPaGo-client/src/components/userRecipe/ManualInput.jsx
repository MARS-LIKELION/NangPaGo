import { motion, AnimatePresence } from "framer-motion";
import { X } from "lucide-react";
import TextArea from "./TextArea";
import FileUpload from "./FileUpload";

function ManualInput({ manuals, setManuals, maxFileSize, onFileSizeError }) {
  const addManual = () => {
    setManuals([...manuals, { description: "", image: null, preview: null }]);
  };

  const updateManual = (index, field, value) => {
    const updatedManuals = [...manuals];
    if (typeof updatedManuals[index] === 'string') {
      updatedManuals[index] = { description: updatedManuals[index], image: null, preview: null };
    }
    updatedManuals[index][field] = value;
    if (field === "image" && !value) {
      updatedManuals[index].preview = null;
    }
    setManuals(updatedManuals);
  };

  const handleManualImageChange = (index, e) => {
    const file = e.target.files[0];
    if (file) {
      setManuals((prev) =>
        prev.map((manual, i) =>
          i === index
            ? {
                ...manual,
                image: file,
                preview: URL.createObjectURL(file),
                previousImageUrl: manual.preview
              }
            : manual
        )
      );
    }
  };

  const handleManualImageCancel = (index) => {
    setManuals((prev) =>
      prev.map((manual, i) =>
        i === index
          ? {
              ...manual,
              image: null,
              preview: ''
            }
          : manual
      )
    );
  };

  const removeManual = (index) => {
    const updatedManuals = manuals.filter((_, i) => i !== index);
    setManuals(updatedManuals);
  };

  return (
    <div className="mt-6">
      <h2 className="text-xl font-semibold mb-4">조리 과정</h2>
      <AnimatePresence>
        {manuals.map((manual, index) => (
          <motion.div
            key={index}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            transition={{ duration: 0.3 }}
            className="relative flex flex-col gap-2 mt-10"
          >
            <motion.div
              initial={{ opacity: 0, y: -10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
              transition={{ duration: 0.3 }}
              className="self-start px-3 py-1 bg-gray-700 text-white text-sm font-semibold rounded-full"
            >
              Step {index + 1}
            </motion.div>
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              transition={{ duration: 0.3 }}
              className="relative flex flex-col gap-3 p-4 border border-gray-300 rounded-lg bg-white shadow-sm"
            >
              {manuals.length > 1 && (
                <motion.button
                  whileHover={{ scale: 1.1 }}
                  whileTap={{ scale: 0.9 }}
                  onClick={() => removeManual(index)}
                  className="absolute top-2 right-2 w-8 h-8 flex items-center justify-center rounded-full bg-gray-300 hover:bg-gray-400 transition-all"
                >
                  <X className="w-4 h-4 text-gray-600" />
                </motion.button>
              )}
              <TextArea
                value={manual.description || ""}
                onChange={(e) => updateManual(index, "description", e.target.value)}
                placeholder="조리 과정 입력"
                rows={2}
              />
              
              <FileUpload
                file={manual.image}
                imagePreview={manual.preview || (typeof manual.image === 'string' ? manual.image : null)}
                onChange={(e) => handleManualImageChange(index, e)}
                onCancel={() => handleManualImageCancel(index)}
              />
            </motion.div>
          </motion.div>
        ))}
      </AnimatePresence>
      <motion.button
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
        onClick={addManual}
        className="mt-8 w-full p-2 bg-green-500 text-white font-semibold rounded-lg shadow-md transition-all duration-300"
      >
        + 조리 과정 추가
      </motion.button>
    </div>
  );
}

export default ManualInput;
