import BlogCarousel from "@/components/blog-carousel";
import BlogListPagination from "@/components/blog-list-pagination";
import DateFormat from "@/components/date-format";
import Loading from "@/components/loading";
import { sendRequest } from "@/http/http";
import Link from "next/link";
import { Suspense } from "react";
import Image from "next/image";
import { convertURL } from "@/utils/urlUtil";
import Avatar from "@/components/avatar";
import FanpageFacebook from "@/components/fanpage-facebook";

const HomePage = async ({
  searchParams,
}: {
  searchParams: { page?: string };
}) => {
  const [topBlogs, blogs] = await Promise.all([
    sendRequest<TResponse<TBlog[]>>({
      url: `/blog-api/blogs/carousel`,
      method: "GET",
    }),
    sendRequest<TResponse<TPagination<TBlog[]>>>({
      url: `/blog-api/blogs-pagination`,
      method: "GET",
      queryParams: {
        size: 10,
        page: searchParams?.page ?? "1",
      },
    }),
  ]);

  return (
    <div>
      <div className="container py-3">
        <h1 className="hidden">Skinlab by tuyen</h1>
        <div>
          {topBlogs?.data?.length > 0 && (
            <BlogCarousel blogs={topBlogs?.data} />
          )}
          <div className="relative">
            <div className="grid 2xl:grid-cols-12 2xl:gap-x-10 ">
              <div className="2xl:col-span-9 space-y-4 2xl:space-y-6">
                <h2 className="text-base sm:text-[18px] md:text-xl lg:text-2xl xl:text-3xl font-bold mb-0 mt-4 lg:mt-8">
                  Danh sách bài viết
                </h2>
                <Suspense fallback={<Loading />}>
                  {blogs.data.result.map((item) => (
                    <Link
                      href={`blog/${convertURL(item.title)}-uuid-${item.id}`}
                      key={item.id}
                      className="flex gap-3 md:gap-4 2xl:gap-5 cursor-pointer transition duration-300 hover:-translate-y-[2px]  hover:scale-[101%] hover:shadow-md"
                    >
                      <div className="">
                        <div className="child relative group-hover:scale(1.2) bg-center bg-cover bg-no-repeat w-[125px] h-[112px] sm:w-[170px] sm:h-[135px] md:w-[220px] md:h-[180px] lg:w-[240px] lg:h-[190px] xl:w-[260px] xl:h-[200px] rounded-2xl overflow-hidden">
                          (
                          <Image
                            src={
                              item.image
                                ? `${process.env.NEXT_PUBLIC_ENDPOINT_STORAGE}${item.image}`
                                : "/images/default_blog.jpg"
                            }
                            alt="Blog Image"
                            sizes="20vw"
                            fill
                            objectFit="cover"
                          />
                          )
                        </div>
                      </div>
                      <div className="flex flex-col gap-y-1 md:gap-y-2 lg:gap-y-3">
                        <h2 className="text-sm md:text-xl lg:text-2xl xl:text-[26px] font-bold">
                          {item.title}
                        </h2>

                        <Avatar user={item.blogger} />
                        <p className="text-[8px] md:text-xs block md:inline">
                          Ngày đăng: <DateFormat date={item.createdAt} />
                        </p>
                        <div>
                          <button className="bg-blue-600 hover:bg-blue-700 hover:-rotate-1 transition duration-300 text-white font-bold text-[8px] md:text-sm py-1 px-3 rounded-md">
                            Chi tiết
                          </button>
                        </div>
                      </div>
                    </Link>
                  ))}
                  <BlogListPagination total={blogs.data.meta.total} />
                </Suspense>
              </div>
              <div className="hidden mt-10 2xl:block 2xl:col-span-3 space-y-4 2xl:space-y-6">
                <div className="sticky top-[150px]">
                  <FanpageFacebook />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HomePage;
