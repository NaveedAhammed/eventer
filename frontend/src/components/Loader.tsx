import clsx from "clsx";

interface Props {
	size?: string;
	color?: string;
	className?: string;
}

function Loader({ size = "2rem", color = "primary", className }: Props) {
	return (
		<div
			className={clsx(
				`loader border-2 border-${color} h-[${size}] w-[${size}]`,
				className
			)}
		></div>
	);
}

export default Loader;
