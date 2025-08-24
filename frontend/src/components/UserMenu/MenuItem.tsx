import type { ReactNode } from "react";
import { Link } from "react-router-dom";

interface Props {
	children: ReactNode;
	linkTo?: string;
	onClick?: () => void;
}

function MenuItem({ children, linkTo, onClick }: Props) {
	if (linkTo) {
		return <Link to={linkTo}>{children}</Link>;
	}

	return (
		<div
			className="flex items-center gap-2 px-4 py-2 cursor-pointer hover:bg-accent transition-all"
			onClick={onClick}
		>
			{children}
		</div>
	);
}

export default MenuItem;
