import Image from "next/image";
import ScrollTop from "./scroll-top";
import Link from "next/link";
import { sendRequest } from "@/http/http";

const Footer = async () => {
  const setting = await sendRequest<TResponse<TSetting>>({
    url: `/blog-api/settings`,
    method: "GET",
  });
  return (
    <>
      <footer id="footer" className="bg-gray-100 py-6">
        <div className="container flex flex-col-reverse gap-y-2 md:flex-row md:justify-between items-center">
          <p className="text-black text-sm md:text-sm text-center">
            Copyright © {new Date().getFullYear()}, {setting.data.siteName}
          </p>
          <div className="flex justify-between">
            <ul className="flex flex-wrap items-center gap-4 m-0 text-lg lg:text-xl xl:text-2xl">
              <li>
                <Link href={setting.data.facebookLink ?? "#"} target="__blank">
                  <Image
                    width={25}
                    height={25}
                    src={"/images/facebook.svg"}
                    alt="facebook"
                    className="hover:scale-[1.1] transition-all ease-in-out duration-250"
                  />
                </Link>
              </li>
              <li>
                <Link href={setting.data.instagramLink ?? "#"} target="__blank">
                  <Image
                    width={25}
                    height={25}
                    src={"/images/instagram.svg"}
                    alt="instagram"
                    className="hover:scale-[1.1] transition-all ease-in-out duration-250"
                  />
                </Link>
              </li>
              <li>
                <Link href={setting.data.xLink ?? "#"} target="__blank">
                  <Image
                    width={25}
                    height={25}
                    src={"/images/x.svg"}
                    alt="x"
                    className="hover:scale-[1.1] transition-all ease-in-out duration-250"
                  />
                </Link>
              </li>
            </ul>
          </div>
          <div>
            <ScrollTop />
          </div>
        </div>
      </footer>
    </>
  );
};

export default Footer;
