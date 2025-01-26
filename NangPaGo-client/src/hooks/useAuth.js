import { useSelector } from "react-redux";

export const useAuth = () => {
  const isLoggedIn = useSelector((state) => Boolean(state.loginSlice.email));
  return { isLoggedIn };
};
