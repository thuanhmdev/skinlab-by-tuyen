"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { Controller, FormProvider, useForm } from "react-hook-form";

import { sendRequest } from "@/http/http";
import { ErrorMessage } from "@hookform/error-message";
import axios from "axios";
import { Session } from "next-auth";
import { useSession } from "next-auth/react";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { z } from "zod";
import KeywordFieldArray from "./keyword-field-array";
import PreviewImage from "@/components/preview-image";
import dynamic from "next/dynamic";

const CKEditor5 = dynamic(() => import("@/components/ckeditor5"), { ssr: false });

interface IProps {
  id?: string | null;
}

const BlogForm = ({ id }: IProps) => {
  const { data: session } = useSession();
  const router = useRouter();

  useEffect(() => {
    if (id && session) {
      const fetchDataBlog = async (id: string) => {
        const result = await sendRequest<TResponse<TBlog>>({
          url: `/blog-api/blogs/${id}`,
          method: "GET",
          typeComponent: "CSR",
        });
        if (result.statusCode === 200) {
          form.setValue("id", result.data.id);
          form.setValue("title", result.data.title);
          form.setValue("file", "");
          form.setValue("content", result.data.content);
          form.setValue("description", result.data.description);
          form.setValue(
            "keyword",
            result.data?.keyword?.length! > 0
              ? result.data?.keyword.split(",").map((item: string) => ({
                value: item,
              }))
              : [{ value: "" }]
          );
          form.setValue("image", result.data.image ?? "");
          // setBlogImageUrl(result.data.image);
        } else {
          toast.error("Server Error...");
        }

      };
      fetchDataBlog(id as string);
    } else {
    }
  }, [id, session]);

  const formSchema = z.object({
    id: z.string(),
    title: z.string().min(1, { message: "This field is required" }),
    file: z.any().optional(),
    content: z.string().min(1, { message: "This field is required" }),
    description: z.string(),
    keyword: z
      .array(
        z.object({
          value: z.string().min(1, { message: "This field is required" }),
        })
      )
      .min(1, "This field is required"),
    image: z.string(),
  });

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      id: "",
      title: "",
      file: "",
      content: "",
      description: "",
      keyword: [{ value: "" }],
      image: "",
    },
  });

  const onSubmit = async (values: z.infer<typeof formSchema>) => {
    const formData = new FormData();
    formData.append("id", values.id);
    formData.append("title", values.title);
    formData.append("content", values.content);
    formData.append("description", values.description);
    formData.append("file", values?.file?.[0] ?? null);
    formData.append(
      "keyword",
      values.keyword
        .flat(0)
        .map((i) => i.value)
        .toString()
        .trim()
    );
    formData.append("image", values.image);

    const response = await axios({
      method: values?.id ? "put" : "post",
      url: `${process.env.NEXT_PUBLIC_ENDPOINT_CSR_COMPONENT}/blog-api/blogs`,
      data: formData,
      headers: {
        "Content-Type": "multipart/form-data",
        Authorization: `Bearer ${(session as Session).accessToken}`,
      },
    });

    if (response.data.statusCode <= 299 && response.data.statusCode >= 200) {
      if (values.id) {
        toast.success(`Updated successful`);
      } else {
        toast.success(`Created successful`);
        router.push(`/admin/blog`);
      }
    }
  };

  return (
    <>
      <FormProvider {...form}>
        <form
          onSubmit={form.handleSubmit(onSubmit, (error) => console.log(error))}
          action=""
          className="flex flex-col gap-y-6 pe-1"
        >
          <div className="flex flex-col gap-y-4">
            <div>
              <label htmlFor="title" className="font-bold">
                Title <span className="text-red-500">*</span>
              </label>

              <Controller
                render={({ field }) => (
                  <input
                    {...field}
                    className="w-full rounded-lg px-3 py-2 border-2 border-gray-300 hover:border-gray-400 outline-1 outline-gray-400 transition-all duration-200 ease-in-out"
                    autoFocus
                  />
                )}
                name="title"
                control={form.control}
              />
              <ErrorMessage
                errors={form.formState.errors}
                name="title"
                render={({ message }) => (
                  <p className="text-red-500 text-sm">{message}</p>
                )}
              />
            </div>
            <div>
              <label htmlFor="description" className="font-bold">
                Short description
              </label>

              <Controller
                render={({ field }) => (
                  <textarea
                    {...field}
                    rows={2}
                    className="w-full rounded-lg px-3 py-2 border-2 border-gray-300 hover:border-gray-400 outline-1 outline-gray-400 transition-all duration-200 ease-in-out"
                    autoFocus
                  />
                )}
                name="description"
                control={form.control}
              />
              <ErrorMessage
                errors={form.formState.errors}
                name="title"
                render={({ message }) => (
                  <p className="text-red-500 text-sm">{message}</p>
                )}
              />
            </div>
            <div>
              <label htmlFor="keywordTag" className="font-bold">
                Keyword SEO
                <span className="text-red-500">*</span>
              </label>
              <KeywordFieldArray />
              <ErrorMessage
                errors={form.formState.errors}
                name="keyword"
                render={({ message }) => (
                  <p className="text-red-500 text-sm">{message}</p>
                )}
              />
            </div>

            <div className="grid w-full max-w-sm items-center gap-1.5">
              <label htmlFor="file" className="font-bold">
                Thumbnail
              </label>
              <input
                id="file"
                type="file"
                className="w-full rounded-lg px-3 py-2 border-2 border-gray-300 hover:border-gray-400 outline-1 outline-gray-400 transition-all duration-200 ease-in-out"
                {...form.register("file")}
              />

              <>
                <PreviewImage />
              </>
            </div>
            <div>
              <label htmlFor="keywordSEO" className="font-bold">
                Content
              </label>
              <Controller
                render={({ field }) => (
                  <CKEditor5 />
                )}
                name="content"
                control={form.control}
              />
              <ErrorMessage
                errors={form.formState.errors}
                name="content"
                render={({ message }) => (
                  <p className="text-red-500 text-sm">{message}</p>
                )}
              />
            </div>
            <div>
              <button
                type="submit"
                className=" bg-blue-500 py-2 px-4 rounded-md text-white font-bold hover:bg-blue-500/80"
              >
                Save
              </button>
            </div>
          </div>
        </form>
      </FormProvider>
    </>
  );
};

export default BlogForm;
