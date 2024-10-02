"use client";
import React from "react";
import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";
dayjs.extend(relativeTime);
import "dayjs/locale/vi";
import useMounted from "@/hooks/use-mounted";
import clsx from "clsx";

const DateFormat = ({
  date,
  classNameCustom = "",
}: {
  date: number;
  classNameCustom?: string;
}) => {
  const hasMounted = useMounted();
  return hasMounted ? (
    <i
      className={clsx({
        ["block"]: true,
        [`text-[8px] md:text-xs`]: !classNameCustom,
        [classNameCustom]: !!classNameCustom,
      })}
    >
      {`${dayjs(date).locale("vi").format("DD/MM/YYYY h:mm A")} (${dayjs(
        dayjs(date).locale("vi")
      ).fromNow()})`}
    </i>
  ) : (
    <></>
  );
};

export default DateFormat;
