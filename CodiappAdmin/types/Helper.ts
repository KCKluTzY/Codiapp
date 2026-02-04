export type HelperStatus = "available" | "unavailable";

export interface Helper {
    id: string;
    name: string;
    maxDistance: number;      // en km
    status: HelperStatus;
    helpsThisMonth: number;
}
