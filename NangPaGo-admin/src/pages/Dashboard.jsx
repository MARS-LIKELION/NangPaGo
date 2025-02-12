import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, LineChart, Line, ComposedChart } from 'recharts';
import { UserIcon, DocumentTextIcon } from '@heroicons/react/24/outline';
import { useState, useEffect } from 'react';
import { getDashboardData } from '../api/total';
import { format, eachDayOfInterval } from 'date-fns';

// 월 평균 로그인 통계 (더미 데이터)
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

export default function Dashboard() {
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

  // 일별 사용자 통계 처리
  const processedData = (() => {
    if (!dashboardData.dailyUserStats || !dashboardData.dailyUserStats.length) return [];

    const sortedData = [...dashboardData.dailyUserStats].sort(
      (a, b) => new Date(a.date) - new Date(b.date)
    );

    const allDates = eachDayOfInterval({
      start: new Date(sortedData[0].date),
      end: new Date(sortedData[sortedData.length - 1].date),
    }).map((date) => format(date, 'yyyy-MM-dd'));

    const mappedData = allDates.map((date) => {
      const found = sortedData.find((entry) => entry.date === date);
      return found || { date, users: 0, unregisteredUsers: 0 };
    });

    const today = format(new Date(), 'yyyy-MM-dd');
    if (mappedData[mappedData.length - 1].date !== today) {
      mappedData.push({ date: today, users: 0, unregisteredUsers: 0 });
    }

    return mappedData;
  })();

  const today = new Date().toISOString().split('T')[0]
  const todayData = dashboardData.dailyUserStats?.find((stat) => stat.date === today) || { users: 0, unregisteredUsers: 0 };

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
        {/* 월별 회원가입 통계 */}
        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">월별 회원가입통계</h3>
            <ComposedChart width={600} height={300} data={dashboardData.monthlyRegisterData || []}>
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

        {/* 월별 게시글 통계 */}
        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">월별 게시글 통계</h3>
            <BarChart width={600} height={300} data={dashboardData.monthPostCountData || []}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="month" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="count" name="게시글 수" fill="#82ca9d" />
            </BarChart>
          </div>
        </div>

        {/* 일일 접속자 통계 */}
        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4 flex justify-between items-center w-[600px]">
              <span>일일 접속자 수 통계</span>
              <div className="flex items-center">
                <div className="mr-4 flex items-center">
                  <span className="text-xs font-medium text-gray-500">오늘의 로그인 사용자</span>
                  <div className="ml-2 bg-red-100 text-red-800 rounded-full px-3 py-1 text-sm">
                    {todayData.users ? `총 ${todayData.users}명` : '0명'}
                  </div>
                </div>
                <div className="flex items-center">
                  <span className="text-xs font-medium text-gray-500">비로그인 사용자</span>
                  <div className="ml-2 bg-blue-100 text-blue-800 rounded-full px-3 py-1 text-sm">
                    {todayData.unregisteredUsers ? `총 ${todayData.unregisteredUsers}명` : '0명'}
                  </div>
                </div>
              </div>
            </h3>
            <LineChart width={600} height={300} data={processedData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" />
              <YAxis
                allowDecimals={false}
                domain={[0, (dataMax) => Math.ceil(dataMax / 10) * 10]}
              />
              <Tooltip />
              <Legend />
              <Line
                type="monotone"
                dataKey="users"
                name="로그인 사용자"
                stroke="#FF0000"
                dot={(data) => data.value === 0 ? false : true}
              />
              <Line
                type="monotone"
                dataKey="unregisteredUsers"
                name="비로그인 사용자"
                stroke="#0000FF"
                dot={(data) => data.value === 0 ? false : true}
              />
            </LineChart>
          </div>
        </div>

        {/* 월 평균 시간대별 사용자 활동 현황 */}
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
