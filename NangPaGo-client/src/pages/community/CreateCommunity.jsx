// CreateUserRecipe.jsx
import { useState, useEffect, useCallback } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { createUserRecipe } from '../../api/userRecipe';
import Header from '../../components/layout/header/Header';
import Footer from '../../components/layout/Footer';
import TextInput from '../../components/userRecipe/TextInput';
import TextArea from '../../components/userRecipe/TextArea';
import FileUpload from '../../components/userRecipe/FileUpload';
import IngredientInput from '../../components/userRecipe/IngredientInput';
import ManualInput from '../../components/userRecipe/ManualInput';
import SubmitButton from '../../components/button/SubmitButton';
import FileSizeErrorModal from '../../components/modal/FileSizeErrorModal';

const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

function CreateUserRecipe() {
  const navigate = useNavigate();
  const location = useLocation();
  const prevPath = sessionStorage.getItem('prevPath') || '/user-recipe';

  // 상태 관리
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [ingredients, setIngredients] = useState([{ name: '', amount: '' }]);
  // manuals: 각 객체에 description과 image (파일)를 저장합니다.
  const [manuals, setManuals] = useState([{ description: '', image: null }]);
  const [mainFile, setMainFile] = useState(null);
  const [isPublic, setIsPublic] = useState(true);
  const [error, setError] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [showFileSizeError, setShowFileSizeError] = useState(false);
  const [isBlocked, setIsBlocked] = useState(false);

  // 이전 경로 저장
  useEffect(() => {
    if (location.state?.from) {
      sessionStorage.setItem('prevPath', location.state.from);
    }
    return () => sessionStorage.removeItem('prevPath');
  }, [location.state?.from]);

  // 대표 이미지 미리보기 처리
  useEffect(() => {
    if (mainFile) {
      if (mainFile.size > MAX_FILE_SIZE) {
        setShowFileSizeError(true);
      } else {
        const objectUrl = URL.createObjectURL(mainFile);
        setImagePreview(objectUrl);
        return () => URL.revokeObjectURL(objectUrl);
      }
    }
  }, [mainFile]);

  // 대표 이미지 파일 변경 핸들러
  const handleFileChange = useCallback((e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      if (selectedFile.size > MAX_FILE_SIZE) {
        setShowFileSizeError(true);
        return;
      }
      setMainFile(selectedFile);
      setIsBlocked(true);
      const objectUrl = URL.createObjectURL(selectedFile);
      setImagePreview(objectUrl);
    }
  }, []);

  const handleCancelFile = () => {
    setMainFile(null);
    setImagePreview(null);
    setIsBlocked(false);
  };

  const handleSubmit = async () => {
    // 필수값 체크 (재료와 조리과정 중 빈 객체는 제외)
    if (
      !title ||
      !content ||
      ingredients.filter((ing) => ing.name || ing.amount).length === 0 ||
      manuals.filter((manual) => manual.description).length === 0
    ) {
      setError('제목, 내용, 재료, 조리 과정을 모두 입력해주세요.');
      return;
    }

    // FormData 구성 (각 필드를 개별적으로 추가)
    const formData = new FormData();

    // 기본 필드 전송
    formData.append('title', title);
    formData.append('content', content);
    formData.append('isPublic', String(isPublic)); // boolean은 문자열로 전송

    // 재료 배열 전송: ingredients[0].name, ingredients[0].amount, ...
    ingredients.forEach((ing, index) => {
      formData.append(`ingredients[${index}].name`, ing.name);
      formData.append(`ingredients[${index}].amount`, ing.amount);
    });

    // 조리 과정 배열 전송: manuals[0].step, manuals[0].description, manuals[0].imageUrl (빈 문자열)
    manuals.forEach((manual, index) => {
      formData.append(`manuals[${index}].step`, String(index + 1));
      formData.append(`manuals[${index}].description`, manual.description);
      formData.append(`manuals[${index}].imageUrl`, ""); // 파일은 따로 전송
    });

    // 파일 전송
    if (mainFile) {
      formData.append('mainFile', mainFile);
    }
    // 기타 파일(조리 과정 이미지) 전송 (여러 파일인 경우 모두 추가)
    manuals
      .filter((manual) => manual.description && manual.image)
      .forEach((manual) => {
        formData.append('otherFiles', manual.image);
      });

    try {
      const responseData = await createUserRecipe(formData);
      if (responseData.data?.id) {
        setIsBlocked(false);
        navigate(`/user-recipe/${responseData.data.id}`, {
          state: { from: '/user-recipe/create' },
        });
      } else {
        setError('레시피 등록 후 ID를 가져올 수 없습니다.');
      }
    } catch (err) {
      console.error('레시피 등록 중 오류 발생:', err);
      setError(err.message);
    }
  };

  return (
    <div className="bg-white shadow-md mx-auto min-w-80 min-h-screen flex flex-col max-w-screen-sm md:max-w-screen-md lg:max-w-screen-lg">
      <Header isBlocked={isBlocked} />
      <div className="flex-1 p-4">
        {error && <p className="text-red-500">{error}</p>}
        <TextInput
          value={title}
          onChange={(e) => {
            setTitle(e.target.value);
            setIsBlocked(true);
          }}
          placeholder="레시피 제목"
        />
        <FileUpload
          file={mainFile}
          onChange={handleFileChange}
          imagePreview={imagePreview}
          onCancel={handleCancelFile}
        />
        <TextArea
          value={content}
          onChange={(e) => {
            setContent(e.target.value);
            setIsBlocked(true);
          }}
          placeholder="레시피 설명"
          rows={5}
        />
        <IngredientInput ingredients={ingredients} setIngredients={setIngredients} />
        <ManualInput manuals={manuals} setManuals={setManuals} />
        <div className="mt-5">
          <SubmitButton onClick={handleSubmit} label="레시피 추가" />
        </div>
      </div>
      <Footer />
      <FileSizeErrorModal
        isOpen={showFileSizeError}
        onClose={() => setShowFileSizeError(false)}
      />
    </div>
  );
}

export default CreateUserRecipe;
