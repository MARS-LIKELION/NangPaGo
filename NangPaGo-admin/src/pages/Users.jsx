import React, { useState } from 'react';

export default function Users() {
  const [users, setUsers] = useState([
    {
      id: 1,
      email: "aladin@example.com",
      nickname: "알라딘",
      birthday: "",
      phone: "010-1234-5678",
      provider: "google",
      created_at: "2024-01-01",
      updated_at: "2024-01-01",
      user_status: "ACTIVE"
    },
    {
      id: 2,
      email: "jasmin@example.com",
      nickname: "자스민",
      birthday: "1992-03-15",
      phone: "",
      provider: "kakao",
      created_at: "2024-01-02",
      updated_at: "2024-01-02",
      user_status: "ACTIVE"
    }
  ]);

  const [showConfirm, setShowConfirm] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [actionType, setActionType] = useState("");

  const handleStatusChange = (user, newStatus) => {
    setSelectedUser(user);
    setActionType(newStatus);
    setShowConfirm(true);
  };

  const confirmStatusChange = () => {
    setUsers(users.map(user =>
      user.id === selectedUser.id
        ? { ...user, user_status: actionType }
        : user
    ));
    setShowConfirm(false);
  };

  return (
    <div className="p-6">
      <h2 className="text-2xl font-semibold text-gray-900 mb-6">사용자 관리</h2>
      <div className="bg-white p-4 rounded-md shadow-md h-[725px]">
        <table className="min-w-full">
          <thead>
          <tr className="bg-gray-200">
            <th className="border border-gray-300 px-4 py-2 text-center">ID</th>
            <th className="border border-gray-300 px-4 py-2 text-center">이메일</th>
            <th className="border border-gray-300 px-4 py-2 text-center">닉네임</th>
            <th className="border border-gray-300 px-4 py-2 text-center">생년월일</th>
            <th className="border border-gray-300 px-4 py-2 text-center">전화번호</th>
            <th className="border border-gray-300 px-4 py-2 text-center">가입 경로</th>
            <th className="border border-gray-300 px-4 py-2 text-center">가입일</th>
            <th className="border border-gray-300 px-4 py-2 text-center">수정일</th>
            <th className="border border-gray-300 px-4 py-2 text-center">상태</th>
            <th className="border border-gray-300 px-4 py-2 text-center">관리</th>
          </tr>
          </thead>
          <tbody>
          {users.map((user) => (
            <tr key={user.id} className="hover:bg-blue-50 bg-white">
              <td className="border border-gray-300 px-4 py-2 text-center">{user.id}</td>
              <td className="border border-gray-300 px-4 py-2 text-center">{user.email}</td>
              <td className="border border-gray-300 px-4 py-2 text-center">{user.nickname}</td>
              <td className="border border-gray-300 px-4 py-2 text-center">{user.birthday || '-'}</td>
              <td className="border border-gray-300 px-4 py-2 text-center">{user.phone || '-'}</td>
              <td className="border border-gray-300 px-4 py-2 text-center">{user.provider}</td>
              <td className="border border-gray-300 px-4 py-2 text-center">{user.created_at}</td>
              <td className="border border-gray-300 px-4 py-2 text-center">{user.updated_at}</td>
              <td className="border border-gray-300 px-4 py-2 text-center">
                  <span className={`px-2 py-1 rounded text-sm ${
                    user.user_status === 'ACTIVE'
                      ? ''
                      : 'text-red-600'
                  }`}>
                    {user.user_status}
                  </span>
              </td>
              <td className="border border-gray-300 px-4 py-2 text-center">
                {user.user_status === 'ACTIVE' ? (
                  <div className="space-x-2">
                    <button
                      className="bg-red-600 text-white px-3 py-1 rounded text-sm hover:bg-red-600"
                      onClick={() => handleStatusChange(user, 'WITHDRAWN')}
                    >
                      WITHDRAWN
                    </button>
                  </div>
                ) : (
                  <button
                    className="bg-gray-500 text-white px-3 py-1 rounded text-sm"
                    onClick={() => handleStatusChange(user, 'ACTIVE')}
                  >
                    ACTIVE
                  </button>
                )}
              </td>
            </tr>
          ))}
          </tbody>
        </table>
      </div>

      {showConfirm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white p-6 rounded-lg shadow-xl max-w-md w-full">
            <h3 className="text-lg font-semibold mb-4">사용자 상태 변경</h3>
            <p className="mb-4">
              {actionType === 'WITHDRAWN' && '해당 사용자의 계정을 비활성화하시겠습니까?'}
              {actionType === 'ACTIVE' && '해당 사용자의 계정을 활성화하시겠습니까?'}
            </p>
            <div className="flex justify-end space-x-3">
              <button
                className="px-4 py-2 border rounded hover:bg-gray-200"
                onClick={() => setShowConfirm(false)}
              >
                취소
              </button>
              <button
                className="px-4 py-2 bg-indigo-500 text-white rounded hover:bg-indigo-600"
                onClick={confirmStatusChange}
              >
                확인
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
