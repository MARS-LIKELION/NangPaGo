import { motion, AnimatePresence } from "framer-motion";
import { X } from "lucide-react";
import TextArea from "./TextArea";
import FileUpload from "./FileUpload";

function ManualInput({ manuals, setManuals }) {
  // 조리 과정 추가
  const addManual = () => {
    setManuals([...manuals, { description: "", image: null }]);
  };

  // 조리 과정 업데이트
  const updateManual = (index, field, value) => {
    const updatedManuals = [...manuals];
    updatedManuals[index][field] = value;
    setManuals(updatedManuals);
  };

  // 이미지 변경 핸들러
  const handleManualImageChange = (index, e) => {
    const file = e.target.files[0];
    if (file) {
      updateManual(index, "image", file);
    }
  };

  // 조리 과정 삭제
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
            {/* 스텝 번호 (작은 둥근 회색 배경, 조리 과정 박스 위쪽에 배치) */}
            <motion.div
              initial={{ opacity: 0, y: -10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
              transition={{ duration: 0.3 }}
              className="self-start px-3 py-1 bg-gray-700 text-white text-sm font-semibold rounded-full"
            >
              Step {index + 1}
            </motion.div>

            {/* 조리 과정 박스 */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              transition={{ duration: 0.3 }}
              className="relative flex flex-col gap-3 p-4 border border-gray-300 rounded-lg bg-white shadow-sm"
            >
              {/* X 삭제 버튼 */}
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

              {/* 조리 과정 입력 */}
              <TextArea
                value={manual.description}
                onChange={(e) => updateManual(index, "description", e.target.value)}
                placeholder="조리 과정 입력"
                rows={2}
              />

              {/* 파일 업로드 */}
              <FileUpload
                file={manual.image}
                onChange={(e) => handleManualImageChange(index, e)}
                imagePreview={manual.image ? URL.createObjectURL(manual.image) : null}
                onCancel={() => updateManual(index, "image", null)}
              />
            </motion.div>
          </motion.div>
        ))}
      </AnimatePresence>

      {/* 조리 과정 추가 버튼 */}
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
