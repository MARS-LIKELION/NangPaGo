export const HEADER_STYLES = {
  // Header 관련 스타일
  header: "sticky top-0 z-50 w-full px-1 mb-4 border-b bg-white h-16",
  headerContainer: "flex flex-row items-center justify-between px-4 h-full",
  logoContainer: "flex items-center justify-center h-full bg-white",
  logo: "h-full w-auto cursor-pointer py-2",
  navContainer: "flex items-center justify-center",

  // Button 관련 스타일
  responsiveButton: "group flex flex-col items-center justify-center space-y-1 px-3 py-2 rounded-md text-xs bg-transparent transition-colors duration-200 sm:flex-row sm:space-y-0 sm:space-x-2 sm:text-sm",
  activeButton: "text-primary font-semibold",
  inactiveButton: "text-text-900 hover:text-primary",
  buttonIcon: "transition-colors duration-200",
  buttonText: "transition-colors duration-200",

  // UserMenu 관련 스타일
  dropdownContainer: "absolute right-0 mt-2 w-48 rounded-md bg-white border border-gray-200 shadow-md z-10 overflow-visible",
  dropdownVisible: "transition ease-out duration-100 transform opacity-100 scale-100",
  dropdownContent: "relative z-20",
  dropdownItem: "bg-white w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2 transition-colors duration-200",
  arrowContainer: "absolute -top-2 right-6 w-4 h-4 rotate-45 transform bg-white border border-gray-200 shadow-md -z-10 transition-colors duration-200 group-hover:bg-gray-100",
  back: "bg-white text-text-900 hover:text-secondary w-full text-left flex items-center mb-2",
  notificationMessage: "py-2",
  notificationStatus: "text-sm text-gray-500",
  notificationDot: "absolute top-0 right-0 translate-x-1/2 -translate-y-1/2 bg-red-500 w-1.5 h-1.5 rounded-full",

  // Icon 크기
  iconSize: "w-[22px] h-[22px] sm:w-[26px] sm:h-[26px] transition-colors duration-200 ease-in-out",
};
