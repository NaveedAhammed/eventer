import { useLocation, useNavigate, useSearchParams } from "react-router-dom";
import MobileLoginFormContainer from "../containers/MobileLoginFormContainer";
import EmailLoginFormContainer from "../containers/EmailLoginFormContainer";

const Login = () => {
	const [searchParams] = useSearchParams();
	const navigate = useNavigate();
	const location = useLocation();
	const from = location.state?.from?.pathname || "/";
	console.log(from);

	const handleNavigation = () => {
		navigate(from, { replace: true });
	};

	const mode = searchParams.get("mode") ? searchParams.get("mode") : "email";

	return (
		<>
			{mode === "email" ? (
				<EmailLoginFormContainer navigationHandler={handleNavigation} />
			) : (
				<MobileLoginFormContainer
					navigationHandler={handleNavigation}
				/>
			)}
		</>
	);
};

export default Login;
