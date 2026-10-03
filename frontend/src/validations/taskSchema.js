import * as yup from "yup";

export const taskSchema = yup.object({
  title: yup
    .string()
    .max(150, "Title cannot exceed 150 characters")
    .required("Title is required"),

  description: yup
    .string()
    .max(5000, "Description cannot exceed 5000 characters")
    .required("Description is required"),

  status: yup.string().required("Status is required"),

  category: yup.string().required("Task category is required"),

  priority: yup.string().required("Priority is required"),

  dueDate: yup
    .date()
    .typeError("Due date is required")
    .min(new Date(), "Due date cannot be in the past")
    .required("Due date is required"),

  estimatedTime: yup
    .number()
    .typeError("Estimated Time is required")
    .positive("Estimated Time must be greater than 0")
    .max(100000, "Estimated time is too large")
    .required("Estimated Time is required"),

  projectId: yup
    .number()
    .typeError("Project is required")
    .positive("Project must be greater than 0")
    .required("Project is required"),

  assignedTo: yup
    .array()
    .of(
      yup
        .number()
        .typeError("User id must be a number")
        .positive("User must be greater than 0")
        .required("User id cannot be null"),
    )
    .min(1, "At least one member is required")
    .max(50, "Cannot assign more than 50 members")
    .required("At least one member is required"),
});
