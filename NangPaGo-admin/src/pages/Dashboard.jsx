import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, LineChart, Line, ComposedChart } from 'recharts';
import { UserIcon, DocumentTextIcon } from '@heroicons/react/24/outline';
import { useState, useEffect } from 'react';
import { getDashboardData } from '../api/total';
import { format, eachDayOfInterval } from 'date-fns';

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
            <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">월별 회원가입 통계</h3>
            <ComposedChart width={600} height={300} data={dashboardData.monthlyRegisterData || []}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="month" />
              <YAxis />
              <Tooltip />
              <Legend
                formatter={(value, entry, index) => {
                  if (entry.dataKey === 'userCount' && index === 0) {
                    return '';
                  }
                  return value;
                }}
              />
              <Bar dataKey="userCount" name="사용자 수" fill="#87CEEB" />
              <Line type="monotone" dataKey="userCount" name="사용자 수(선)" stroke="#8884d8" />
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
              <span>일일 접속자 통계</span>
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

        {/* 최근 30일간 시간대별 사용자 활동 분석 */}
        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
              최근 30일간 시간대별 사용자 활동 분석
            </h3>
            <LineChart width={600} height={300} data={dashboardData.hourlyUserActionCountsDto}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis
                dataKey="hour"
                angle={0} // Remove rotation for consistency with other charts
                textAnchor="middle"
                height={50}
              />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line
                type="monotone"
                name="사용자 활동 빈도"
                dataKey="count"
                stroke="#6366f1"
                strokeWidth={2}
                dot={false}
              />
            </LineChart>
            <div className="mt-2 text-xs text-gray-500 text-right w-[600px]">
              *사용자 활동은 로그인, 게시물 및 댓글 작성/수정/삭제 등 다양한 활동을 기반으로 측정됩니다.
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
