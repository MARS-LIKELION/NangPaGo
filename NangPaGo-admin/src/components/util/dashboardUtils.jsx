import { format, eachDayOfInterval } from 'date-fns';

export const processDailyUserStats = (dailyUserStats) => {
  if (!dailyUserStats || !dailyUserStats.length) return [];

  const sortedData = [...dailyUserStats].sort(
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
};

export const getTodayData = (dailyUserStats) => {
  const today = new Date().toISOString().split('T')[0];
  return dailyUserStats?.find((stat) => stat.date === today) || {
    users: 0,
    unregisteredUsers: 0,
  };
};

export const groupDataByAction = (data) => {
  if (!data || !data.length) return [];

  const userActionData = {};
  data.forEach(({ hour, action, count }) => {
    if (!userActionData[action]) {
      userActionData[action] = {};
    }
    userActionData[action][hour] = count;
  });

  const hours = [...new Set(data.map((item) => item.hour))];
  return hours.map((hour) => {
    const entry = { hour };
    Object.keys(userActionData).forEach((action) => {
      entry[action] = userActionData[action][hour] || 0;
    });
    return entry;
  });
};
