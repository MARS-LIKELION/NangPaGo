import React, {useState, useEffect} from 'react';
import {getAuditLogs} from '../api/audit';

export default function Audit() {
  const [auditLogs, setAuditLogs] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [pageSize] = useState(10);

  const fetchData = async () => {
    try {
      const response = await getAuditLogs(currentPage, pageSize);
      setAuditLogs(response.data.data.content);
      setTotalPages(response.data.data.totalPages);
    } catch (error) {
      console.error('데이터 가져오기 에러: ', error);
    }
  };

  useEffect(() => {
    fetchData();
  }, [currentPage]);

  return (
      <div className="p-6">
        <h2 className="text-2xl font-semibold text-gray-900 mb-6">감사 로그</h2>
        <div className="bg-white p-4 rounded-md shadow-md flex flex-col">
          <div className="flex-1 overflow-x-auto">
            <table className="w-full table-fixed border-collapse min-w-[800px]">
              <colgroup>
                <col className="w-[15%]"/>
                {/* Action */}
                <col className="w-[10%]"/>
                {/* User ID */}
                <col className="w-[30%]"/>
                {/* Request DTO */}
                <col className="w-[25%]"/>
                {/* Arguments */}
                <col className="w-[20%]"/>
                {/* Timestamp */}
              </colgroup>
              <thead>
              <tr>
                <th className="px-4 py-3 text-left text-sm font-semibold border-b">Action</th>
                <th className="px-4 py-3 text-left text-sm font-semibold border-b">User
                  ID
                </th>
                <th className="px-4 py-3 text-left text-sm font-semibold border-b">Request
                  DTO
                </th>
                <th className="px-4 py-3 text-left text-sm font-semibold border-b">Arguments</th>
                <th className="px-4 py-3 text-left text-sm font-semibold border-b">Timestamp</th>
              </tr>
              </thead>
              <tbody>
              {auditLogs.map((log, index) => (
                  <tr
                      key={index}
                      className={`${
                          index % 2 === 0 ? 'bg-gray-100' : 'bg-white'
                      } hover:bg-blue-50 border-b`}
                  >
                    <td className="px-4 py-2 text-sm text-gray-700 overflow-hidden whitespace-nowrap text-ellipsis">
                      {log.action}
                    </td>
                    <td className="px-4 py-2 text-sm text-gray-700 overflow-hidden whitespace-nowrap text-ellipsis">
                      {log.userId}
                    </td>
                    <td className="px-4 py-2 text-sm text-gray-700 overflow-hidden whitespace-nowrap text-ellipsis">
                      {log.requestDto}
                    </td>
                    <td className="px-4 py-2 text-sm text-gray-700 overflow-hidden whitespace-nowrap text-ellipsis">
                      {log.arguments}
                    </td>
                    <td className="px-4 py-2 text-sm text-gray-700 overflow-hidden whitespace-nowrap text-ellipsis">
                      {log.timestamp}
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
                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor"
                     viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round"
                        strokeWidth="2" d="M15 19l-7-7 7-7"/>
                </svg>
                이전
              </button>

              <div className="flex items-center space-x-3">
                <span className="text-sm text-gray-600">페이지</span>
                <span className="text-sm font-medium text-indigo-600">
                {currentPage + 1}
              </span>
                <span className="text-sm text-gray-600">/ {Math.max(totalPages,
                    1)}</span>
              </div>

              <button
                  onClick={() => setCurrentPage(
                      prev => Math.min(prev + 1, totalPages - 1))}
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
                <svg className="w-5 h-5 ml-2" fill="none" stroke="currentColor"
                     viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round"
                        strokeWidth="2" d="M9 5l7 7-7 7"/>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
  );
} 
