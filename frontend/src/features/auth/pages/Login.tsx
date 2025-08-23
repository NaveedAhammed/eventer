import { useLocation, useNavigate } from "react-router-dom";
import LoginFormContainer from "../containers/LoginFormContainer";

const Login = () => {
	const navigate = useNavigate();
	const location = useLocation();
	const from = location.state?.from?.pathname || "/";

	const handleNavigation = () => {
		navigate(from, { replace: true });
	};

	return <LoginFormContainer navigationHandler={handleNavigation} />;
};

export default Login;
