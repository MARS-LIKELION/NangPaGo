import { useState, useEffect, useCallback } from 'react';
import { useNavigate, useLocation, useParams } from 'react-router-dom';
import { fetchPostById } from '../../api/post';
import { updateCommunity } from '../../api/community';
import Header from '../../components/layout/header/Header';
import Footer from '../../components/layout/Footer';
import TextInput from '../../components/community/TextInput';
import TextArea from '../../components/community/TextArea';
import FileUpload from '../../components/community/FileUpload';
import { ERROR_STYLES } from '../../common/styles/ErrorMessage';
import SubmitButton from '../../components/button/SubmitButton';
import FileSizeErrorModal from '../../components/modal/FileSizeErrorModal';

const DEFAULT_IMAGE_URL =
  'https://storage.googleapis.com/nangpago-9d371.firebasestorage.app/dc137676-6240-4920-97d3-727c4b7d6d8d_360_F_517535712_q7f9QC9X6TQxWi6xYZZbMmw5cnLMr279.jpg';
const MAX_FILE_SIZE = 10 * 1024 * 1024;

function ModifyCommunity() {
  const navigate = useNavigate();
  const location = useLocation();
  const prevPath = sessionStorage.getItem('prevPath') || '/community';
  const { id } = useParams();

  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [file, setFile] = useState(null);
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
        console.log('Fetched manuals:', data.manuals); // ✅ API 응답 확인용
  
        setTitle(data.title);
        setContent(data.content);
        setIsPublic(data.isPublic);
  
        // ✅ 기존 이미지 URL이 유지되도록 변경
        setManuals(
          (data.manuals || []).map((manual) => ({
            description: typeof manual === 'string'
              ? manual.replace(/^\d+\.\s*\d*\.\s*/, '') // ✅ 숫자가 여러 번 반복되면 모두 제거
              : manual.description.replace(/^\d+\.\s*\d*\.\s*/, ''),
            image: manual.image && typeof manual.image === 'string'
              ? manual.image  // ✅ 기존 이미지 URL 유지
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
    if (file) {
      if (file.size > MAX_FILE_SIZE) {
        setShowFileSizeError(true);
      } else {
        const objectUrl = URL.createObjectURL(file);
        setImagePreview(objectUrl);

        return () => URL.revokeObjectURL(objectUrl);
      }
    }
  }, [file]);

  const handleFileChange = useCallback(
    (e) => {
      const selectedFile = e.target.files[0];
      if (selectedFile && selectedFile !== file) {
        setFile(selectedFile);
        setIsBlocked(true);
      }
    },
    [file],
  );

  useEffect(() => {
    const handleRefreshUnload = (e) => {
      if (isBlocked) {
        e.preventDefault();
        e.returnValue = '';
      }
    };

    const handleBackNavigation = (e) => {
      if (isBlocked) {
        const confirmed = window.confirm(
          '작성 중인 내용을 저장하지 않고 이동하시겠습니까?',
        );
        if (confirmed) {
          setIsBlocked(false);
          navigate(prevPath);
        } else {
          history.pushState(null, '', window.location.href);
        }
      }
    };

    window.addEventListener('beforeunload', handleRefreshUnload);
    window.addEventListener('popstate', handleBackNavigation);

    history.pushState(null, '', window.location.href);

    return () => {
      window.removeEventListener('beforeunload', handleRefreshUnload);
      window.removeEventListener('popstate', handleBackNavigation);
    };
  }, [isBlocked]);

  const handleCancel = () => {
    setFile(null);
    setImagePreview(null);
  };

  const handleSubmit = async () => {
    if (!title || !content) {
      setError('제목과 내용을 모두 입력해주세요.');
      return;
    }
    try {
      const formData = new FormData();
      formData.append('title', title);
      formData.append('content', content);
      formData.append('isPublic', isPublic);
      // 파일 선택 여부에 따라 처리: 
      // 새 파일(file)이 있으면 전송, 없으면 기존 이미지 URL을 함께 전송
      if (file) {
        formData.append('file', file);
      } else {
        formData.append('existingImageUrl', imagePreview || '');
      }
      await updateCommunity(id, formData);
      setIsBlocked(false);
      navigate(`/community/${id}`, {
        state: { from: `/community/${id}/modify` },
      });
    } catch (err) {
      console.error('게시글 수정 중 오류 발생:', err);
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
          placeholder="제목"
        />
        <FileUpload
          file={file}
          onChange={handleFileChange}
          imagePreview={imagePreview}
          onCancel={() => {
            setFile(null);
            setImagePreview(null);
            setIsBlocked(true);
          }}
        />
        <TextArea
          value={content}
          onChange={(e) => {
            setContent(e.target.value);
            setIsBlocked(true);
          }}
          placeholder="내용을 입력해 주세요."
          rows={11}
        />
        <div className="flex items-center mb-4">
          <input
            type="checkbox"
            id="is-public"
            checked={!isPublic}
            onChange={(e) => {
              setIsPublic(!e.target.checked);
              setIsBlocked(true);
            }}
            className="mr-2 w-4 h-4 appearance-none border border-text-400 rounded-md checked:bg-primary"
          />
          <label htmlFor="is-public" className="text-sm text-text-600">
            비공개 (체크 시 로그인한 사용자만 볼 수 있습니다.)
          </label>
        </div>
        <p className={ERROR_STYLES.community}>{error}</p>
        <div className="mt-4">
          {' '}
          <SubmitButton onClick={handleSubmit} label="수정 완료" />
        </div>
      </div>
      <Footer className="mt-4" />
      <FileSizeErrorModal
        isOpen={showFileSizeError}
        onClose={() => setShowFileSizeError(false)}
      />
    </div>
  );
}

export default ModifyCommunity;
