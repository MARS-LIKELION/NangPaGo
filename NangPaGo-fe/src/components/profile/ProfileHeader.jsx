import { useNavigate } from 'react-router-dom';

const ProfileHeader = ({ nickName, providerName }) => {
  const navigate = useNavigate();

  return (
    <div className="flex items-center justify-between py-4 border-b">
      <div>
        <div className="text-lg font-medium">{nickName}</div>
        <div className="text-text-500">연결된 계정 : {providerName}</div>
      </div>
      <button
        onClick={() => navigate('/my-page/modify')}
        className="text-primary text-[20px] bg-white"
      >
        &gt;
      </button>
    </div>
  );
};

export default ProfileHeader;
