import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, LineChart, Line, ComposedChart, } from 'recharts';
import { UserIcon, DocumentTextIcon } from '@heroicons/react/24/outline'
import { useState, useEffect } from 'react';
import { getDashboardData } from '../api/total';

// 더미 데이터
const dailyUserStats = [
  { name: '6/7', users: 0 },
  { name: '6/8', users: 445 },
  { name: '6/9', users: 389 },
  { name: '6/10', users: 401 },
  { name: '6/11', users: 367 },
  { name: '6/12', users: 388 },
  { name: '6/13', users: 152 },
  { name: '6/14', users: 434 },
  { name: '6/15', users: 456 },
  { name: '6/16', users: 390 },
  { name: '6/17', users: 387 },
  { name: '6/18', users: 411 },
  { name: '6/19', users: 394 },
  { name: '6/20', users: 378 },
  { name: '6/21', users: 402 },
  { name: '6/22', users: 445 },
  { name: '6/23', users: 467 },
  { name: '6/24', users: 458 },
  { name: '6/25', users: 398 },
  { name: '6/26', users: 378 },
  { name: '6/27', users: 401 },
  { name: '6/28', users: 423 },
  { name: '6/29', users: 445 },
  { name: '6/30', users: 467 }
];

const monthlyAverageLoginStats = [
  { time: '00:00', avgLogins: 300 },
  { time: '01:00', avgLogins: 98 },
  { time: '02:00', avgLogins: 67 },
  { time: '03:00', avgLogins: 45 },
  { time: '04:00', avgLogins: 34 },
  { time: '05:00', avgLogins: 56 },
  { time: '06:00', avgLogins: 123 },
  { time: '07:00', avgLogins: 234 },
  { time: '08:00', avgLogins: 456 },
  { time: '09:00', avgLogins: 567 },
  { time: '10:00', avgLogins: 678 },
  { time: '11:00', avgLogins: 745 },
  { time: '12:00', avgLogins: 834 },
  { time: '13:00', avgLogins: 756 },
  { time: '14:00', avgLogins: 645 },
  { time: '15:00', avgLogins: 567 },
  { time: '16:00', avgLogins: 634 },
  { time: '17:00', avgLogins: 745 },
  { time: '18:00', avgLogins: 867 },
  { time: '19:00', avgLogins: 923 },
  { time: '20:00', avgLogins: 845 },
  { time: '21:00', avgLogins: 734 },
  { time: '22:00', avgLogins: 534 },
  { time: '23:00', avgLogins: 323 }
];

export default function Home() {
  const [dashboardData, setDashboardData] = useState({});
  const [months, setMonths] = useState(11);

  useEffect(() => {
    const fetchData = async () => {
      try {
      const response = await getDashboardData(months);
        setDashboardData(response.data);
      } catch (error) {
        console.error('대시보드 데이터 가져오기 에러: ', error);
      }
    };

    fetchData();
  }, []);

  return (
    <div className="p-6">
      {/* 통계 카드 */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-2">
        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <UserIcon className="h-6 w-6 text-gray-400" />
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 truncate">
                    총 사용자
                  </dt>
                  <dd className="text-3xl font-semibold text-gray-900">
                    {dashboardData.totals?.userCount || 0}
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <DocumentTextIcon className="h-6 w-6 text-gray-400" />
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 truncate">
                    총 게시글
                  </dt>
                  <dd className="text-3xl font-semibold text-gray-900">
                    {dashboardData.totals?.communityCount || 0}
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* 그래프 */}
      <div className="mt-8 grid grid-cols-1 gap-5 sm:grid-cols-2">
        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">월별 회원가입통계</h3>
            <ComposedChart width={600} height={300} data={dashboardData.monthlyRegisterData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="month" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="userCount" name="사용자 수" fill="#87CEEB" />
              <Line type="monotone" dataKey="userCount" stroke="#8884d8" />
            </ComposedChart>
          </div>
        </div>

        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">월별 게시글 통계</h3>
            <BarChart width={600} height={300} data={dashboardData.monthPostCountData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="month" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="count" name="게시글 수" fill="#82ca9d" />
            </BarChart>
          </div>
        </div>

        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">일일 접속자 통계</h3>
            <LineChart width={600} height={300} data={dailyUserStats}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line type="monotone" dataKey="users" stroke="#4f46e5" />
            </LineChart>
          </div>
        </div>

        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">월 평균 시간대별 사용자 활동 현황</h3>
            <LineChart width={600} height={300} data={monthlyAverageLoginStats}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis
                dataKey="time"
                interval={2}
                angle={-45}
                textAnchor="end"
                height={50}
              />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line
                type="monotone"
                name="평균 로그인 수"
                dataKey="avgLogins"
                stroke="#6366f1"
                strokeWidth={2}
                dot={false}
              />
            </LineChart>
          </div>
        </div>
      </div>
    </div>
  );
}
