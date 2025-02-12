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

const MAX_FILE_SIZE = 10 * 1024 * 1024;

function CreateUserRecipe() {
  const navigate = useNavigate();
  const location = useLocation();
  const prevPath = sessionStorage.getItem('prevPath') || '/user-recipe';
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [ingredients, setIngredients] = useState([{ name: '', amount: '' }]);
  const [manuals, setManuals] = useState([{ description: '', image: null }]);
  const [mainFile, setMainFile] = useState(null);
  const [isPublic, setIsPublic] = useState(true);
  const [error, setError] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [showFileSizeError, setShowFileSizeError] = useState(false);
  const [isBlocked, setIsBlocked] = useState(false);

  useEffect(() => {
    if (location.state?.from) {
      sessionStorage.setItem('prevPath', location.state.from);
    }
    return () => sessionStorage.removeItem('prevPath');
  }, [location.state?.from]);

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
    if (
      !title ||
      !content ||
      ingredients.filter((ing) => ing.name || ing.amount).length === 0 ||
      manuals.filter((manual) => manual.description).length === 0
    ) {
      setError('제목, 내용, 재료, 조리 과정을 모두 입력해주세요.');
      return;
    }

    const formData = new FormData();

    formData.append('title', title);
    formData.append('content', content);
    formData.append('isPublic', String(isPublic));

    ingredients.forEach((ing, index) => {
      formData.append(`ingredients[${index}].name`, ing.name);
      formData.append(`ingredients[${index}].amount`, ing.amount);
    });

    if (mainFile) {
      formData.append('mainFile', mainFile);
    }

    manuals.forEach((manual, index) => {
    formData.append(`manuals[${index}].description`, manual.description || '');
    formData.append(`manuals[${index}].step`, index + 1);

     if (manual.image) {
      const file = manual.image;
      const newFile = new File([file], `step${index + 1}_${file.name}`, {
        type: file.type,
      });
      formData.append('otherFiles', newFile);
    } else {
      formData.append(`otherFiles[${index}]`, null);
    }
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
        <ManualInput
          manuals={manuals}
          setManuals={setManuals}
          maxFileSize={MAX_FILE_SIZE}
          onFileSizeError={() => setShowFileSizeError(true)}
        />
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
