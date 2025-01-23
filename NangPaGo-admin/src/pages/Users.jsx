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
      user_status: "BANNED"
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
    },
    {
      id: 3,
      email: "cbk@example.com",
      nickname: "최보경",
      birthday: "2000-01-01",
      phone: "",
      provider: "naver",
      created_at: "2024-01-23",
      updated_at: "2024-01-23",
      user_status: "OTHERS"
    },
    {
      id: 4,
      email: "npg@example.com",
      nickname: "냉파고",
      birthday: "",
      phone: "",
      provider: "naver",
      created_at: "2024-01-02",
      updated_at: "2024-01-02",
      user_status: "WITHDRAWN"
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
      <div className="bg-white p-4 rounded-md shadow-md h-[725px] flex flex-col">
        <table className="min-w-full border-collapse">
          <thead>
          <tr>
            <th className="px-4 py-3 text-left text-sm font-semibold border-b">ID</th>
            <th className="px-4 py-3 text-left text-sm font-semibold border-b">이메일</th>
            <th className="px-4 py-3 text-left text-sm font-semibold border-b">닉네임</th>
            <th className="px-4 py-3 text-left text-sm font-semibold border-b">생년월일</th>
            <th className="px-4 py-3 text-left text-sm font-semibold border-b">전화번호</th>
            <th className="px-4 py-3 text-left text-sm font-semibold border-b">가입 경로</th>
            <th className="px-4 py-3 text-left text-sm font-semibold border-b">가입일</th>
            <th className="px-4 py-3 text-left text-sm font-semibold border-b">수정일</th>
            <th className="px-4 py-3 text-left text-sm font-semibold border-b w-28">상태</th>
          </tr>
          </thead>
          <tbody>
          {users.map((user, index) => (
            <tr
              key={user.id}
              className={`${
                index % 2 === 0 ? 'bg-gray-100' : 'bg-white'
              } hover:bg-blue-50 border-b`}
            >
              <td className="px-4 py-2 text-sm text-gray-700">{user.id}</td>
              <td className="px-4 py-2 text-sm text-gray-700">{user.email}</td>
              <td
                className="px-4 py-2 text-sm text-gray-700">{user.nickname}</td>
              <td className="px-4 py-2 text-sm text-gray-700">{user.birthday || '-'}</td>
              <td className="px-4 py-2 text-sm text-gray-700">{user.phone || '-'}</td>
              <td className="px-4 py-2 text-sm text-gray-700">{user.provider}</td>
              <td className="px-4 py-2 text-sm text-gray-700">{user.created_at}</td>
              <td className="px-4 py-2 text-sm text-gray-700">{user.updated_at}</td>
              <td className="px-4 py-2 text-sm text-gray-700 w-28">
                <select
                  value={user.user_status}
                  onChange={(e) => handleStatusChange(user, e.target.value)}
                  className="border rounded px-2 py-1 text-sm"
                >
                  <option value="ACTIVE">ACTIVE</option>
                  <option value="WITHDRAWN">WITHDRAWN</option>
                  <option value="BANNED">BANNED</option>
                  <option value="OTHERS">OTHERS</option>
                </select>
              </td>
            </tr>
          ))}
          </tbody>
        </table>
      </div>

      {showConfirm && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white p-6 rounded-lg shadow-xl max-w-md w-full">
            <h3 className="text-lg font-semibold mb-4">사용자 상태 변경</h3>
            <p className="mb-4">
              {actionType === 'ACTIVE' && '해당 사용자의 계정을 정상으로 바꾸시겠습니까?'}
              {actionType === 'WITHDRAWN' && '해당 사용자를 탈퇴시키시겠습니까?'}
              {actionType === 'BANNED' && '해당 사용자의 활동을 금지시키겠습니까?'}
              {actionType === 'OTHERS' && '해당 사용자의 계정을 기타로 바꾸시겠습니까?'}
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
