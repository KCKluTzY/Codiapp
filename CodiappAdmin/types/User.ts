export type UserStatus = "active" | "suspended" | "waiting";

export interface User {
    id: string;
    name: string;
    age: number;
    city: string;
    status: UserStatus;
    tutor: string | null;
}
