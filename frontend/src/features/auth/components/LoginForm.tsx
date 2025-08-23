import type { UseFormRegister, FieldErrors } from "react-hook-form";
import InputField from "@/components/Input/Input";
import Button from "@/components/Button/Button";
import { LuLockKeyhole } from "react-icons/lu";
import { HiOutlineEyeOff } from "react-icons/hi";
import Heading from "@/components/Heading/Heading";
import type { LoginSchema } from "../schema/loginSchema";
import { FiUser } from "react-icons/fi";

interface Props {
	onSubmit: () => void;
	register: UseFormRegister<LoginSchema>;
	errors: FieldErrors<LoginSchema>;
	isSubmitting: boolean;
}

function LoginForm({ onSubmit, register, errors, isSubmitting }: Props) {
	return (
		<form onSubmit={onSubmit} className="bg-white w-96 flex flex-col gap-4">
			<Heading
				title="Welcome Back!"
				subTitle="Login to your account to continue booking events and managing your
				favorites."
			/>

			<InputField
				leadingIcon={FiUser}
				type="text"
				placeholder="Username or Email"
				registration={register("usernameOrEmail")}
				error={errors.usernameOrEmail}
				disabled={isSubmitting}
			/>

			<InputField
				leadingIcon={LuLockKeyhole}
				type="password"
				registration={register("password")}
				error={errors.password}
				placeholder="Password"
				trailingIcon={HiOutlineEyeOff}
				disabled={isSubmitting}
			/>

			<Button type="submit" disabled={isSubmitting}>
				Login
			</Button>
		</form>
	);
}

export default LoginForm;
