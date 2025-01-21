import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, LineChart, Line } from 'recharts';
import { UserIcon, DocumentTextIcon } from '@heroicons/react/24/outline'

// 더미 데이터
const userStats = [
  { name: '1월', users: 400 },
  { name: '2월', users: 300 },
  { name: '3월', users: 600 },
  { name: '4월', users: 800 },
  { name: '5월', users: 1000 },
  { name: '6월', users: 1200 },
];

const postStats = [
  { name: '1월', posts: 200 },
  { name: '2월', posts: 400 },
  { name: '3월', posts: 300 },
  { name: '4월', posts: 600 },
  { name: '5월', posts: 800 },
  { name: '6월', posts: 1000 },
];

export default function Home() {
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
                  <dt className="text-sm font-medium text-gray-500 truncate">총 사용자</dt>
                  <dd className="text-3xl font-semibold text-gray-900">1,234</dd>
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
                  <dt className="text-sm font-medium text-gray-500 truncate">총 게시글</dt>
                  <dd className="text-3xl font-semibold text-gray-900">5,678</dd>
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
            <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">월별 사용자 통계</h3>
            <LineChart width={500} height={300} data={userStats}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line type="monotone" dataKey="users" stroke="#8884d8" />
            </LineChart>
          </div>
        </div>

        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">월별 게시글 통계</h3>
            <BarChart width={500} height={300} data={postStats}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="posts" fill="#82ca9d" />
            </BarChart>
          </div>
        </div>
      </div>
    </div>
  )
} 