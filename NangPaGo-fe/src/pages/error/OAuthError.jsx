import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

function OAuthError() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const message = searchParams.get('existingProvider') + '로 이미 가입한 이력이 있습니다.';
    alert(message);
    navigate('/', { replace: true });
  }, [navigate, searchParams]);

  return null;
}

export default OAuthError;
