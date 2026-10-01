import * as yup from "yup";

export const projectSchema = yup.object({
  title: yup
    .string()
    .max(200, "Tittle cannot be exceed 200 characters")
    .required("Title is required"),
  description: yup
    .string()
    .max(5000, "Description cannot be exceed 5000 characters")
    .required("Description is required"),
  priority: yup.string().required("Priority is required"),
  progress: yup
    .number()
    .required("Progress is required")
    .min(0, "Progress cannot be less than 0")
    .max(100, "Progress cannot be more than 100"),
  status: yup.string().required("Status is required"),
  members: yup
    .array()
    .min(1, "At least one member is required")
    .max(100, "Cannot add more than 100 members"),
  logo: yup
    .string()
    .max(500, "Logo cannot be exceed 500 characters")
    .required("Logo is required"),
  organizationName: yup
    .string()
    .max(200, "Organization name cannot be exceed 200 characters")
    .required("Organization name is required"),
  url: yup
    .string()
    .url("Invalid URL")
    .max(500, "URL cannot be exceed 500 characters"),
});
