import * as yup from "yup";

export const createMemberSchema = yup.object({
  fullName: yup
    .string()
    .max(100, "Full name cannot exceed 100 characters")
    .required("Name cannot be blank"),

  email: yup
    .string()
    .email("Please provide a valid email address")
    .max(150, "Email cannot exceed 150 characters")
    .required("Email cannot be blank"),

  designation: yup
    .string()
    .max(150, "Designation cannot exceed 150 characters")
    .required("Designation cannot be blank"),

  role: yup.string().required("Role is required"),
});
