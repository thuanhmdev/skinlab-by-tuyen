/** @type {import('next').NextConfig} */
const nextConfig = {
  images: {
    remotePatterns: [
      {
        protocol: "http",
        hostname: "103.167.89.154",
        pathname: "/**",
      },
      {
        protocol: "https",
        hostname: "skinlabbytuyen.io.vn",
        port: "443",
        pathname: "/**",
      },
      {
        protocol: "http",
        hostname: "skinlabbytuyen.io.vn",
        pathname: "/**",
      },
      {
        protocol: "http",
        hostname: "localhost",
        port: "8080",
        pathname: "/**",
      },
      {
        protocol: "http",
        hostname: "host.docker.internal",
        pathname: "/**",
      },
      {
        protocol: "https",
        hostname: "lh3.googleusercontent.com",
        pathname: "/**",
      },
    
    ],
  },
  reactStrictMode: false,

};

export default nextConfig;
