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

function ModifyUserRecipe() {
  const navigate = useNavigate();
  const { id } = useParams();
  const location = useLocation();
  const prevPath = sessionStorage.getItem('prevPath') || '/user-recipe';

  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [ingredients, setIngredients] = useState([{ name: '', amount: '' }]);
  const [manuals, setManuals] = useState([{ step: 1, description: '', image: null, preview: '' }]);
  const [mainFile, setMainFile] = useState(null);
  const [existingImageUrl, setExistingImageUrl] = useState('');
  const [isPublic, setIsPublic] = useState(true);
  const [error, setError] = useState(null);
  const [imagePreview, setImagePreview] = useState('');
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
        setTitle(data.title);
        setContent(data.content);
        setIsPublic(data.isPublic);
        setIngredients(data.ingredients || []);
        setManuals(
          (data.manuals || []).map((man) => ({
            step: man.step,
            description: man.description,
            image: null,
            preview: man.imageUrl || ''
          }))
        );
        setExistingImageUrl(data.mainImageUrl || '');
        setImagePreview(data.mainImageUrl || '');
      } catch (err) {
        console.error(err);
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
    }
  }, []);

  const handleCancelFile = () => {
    setMainFile(null);
    setImagePreview(existingImageUrl);
    setIsBlocked(true);
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

    const requestDto = {
      title,
      content,
      isPublic,
      mainImageUrl: existingImageUrl,
      ingredients,
      manuals: manuals.map(manual => ({
        step: manual.step,
        description: manual.description,
        imageUrl: manual.preview || ""
      }))
    };

    const formData = new FormData();
    formData.append('requestDto', new Blob([JSON.stringify(requestDto)], { type: "application/json" }));

    if (mainFile) {
      formData.append('mainFile', mainFile);
    }

    manuals.forEach((manual, index) => {
      if (manual.image && manual.image instanceof File) {
        formData.append('otherFiles', manual.image);
      } else {
        formData.append('otherFiles', new Blob([]), `placeholder-${index}`);
      }
    });
    
    try {
      const responseData = await updateUserRecipe(id, formData);
      if (responseData.data?.id) {
        setIsBlocked(false);
        navigate(`/user-recipe/${responseData.data.id}`, { state: { from: '/user-recipe/modify' } });
      } else {
        setError('레시피 수정 후 ID를 가져올 수 없습니다.');
      }
    } catch (err) {
      console.error(err);
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
      <FileSizeErrorModal isOpen={showFileSizeError} onClose={() => setShowFileSizeError(false)} />
    </div>
  );
}

export default ModifyUserRecipe;
