import { LuMenu } from "react-icons/lu";
import MenuItem from "./MenuItem";
import { IoPersonCircleOutline } from "react-icons/io5";
import { useState } from "react";

function UserMenu() {
	const [isOpen, setIsOpen] = useState(false);

	return (
		<div className="relative">
			<div
				className="flex items-center gap-1 p-2 bg-accent cursor-pointer rounded-full"
				onClick={() => setIsOpen((prev) => !prev)}
			>
				<LuMenu size={20} />
			</div>
			{isOpen && (
				<div className="absolute top-full translate-y-3 right-0 w-48 rounded-lg overflow-hidden py-4 bg-white shadow-xl">
					<MenuItem>
						<IoPersonCircleOutline size={18} />
						<span>Profile</span>
					</MenuItem>
					<MenuItem>Logout</MenuItem>
				</div>
			)}
		</div>
	);
}

export default UserMenu;
