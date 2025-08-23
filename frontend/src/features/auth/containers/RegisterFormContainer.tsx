import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { zodResolver } from "@hookform/resolvers/zod";
import authService from "../services/authService";
import RegisterForm from "../components/RegisterForm";
import { registerSchema, type RegisterSchema } from "../schema/registerSchema";
import Button from "@/components/Button/Button";
import { FcGoogle } from "react-icons/fc";
import { FaGithub } from "react-icons/fa";
import toast from "react-hot-toast";
import { getErrorMessage } from "@/utils/helpers";
import useAuth from "@/hooks/useAuth";

const RegisterFormContainer = () => {
	const { setAccessToken } = useAuth();

	const navigate = useNavigate();
	const {
		handleSubmit,
		register,
		formState: { errors, isSubmitting },
	} = useForm<RegisterSchema>({
		resolver: zodResolver(registerSchema),
	});

	const onSubmit = async (data: RegisterSchema) => {
		try {
			const accessToken = await authService.register(data);
			setAccessToken(accessToken);
			navigate("/", { replace: true });
		} catch (error) {
			const message = getErrorMessage(error);
			toast.error(message);
		}
	};

	const googleOAuthHandler = () => {
		window.location.href =
			"http://localhost:8080/oauth2/authorization/google?role=ORGANIZER";
	};

	const githubOAuthHandler = () => {
		window.location.href =
			"http://localhost:8080/oauth2/authorization/github?role=USER";
	};

	return (
		<div className="py-10 flex flex-col">
			<RegisterForm
				onSubmit={handleSubmit(onSubmit)}
				register={register}
				errors={errors}
				isSubmitting={isSubmitting}
			/>

			<div className="border-t border-gray-200 mt-6 relative">
				<span className="text-gray-400 text-xs absolute left-[50%] top-[50%] translate-x-[-50%] translate-y-[-55%] bg-white px-2">
					or you can sign in with
				</span>
			</div>

			<Button
				variant="outline"
				className="gap-2 text-sm mt-6"
				onClick={googleOAuthHandler}
			>
				<FcGoogle size={20} />
				<span>Continue with Google</span>
			</Button>

			<Button
				variant="outline"
				className="gap-2 text-sm mt-4"
				onClick={githubOAuthHandler}
			>
				<FaGithub size={20} />
				<span>Continue with GitHub</span>
			</Button>
		</div>
	);
};

export default RegisterFormContainer;
