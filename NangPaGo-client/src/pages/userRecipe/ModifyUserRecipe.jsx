import { useState, useEffect, useCallback } from 'react';
import { useNavigate, useLocation, useParams } from 'react-router-dom';
import { fetchUserRecipeById, updateUserRecipe } from '../../api/userRecipe';
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
const DEFAULT_IMAGE_URL = '';

function ModifyUserRecipe() {
  const navigate = useNavigate();
  const { id } = useParams();
  const location = useLocation();
  const prevPath = sessionStorage.getItem('prevPath') || '/user-recipe';

  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [ingredients, setIngredients] = useState([{ name: '', amount: '' }]);
  const [manuals, setManuals] = useState([{ description: '', image: null }]);
  const [mainFile, setMainFile] = useState(null);
  const [existingImageUrl, setExistingImageUrl] = useState(null);
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
    const fetchData = async () => {
      try {
        const data = await fetchUserRecipeById(id);
        console.log('Fetched manuals:', data.manuals); // 🔍 API 응답 확인용
  
        setTitle(data.title);
        setContent(data.content);
        setIsPublic(data.isPublic);
  
        // ✅ 기존 이미지 URL이 유지되도록 변경
        setManuals(
          (data.manuals || []).map((manual) => ({
            description: typeof manual === 'string'
  ? manual.replace(/^[\d.\s]+/, '')  // ✅ 숫자, 점(.), 공백을 모두 제거
  : manual.description.replace(/^[\d.\s]+/, ''),

            image: manual.image && typeof manual.image === 'string'
              ? manual.image
              : null,
          }))
        );
        
  
        if (data.mainImageUrl) {
          setExistingImageUrl(data.mainImageUrl);
          setImagePreview(data.mainImageUrl);
        }
      } catch (err) {
        console.error('레시피 데이터를 불러오는 중 오류 발생:', err);
        setError('수정할 레시피 데이터를 불러오는데 실패했습니다.');
      }
    };
    fetchData();
  }, [id]);
  

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
      return () => URL.revokeObjectURL(objectUrl);
    }
  }, []);

  const handleCancelFile = () => {
    setMainFile(null);
    setImagePreview(existingImageUrl);
    setIsBlocked(true);
  };

  const handleSubmit = async () => {
    if (!title || !content || ingredients.length === 0 || manuals.length === 0) {
      setError('제목, 내용, 재료, 조리 과정을 모두 입력해주세요.');
      return;
    }
    const formData = new FormData();
    formData.append('title', title);
    formData.append('content', content);
    formData.append('isPublic', isPublic);
  
    ingredients.forEach((ingredient) => {
      const ingredientText = `${ingredient.name} ${ingredient.amount}`.trim();
      formData.append('ingredients', ingredientText);
    });
  
    manuals.forEach((manual, index) => {
      formData.append(`manuals[${index}]`, `${manual.description}`);  // ✅ 숫자 없이 저장
    
      
      // 기존 이미지가 URL인 경우 유지
      if (manual.image && typeof manual.image === 'string') {
        formData.append(`manualImages[${index}]`, manual.image);
      } else if (manual.image && manual.image instanceof File) {
        formData.append('otherFiles', manual.image);
      }
    });
  
    if (mainFile) {
      formData.append('mainFile', mainFile);
    }
  
    try {
      const responseData = await updateUserRecipe(id, formData);
      if (responseData.data?.id) {
        setIsBlocked(false);
        navigate(`/user-recipe/${responseData.data.id}`, { state: { from: '/user-recipe/modify' } });
      } else {
        setError('레시피 수정 후 ID를 가져올 수 없습니다.');
      }
    } catch (err) {
      console.error('레시피 수정 중 오류 발생:', err);
      setError(err.message);
    }
  };
  

  return (
    <div className="bg-white shadow-md mx-auto min-w-80 min-h-screen flex flex-col max-w-screen-sm md:max-w-screen-md lg:max-w-screen-lg">
      <Header isBlocked={isBlocked} />
      <div className="flex-1 p-4">
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
          <SubmitButton onClick={handleSubmit} label="레시피 수정" />
        </div>
        {error && <p className="mt-2 text-red-500">{error}</p>}
      </div>
      <Footer />
      <FileSizeErrorModal
        isOpen={showFileSizeError}
        onClose={() => setShowFileSizeError(false)}
      />
    </div>
  );
}

export default ModifyUserRecipe;
