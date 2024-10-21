import slugify from "slugify";

export const convertURL = (url: string) => {
  return slugify(url, {
    replacement: "-", // replace spaces with replacement character, defaults to `-`
    remove: undefined, // remove characters that match regex, defaults to `undefined`
    lower: false, // convert to lower case, defaults to `false`
    strict: false, // strip special characters except replacement, defaults to `false`
    locale: "vi", // language code of the locale to use
    trim: true, // trim leading and trailing replacement chars, defaults to `true`
  });
};

export const getIdFromSlug = (slug: string) => {
  if (!slug) return "";
  return slug.split("-uuid-").reverse()?.[0];
};