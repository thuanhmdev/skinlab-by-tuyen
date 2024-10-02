import React from "react";
import DateFormat from "./date-format";
import Image from "next/image";
import clsx from "clsx";

interface IAvatar {
  user: TUser;
  date?: number;
  classNameAvatar?: string;
  classNameTextAvatar?: string;
  hiddenBadge?: boolean;
}

const Avatar = ({
  user,
  date = 0,
  classNameAvatar = "",
  classNameTextAvatar = "",
  hiddenBadge = false,
}: IAvatar) => {
  return (
    <>
      <div className="flex items-center gap-x-2">
        <div
          className={clsx({
            [`rounded-full relative overflow-hidden  bg-center bg-no-repeat bg-cover`]:
              true,
            [`w-[32px] h-[32px] md:w-[40px] md:h-[40px] xl:w-[48px] xl:h-[48px]`]:
              !classNameAvatar,
            [classNameAvatar]: !!classNameAvatar,
          })}
        >
          {user && (
            <Image
              src={
                user.role.name === "ADMIN"
                  ? `${process.env.NEXT_PUBLIC_ENDPOINT_STORAGE}${user.image}`
                  : user.imageProvider
              }
              alt="avatar"
              fill
              className="object-cover"
              sizes="20vw"
            />
          )}
        </div>

        <div>
          <p className="text-blue-500 font-semibold">
            <span
              className={clsx({
                [`text-[10px] md:text-sm xl:text-base`]: !classNameTextAvatar,
                [classNameTextAvatar]: !!classNameTextAvatar,
              })}
            >
              {user.name}
            </span>
            {user.role.name === "ADMIN" && !hiddenBadge && (
              <span className="px-1 text-[8px] lg:text-[10px] ml-2 py-1 bg-yellow-500 text-white rounded-md">
                Tác giả
              </span>
            )}
          </p>
          {date > 0 && <DateFormat date={date} />}
        </div>
      </div>
    </>
  );
};

export default Avatar;
