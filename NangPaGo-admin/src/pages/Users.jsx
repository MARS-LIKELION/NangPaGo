import React, { useState, useEffect } from 'react';
import { getUserList } from '../api/usermanage';

export default function Users() {
  const [users, setUsers] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
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

  useEffect(() => {
      const fetchData = async () => {
        try {
          const response = await getUserList(currentPage);
          console.log(response.data); // 확인용 로그
          setUsers(response.data.content); // 사용자 목록
          setTotalPages(response.data.totalPages); // 총 페이지 수
        } catch (error) {
          console.error('데이터 가져오기 에러: ', error);
        }
      };
      fetchData();
    }, [currentPage]);

  return (
    <div className="p-6">
      <h2 className="text-2xl font-semibold text-gray-900 mb-6">사용자 관리</h2>
      <div className="bg-white p-4 rounded-md shadow-md h-[800px] flex flex-col">
      <div className="flex-1 min-h-[580px] overflow-auto">
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
                <td className="px-4 py-2 text-sm text-gray-700">{user.oAuth2Provider}</td>
                <td className="px-4 py-2 text-sm text-gray-700">{user.createdAt}</td>
                <td className="px-4 py-2 text-sm text-gray-700">{user.updatedAt}</td>
                <td className="px-4 py-2 text-sm text-gray-700 w-28">
                  {user.userStatus === 'WITHDRAWN' ? (
                    <div className="text-gray-500 px-2 py-1">
                      WITHDRAWN
                    </div>
                  ) : (
                  <select
                    value={user.userStatus}
                    onChange={(e) => handleStatusChange(user, e.target.value)}
                    className="border rounded px-2 py-1 text-sm"
                  >
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="BANNED">BANNED</option>
                  </select>
                )}</td>
              </tr>
            ))}
            </tbody>
          </table>
          </div>
        <div className="flex-shrink-0 mt-auto pt-4 pb-8">
          <div className="flex items-center justify-center space-x-8">
            <button
              onClick={() => setCurrentPage(prev => Math.max(prev - 1, 0))}
              disabled={currentPage === 0}
              className={`
                flex items-center px-4 py-2 text-sm rounded-md transition-colors
                ${currentPage === 0
                  ? 'text-gray-300 cursor-not-allowed'
                  : 'text-gray-600 hover:text-indigo-600'
                }
              `}
            >
              <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 19l-7-7 7-7" />
              </svg>
              이전
            </button>

            <div className="flex items-center space-x-3">
              <span className="text-sm text-gray-600">페이지</span>
              <span className="text-sm font-medium text-indigo-600">
                {currentPage + 1}
              </span>
              <span className="text-sm text-gray-600">/ {totalPages}</span>
            </div>

            <button
              onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages - 1))}
              disabled={currentPage === totalPages - 1}
              className={`
                flex items-center px-4 py-2 text-sm rounded-md transition-colors
                ${currentPage === totalPages - 1
                  ? 'text-gray-300 cursor-not-allowed'
                  : 'text-gray-600 hover:text-indigo-600'
                }
              `}
            >
              다음
              <svg className="w-5 h-5 ml-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
        </div>
      </div>
      {showConfirm && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white p-6 rounded-lg shadow-xl max-w-md w-full">
            <h3 className="text-lg font-semibold mb-4">사용자 상태 변경</h3>
            <p className="mb-4">
              {actionType === 'ACTIVE' && '해당 사용자의 계정을 정상으로 바꾸시겠습니까?'}
              {actionType === 'BANNED' && '해당 사용자의 활동을 금지시키겠습니까?'}
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
