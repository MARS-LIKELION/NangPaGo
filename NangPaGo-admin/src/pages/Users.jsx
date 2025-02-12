import React, { useState, useEffect } from 'react';
import { getUserList, banUser, unBanUser } from '../api/usermanage';
import { SOCIAL_BUTTON_STYLES } from '../common/styles/CommonButton.js';

export default function Users() {
  const [users, setUsers] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showConfirm, setShowConfirm] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [actionType, setActionType] = useState("");
  const [selectedStatuses, setSelectedStatuses] = useState([]);
  const [selectedProviders, setSelectedProviders] = useState([]);
  const [pageSize, setPageSize] = useState(10);
  const [sortField, setSortField] = useState('ID');
  const [isAscending, setIsAscending] = useState(true);
  const [dataUpdateFlag, setDataUpdateFlag] = useState(0);

  const [searchType, setSearchType] = useState('EMAIL');
  const [searchKeyword, setSearchKeyword] = useState('');
  const [appliedSearchType, setAppliedSearchType] = useState('');
  const [appliedSearchKeyword, setAppliedSearchKeyword] = useState('');

  const userStatuses = [
    { value: 'ACTIVE', label: '정상' },
    { value: 'WITHDRAWN', label: '탈퇴' },
    { value: 'BANNED', label: '밴' },
    { value: 'OTHERS', label: '기타' }
  ];

  const oAuthProviders = [
    { value: 'GOOGLE', label: 'Google' },
    { value: 'KAKAO', label: 'Kakao' },
    { value: 'NAVER', label: 'Naver' }
  ];

  const handleProviderChange = (value) => {
    setSelectedProviders(prev => {
      if (prev.includes(value)) {
        return prev.filter(provider => provider !== value);
      } else {
        return [...prev, value];
      }
    });
    setCurrentPage(0);
  };

  const handleStatusChange = (value) => {
    setSelectedStatuses(prev => {
      if (prev.includes(value)) {
        return prev.filter(status => status !== value);
      } else {
        return [...prev, value];
      }
    });
    setCurrentPage(0);
  };

  const handleSearch = (e) => {
    e.preventDefault();
    setAppliedSearchType(searchType);
    setAppliedSearchKeyword(searchKeyword);
    setCurrentPage(0);
  };

  const toggleSort = (field) => {
    if (sortField === field) {
      setIsAscending(prev => !prev);
    } else {
      setSortField(field);
      setIsAscending(true);
    }
  };

  const handleUserStatusChange = (user, newStatus) => {
    setSelectedUser(user);
    setActionType(newStatus);
    setShowConfirm(true);
  };

  const confirmStatusChange = async () => {
    try {
      if (actionType === 'BANNED') {
        await banUser(selectedUser.id);
      } else if (actionType === 'ACTIVE') {
        await unBanUser(selectedUser.id);
      }
      setDataUpdateFlag(prev => prev + 1);
      setShowConfirm(false);
    } catch (error) {
      console.error('상태 변경 중 에러 발생:', error);
    }
  };

  const fetchData = async () => {
    try {
      const sortType = `${sortField}_${isAscending ? 'ASC' : 'DESC'}`;
      const response = await getUserList(
        currentPage,
        sortType,
        selectedStatuses,
        selectedProviders,
        pageSize,
        appliedSearchType,
        appliedSearchKeyword
      );
      setUsers(response.data.data.content);
      setTotalPages(response.data.data.totalPages);
    } catch (error) {
      console.error('데이터 가져오기 에러: ', error);
    }
  };

  useEffect(() => {
    fetchData();
  }, [currentPage, isAscending, sortField, selectedStatuses, selectedProviders, dataUpdateFlag, appliedSearchType, appliedSearchKeyword]);

  return (
    <div className="p-6">
      <h2 className="text-2xl font-semibold text-gray-900 mb-6">사용자 관리</h2>
      <div className="bg-white p-4 rounded-md shadow-md mb-6">
        <div className="flex flex-col space-y-4">
          <div>
            <h3 className="text-sm font-medium text-gray-700 mb-2">사용자 상태</h3>
            <div className="flex flex-wrap gap-6">
              {userStatuses.map(status => (
                <label key={status.value} className="flex items-center space-x-2">
                  <input
                    type="checkbox"
                    checked={selectedStatuses.includes(status.value)}
                    onChange={() => handleStatusChange(status.value)}
                    className="text-indigo-600 focus:ring-indigo-500 h-4 w-4 rounded"
                  />
                  <span className="text-sm text-gray-700">{status.label}</span>
                </label>
              ))}
            </div>
          </div>
          <div>
            <h3 className="text-sm font-medium text-gray-700 mb-2">가입 경로</h3>
            <div className="flex flex-wrap gap-6">
              {oAuthProviders.map(provider => (
                <label key={provider.value} className="flex items-center space-x-2">
                  <input
                    type="checkbox"
                    checked={selectedProviders.includes(provider.value)}
                    onChange={() => handleProviderChange(provider.value)}
                    className="text-indigo-600 focus:ring-indigo-500 h-4 w-4 rounded"
                  />
                  <span className="text-sm text-gray-700">{provider.label}</span>
                </label>
              ))}
            </div>
          </div>
        </div>
      </div>
      <div className="flex justify-end mb-4">
        <form onSubmit={handleSearch}
              className="flex items-center space-x-2">
          <select
            value={searchType}
            onChange={(e) => setSearchType(e.target.value)}
            className="border rounded px-3 py-1.5 text-sm"
          >
            <option value="EMAIL">이메일</option>
            <option value="NICKNAME">닉네임</option>
          </select>
          <input
            type="text"
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            placeholder="검색어를 입력하세요"
            className="border rounded px-3 py-1.5 text-sm w-64"
          />
          <button
              type="submit"
              className={`${SOCIAL_BUTTON_STYLES.common.btn} ml-2 px-4 py-1.5 text-sm font-medium`}
          >
            검색
          </button>
        </form>
      </div>
      <div className="bg-white p-4 rounded-md shadow-md flex flex-col">
        <div className="flex-1 overflow-x-auto">
          <table className="w-full table-fixed border-collapse min-w-[800px]">
            <colgroup>
              <col className="w-[5%]" />{/* ID */}
              <col className="w-[16%]" />{/* 이메일 */}
              <col className="w-[12%]" />{/* 닉네임 */}
              <col className="w-[8%]" />{/* 가입 경로 */}
              <col className="w-[14%]" />{/* 가입일 */}
              <col className="w-[14%]" />{/* 수정일 */}
              <col className="w-[9%]" />{/* 상태 */}
            </colgroup>
          <thead>
            <tr>
              <th className="px-4 py-3 text-left text-sm font-semibold border-b">ID</th>
              <th className="px-4 py-3 text-left text-sm font-semibold border-b">이메일</th>
              <th
                className="px-4 py-3 text-left text-sm font-semibold border-b cursor-pointer group"
                onClick={() => toggleSort('NICKNAME')}
              >
                <div className="flex items-center">
                  닉네임
                  <span className="ml-1">
                    {sortField === 'NICKNAME' ? (
                      isAscending ? (
                        <svg className="w-4 h-4 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 15l7-7 7 7"/>
                        </svg>
                      ) : (
                        <svg className="w-4 h-4 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"/>
                        </svg>
                      )
                    ) : (
                      <svg className="w-4 h-4 text-gray-300 group-hover:text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4"/>
                      </svg>
                    )}
                  </span>
                </div>
              </th>
              <th className="px-4 py-3 text-left text-sm font-semibold border-b">가입 경로</th>
              <th
                className="px-4 py-3 text-left text-sm font-semibold border-b cursor-pointer group"
                onClick={() => toggleSort('ID')}
              >
                <div className="flex items-center">
                  가입일
                  <span className="ml-1">
                    {sortField === 'ID' ? (
                      isAscending ? (
                        <svg className="w-4 h-4 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 15l7-7 7 7"/>
                        </svg>
                      ) : (
                        <svg className="w-4 h-4 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"/>
                        </svg>
                      )
                    ) : (
                      <svg className="w-4 h-4 text-gray-300 group-hover:text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4"/>
                      </svg>
                    )}
                  </span>
                </div>
              </th>
              <th className="px-4 py-3 text-left text-sm font-semibold border-b">수정일</th>
              <th className="px-4 py-3 text-left text-sm font-semibold border-b">상태</th>
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
                <td className="px-4 py-2 text-sm text-gray-700 overflow-hidden whitespace-nowrap text-ellipsis">{user.id}</td>
                <td className="px-4 py-2 text-sm text-gray-700 overflow-hidden whitespace-nowrap text-ellipsis">{user.email}</td>
                <td className="px-4 py-2 text-sm text-gray-700 overflow-hidden whitespace-nowrap text-ellipsis">{user.nickname}</td>
                <td className="px-4 py-2 text-sm text-gray-700 overflow-hidden whitespace-nowrap text-ellipsis">{user.oAuth2Provider}</td>
                <td className="px-4 py-2 text-sm text-gray-700 overflow-hidden whitespace-nowrap text-ellipsis">{user.createdAt}</td>
                <td className="px-4 py-2 text-sm text-gray-700 overflow-hidden whitespace-nowrap text-ellipsis">{user.updatedAt}</td>
                <td className="px-4 py-2 text-sm text-gray-700 w-[120px]">
                  {user.userStatus === 'WITHDRAWN' ? (
                    <div className="text-gray-500 px-2 py-1.5 w-[100px] overflow-hidden whitespace-nowrap text-ellipsis h-[32px] flex items-center border rounded">
                      WITHDRAWN
                    </div>
                  ) : (
                    <select
                      value={user.userStatus}
                      onChange={(e) => handleUserStatusChange(user, e.target.value)}
                      className="border rounded px-2 py-1.5 text-sm w-[100px] h-[32px]"
                    >
                      <option value="ACTIVE">ACTIVE</option>
                      <option value="BANNED">BANNED</option>
                    </select>
                  )}
                </td>
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
              <span className="text-sm text-gray-600">/ {Math.max(totalPages, 1)}</span>
            </div>

            <button 
              onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages - 1))}
              disabled={currentPage === totalPages - 1 || totalPages === 0}
              className={`
                flex items-center px-4 py-2 text-sm rounded-md transition-colors
                ${(currentPage === totalPages - 1 || totalPages === 0)
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
