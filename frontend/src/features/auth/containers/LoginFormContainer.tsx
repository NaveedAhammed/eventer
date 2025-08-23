import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import Button from "@/components/Button/Button";
import { FcGoogle } from "react-icons/fc";
import { FaGithub } from "react-icons/fa";
import { MdOutlinePhoneIphone } from "react-icons/md";
import LoginForm from "../components/LoginForm";
import authService from "../services/authService";
import toast from "react-hot-toast";
import { getErrorMessage } from "@/utils/helpers";
import { useNavigate } from "react-router-dom";
import useAuth from "@/hooks/useAuth";
import { loginSchema, type LoginSchema } from "../schema/loginSchema";

const LoginFormContainer = ({
	navigationHandler,
}: {
	navigationHandler: () => void;
}) => {
	const {
		handleSubmit,
		register,
		formState: { errors, isSubmitting },
	} = useForm<LoginSchema>({
		resolver: zodResolver(loginSchema),
	});

	const navigate = useNavigate();
	const { setAccessToken } = useAuth();

	const onSubmit = async (data: LoginSchema) => {
		try {
			const accessToken = await authService.login(data);
			if (accessToken) {
				navigationHandler();
				setAccessToken(accessToken);
			}
		} catch (error) {
			const message = getErrorMessage(error);
			toast.error(message);
		}
	};

	return (
		<div className="py-10 flex flex-col">
			<LoginForm
				register={register}
				errors={errors}
				isSubmitting={isSubmitting}
				onSubmit={handleSubmit(onSubmit, (e) => console.log(e))}
			/>

			<div className="border-t border-gray-200 mt-6 relative">
				<span className="text-gray-400 text-xs absolute left-[50%] top-[50%] translate-x-[-50%] translate-y-[-55%] bg-white px-2">
					or you can sign in with
				</span>
			</div>

			<Button
				variant="outline"
				className="gap-2 text-sm mt-6"
				onClick={() =>
					navigate("/login?mode=mobile", { replace: true })
				}
			>
				<MdOutlinePhoneIphone size={20} />
				<span>Continue with Phone</span>
			</Button>

			<Button variant="outline" className="gap-2 text-sm mt-4">
				<FcGoogle size={20} />
				<span>Continue with Google</span>
			</Button>

			<Button variant="outline" className="gap-2 text-sm mt-4">
				<FaGithub size={20} />
				<span>Continue with GitHub</span>
			</Button>
		</div>
	);
};

export default LoginFormContainer;
